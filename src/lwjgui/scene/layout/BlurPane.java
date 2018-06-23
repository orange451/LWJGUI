package lwjgui.scene.layout;

import org.joml.Vector2i;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import lwjgui.Color;
import lwjgui.gl.BlurShader;
import lwjgui.gl.OffscreenBuffer;
import lwjgui.gl.TexturedQuad;
import lwjgui.scene.Context;

public class BlurPane extends StackPane {
	private Vector2i oldSize = new Vector2i(1,1);
	private float blurRadius = 64;
	private Color internalBackground;

	private BlurBuffer buffer;
	private OffscreenBuffer bufferTemp;
	private int nanoImage = -1;

	public BlurPane() {
		resizeBuffer();
		
		this.setBackground(new Color(48,48,48,48));
	}

	private void resizeBuffer() {
		if ( buffer == null ) {
			bufferTemp = new OffscreenBuffer((int)oldSize.x, (int)oldSize.y);
			buffer = new BlurBuffer(bufferTemp.getWidth(), bufferTemp.getHeight(), bufferTemp);
		} else {
			buffer.resize((int)oldSize.x, (int)oldSize.y);
			bufferTemp.resize(buffer.getWidth(), buffer.getHeight());
		}
		if ( this.cached_context == null ) {
			return;
		}

		if ( this.cached_context.isModernOpenGL() ) {
			nanoImage = NanoVGGL3.nvglCreateImageFromHandle(this.cached_context.getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		} else {
			nanoImage = NanoVGGL2.nvglCreateImageFromHandle(this.cached_context.getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		}
	}

	@Override
	public void render(Context context) {
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
			
			// Blit
			blit(context);
			
			// Blur
			blur(context);
			
			// Restore nanovg
			NanoVG.nvgRestore(context.getNVG());
			context.refresh(); // Restore glViewport

			// Render FBO to screen
			long nanovg = context.getNVG();
			float x = (int)this.getAbsoluteX();
			float y = (int)this.getAbsoluteY();
			float w = (int)this.getWidth();
			float h = (int)this.getHeight();
			NVGPaint imagePaint = NanoVG.nvgImagePattern(nanovg, x, y, w, h, 0, nanoImage, 1, NVGPaint.calloc());
			NanoVG.nvgBeginPath(nanovg);
			NanoVG.nvgRect(nanovg, x, y, w, h);
			NanoVG.nvgFillPaint(nanovg, imagePaint);
			NanoVG.nvgFill(nanovg);
			imagePaint.free();
		}

		// Render children
		super.render(context);
	}
	
	@Override
	public void setBackground(Color color) {
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
		GL11.glClearColor(1, 1, 1, 0);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		// Render the blur buffer (uses the buffer temp as texture)
		this.buffer.render(context);
		
		// unbind
		this.buffer.unbind();
	}

	private void blit(Context context) {
		// Source
		int ratio = context.getPixelRatio();
		int srcfbo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, srcfbo);
		
		// Destination
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, bufferTemp.getFboId());
		int destwid = bufferTemp.getWidth();
		int desthei = bufferTemp.getHeight();
		
		int sx1 = (int)getAbsoluteX()*ratio;
		int sy1 = (int)getAbsoluteY()*ratio;
		int sx2 = sx1 + destwid*ratio;
		int sy2 = sy1 + desthei*ratio;
		
		// Blit
		GL30.glBlitFramebuffer( sx1,sy1,sx2,sy2,
				                0,0, destwid,desthei,
				                GL11.GL_COLOR_BUFFER_BIT,
				                GL11.GL_NEAREST);
		
		// Rebind source
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER,srcfbo);
	}
	
	class BlurBuffer2 {
		
	}
	
	class BlurBuffer extends OffscreenBuffer {

		private OffscreenBuffer source;
		
		public BlurBuffer(int width, int height, OffscreenBuffer source) {
			super(width, height);
			
			if ( this.quad != null ) {
				this.quad.cleanup();
			}
			
			this.source = source;
			this.quadShader = new BlurShader();
		}
		
		@Override
		public void render(Context context, int x, int y, int w, int h) {
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
			
			GL20.glUniform4f(GL20.glGetUniformLocation(quadShader.getProgram(), "uColor"), internalBackground.getRed()/255f, internalBackground.getGreen()/255f, internalBackground.getBlue()/255f, internalBackground.getAlpha()/255f);
			GL20.glUniform1f(GL20.glGetUniformLocation(quadShader.getProgram(), "uBlurSize"), blurRadius);
			
			if ( quad != null ) {
				quad.render();
			}
		}
	}
}