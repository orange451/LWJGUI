package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;

public class CombinedButton extends Control {
	protected HBox internal;
	protected ObservableList<Button> buttons;

	public CombinedButton() {
		this.flag_clip = false;
		
		this.internal = new HBox();
		this.internal.setSpacing(-0.5f);
		this.children.add(internal);
		
		this.buttons = new ObservableList<Button>();
		this.buttons.setAddCallback(new ElementCallback<Button>() {
			@Override
			public void onEvent(Button changed) {
				internal.getChildren().add(changed);
			}
		});
	}
	
	public CombinedButton(Button...buttons) {
		this();
		
		for (int i = 0; i < buttons.length; i++) {
			this.buttons.add(buttons[i]);
		}
	}

	@Override
	public String getElementType() {
		return "combinedbutton";
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
	
	/*
	@Override
	protected double getMinimumPotentialWidth() {
		return internal.getWidth();
	}
	*/
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		for (int i = 0; i < buttons.size(); i++) {
			Button b = buttons.get(i);
			b.setDisabled(this.isDisabled());
			b.setStylesheet(this.getStylesheet());
			
			boolean first = false;
			boolean last = false;
			
			if ( i == 0 )
				first = true;
			if ( i == buttons.size()-1 )
				last = true;

			if ( !first ) {
				b.setBorderRadii(0, b.getBorderRadii()[1], b.getBorderRadii()[2], 0);
			}
			if ( !last ) {
				b.setBorderRadii(b.getBorderRadii()[0], 0, 0, b.getBorderRadii()[3]);
			}
		}
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		internal.render(context);
	}

}
