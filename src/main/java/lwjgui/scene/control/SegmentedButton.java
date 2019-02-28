package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;

public class SegmentedButton extends Control {
	private HBox internal;
	private ObservableList<ToggleButton> buttons;

	public SegmentedButton(ToggleButton...buttons) {
		this.flag_clip = false;
		
		this.internal = new HBox();
		this.internal.setSpacing(1);
		this.children.add(internal);
		
		this.buttons = new ObservableList<ToggleButton>();
		for (int i = 0; i < buttons.length; i++) {
			internal.getChildren().add(buttons[i]);
			this.buttons.add(buttons[i]);
		}
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		for (int i = 0; i < buttons.size(); i++) {
			ToggleButton b = buttons.get(i);
			
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

	@Override
	public void render(Context context) {
		internal.render(context);
	}

}
