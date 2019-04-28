package lwjgui;

import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import lwjgui.gl.GenericShader;
import lwjgui.gl.Renderer;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.OpenGLPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class OpenGLPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane
		StackPane root = new StackPane();
		
		VBox vbox = new VBox();
		root.getChildren().add(vbox);
		
		// Create an OpenGL pane (canvas)
		OpenGLPane ogl = new OpenGLPane();
		ogl.setPrefSize(24, 24);
		
		// Add a rendering callback to the opengl pane
		ogl.setRendererCallback(new RenderingCallbackTest());
		
		// Create label, set icon to opengl pane
		Label testLabel = new Label( "This label's icon is rendered with OpenGL!", ogl );
		vbox.getChildren().add(testLabel);
		
		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
	
	static class RenderingCallbackTest implements Renderer {
		private GenericShader shader;
		private int vao;
		private int vbo;
		private float rot;

		public RenderingCallbackTest() {
			// Setup geometry
			int vertSize = 3;	// vec3 in shader
			int texSize = 2;		// vec2 in shader
			int colorSize = 4;	// vec4 in shader
			int size = vertSize + texSize + colorSize; // Stride length
			int verts = 3; // Number of vertices
			int bytes = Float.BYTES; // Bytes per element (float)
			
			// Test shader
			shader = new GenericShader(); // Will load a testing vert/frag quad shader
			
			stackPush();
			{
				// Initial vertex data
				FloatBuffer buffer = stackMallocFloat(verts * size);
				buffer.put(new float[] {-0.5f, +0.5f, +0.0f});	// Vert 1 position
				buffer.put(new float[] {0.0f, 0.0f});			// Vert 1 texture
				buffer.put(new float[] {1.0f,0.0f,0.0f,1.0f});	// Vert 1 color
				
				buffer.put(new float[] {+0.5f, +0.5f, +0.0f});	// Vert 2 position
				buffer.put(new float[] {0.0f, 0.0f});			// Vert 2 texture
				buffer.put(new float[] {0.0f,1.0f,0.0f,1.0f});	// Vert 2 color
				
				buffer.put(new float[] {+0.0f, -0.5f, +0.0f});	// Vert 3 position
				buffer.put(new float[] {0.0f, 0.0f});			// Vert 3 texture
				buffer.put(new float[] {0.0f,0.0f,1.0f,1.0f});	// Vert 3 color
				buffer.flip();

				// Generate buffers
				vbo = glGenBuffers();
				vao = glGenVertexArrays();

				// Upload Vertex Buffer
				glBindBuffer(GL_ARRAY_BUFFER, vbo);
				glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

				// Set attributes (automatically stored to currently bound VAO)
				glBindVertexArray(vao);
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

		@Override
		public void render(Context context) {
			rot += 1.0e-3f;
			
			// Bind shader for drawing
			shader.bind();
			shader.projectOrtho( -0.5f, -0.5f, 1.0f, 1.0f );
			shader.setWorldMatrix(new Matrix4f().rotateY(rot));

			// Disable culling (just in case)
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			// Render geometry
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLES, 0, 3);
		}
	}
}