package lwjgui;

import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

/**
 * This is a simplified method of creating LWJGUI-based software. By using LWJGUIProgram, you can quickly assemble a user-interface for any type of project.
 */
public class LWJGUIProgramExample extends LWJGUIProgram {
	
	public static void main(String[] args) {
		LWJGUIProgram.start(new LWJGUIProgramExample(), args);
	}

	/*
	 * This is called right before the render loop is started and allows you to initialize the program.
	 * 
	 * @see lwjgui.LWJGUIProgram#init(java.lang.String[], lwjgui.scene.Window)
	 */
	@Override
	public void init(String[] args, Window window) {
		StackPane pane = new StackPane();
		window.getScene().setRoot(pane);
		
		Label label = new Label("Hello World!");
		pane.getChildren().add(label);
	}

	/*
	 * This is called from the program's render while loop, right before LWJGUI.render().
	 * 
	 * @see lwjgui.LWJGUIProgram#run()
	 */
	@Override
	public void run() {}

	@Override
	public String getProgramName() {
		return "Lightweight Java Graphical User Interface";
	}

	@Override
	public int getDefaultWindowWidth() {
		return 512;
	}

	@Override
	public int getDefaultWindowHeight() {
		return getDefaultWindowWidth()/2;
	}
	
}
