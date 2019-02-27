package lwjgui;

import java.io.IOException;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.CheckBox;
import lwjgui.scene.control.Menu;
import lwjgui.scene.control.MenuBar;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.control.ProgressBar;
import lwjgui.scene.control.RadioButton;
import lwjgui.scene.control.SeparatorMenuItem;
import lwjgui.scene.control.Tab;
import lwjgui.scene.control.TabPane;
import lwjgui.scene.control.ToggleGroup;
import lwjgui.scene.control.ToolBar;
import lwjgui.scene.control.text_input.TextArea;
import lwjgui.scene.layout.HBox;
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
		
		// Menu Bar
		MenuBar menuBar = new MenuBar();
		{
			// File Menu
			{
				Menu file = new Menu("File");
				file.getItems().add(new MenuItem("New"));
				file.getItems().add(new MenuItem("Exit"));
				menuBar.getItems().add(file);
			}
			
			// Edit Menu
			{
				Menu edit = new Menu("Edit");
				edit.getItems().add(new MenuItem("Undo"));
				edit.getItems().add(new MenuItem("Redo"));
				edit.getItems().add(new SeparatorMenuItem());
				edit.getItems().add(new MenuItem("Copy"));
				edit.getItems().add(new MenuItem("Paste"));
				menuBar.getItems().add(edit);
			}
			
			// Help Menu
			{
				Menu help = new Menu("Help");
				MenuItem click = new MenuItem("Click me!");
				help.getItems().add(click);
				menuBar.getItems().add(help);
				
				click.setOnAction((event)->{
					try {
						LWJGUIUtil.openURLInBrowser("https://github.com/orange451/LWJGUI");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}
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
		
		// Set the scene
		window.setScene(new Scene(background, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Complex Example";
	}
}