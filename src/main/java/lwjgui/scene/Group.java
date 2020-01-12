package lwjgui.scene;

import lwjgui.collections.ObservableList;

public class Group extends Parent {

	@Override
	protected void resize() {
		// Fit this pane to the width of its elements.
		float maxWidthInside = (float) (getMaxElementWidth());
		maxWidthInside = (float) Math.max(maxWidthInside, getPrefWidth());
		size.x = maxWidthInside;
		
		// Fit this pane to the height of its elements.
		float maxHeightInside = (float) (getMaxElementHeight());
		maxHeightInside = (float) Math.max(maxHeightInside, getPrefHeight());
		size.y = maxHeightInside;
	
		// Apply normal resizing
		super.resize();
	}

	@Override
	public String getElementType() {
		return "group";
	}
	
	/**
	 *
	 * @return modifiable list of children.
	 */
	@Override
	public ObservableList<Node> getChildren() {
		return this.children;
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		for (int i = 0; i < getChildren().size(); i++) {
			// Clip to my bounds
			clip(context);

			// Draw child
			Node child = getChildren().get(i);
			if ( child == null )
				continue;
			child.render(context);
		}
	}

}
