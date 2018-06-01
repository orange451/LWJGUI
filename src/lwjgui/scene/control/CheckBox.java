package lwjgui.scene.control;

import java.awt.Point;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.event.ButtonEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.theme.Theme;

public class CheckBox extends ButtonBase {
	public Label internalLabel;
	public Label internalLabel2;
	private boolean checked;
	
	private int size = 18;
	private int spacing = 4;

	public CheckBox(String name) {
		super(name);

		this.internalLabel = new Label("\u2713");
		this.internalLabel2 = new Label("\u2713");
		this.internalLabel2.setTextFill(Theme.currentTheme().getButton());
		this.cornerRadius = 3;

		this.setPadding(Insets.EMPTY);

		this.setOnAction(new ButtonEvent() {
			@Override
			public void onEvent() {
				checked = !checked;
			}
		});
	}
	
	@Override
	protected void resize() {
		this.setMinWidth(size + this.inside.holder.getWidth() + spacing);
		super.resize();
	}
	
	@Override
	protected Point getDrawSize() {
		return new Point( size, size );
	}

	@Override
	public void render(Context context) {
		this.inside.offset.x = size + spacing;
		this.inside.alignment = Pos.CENTER_LEFT;
		
		super.render(context);

		if ( checked ) {
			this.setAlignment(Pos.CENTER_LEFT);

			// Update the size of the checkmark
			internalLabel.setFontSize(size*2);
			internalLabel2.setFontSize(size*2);
			
			// Position the checkmark inside the box
			internalLabel.position(this);
			internalLabel2.position(this);
			
			// Offset a little bit
			internalLabel.offset(1, -2);
			internalLabel2.offset(1, -1);
			
			// Render checkmark
			internalLabel2.render(context);
			internalLabel.render(context);
		}
	}

	public void setChecked(boolean b) {
		this.checked = b;
	}
}
