package lwjgui.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import lwjgui.Context;

public class OffscreenBuffer {

	private Context context;
	private int width = 0;
	private int height = 0;
	private int texId = 0;
	private int fboId = 0;
	private boolean quadDirty = true;
	private TexturedQuad quad = null;
	private GenericShader quadShader = null;
	
	public OffscreenBuffer(Context context, int width, int height) {
		
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(String.format("invalid size: %dx%d", width, height));
		}
		
		this.context = context;
		
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
		}
		
		// Create texture
		texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		
		// update the framebuf
		if (fboId == 0) {
			fboId = GL30.glGenFramebuffers();
		}
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texId, 0);
		
		// remove the old quad
		quadDirty = true;
		
		return true;
	}
	
	public int getTexId() {
		return texId;
	}
	
	public int getFboId() {
		return fboId;
	}
	
	public int bind() {
		int oldFboId = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, getFboId());
		return oldFboId;
	}
	
	public void render() {
		render(0, 0, width, height);
	}
	
	public void render(int x, int y, int w, int h) {
		
		if (quadShader == null) {
			quadShader = new GenericShader();
		}
		quadShader.bind();
		quadShader.projectOrtho(x, y, w, h);
		
		if (quadDirty) {
			quadDirty = false;
			if (quad != null) {
				quad.cleanup();
			}
			quad = new TexturedQuad(0, 0, w, h, texId, quadShader);
		}
		quad.render();
	}
	
	public void unbind(int oldFboId) {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, oldFboId);
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
}
