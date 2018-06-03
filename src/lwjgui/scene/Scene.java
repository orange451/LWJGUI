package lwjgui.scene;

import lwjgui.Context;

public class Scene extends Region {
	private Node root;

	@Override
	public double getAbsoluteX() {
		return 0;
	}
	
	@Override
	public double getAbsoluteY() {
		return 0;
	}
	
	@Override
	public double getWidth() {
		return this.getPrefWidth();
	}
	
	@Override
	public double getHeight() {
		return this.getPrefHeight();
	}
	
	public void setRoot(Node node) {
		this.root = node;
	}

	public Node getRoot() {
		return this.root;
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		if ( root == null )
			return;
		
		if ( root instanceof Region ) {
			((Region) root).setFillToParentHeight(true);
			((Region) root).setFillToParentWidth(true);
		}
		
		this.flag_clip = true;
		position(null);
		root.position(this);
		root.render(context);
	}
}
