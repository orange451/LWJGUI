package lwjgui.scene.control;

import org.joml.Vector2d;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.theme.Theme;

public class RadioButton extends ToggleButton {
	private int size = 16;

	public RadioButton( String name, ToggleGroup group ) {
		super(name, group);
		this.setPadding(Insets.EMPTY);
		this.setAlignment(Pos.CENTER_LEFT);
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
		this.cachedWidth = -1;
		
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

	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		//this.graphicLabel.offset.x = size + spacing;
		//this.graphicLabel.alignment = Pos.CENTER_LEFT;		
		this.setBorderRadii(size/2f);
		this.textOffset = size+4;
		
		double wid = this.getWidth();
		this.forceWidth(size);
		super.render(context);
		this.forceWidth(wid);

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
