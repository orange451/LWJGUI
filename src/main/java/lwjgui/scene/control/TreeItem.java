package lwjgui.scene.control;

import lwjgui.font.Font;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;

public class TreeItem<E> extends TreeBase<E> {
	private E root;
	private boolean opened;
	protected TreeItemLabel label;
	private TreeView<E> parentView;

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
		
		if ( this.parentView != null ) {
			this.parentView.needsRefresh = true;
		}
	}
	
	@Override
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

	protected void setTree(TreeView<E> treeView) {
		if ( !treeView.equals(this.parentView) )
			treeView.needsRefresh = true;
		
		this.parentView = treeView;
		for (int i = 0; i < this.items.size(); i++) {
			this.items.get(i).setTree(treeView);
		}
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
		this.label.setMouseTransparent(true);
		this.setBackgroundLegacy(null);
		setText(text);
		
		this.getChildren().add(this.label);
	}
	
	public void setText(String text) {
		if ( text == null )
			return;
		
		if ( text.equals(this.label.getText()) )
			return;
		
		this.label.setText(text);
	}
	
	public void setGraphic(Node node) {
		if (node == null)
			return;
		this.graphic = node;
		this.graphic.setPrefSize(16, 16);
		this.label.setGraphic(node);
	}
	
	@Override
	public void position(Node parent) {
		super.position(parent);
		if ( graphic != null ) {
			graphic.offset(0, 1);
		}
	}

	@Override
	public String getElementType() {
		return null;
	}
}