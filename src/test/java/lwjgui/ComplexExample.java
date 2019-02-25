package lwjgui;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.CheckBox;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.Menu;
import lwjgui.scene.control.MenuBar;
import lwjgui.scene.control.ProgressBar;
import lwjgui.scene.control.RadioButton;
import lwjgui.scene.control.SegmentedButton;
import lwjgui.scene.control.Tab;
import lwjgui.scene.control.TabPane;
import lwjgui.scene.control.ToggleButton;
import lwjgui.scene.control.ToggleGroup;
import lwjgui.scene.control.ToolBar;
import lwjgui.scene.control.text_input.TextArea;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.VBox;

public class ComplexExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		VBox background = new VBox();
		window.getScene().setRoot(background);
		
		// Menu Bar
		MenuBar menuBar = new MenuBar();
		menuBar.getItems().add(new Menu("File"));
		menuBar.getItems().add(new Menu("Edit"));
		menuBar.getItems().add(new Menu("Help"));
		background.getChildren().add(menuBar);
		
		// Tool Bar
		ToolBar toolBar = new ToolBar();
		toolBar.getItems().add(new Button("New"));
		toolBar.getItems().add(new Button("Delete"));
		toolBar.getItems().add(new Button("Save"));
		toolBar.getItems().add(new Button("Exit"));
		background.getChildren().add(toolBar);
		
		// Tab Pane
		TabPane tabPane = new TabPane();
		tabPane.setFillToParentHeight(true);
		tabPane.setFillToParentWidth(true);
		background.getChildren().add(tabPane);
		
		// Tab 1
		{
			Tab tab = new Tab("Example Controls");
			tabPane.getTabs().add(tab);
			
			VBox primary = new VBox();
			primary.setPadding(new Insets(8));
			primary.setSpacing(8);
			primary.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
			tab.setContent(primary);
			
			// Middle content
			{
				HBox hbox = new HBox();
				hbox.setSpacing(8);
				hbox.setFillToParentHeight(true);
				hbox.setFillToParentWidth(true);
				primary.getChildren().add(hbox);
				
				// Left side
				{
					VBox vbox = new VBox();
					vbox.setSpacing(8);
					hbox.getChildren().add(vbox);
					
					ToggleGroup toggle = new ToggleGroup();
					RadioButton b1 = new RadioButton("RadioButton 1", toggle);
					RadioButton b2 = new RadioButton("RadioButton 2", toggle);
					vbox.getChildren().addAll(b1, b2);
	
					CheckBox c1 = new CheckBox("CheckBox 1");
					CheckBox c2 = new CheckBox("CheckBox 2");
					vbox.getChildren().addAll(c1, c2);
				}
				
				// Right side
				{
					TextArea text = new TextArea();
					text.setWordWrap(true);
					text.setFillToParentHeight(true);
					text.setFillToParentWidth(true);
					hbox.getChildren().add(text);
					
					text.setText("Lorem ipsum dolor sit amet, volumus percipit eleifend in nec. Postea prompta quaerendum mel ei. Qui audiam alterum ut, summo labitur evertitur ad pro. Recteque prodesset his ei, melius epicuri neglegentur et pro, mel an labores civibus adipiscing. Ullum senserit no mea. An vidisse impedit sadipscing est. Unum animal euismod vel no, eum decore sapientem ea.");
				}
			}
			
			// Bottom content
			{
				HBox hbox = new HBox();
				hbox.setSpacing(6);
				hbox.setAlignment(Pos.CENTER);
				hbox.setFillToParentWidth(true);
				primary.getChildren().add(hbox);
				
				ProgressBar bar = new ProgressBar();
				bar.setPadding(new Insets(0, 4, 0, 4));
				bar.setPrefWidth(0);
				bar.setFillToParentWidth(true);
				bar.setProgress(0.7);
				hbox.getChildren().add(bar);

				Button cancel = new Button("Cancel");
				hbox.getChildren().add(cancel);
				
				Button save = new Button("Save...");
				hbox.getChildren().add(save);
			}
			
		}
		
		// Tab 2
		{
			Tab tab = new Tab("Tab 2");
			tabPane.getTabs().add(tab);
		}
		
		// Tab 3
		{
			Tab tab = new Tab("Tab 3");
			tabPane.getTabs().add(tab);
		}
		
		/*
		// Create a vbox to store examples vertically
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(32);
		background.getChildren().add(vbox);
		
		{
			// Segmented Button
			displaySegmentedButton(vbox);
		}
		
		// Create hbox used to store two control types
		HBox hbox = new HBox();
		hbox.setSpacing(32);
		vbox.getChildren().add(hbox);
		
		{
			// Checkboxes
			displayCheckboxes(hbox);		
			
			// Redio Buttons
			displayRadioButtons(hbox);
		}*/
	}
	
	private static VBox exampleBox(String title) {
		VBox pane = new VBox();
		pane.setSpacing(4);
		pane.setAlignment(Pos.CENTER_LEFT);
		
		Label t = new Label(title);
		pane.getChildren().add(t);
		
		return pane;
	}

	private static void displaySegmentedButton(Pane parent) {
		HBox t = new HBox();
		parent.getChildren().add(t);
		
		ToggleGroup g = new ToggleGroup();
		ToggleButton b1 = new ToggleButton("Day", g);
		ToggleButton b2 = new ToggleButton("Week", g);
		ToggleButton b3 = new ToggleButton("Month", g);
		ToggleButton b4 = new ToggleButton("Year", g);
		
		SegmentedButton b = new SegmentedButton(b1, b2, b3, b4);
		t.getChildren().add(b);
	}

	private static void displayRadioButtons(Pane parent) {
		// Create a vertical box to hold radio buttons
		VBox radio = exampleBox("Radio Buttons:");
		parent.getChildren().add(radio);
		
		// Create radio options
		ToggleGroup g = new ToggleGroup(); // This prevents multiple radio options from being selected
		radio.getChildren().add(new RadioButton("Option 1", g));
		radio.getChildren().add(new RadioButton("Option 2", g));
		radio.getChildren().add(new RadioButton("Option 3", g));
		
		// Force select the third toggle
		g.selectToggle(g.getToggles().get(2));
	}

	private static void displayCheckboxes(Pane parent) {
		// Create a vertical box to hold checkboxes
		VBox pane = exampleBox("Check boxes:");
		parent.getChildren().add(pane);
		
		// Various ways to add checkboxes
		CheckBox b = new CheckBox("Hello World");
		b.setDisabled(true);
		pane.getChildren().add(b);
		pane.getChildren().add(new CheckBox("Testing"));
		pane.getChildren().add(new CheckBox("Check if you are cool!"));
		
		// Make the first checkbox selected visually and checked
		b.setChecked(true);
		LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext().setSelected(b);
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Complex Example";
	}

	@Override
	public int getDefaultWindowWidth() {
		return WIDTH;
	}

	@Override
	public int getDefaultWindowHeight() {
		return HEIGHT;
	}
}