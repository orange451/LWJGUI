package lwjgui.scene.control;

import java.awt.Point;

import lwjgui.event.ButtonEvent;
import lwjgui.event.EventHandler;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class CheckBox extends ButtonBase {
	public Label internalLabel;
	public Label internalLabel2;
	private boolean checked;
	
	private int size = 18;
	private int spacing = 4;
	
	private String checkmark = "\u2713";

	public CheckBox(String name) {
		super(name);

		this.internalLabel = new Label(checkmark);
		this.internalLabel2 = new Label(checkmark);
		this.internalLabel2.setTextFill(Theme.currentTheme().getControl());

		this.setPadding(Insets.EMPTY);

		this.setOnAction(new EventHandler<ButtonEvent>() {
			@Override
			public void handle(ButtonEvent event) {
				checked = !checked;
			}
		});
	}
	
	@Override
	protected void resize() {
		this.setMinWidth(size + this.graphicLabel.holder.getWidth() + spacing);
		this.setMinHeight(size);
		super.resize();
	}
	
	@Override
	protected Point getDrawSize() {
		return new Point( size, size );
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public void render(Context context) {
		this.graphicLabel.offset.x = size + spacing;
		this.graphicLabel.alignment = Pos.CENTER_LEFT;
		
		super.render(context);

		if ( checked ) {
			this.setAlignment(Pos.CENTER_LEFT);

			// Update the size of the checkmark
			internalLabel.setFontSize((float) (size*1.2));
			internalLabel2.setFontSize((float) (size*1.2));
			
			// Position the checkmark inside the box
			internalLabel.position(this);
			internalLabel2.position(this);
			
			// Offset a little bit
			double diffX = this.size - internalLabel.getWidth();
			double diffY = this.size - internalLabel.getHeight();
			int ox = (int) (diffX/2f)+1;
			int oy = (int) (diffY/2f)-1;
			internalLabel.offset(ox, oy);
			internalLabel2.offset(ox, oy+1);
			
			// Render checkmark
			internalLabel2.render(context);
			internalLabel.render(context);
		}
	}

	/**
	 * Sets whether or not this checkbox is checked.
	 * @param b
	 */
	public void setChecked(boolean b) {
		this.checked = b;
	}
	
	/**
	 * 
	 * @return Returns whether or not the checkbox is checked.
	 */
	public boolean isChecked() {
		return this.checked;
	}
	
	/**
	 * Sets the character used as the checkmark for this checkbox.
	 * @param c
	 */
	public void setCheckCharacter(String c) {
		this.checkmark = c;
	}
}
