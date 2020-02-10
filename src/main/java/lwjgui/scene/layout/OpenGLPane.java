package lwjgui.scene.layout;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.joml.Vector2i;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL2;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import lwjgui.gl.OffscreenBuffer;
import lwjgui.gl.Renderer;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.style.Background;
import lwjgui.style.BackgroundSolid;
import lwjgui.theme.Theme;

public class OpenGLPane extends Pane {
	private Vector2i oldSize = new Vector2i(1,1);
	private OpenGLPaneContext internalContext;
	private OffscreenBuffer buffer;
	private Renderer renderer;
	private Color internalBackground;
	private int nanoImage = -1;
	private boolean flipY;
	private boolean autoClear = true;
	
	class OpenGLPaneContext extends Context {
		public OpenGLPaneContext(long window) {
			super(null);
		}
		
		@Override
		public void refresh() {
			this.updateContext();
			super.refresh();
		}
		
		public void refresh(Context other) {
			this.refresh();
		}
	}
	
	public OpenGLPane() {
		// Should not resize here, because we may not be in a OPENGL context.
		//resizeBuffer();
		
		this.setBackground(new BackgroundSolid(Theme.current().getPane()));
	}
	
	@Override
	protected void init() {
		super.init();
		if ( internalContext == null ) {
			internalContext = new OpenGLPaneContext(-1);
			internalContext.init();
			buffer = new OffscreenBuffer((int)oldSize.x, (int)oldSize.y);
			if ( this.window.getContext().isModernOpenGL() ) {
				nanoImage = NanoVGGL3.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
			} else {
				nanoImage = NanoVGGL2.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		internalContext.dispose();
		internalContext = null;
		buffer.cleanup();
		NanoVG.nvgDeleteImage(window.getContext().getNVG(), nanoImage);
		nanoImage = -1;
	}
	
	private void resizeBuffer() {
		NanoVG.nvgDeleteImage(window.getContext().getNVG(), nanoImage);
		buffer.resize((int) oldSize.x, (int) oldSize.y);
		if ( this.window.getContext().isModernOpenGL() ) {
			nanoImage = NanoVGGL3.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		} else {
			nanoImage = NanoVGGL2.nvglCreateImageFromHandle(this.window.getContext().getNVG(), buffer.getTexId(), oldSize.x, oldSize.y, NanoVG.NVG_IMAGE_FLIPY);
		}
	}

	@Override
	public String getElementType() {
		return "openglpane";
	}
	
	public void setRendererCallback(Renderer renderer) {
		this.renderer = renderer;
	}
	
	public void setFlipY(boolean flip) {
		this.flipY = flip;
	}
	
	public boolean getFlipY() {
		return this.flipY;
	}
	
	public void setAutoClear(boolean autoClear) {
		this.autoClear = autoClear;
	}
	
	public boolean isAutoClear() {
		return this.autoClear;
	}
	
	@Override
	public void setBackground(Background color) {
		if ( color instanceof BackgroundSolid ) {
			this.internalBackground = ((BackgroundSolid)color).getColor();
		}
	}
	
	@Override
	public Background getBackground() {
		return null;
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
		if ( renderer != null && nanoImage != -1 ) {
			NanoVG.nvgSave(context.getNVG());
			//NanoVG.nvgEndFrame(context.getNVG());
			{
				// Bind & render to FBO
				this.buffer.bind();
				{
					// Background fill
					if ( this.internalBackground != null && isAutoClear() ) {
						float r = internalBackground.getRed()/255f;
						float g = internalBackground.getGreen()/255f;
						float b = internalBackground.getBlue()/255f;
						GL11.glClearColor(r, g, b, 1);
						GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
					}
					
					// Viewport
					GL11.glViewport(0, 0, (int)getWidth(), (int)getHeight());
					
					// Drawing
					NanoVG.nvgBeginFrame(internalContext.getNVG(), (int)getWidth(), (int)getHeight(), window.getPixelRatio());
					//internalContext.refresh(context);
					renderer.render(internalContext, (int)getWidth(), (int)getHeight());
					NanoVG.nvgEndFrame(internalContext.getNVG());
				}
				this.buffer.unbind();
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
			try (MemoryStack stack = stackPush()) {
				NVGPaint imagePaint = NanoVG.nvgImagePattern(nanovg, x, y, w, h, 0, nanoImage, 1, NVGPaint.callocStack(stack));
				NanoVG.nvgBeginPath(nanovg);
				NanoVG.nvgRect(nanovg, x, y, w, h);
				NanoVG.nvgFillPaint(nanovg, imagePaint);
				NanoVG.nvgFill(nanovg);
			}
		}
		
		// Render children
		super.render(context);
	}
}
