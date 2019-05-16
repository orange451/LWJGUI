package lwjgui;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lwjgui.font.FontMetaData;
import lwjgui.font.FontStyle;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.CodeArea;
import lwjgui.scene.layout.BorderPane;

public class CodeAreaSyntaxHighlightingExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		BorderPane pane = new BorderPane();
		
		// Create code area
		CodeArea c = new CodeArea();
		c.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
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
		
		// Do syntax highlighting on text change
		c.setOnTextChange((event)->{
			// Clear current highlighting
			c.resetHighlighting();

			// Match patterns to the text of the code area
			String text = c.getText();
			Matcher matcher = PATTERN.matcher(text);
			
			// Iterate through matched patterns, apply highlighting.
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
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}