package lwjgui.scene.control;

import java.awt.Point;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;

public class RadioButton extends ToggleButton {
	private int size = 18;

	public RadioButton( String name, ToggleGroup group ) {
		super(name, group);
		this.setPadding(Insets.EMPTY);
		this.setAlignment(Pos.CENTER_LEFT);
		
		this.textOffset = 24;
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
			int xx = (int) (this.getAbsoluteX()+this.size/2f);
			int yy = (int) (this.getAbsoluteY()+this.size/2f);
			
			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgShapeAntiAlias(nvg, true);
			NanoVG.nvgCircle(nvg, xx, yy, size*0.2f);
			//NanoVG.nvgFillColor(nvg, this.graphicLabel.label.getTextFill().getNVG());
			NanoVG.nvgFill(nvg);
		}
	}
}
