package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.system.MemoryStack.stackMallocFloat;
import static org.lwjgl.system.MemoryStack.stackPop;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.gl.GenericShader;
import lwjgui.gl.Renderer;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.OpenGLPane;
import lwjgui.scene.layout.StackPane;

public class OpenGLPaneExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("OpenGL Pane", WIDTH, HEIGHT, true, false);

		// Initialize lwjgui for this window
		Window newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();

		// Add some components
		addComponents(scene);

		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}

		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a simple pane
		StackPane root = new StackPane();
		scene.setRoot(root);
		
		// Create an OpenGL pane (canvas)
		OpenGLPane ogl = new OpenGLPane();
		ogl.setPrefSize(100, 100);
		root.getChildren().add(ogl);
		
		// Add a rendering callback to the opengl pane
		ogl.setRendererCallback(new RenderingCallbackTest());
		
		// Add a label on top of the OpenGL pane
		ogl.getChildren().add(new Label("Hello World"));
	}

	private static class RenderingCallbackTest implements Renderer {
		private GenericShader shader;
		private int vao;
		private int vbo;

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
			// Bind shader for drawing
			shader.bind();
			shader.projectOrtho( -0.5f, -0.5f, 1.0f, 1.0f );

			// Disable culling (just in case)
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			// Render geometry
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLES, 0, 3);
		}
	}
}