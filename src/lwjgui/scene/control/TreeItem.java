package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUIUtil;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class TreeItem<E> extends TreeBase<E> {
	private E root;
	private boolean opened;
	protected TreeItemLabel label;
	
	public TreeItem(E root, Node icon) {
		this.label = new TreeItemLabel(root.toString());
		this.label.setGraphic(icon);
	}
	
	public void setExpanded(boolean expanded) {
		this.opened = expanded;
	}
	
	public boolean isExpanded() {
		return this.opened;
	}
	
	public TreeItem(E root) {
		this(root, null);
	}

	public E getRoot() {
		return root;
	}
}

class TreeItemLabel extends HBox {
	protected Label label;
	private Node graphic;
	
	public TreeItemLabel(String text) {
		this.label = new Label();
		this.setSpacing(4);
		this.setMouseTransparent(true);
		this.setBackground(null);
		setText(text);
	}
	
	public void setText(String text) {
		this.label.setText(text);
		update();
	}
	
	public void setGraphic(Node node) {
		this.graphic = node;
		update();
	}
	
	private void update() {
		this.getChildren().clear();
		if ( graphic != null ) {
			this.getChildren().add(graphic);
			this.graphic.setPrefSize(16, 16);
		}
		this.getChildren().add(label);
	}
}

class TreeNode<E> extends HBox {
	protected TreeItem<E> item;
	protected TreeView<E> root;
	private StackPane inset;
	
	Label openGraphic;
	
	public TreeNode(TreeItem<E> item) {
		this.item = item;
		
		inset = new StackPane();
		inset.setMouseTransparent(true);
		inset.setPrefSize(1, 1);
		inset.setBackground(null);
		getChildren().add(inset);
		
		StackPane stateButton = new StackPane();
		stateButton.setAlignment(Pos.CENTER);
		stateButton.setPrefSize(18, 18);
		stateButton.setBackground(null);
		openGraphic = new Label();
		openGraphic.setFont(Font.COURIER);
		openGraphic.setFontSize(12);
		openGraphic.setMouseTransparent(true);
		stateButton.getChildren().add(openGraphic);
		
		getChildren().add(stateButton);
		getChildren().add(item.label);
		
		stateButton.setMousePressedEvent(new MouseEvent() {
			@Override
			public void onEvent(int button) {
				if ( item.getItems().size() == 0 )
					return;
				
				item.setExpanded(!item.isExpanded());
				
				this.consume();
			}
		});
		
		this.setMousePressedEvent(new MouseEvent() {
			long lastPressed = -1;
			@Override
			public void onEvent(int button) {
				long handle = cached_context.getWindowHandle();
				boolean isCtrlDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
								|| GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS
								|| GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SUPER) == GLFW.GLFW_PRESS
								|| GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SUPER) == GLFW.GLFW_PRESS;
				boolean isShiftDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
								|| GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
				
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
					root.clearSelectedItems();
					root.selectItem(item);
					
					// Double click
					if ( System.currentTimeMillis() - lastPressed < 300 ) {
						item.setExpanded(!item.isExpanded());
					}
				}
				cached_context.setSelected(TreeNode.this);
				lastPressed = System.currentTimeMillis();
			}
		});
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
				openGraphic.setText("▼");
			} else {
				openGraphic.setText("►");
			}
		}
	}

	public void setInset(int i) {
		inset.setMinWidth(i);
	}
	
	@Override
	public void render(Context context) {
		super.render(context);
		
		if ( root == null )
			return;
		
		// Set appropriate background color
		boolean selected = root.isItemSelected(item);
		boolean active = context.isFocused();
		Color color = selected?(active?Theme.currentTheme().getSelection():Theme.currentTheme().getSelectionPassive()):Theme.currentTheme().getPane();
		this.setBackground(color);
		
		// Set appropriate colors
		item.label.label.setTextFill((selected&&active)?Theme.currentTheme().getPane():Theme.currentTheme().getText());
		openGraphic.setTextFill(item.label.label.getTextFill());
		
		// Draw fancy outline
		if ( selected && active ) {
			this.clip(context);
			LWJGUIUtil.outlineRect(context, getAbsoluteX(), getAbsoluteY()+1, getWidth()-1, getHeight()-3, Theme.currentTheme().getSelectionAlt());
		}
	}
}