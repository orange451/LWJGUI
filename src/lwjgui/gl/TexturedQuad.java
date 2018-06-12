package lwjgui.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

public class TexturedQuad {
	private final GenericShader shader;
	private final int vaoId;
	private final int vboId;
	private final int iboId;

	public int texId;

	public TexturedQuad(int x, int y, int w, int h, int texId, GenericShader shader) {

		this.shader = shader;
		this.texId = texId;

		// make the vertex array
		vaoId = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoId);

		try (MemoryStack m = MemoryStack.stackPush()) {

			// make the indices
			ByteBuffer indexBuf = m.bytes(new byte[] {
					0, 1, 2,
					0, 2, 3
			});
			iboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, iboId);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuf, GL15.GL_STATIC_DRAW);

			// make the vertices
			FloatBuffer vertexBuf = m.floats(new float[] {
					x + 0, y + 0, 0, 0,
					x + w, y + 0, 1, 0,
					x + w, y + h, 1, 1,
					x + 0, y + h, 0, 1
			});
			vboId = GL15.glGenBuffers();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuf, GL15.GL_STATIC_DRAW);
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, Float.BYTES*4, 0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, Float.BYTES*4, Float.BYTES*2);
		}

		// unbind things
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}

	public void render() {

		// bind stuff
		shader.bind();
		GL30.glBindVertexArray(vaoId);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

		// draw it!
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, 0);

		// unbind things
		GL30.glBindVertexArray(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void cleanup() {
		GL30.glDeleteVertexArrays(vaoId);
		GL15.glDeleteBuffers(vboId);
		GL15.glDeleteBuffers(iboId);
		// NOTE: don't cleanup the shader
	}
}
