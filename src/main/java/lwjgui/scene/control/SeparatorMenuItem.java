package lwjgui.scene.control;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class SeparatorMenuItem extends MenuItem {
	
	public SeparatorMenuItem() {
		super(null);
		
		this.setPrefHeight(7);
		this.setAlignment(Pos.CENTER);
		
		// Container panel to hold the separator line
		StackPane test = new StackPane();
		test.setBackground(null);
		test.setFillToParentWidth(true);
		test.setPrefHeight(8);
		test.setPadding(new Insets(3));
		this.children.add(test);
		
		// The separator line (1 pixel tall)
		StackPane draw = new StackPane();
		draw.setMinHeight(1);
		draw.setMaxHeight(1);
		draw.setFillToParentWidth(true);
		draw.setBackground(Theme.current().getControlOutline());
		test.getChildren().add(draw);
	}
	
	@Override
	protected boolean isSelected() {
		return false;
	}

	@Override
	public void render(Context context) {
		for (int i = 0; i < this.children.size(); i++) {
			this.children.get(i).render(context);
		}
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
}
