package lwjgui.scene.layout;

import org.joml.Vector2i;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;

import lwjgui.Color;
import lwjgui.gl.OffscreenBuffer;
import lwjgui.gl.Renderer;
import lwjgui.scene.Context;

public class OpenGLPane extends StackPane {
	private Vector2i oldSize = new Vector2i(1,1);
	private OffscreenBuffer buffer;
	private Renderer renderer;
	private Color internalBackground;
	private int nanoImage = -1;
	private boolean flipY;
	
	public OpenGLPane() {
		resizeBuffer();
	}
	
	private void resizeBuffer() {
		if ( buffer == null ) {
			buffer = new OffscreenBuffer((int)oldSize.x, (int)oldSize.y);
		} else {
			buffer.resize((int)oldSize.x, (int)oldSize.y);
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
	
	public void setRendererCallback(Renderer renderer) {
		this.renderer = renderer;
	}
	
	public void setFlipY(boolean flip) {
		this.flipY = flip;
	}
	
	@Override
	public void setBackground(Color color) {
		this.internalBackground = color;
		super.setBackground(null);
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
		if ( renderer != null && nanoImage != -1 ) {
			NanoVG.nvgSave(context.getNVG());
			NanoVG.nvgEndFrame(context.getNVG());
			{
				// Bind & render to FBO
				this.buffer.bind();
				if ( this.internalBackground != null ) {
					float r = internalBackground.getRed()/255f;
					float g = internalBackground.getGreen()/255f;
					float b = internalBackground.getBlue()/255f;
					GL11.glClearColor(r, g, b, 1);
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
				}
				GL11.glViewport(0, 0, (int)getWidth(), (int)getHeight());
				renderer.render(context);
				this.buffer.unbind();
	
				// Render FBO to screen
				//this.buffer.render(context, (int)this.getAbsoluteX()+(int)getWidth(), (int)(context.getHeight()-this.getHeight())-(int)this.getAbsoluteY());
			}
			
			// Restore nanovg
			NanoVG.nvgRestore(context.getNVG());
			context.refresh(); // Restore glViewport

			// Render FBO to screen
			long nanovg = context.getNVG();
			float x = (int)this.getX();
			float y = (int)this.getY();
			float w = (int)this.getWidth();
			float h = (int)this.getHeight();
			if ( flipY ) {
				y = y + h;
				h = -h;
			}
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
}
