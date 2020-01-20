package lwjgui.glfw;

public class GLFWException extends RuntimeException {

	private static final long serialVersionUID = -1821842574310124426L;

	public GLFWException(String message) {
		super(message);
	}
	
	public GLFWException(Exception e) {
		super(e);
	}

}
