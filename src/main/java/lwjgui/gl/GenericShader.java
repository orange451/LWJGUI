package lwjgui.gl;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;

public class GenericShader {
	private final int id;
	private final int vertexId;
	private final int fragmentId;
	protected final int posLoc;
	protected final int texCoordLoc;
	protected final int projMatLoc;
	protected final int viewMatLoc;
	protected final int worldMatLoc;
	private int texId;


	private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

	public GenericShader() {
		this(
				Thread.currentThread().getContextClassLoader().getResource("lwjgui/gl/vertex.glsl"),
				Thread.currentThread().getContextClassLoader().getResource("lwjgui/gl/fragment.glsl")
			);
	}

	public GenericShader(URL vertexShader, URL fragmentShader) {
		// make the shader
		vertexId = compileShader(vertexShader, true);
		fragmentId = compileShader(fragmentShader, false);
		posLoc = 0;
		texCoordLoc = 1;
		id = createProgram(
				vertexId,
				new int[] { fragmentId },
				new String[] { "inPos", "inTexCoord" },
				new int[] { posLoc, texCoordLoc }
				);

		projMatLoc = GL20.glGetUniformLocation(id, "projectionMatrix");
		viewMatLoc = GL20.glGetUniformLocation(id, "viewMatrix");
		worldMatLoc = GL20.glGetUniformLocation(id, "worldMatrix");
		
		// Generic white texture
		texId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
		int wid = 1;
		int hei = 1;
		ByteBuffer data = BufferUtils.createByteBuffer(wid*hei*4);
		while(data.hasRemaining()) {
			data.put((byte) (255 & 0xff));
		}
		data.flip();
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, wid, hei, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	}

	public void bind() {
		GL20.glUseProgram(id);

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
	}

	public void cleanup() {
		GL20.glDeleteShader(vertexId);
		GL20.glDeleteShader(fragmentId);
		GL20.glDeleteProgram(id);
	}

	protected static int createProgram(int vertexShaderId, int[] fragmentShaderIds, String[] attrs, int[] indices) {

		// build the shader program
		int id = GL20.glCreateProgram();
		GL20.glAttachShader(id, vertexShaderId);
		for (int fragmentShaderId : fragmentShaderIds) {
			GL20.glAttachShader(id, fragmentShaderId);
		}

		assert (attrs.length == indices.length);
		for (int i=0; i<attrs.length; i++) {
			GL20.glBindAttribLocation(id, indices[i], attrs[i]);
		}

		GL20.glLinkProgram(id);
		boolean isSuccess = GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_TRUE;
		if (!isSuccess) {
			throw new RuntimeException("Shader program did not link:\n" + GL20.glGetProgramInfoLog(id, 4096));
		}

		return id;
	}

	protected static int compileShader(URL url, boolean isVertex) {
		if (url == null)
			return -1;

		try {
			InputStream in = url.openStream();
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br = new BufferedReader(isr);

			String source = "";
			String st;
			while ((st = br.readLine()) != null)
				source += st + "\n";
			
			br.close();
			isr.close();
			in.close();	 
			
			return compileShader(source, isVertex);
		} catch (IOException ex) {
			throw new RuntimeException("can't compile shader at: " + url, ex);
		}
	}

	protected static int compileShader(String source, boolean isVertex) {

		int type;
		if (isVertex) {
			type = GL20.GL_VERTEX_SHADER;
		} else {
			type = GL20.GL_FRAGMENT_SHADER;
		}

		// try to massage JavaFX shaders into modern OpenGL
		if (source.startsWith("#ifdef GL_ES\n")) 
			source = modernizeShader(source, isVertex);
		
		// If ES
        String glVersion = new String(GL11.glGetString(GL11.GL_VERSION));
        boolean isOpenGLES = glVersion.contains("OpenGL ES");
        if ( isOpenGLES )
        	source = source.replace("#version 330", "#version 300 es\r\nprecision highp float;\r\nprecision highp sampler2DShadow;\r\n");

		int id = GL20.glCreateShader(type);
		GL20.glShaderSource(id, source);
		GL20.glCompileShader(id);

		boolean isSuccess = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) != GL11.GL_FALSE;
		if (!isSuccess) {

			// get debug info
			StringBuilder buf = new StringBuilder();
			buf.append("Shader did not compile\n");

			// show the compiler log
			buf.append("\nCOMPILER LOG:\n");
			buf.append(GL20.glGetShaderInfoLog(id, 4096));

			// show the source with correct line numbering
			buf.append("\nSOURCE:\n");
			String[] lines = source.split("\\n");
			for (int i=0; i<lines.length; i++) {
				buf.append(String.format("%4d: ", i + 1));
				buf.append(lines[i]);
				buf.append("\n");
			}

			throw new RuntimeException(buf.toString());
		}

		return id;
	}

	private static String modernizeShader(String source, boolean isVertex) {

		// replace attribute with in
		source = source.replaceAll("attribute ", "in ");

		if (isVertex) {

			// replace varying with out
			source = source.replaceAll("varying ", "out ");

		} else {

			// replace varying with in
			source = source.replaceAll("varying ", "in ");

			// add an out var for the color
			source = source.replaceAll("gl_FragColor", "outFragColor");
			source = "out vec4 outFragColor;\n\n" + source;

			// replace calls to texture2D with texture
			source = source.replaceAll("texture2D", "texture");
		}

		source = "#version 150\n\n" + source;

		return source;
	}

	private FloatBuffer matrix44Buffer = BufferUtils.createFloatBuffer(16);

	/**
	 * Fits the projection around the current contexts size.
	 * @param context
	 */
	public void project(Context context) {
		int width = LWJGUI.getThreadWindow().getWidth();
		int height = LWJGUI.getThreadWindow().getWidth();

		projectOrtho(0, 0, width, height);
	}

	/**
	 * Manually fit the projection.
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void projectOrtho(float x, float y, float w, float h) {
		setProjectionMatrix(new Matrix4f().ortho(x, x+w, y+h, y, -32000, 32000));
		setViewMatrix(IDENTITY_MATRIX);
		setWorldMatrix(IDENTITY_MATRIX);
	}

	public void setProjectionMatrix(Matrix4f mat) {
		mat.get(matrix44Buffer);
		glUniformMatrix4fv(projMatLoc, false, matrix44Buffer);
	}

	public void setViewMatrix(Matrix4f mat) {
		mat.get(matrix44Buffer);
		glUniformMatrix4fv(viewMatLoc, false, matrix44Buffer);
	}

	public void setWorldMatrix(Matrix4f mat) {
		mat.get(matrix44Buffer);
		glUniformMatrix4fv(worldMatLoc, false, matrix44Buffer);
	}

	public int getProgram() {
		return id;
	}
}
