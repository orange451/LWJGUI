package lwjgui.scene.control;

import java.awt.Point;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.theme.Theme;

public class RadioButton extends ToggleButton {
	private int size = 18;

	public RadioButton( String name, ToggleGroup group ) {
		super(name, group);
		this.setPadding(Insets.EMPTY);
		this.setAlignment(Pos.CENTER_LEFT);
		
		this.textOffset = size+4;
	}
	
	public RadioButton(String name) {
		this(name, null);
	}
	
	public RadioButton() {
		this("");
	}
	
	@Override
	protected boolean isPressed() {
		return super.isPressed() && !this.isSelected();
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

	@Override
	public void render(Context context) {
		//this.graphicLabel.offset.x = size + spacing;
		//this.graphicLabel.alignment = Pos.CENTER_LEFT;
		
		this.setCornerRadius(size/2f);
		
		super.render(context);

		if ( selected ) {
			clip(context);
			long nvg = context.getNVG();
			int xx = (int) (this.getX()+this.size/2f);
			int yy = (int) (this.getY()+this.size/2f);
			
			float r = size * 0.225f;

			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgShapeAntiAlias(nvg, true);
			NanoVG.nvgFillColor(nvg, Theme.current().getControl().getNVG());
			NanoVG.nvgCircle(nvg, xx, yy+1, r);
			NanoVG.nvgFill(nvg);

			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgShapeAntiAlias(nvg, true);
			NanoVG.nvgFillColor(nvg, (this.isDisabled()?Theme.current().getShadow():Theme.current().getText()).getNVG());
			NanoVG.nvgCircle(nvg, xx, yy, r);
			NanoVG.nvgFill(nvg);
		}
	}
}
