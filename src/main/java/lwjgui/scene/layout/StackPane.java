package lwjgui.scene.layout;

public class StackPane extends Pane {
	
	public StackPane() {
		//this.setFillToParentWidth(true);
		//this.setFillToParentHeight(true);
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public String getElementType() {
		return "stackpane";
	}
}
