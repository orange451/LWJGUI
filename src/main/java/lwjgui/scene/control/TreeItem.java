package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUIUtil;
import lwjgui.event.EventHandler;
import lwjgui.event.MouseEvent;
import lwjgui.font.Font;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class TreeItem<E> extends TreeBase<E> {
	private E root;
	private boolean opened;
	protected TreeItemLabel label;

	protected ContextMenu context;
	
	public TreeItem(E root, Node icon) {
		this.root = root;
		this.label = new TreeItemLabel(root.toString());
		this.label.setGraphic(icon);
		
		this.setBackground(null);
	}
	
	public void setText(String text) {
		this.label.label.setText(text);
	}
	
	public String getText() {
		return this.label.label.getText();
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
	
	public void setContextMenu(ContextMenu menu) {
		this.context = menu;
	}
	
	public ContextMenu getContextMenu() {
		return this.context;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		label.setText(root.toString());
	}
}

class TreeItemLabel extends HBox {
	protected Label label;
	private Node graphic;
	
	public TreeItemLabel(String text) {
		this.label = new Label();
		this.label.setFontSize(16);
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
	
	@Override
	public void position(Node parent) {
		super.position(parent);
		if ( graphic != null ) {
			graphic.offset(0, 1);
		}
	}
}