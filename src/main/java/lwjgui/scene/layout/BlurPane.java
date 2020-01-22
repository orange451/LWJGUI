package lwjgui.scene.layout;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.joml.Vector2i;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUI;
import lwjgui.gl.BlurShader;
import lwjgui.gl.BlurShaderOld;
import lwjgui.gl.OffscreenBuffer;
import lwjgui.gl.TexturedQuad;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.style.Background;
import lwjgui.style.BackgroundSolid;

public class BlurPane extends StackPane {
	private Vector2i oldSize = new Vector2i(2,2);
	private float blurRadius = 52;
	private Background internalBackground;

	private BlurBuffer buffer;
	private OffscreenBuffer bufferTemp;
	private int nanoImage = -1;

	public BlurPane() {
		this.setBackground(new BackgroundSolid(new Color(150,150,150,255)));
	}
	
	@Override
	protected void init() {
		super.init();
		bufferTemp = new OffscreenBuffer((int)oldSize.x, (int)oldSize.y);
		buffer = new BlurBuffer(bufferTemp.getWidth(), bufferTemp.getHeight(), bufferTemp);
		if ( this.window.getContext().isModernOpenGL() ) {
			nanoImage = NanoVGGL3.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		} else {
			nanoImage = NanoVGGL2.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		bufferTemp.cleanup();
		buffer.cleanup();
		NanoVG.nvgDeleteImage(window.getContext().getNVG(), nanoImage);
	}
	
	@Override
	public String getElementType() {
		return "blurpane";
	}

	private void resizeBuffer() {
		NanoVG.nvgDeleteImage(this.window.getContext().getNVG(), nanoImage);
		buffer.resize((int)oldSize.x, (int)oldSize.y);
		bufferTemp.resize(buffer.getWidth(), buffer.getHeight());
		if ( this.window.getContext().isModernOpenGL() ) {
			nanoImage = NanoVGGL3.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		} else {
			nanoImage = NanoVGGL2.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		}
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		// Check for resize
		Vector2i newDims = new Vector2i((int)getWidth(),(int)getHeight());
		if ( !newDims.equals(oldSize) || nanoImage == -1 ) {
			oldSize.set(newDims);
			resizeBuffer();
		}

		// FBO Rendering
		if ( nanoImage != -1 ) {
			NanoVG.nvgSave(context.getNVG());
			NanoVG.nvgEndFrame(context.getNVG());
			
			// Blit (Copy current FBO to BlurPane FBO)
			blit(context);
			
			// Blur (Blur the FBO)
			blur(context);
			
			// Restore nanovg
			NanoVG.nvgRestore(context.getNVG());
			context.refresh(); // Restore glViewport

			// Render children (Apply Styling)
			super.render(context);

			// Render FBO to screen (Draw our blurred image ontop)
			long nanovg = context.getNVG();
			float x = (int)this.getX();
			float y = (int)this.getY();
			float w = (int)this.getWidth();
			float h = (int)this.getHeight();
			try (MemoryStack stack = stackPush()) {
				NVGPaint imagePaint = NanoVG.nvgImagePattern(nanovg, x, y, w, h, 0, nanoImage, 1, NVGPaint.callocStack(stack));
				NanoVG.nvgBeginPath(nanovg);
				NanoVG.nvgRoundedRectVarying(context.getNVG(), (int)x, (int)y, (int)w, (int)h, (float)this.getBorderRadii()[0], (float)this.getBorderRadii()[1], (float)this.getBorderRadii()[2], (float)this.getBorderRadii()[3]);
				NanoVG.nvgFillPaint(nanovg, imagePaint);
				NanoVG.nvgFill(nanovg);
			}
		}
	}
	
	@Override
	public void setBackground(Background color) {
		this.internalBackground = color;
		super.setBackground(null);
	}
	
	public void setBlurRadius(float radius) {
		this.blurRadius = radius;
	}
	
	private void blur(Context context) {
		// Bind final buffer
		this.buffer.bind();
		
		// Clear
		GL11.glClearColor(1, 1, 1, 1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		
		// Render the blur buffer (uses the buffer temp as texture)
		this.buffer.render(context);
		
		// unbind
		this.buffer.unbind();
	}

	private void blit(Context context) {
		// Source
		float ratio = window.getPixelRatio();
		int srcfbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, srcfbo);
		
		// Destination
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, bufferTemp.getFboId());
		int destwid = bufferTemp.getWidth();
		int desthei = bufferTemp.getHeight();
		
		int sx1 = (int)(getX()*ratio);
		int sy1 = (int)(getY()*ratio);
		int sx2 = sx1 + (int) (destwid*ratio);
		int sy2 = sy1 + (int) (desthei*ratio);
		
		// Blit
		GL30.glBlitFramebuffer( sx1,sy1,sx2,sy2,
				                0,0, destwid,desthei,
				                GL11.GL_COLOR_BUFFER_BIT,
				                GL11.GL_NEAREST);
		
		// Rebind source
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,srcfbo);
	}
	
	class BlurBuffer extends OffscreenBuffer {

		private OffscreenBuffer source;
		
		public BlurBuffer(int width, int height, OffscreenBuffer source) {
			super(width, height);
			
			if ( this.quad != null ) {
				this.quad.cleanup();
			}
			
			this.source = source;
			if ( LWJGUI.getThreadWindow().getContext().isCoreOpenGL() ) {
				this.quadShader = new BlurShader();
			} else {
				this.quadShader = new BlurShaderOld();
			}
		}
		
		@Override
		public void render(Context context, int x, int y, int w, int h) {
			if ( !isVisible() )
				return;
			
			if ( this.quad == null || quadDirty ) {
				if ( this.quad != null ) {
					this.quad.cleanup();
				}
				quad = new TexturedQuad(0, 0, w, h, source.getTexId());
			}
			
			GL11.glViewport(x, y, w, h);
			this.quadDirty = false;
			quadShader.bind();
			quadShader.projectOrtho(0, h, w, -h);
			
			// bind stuff
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, source.getTexId());
			
			if ( internalBackground instanceof BackgroundSolid ) {
				BackgroundSolid bg = (BackgroundSolid)internalBackground;
				GL20.glUniform4f(GL20.glGetUniformLocation(quadShader.getProgram(), "uColor"),
						bg.getColor().getRed()/255f-0.5f,
						bg.getColor().getGreen()/255f-0.5f,
						bg.getColor().getBlue()/255f-0.5f,
						bg.getColor().getAlpha()/255f);
			}
			GL20.glUniform1f(GL20.glGetUniformLocation(quadShader.getProgram(), "uBlurSize"), blurRadius);
			GL20.glUniform2f(GL20.glGetUniformLocation(quadShader.getProgram(), "uTexelSize"), 1.0f/(float)w, 1.0f/(float)h);
			GL20.glUniform4f(GL20.glGetUniformLocation(quadShader.getProgram(), "uCornerRadii"), (float)Math.max(BlurPane.this.getBorderRadii()[0], 0.1), (float)Math.max(BlurPane.this.getBorderRadii()[1], 0.1), (float)Math.max(BlurPane.this.getBorderRadii()[2], 0.1), (float)Math.max(BlurPane.this.getBorderRadii()[3], 0.1));
			
			
			// Draw quad
			if ( context.isCoreOpenGL() ) {
				if ( quad != null ) {
					quad.render();
				}
			} else {

				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, source.getTexId());
				
				GL11.glBegin(GL11.GL_QUADS);
					GL11.glColor3f(1.0f, 1.0f, 1.0f);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(0, 0);
	
					GL11.glColor3f(1.0f, 1.0f, 1.0f);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex2f(w, 0);
	
					GL11.glColor3f(1.0f, 1.0f, 1.0f);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2f(w, h);
	
					GL11.glColor3f(1.0f, 1.0f, 1.0f);
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2f(0, h);
				GL11.glEnd();
			}
		}
	}
}
