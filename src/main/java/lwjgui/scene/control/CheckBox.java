package lwjgui.scene.control;

import org.joml.Vector2d;

import lwjgui.LWJGUIUtil;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class CheckBox extends ButtonBase {
	private boolean checked;
	
	private int size = 18;
	
	private String checkmark = "\u2714";

	public CheckBox(String name) {
		super(name);
		
		this.setBorderRadii(2.5f);

		this.setAlignment(Pos.CENTER_LEFT);
		
		this.textOffset = size+4;

		this.setPadding(Insets.EMPTY);

		this.setOnActionInternal(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				checked = !checked;
			}
		});
	}

	@Override
	public String getElementType() {
		return "checkbox";
	}
	
	@Override
	protected void resize() {
		this.cachedWidth = -1;
		
		//this.setMinWidth(size + this.graphicLabel.holder.getWidth() + spacing);
		this.setMinHeight(size);
		this.forceWidth(getTextWidth() + textOffset);
		
		super.resize();
	}
	
	@Override
	protected void position(Node parent) {
		resize();
		super.position(parent);
		resize();
	}
	
	@Override
	protected Vector2d getDrawSize() {
		return new Vector2d( size, size );
	}
	
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		double wid = this.getWidth();
		this.forceWidth(size);
		super.render(context);
		this.forceWidth(wid);

		if ( checked ) {
			float drawSize = size*1.2f;
			double xx = getX()+size/2f + 1;
			double yy = getY()+drawSize/2f+1;
			LWJGUIUtil.drawText(window.getContext(), checkmark, Font.DINGBAT, FontStyle.REGULAR, drawSize, Theme.current().getControl(), (int)xx, (int)yy+1, Pos.CENTER);
			LWJGUIUtil.drawText(window.getContext(), checkmark, Font.DINGBAT, FontStyle.REGULAR, drawSize, this.isDisabled()?Theme.current().getShadow():Theme.current().getText(), (int)xx, (int)yy, Pos.CENTER);
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
