package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class SearchField extends TextField {
	
	private Label searchNode;
	private Label clearNode;
	
	private EventHandler<ActionEvent> searchEvent;
	
	public SearchField() {
		this("");
	}
	
	public SearchField(String text) {
		super();
		this.setText(text);
		
		this.setPrefWidth(200);
		
		this.setPrompt("Search");
		
		this.internalScrollPane.setPadding(
			new Insets(
				this.internalScrollPane.getPadding().getTop(),
				20, 
				this.internalScrollPane.getPadding().getBottom(),
				24
			)
		);
		
		this.cornerRadius = 10;

		searchNode = new Label(new String(Character.toChars(0x1F50D)));
		searchNode.setTextFill(Theme.current().getShadow());
		searchNode.setFontSize(16);
		this.getChildren().add(searchNode);
		
		clearNode = new Label(new String(Character.toChars(0x2716)));
		clearNode.setTextFill(Theme.current().getShadow());
		clearNode.setFontSize(16);
		this.getChildren().add(clearNode);
		
		// Clear button
		clearNode.setMouseTransparent(false);
		clearNode.setOnMouseClicked((event)->{
			SearchField.this.setText("");
		});
		
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
			
			LWJGUI.runLater(()->{
				SearchField.this.cached_context.setSelected(null);
			});
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
					SearchField.this.cached_context.setSelected(null);
				}
			}
		});
	}
	
	public void setOnSearchEvent(EventHandler<ActionEvent> event) {
		this.searchEvent = event;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		searchNode.setLocalPosition(this, this.internalScrollPane.getPadding().getLeft()/2-searchNode.getWidth()/2+1,this.getHeight()/2-searchNode.getHeight()/2+1);
		clearNode.setLocalPosition(this, this.getWidth()-this.internalScrollPane.getPadding().getRight(),this.getHeight()/2-clearNode.getHeight()/2);
	}
}
