package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.theme.Theme;

public class SearchField extends CustomTextFieldBase {
	
	private EventHandler<ActionEvent> searchEvent;
	
	public SearchField() {
		this("");
	}
	
	public SearchField(String text) {
		super();
		this.setText(text);
		
		this.setPrefWidth(200);
		
		this.setPrompt("Search");
		
		this.setBorderRadii(10);

		// Search button
		Label searchNode = new Label(new String(Character.toChars(0x1F50D)));
		searchNode.setTextFill(Theme.current().getShadow());
		searchNode.setFontSize(16);
		this.setLeftNode(searchNode);
		
		// Clear button
		Label clearNode = new Label(new String(Character.toChars(0x2716)));
		clearNode.setTextFill(Theme.current().getShadow());
		clearNode.setFontSize(16);
		clearNode.setMouseTransparent(false);
		clearNode.setOnMouseClicked((event)->{
			SearchField.this.setText("");
		});
		this.setRightNode(clearNode);
		
		// Clear Node color changing
		clearNode.setOnMouseEntered((event)->{
			clearNode.setTextFill(Theme.current().getText());
		});
		clearNode.setOnMouseExited((event)->{
			clearNode.setTextFill(Theme.current().getShadow());
		});
		
		// Fire search event when you click search node
		searchNode.setOnMouseReleased((event)->{
			if ( this.searchEvent != null ) {
				EventHelper.fireEvent(searchEvent, new ActionEvent());
			}
			
			window.getContext().setSelected(null);
		});
		
		// Search Node color changing
		searchNode.setOnMouseEntered((event)->{
			searchNode.setTextFill(Theme.current().getText());
		});
		searchNode.setOnMouseExited((event)->{
			searchNode.setTextFill(Theme.current().getShadow());
		});
		
		// Fire search event when you press enter
		this.setOnKeyPressed((event)->{
			if ( event.getKey() == GLFW.GLFW_KEY_ENTER ) {
				if ( SearchField.this.isDescendentSelected() ) {
					if ( this.searchEvent != null ) {
						EventHelper.fireEvent(searchEvent, new ActionEvent());
					}
					window.getContext().setSelected(null);
				}
			}
		});
	}
	
	public void setOnSearchEvent(EventHandler<ActionEvent> event) {
		this.searchEvent = event;
	}
}
