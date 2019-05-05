package lwjgui.scene.control;

import lwjgui.collections.ObservableList;

public class SegmentedButton extends CombinedButton {
	
	public SegmentedButton() {
		super();
	}
	
	public SegmentedButton(Button...buttons) {
		super(buttons);
	}
	
	public ObservableList<Button> getButtons() {
		return buttons;
	}
	
	@Override
	protected void resize() {
		super.resize();
		
		// Force height of all buttons to match the largest one
		int h = (int) this.getMaxElementHeight();
		for (int i = 0; i < buttons.size(); i++) {
			buttons.get(i).setMinHeight(h);
		}
	}
}
