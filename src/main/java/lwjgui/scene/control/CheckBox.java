package lwjgui.scene.control;

import java.awt.Point;

import lwjgui.LWJGUIUtil;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class CheckBox extends ButtonBase {
	private boolean checked;
	
	private int size = 18;
	
	private String checkmark = "\u2714";

	public CheckBox(String name) {
		super(name);
		
		this.setCornerRadius(2);

		this.setAlignment(Pos.CENTER_LEFT);
		
		this.textOffset = size+4;

		this.setPadding(Insets.EMPTY);

		this.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				checked = !checked;
			}
		});
	}
	
	@Override
	protected void resize() {
		//this.setMinWidth(size + this.graphicLabel.holder.getWidth() + spacing);
		this.setMinHeight(size);
		this.setMinWidth(getTextWidth() + textOffset);
		
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
		super.render(context);

		if ( checked ) {
			float drawSize = size*1.1f;
			LWJGUIUtil.drawText(checkmark, Font.DINGBAT, FontStyle.REGULAR, drawSize, Theme.current().getControl(), (int)getX()+(int)size/2f, (int)getY()+(int)(drawSize)/2f+2, Pos.CENTER);
			LWJGUIUtil.drawText(checkmark, Font.DINGBAT, FontStyle.REGULAR, drawSize, this.isDisabled()?Theme.current().getShadow():Theme.current().getText(), (int)getX()+(int)size/2f, (int)getY()+(int)(drawSize)/2f+1, Pos.CENTER);
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
