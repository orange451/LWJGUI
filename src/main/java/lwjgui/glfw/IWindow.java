package lwjgui.glfw;

public interface IWindow {

	public void updateDisplay(int fps);

	public void closeDisplay();

	public void setVisible(boolean flag);

	public float getDelta();
	
	public void dispose();

}
