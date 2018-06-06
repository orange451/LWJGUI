package lwjgui.scene.control;

import org.joml.Vector2d;
import lwjgui.LWJGUI;
import lwjgui.geometry.Insets;

public class Button extends ButtonBase {
	
	public Button(String name) {
		super(name);
		
		this.setMinSize(32, 24);
		this.setPadding(new Insets(4,8,4,8));
		this.setText(name);
	}
	
	public void setCornerRadius( double radius ) {
		this.cornerRadius = radius;
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}
	
	@Override
	public Vector2d getAvailableSize() {
		return new Vector2d(getMaxWidth(),getMaxHeight());
	}
	
	@Override
	protected void resize() {
		super.resize();
	}

	@Override
	public void setText(String string) {
		super.setText(string);
		update();
	}
	
	private void update() {
		LWJGUI.runLater(() -> {
			this.setPrefWidth(graphicLabel.getMaximumPotentialWidth()+this.getPadding().getWidth());
		});
	}
}
