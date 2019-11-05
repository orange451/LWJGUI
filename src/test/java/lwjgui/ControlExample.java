package lwjgui;

import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.CheckBox;
import lwjgui.scene.control.ComboBox;
import lwjgui.scene.control.CustomTextField;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.RadioButton;
import lwjgui.scene.control.SegmentedButton;
import lwjgui.scene.control.ToggleButton;
import lwjgui.scene.control.ToggleGroup;
import lwjgui.scene.control.ToggleSwitch;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class ControlExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	private static Button lockableButton;
	private static ComboBox<String> combo;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		StackPane background = new StackPane();
		
		// Create a vbox to store examples vertically
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(24);
		background.getChildren().add(vbox);

		// Segmented Button
		{
			displaySegmentedButton(vbox);
		}
		
		// Add drop down
		{
			displayDropdown(vbox);
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
		}
		
		// Disableable button
		{
			lockableButton = new Button("Click");
			vbox.getChildren().add(lockableButton);
		}
		
		
		// Set the scene
		window.setScene(new Scene(background, WIDTH, HEIGHT));
		window.show();
	}
	
	private void displayDropdown(VBox vbox) {
		HBox temp = new HBox();
		temp.setSpacing(8);
		vbox.getChildren().add(temp);
		
		combo = new ComboBox<String>("");
		combo.setPrefWidth(120);
		combo.getItems().add("Visual Basic");
		combo.getItems().add("Java");
		combo.getItems().add("C++");
		combo.getItems().add("Other");
		temp.getChildren().add(combo);
		
		CustomTextField t = new CustomTextField();
		temp.getChildren().add(t);
		
		Label clearNode = new Label(new String(Character.toChars(0x2716)));
		t.setRightNode(clearNode);
		
		clearNode.setOnMouseClicked((event)->{
			t.setText("");
		});
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
		t.setSpacing(8);
		t.setAlignment(Pos.CENTER);
		parent.getChildren().add(t);
		
		ToggleGroup g = new ToggleGroup();
		ToggleButton b1 = new ToggleButton("Day", g);
		ToggleButton b2 = new ToggleButton("Week", g);
		ToggleButton b3 = new ToggleButton("Month", g);
		ToggleButton b4 = new ToggleButton("Year", g);
		
		SegmentedButton b = new SegmentedButton(b1, b2, b3, b4);
		t.getChildren().add(b);
		
		ToggleSwitch toggle = new ToggleSwitch("Toggle");
		toggle.setOnActionEvent((event)->{
			combo.setDisabled(toggle.isSelected());
		});
		t.getChildren().add( toggle );
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
		
		// Toggle lockable button
		CheckBox lock = new CheckBox("Click to lock button");
		lock.setOnAction((event)->{
			lockableButton.setDisabled(lock.isChecked());
		});
		pane.getChildren().add(lock);
		
		// Make the first checkbox selected visually and checked
		b.setChecked(true);
		LWJGUI.getCurrentContext().setSelected(b);
	}
}