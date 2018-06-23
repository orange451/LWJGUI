package lwjgui.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import lwjgui.scene.Context;

public class OffscreenBuffer {
	private int width = 0;
	private int height = 0;
	private int texId = 0;
	private int fboId = 0;
	private int renderId = 0;
	protected boolean quadDirty = true;
	protected TexturedQuad quad = null;
	protected GenericShader quadShader = null;
	
	public OffscreenBuffer(int width, int height) {
		
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(String.format("invalid size: %dx%d", width, height));
		}
		
		// lazily create the quad and shader,
		// in case we want to render this buf in a different context than the one we created it in
		// (vertex arrays aren't shared between contexts, so neither are quads)
		quad = null;
		quadShader = null;
		resize(width, height);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean resize(int width, int height) {
		if (this.width == width && this.height == height) {
			return false;
		}
		
		this.width = width;
		this.height = height;
	
		// resize the texture
		if (texId != 0) {
			GL11.glDeleteTextures(texId);
			GL30.glDeleteFramebuffers(fboId);
			GL30.glDeleteRenderbuffers(renderId);
			texId = 0;
			fboId = 0;
			renderId = 0;
		}
		
		// Create texture
		if ( texId == 0 )
			texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
		
		// Set default filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		
		// update the framebuf
		if (fboId == 0)
			fboId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
		
		// The depth buffer
		if ( renderId == 0 )
			renderId = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, renderId);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, renderId);
		
		// remove the old quad
		quadDirty = true;
		

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		return true;
	}
	
	public int getTexId() {
		return texId;
	}
	
	public int getFboId() {
		return fboId;
	}
	
	private int unbindTo = -1;
	public void bind() {
		unbindTo = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getFboId());
	}
	
	public void unbind() {
		if ( unbindTo == -1 )
			return;
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, unbindTo);
	}
	
	public void render(Context context) {
		render(context, 0, 0);
	}
	
	public void render(Context context, int x, int y) {
		render(context, x, y, width, height);
	}
	
	public void render(Context context, int x, int y, int w, int h) {
		if (quadShader == null) {
			quadShader = new GenericShader();
		}
		x *= context.getPixelRatio();
		y *= context.getPixelRatio();
		GL11.glViewport(x, y, w*context.getPixelRatio(), h*context.getPixelRatio());
		quadShader.bind();
		quadShader.projectOrtho(0, 0, w, h);
		
		if (quadDirty) {
			quadDirty = false;
			if (quad != null) {
				quad.cleanup();
			}
			quad = new TexturedQuad(0, 0, w, h, texId);
		}
		if ( quad != null ) {
			quad.render();
		}
	}
	
	public void cleanup() {
		GL11.glDeleteTextures(texId);
		if (fboId != 0) {
			GL30.glDeleteFramebuffers(fboId);
		}
		if (quad != null) {
			quad.cleanup();
		}
		if (quadShader != null) {
			quadShader.cleanup();
		}
	}

	public TexturedQuad getQuad() {
		return quad;
	}
}
