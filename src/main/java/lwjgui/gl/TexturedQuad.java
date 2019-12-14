package lwjgui.gl;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class TexturedQuad {
	private final int vaoId;
	private final int vboId;

	public int texId;

	public TexturedQuad(int x, int y, int w, int h, int texId) {
		this.texId = texId;

		// Setup geometry
		int vertSize = 3; // vec3 in shader
		int texSize = 2; // vec2 in shader
		int colorSize = 4; // vec4 in shader
		int size = vertSize + texSize + colorSize; // Stride length
		int verts = 6; // Number of vertices
		int bytes = Float.BYTES; // Bytes per element (float)
		
		stackPush();
		{
			// Initial vertex data
			FloatBuffer buffer = stackMallocFloat(verts * size);
			buffer.put(new float[] {x+0, y+0, 0});			// Vert 1 position
			buffer.put(new float[] {0.0f, 0.0f});			// Vert 1 texture
			buffer.put(new float[] {1.0f,1.0f,1.0f,1.0f});	// Vert 1 color

			buffer.put(new float[] {x+w, y+0, 0});			// Vert 2 position
			buffer.put(new float[] {1.0f, 0.0f});			// Vert 2 texture
			buffer.put(new float[] {1.0f,1.0f,1.0f,1.0f});	// Vert 2 color

			buffer.put(new float[] {x+w, y+h, 0});			// Vert 3 position
			buffer.put(new float[] {1.0f, 1.0f});			// Vert 3 texture
			buffer.put(new float[] {1.0f,1.0f,1.0f,1.0f});	// Vert 3 color

			buffer.put(new float[] {x+0, y+0, 0});			// Vert 4 position
			buffer.put(new float[] {0.0f, 0.0f});			// Vert 4 texture
			buffer.put(new float[] {1.0f,1.0f,1.0f,1.0f});	// Vert 4 color

			buffer.put(new float[] {x+w, y+h, 0});			// Vert 5 position
			buffer.put(new float[] {1.0f, 1.0f});			// Vert 5 texture
			buffer.put(new float[] {1.0f,1.0f,1.0f,1.0f});	// Vert 5 color

			buffer.put(new float[] {x+0, y+h, 0});			// Vert 6 position
			buffer.put(new float[] {0.0f, 1.0f});			// Vert 6 texture
			buffer.put(new float[] {1.0f,1.0f,1.0f,1.0f});	// Vert 6 color
			buffer.flip();

			// Generate buffers
			vboId = glGenBuffers();
			vaoId = glGenVertexArrays();

			// Upload Vertex Buffer
			glBindBuffer(GL_ARRAY_BUFFER, vboId);
			glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

			// Set attributes (automatically stored to currently bound VAO)
			glBindVertexArray(vaoId);
			glEnableVertexAttribArray(0); // layout 0 shader
			glEnableVertexAttribArray(1); // layout 1 shader
			glEnableVertexAttribArray(2); // layout 2 shader
			int vertOffset = 0;
			glVertexAttribPointer( 0, vertSize,  GL_FLOAT, false, size*bytes, vertOffset );
			int texOffset = vertSize*bytes;
			glVertexAttribPointer( 1, texSize,   GL_FLOAT, false, size*bytes, texOffset );
			int colorOffset = texOffset + texSize*bytes;
			glVertexAttribPointer( 2, colorSize, GL_FLOAT, false, size*bytes, colorOffset );

			// Unbind
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindVertexArray(0);
		}
		stackPop();
	}

	public void render() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		
		// bind stuff
		GL30.glBindVertexArray(vaoId);
		if ( texId > -1 ) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		}

		// draw it!
		glBindVertexArray(vaoId);
		glDrawArrays(GL_TRIANGLES, 0, 6);

		// unbind things
		GL30.glBindVertexArray(0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void cleanup() {
		GL30.glDeleteVertexArrays(vaoId);
		GL15.glDeleteBuffers(vboId);
		// NOTE: don't cleanup the shader
	}
}
