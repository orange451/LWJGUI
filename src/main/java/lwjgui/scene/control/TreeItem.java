package lwjgui.scene.control;

import lwjgui.font.Font;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;

public class TreeItem<E> extends TreeBase<E> {
	private E root;
	private boolean opened;
	protected TreeItemLabel label;

	protected ContextMenu context;
	
	public TreeItem(E root, Node icon) {
		this.root = root;
		this.label = new TreeItemLabel(root.toString());
		this.label.setGraphic(icon);
		
		this.setBackgroundLegacy(null);
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
		this.setAlignment(Pos.CENTER_LEFT);
		this.label = new Label();
		this.label.setFont(Font.SEGOE);
		this.label.setFontSize(16);
		this.label.setPrefHeight(23);
		this.label.setAlignment(Pos.CENTER_LEFT);
		this.setSpacing(4);
		this.setMouseTransparent(true);
		this.setBackgroundLegacy(null);
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