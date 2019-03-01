package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.scene.Node;

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
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		for (int i = 0; i < buttons.size(); i++) {
			ToggleButton b = (ToggleButton) buttons.get(i);
			
			boolean first = false;
			boolean last = false;
			
			if ( i == 0 )
				first = true;
			if ( i == buttons.size()-1 )
				last = true;

			if ( !first ) {
				b.cornerNW = 0;
				b.cornerSW = 0;
			}
			if ( !last ) {
				b.cornerNE = 0;
				b.cornerSE = 0;
			}
		}
	}
}
