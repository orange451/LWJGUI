package lwjgui;


import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.font.FontMetaData;
import lwjgui.font.FontStyle;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.text_input.CodeArea;
import lwjgui.scene.layout.BorderPane;

public class CodeAreaSyntaxHighlightingExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( LWJGUIUtil.restartJVMOnFirstThread(true, args) )
			return;
		
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Text Area Example", WIDTH, HEIGHT, true, false);
		
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
		// Create background pane
		BorderPane pane = new BorderPane();
		scene.setRoot(pane);
		
		// Create code area
		CodeArea c = new CodeArea();
		c.setFillToParentHeight(true);
		c.setFillToParentWidth(true);
		c.setText("print(\"Hello World\")\n"
				+ "\n"
				+ "var a = 10\n"
				+ "var test = \"I'm a string\"");
		pane.setCenter(c);
		
		// Syntax highlighting variables
		final String KEYWORD_PATTERN = "\\b(" + String.join("|", new String[] {"print", "var"}) + ")\\b";
		final String STRING_PATTERN = "(\\[\\[)(.|\\R)*?(\\]\\])" + "|" + "\"([^\"\\\\]|\\\\.)*\"";
		final Pattern PATTERN = Pattern.compile(
				"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
				+ "|(?<STRING>" + STRING_PATTERN + ")"
		);
		
		// Do syntax highlighting
		c.setOnTextChange((event)->{
			c.resetHighlighting();

			String text = c.getText();
			Matcher matcher = PATTERN.matcher(text);
			
			// Iterate through pattern recognition, apply highlighting.
			while ( matcher.find() ) {
				int start = matcher.start();
				int end = matcher.end()-1;
				
				// Name of pattern
				if ( matcher.group("KEYWORD") != null ) {
					c.setHighlighting(start, end, new FontMetaData().color(Color.BLUE).style(FontStyle.BOLD));
				} else if ( matcher.group("STRING") != null ) {
					c.setHighlighting(start, end, new FontMetaData().color(Color.RED));
				}
			}
		});
		
		// Force syntax highlighting (triggers change event)
		c.appendText("");
	}
}