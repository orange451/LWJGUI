package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.event.EventHandler;
import lwjgui.event.MouseEvent;
import lwjgui.font.Font;
import lwjgui.geometry.Pos;
import lwjgui.glfw.input.KeyboardHandler;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.style.BackgroundSolid;
import lwjgui.theme.Theme;

public class TreeNode<E> extends HBox {
	protected TreeItem<E> item;
	protected TreeView<E> root;
	private StackPane inset;
	
	Label openGraphic;
	
	public TreeNode(TreeItem<E> item) {
		this.item = item;
		
		this.setBackgroundLegacy(null);
		
		inset = new StackPane();
		inset.setMouseTransparent(true);
		inset.setPrefSize(1, 1);
		inset.setBackgroundLegacy(null);
		this.setAlignment(Pos.CENTER_LEFT);
		getChildren().add(inset);
		
		StackPane stateButton = new StackPane() {
			@Override
			public String getElementType() {
				return "treeitem";
			}
		};
		stateButton.setAlignment(Pos.CENTER);
		stateButton.setPrefSize(20, 20);
		stateButton.setBackgroundLegacy(null);
		openGraphic = new Label();
		openGraphic.setFont(Font.COURIER);
		openGraphic.setFontSize(13);
		openGraphic.setMouseTransparent(true);
		stateButton.getChildren().add(openGraphic);
		
		getChildren().add(stateButton);
		getChildren().add(item.label);
		
		stateButton.setOnMousePressed( event -> {
			if ( event.button == GLFW.GLFW_MOUSE_BUTTON_LEFT ) {
				if ( item.getItems().size() == 0 )
					return;
				
				item.setExpanded(!item.isExpanded());
			}
			
			event.consume();
		});
		
		// Double click
		setOnMouseClicked(cc -> {
			if ( cc.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT )
				return;
			
			EventHandler<MouseEvent> t = item.getOnMouseClicked();
			MouseEvent ev = new MouseEvent(cc.mouseX, cc.mouseY, cc.button,cc.getClickCount());
			if ( t != null )
				t.handle(ev);
			
			if ( !ev.isConsumed() ) {
				if ( cc.getClickCount() == 2 ) {
					item.setExpanded(!item.isExpanded());
				}
			}
		});
		
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			//long lastPressed = -1;
			
			@Override
			public void handle(MouseEvent event) {
				KeyboardHandler kh = window.getKeyboardHandler();
				MouseHandler mh = window.getMouseHandler();
				boolean isCtrlDown = kh.isCtrlPressed();
				boolean isShiftDown = kh.isShiftPressed();
				boolean isLeftDown = event.button == GLFW.GLFW_MOUSE_BUTTON_LEFT;
				boolean isRightDown = event.button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
				
				if ( isLeftDown ) {
					if ( isCtrlDown ) { // Control click
						if ( root.isItemSelected(item) ) {
							root.deselectItem(item);
						} else {
							root.selectItem(item);
						}
					} else if ( isShiftDown ) { // Shift click
						int start = root.getItemIndex(root.getLastSelectedItem());
						if ( start == -1 )
							start = 0;
						int end = root.getItemIndex(item);
						if (end == -1)
							end = root.getItems().size()-1;
						
						root.selectItems(new IndexRange(start,end));
					} else { // Normal click
						
						// Deselect all BUT the clicked item
						ObservableList<TreeItem<E>> items = root.getSelectedItems();
						items.remove(item);
						for (int i = 0; i < items.size(); i++) {
							root.deselectItem(items.get(i--));
						}
						
						// Select clicked item
						root.selectItem(item);
					}
				} else if ( isRightDown ) {
					if ( !root.isItemSelected(item) ) {
						// Deselect all BUT the clicked item
						ObservableList<TreeItem<E>> items = root.getSelectedItems();
						items.remove(item);
						for (int i = 0; i < items.size(); i++) {
							root.deselectItem(items.get(i--));
						}
						
						// Select clicked item
						root.selectItem(item);
					}
					
					ContextMenu context = item.context;
					System.out.println(context);
					if ( context != null ) {
						context.show(getScene(), event.getMouseX(), event.getMouseY());
					}
				}
				window.getContext().setSelected(TreeNode.this);
			}
			
		});

		item.label.setOnMouseClicked(this.getOnMouseClicked());
		item.label.setOnMousePressed(this.getMousePressedEvent());
	}
	
	@Override
	public void position(Node parent) {
		this.setPrefWidth(0);
		super.position(parent);
		
		if ( item.getItems().size() == 0 ) {
			item.setExpanded(false);
			openGraphic.setText("");
		} else {
			if ( item.isExpanded() ) {
				openGraphic.setText("\u25bc");
			} else {
				openGraphic.setText("\u25ba");
			}
		}
	}

	public void setInset(int i) {
		inset.setMinWidth(i);
	}
	
	public TreeItem<E> getItem() {
		return this.item;
	}
	
	public TreeView<E> getRoot() {
		return this.root;
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		super.render(context);
		
		if ( root == null )
			return;
		
		if ( window == null )
			return;
		
		// Set appropriate background color
		boolean selected = root.isItemSelected(item);
		boolean active = window.isFocused();
		Color color = selected?(active?Theme.current().getSelection():Theme.current().getSelectionPassive()):null;
		this.setBackground(new BackgroundSolid(color));
		
		// Set appropriate colors
		item.label.label.setTextFill((selected&&active)?Theme.current().getPane():Theme.current().getText());
		openGraphic.setTextFill(item.label.label.getTextFill());
		
		// Draw fancy outline
		if ( selected && active ) {
			this.clip(context);
			LWJGUIUtil.outlineRect(context, getX(), getY()+1, getWidth()-1, getHeight()-3, Theme.current().getSelectionAlt());
		}
	}
	
	@Override
	public String getElementType() {
		return "treeitem";
	}
}