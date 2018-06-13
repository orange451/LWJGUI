package lwjgui.scene.layout;

import org.joml.Vector2i;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.gl.OffscreenBuffer;
import lwjgui.gl.Renderer;

public class OpenGLPane extends StackPane {
	private Vector2i oldSize = new Vector2i(1,1);
	private OffscreenBuffer buffer;
	private Renderer renderer;
	private Color internalBackground;
	
	public OpenGLPane() {
		resizeBuffer();
	}
	
	private void resizeBuffer() {
		if ( buffer == null ) {
			buffer = new OffscreenBuffer((int)oldSize.x, (int)oldSize.y);
		} else {
			buffer.resize((int)oldSize.x, (int)oldSize.y);
		}
	}
	
	public void setRendererCallback(Renderer renderer) {
		this.renderer = renderer;
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
		if ( !newDims.equals(oldSize) ) {
			oldSize.set(newDims);
			resizeBuffer();
		}
		
		// FBO Rendering
		if ( renderer != null ) {
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
				this.buffer.render(context, (int)this.getAbsoluteX(), (int)(context.getHeight()-this.getHeight())-(int)this.getAbsoluteY());
			}
			NanoVG.nvgRestore(context.getNVG());
		}
		
		// Render children
		context.refresh(); // Restore glViewport
		super.render(context);
	}
}
