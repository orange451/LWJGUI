package lwjgui.scene.control;

import java.awt.Point;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.Context;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.theme.Theme;

public class RadioButton extends ToggleButton {
	private int size = 18;
	private int spacing = 4;

	public RadioButton( String name, ToggleGroup group ) {
		super(name, group);
		this.setPadding(Insets.EMPTY);
	}
	
	public RadioButton(String name) {
		this(name, null);
	}
	
	public RadioButton() {
		this("");
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

	@Override
	public void render(Context context) {
		this.graphicLabel.offset.x = size + spacing;
		this.graphicLabel.alignment = Pos.CENTER_LEFT;
		
		this.cornerRadius = size/2f;
		
		super.render(context);

		if ( selected ) {
			clip(context);
			long nvg = context.getNVG();
			int xx = (int) (this.getAbsoluteX()+this.size/2f);
			int yy = (int) (this.getAbsoluteY()+this.size/2f);
			
			NanoVG.nvgBeginPath(nvg);
<<<<<<< HEAD
			NanoVG.nvgShapeAntiAlias(nvg, true);
=======
>>>>>>> 567b5e317077847329cf53dbb35f745f46051def
			NanoVG.nvgCircle(nvg, xx, yy, size*0.2f);
			NanoVG.nvgFillColor(nvg, Theme.currentTheme().getText().getNVG());
			NanoVG.nvgFill(nvg);
		}
	}
}
