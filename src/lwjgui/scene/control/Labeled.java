package lwjgui.scene.control;

import java.awt.Point;

import lwjgui.Color;
import lwjgui.Context;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.HBox;

public abstract class Labeled extends Control {
	protected ButtonLabel inside;
	
	class ButtonLabel {
		protected HBox holder;
		protected Point offset = new Point();
		private Label label;
		public Pos alignment;
		
		ButtonLabel() {
			alignment = Pos.CENTER;
			
			holder = new HBox();
			holder.setFillToParentWidth(false);
			
			label = new Label("Label");
			holder.getChildren().add(label);
			
			holder.setBackground(null);
		}

		public void render(Node parent, Context context) {
			//label.setMinWidth(label.getTextWidth());
			//label.setMaxWidth(label.getMinWidth());
			
			holder.position(parent);
			holder.offset(offset.x, offset.y);
			holder.render(context);
		}

		public double getMaximumPotentialWidth() {
			return label.getWidth(); 
		}
	}
	
	public Labeled(String name) {
		this.inside = new ButtonLabel();
		this.setText(name);
	}
	
	public float getFontSize() {
		return this.inside.label.getFontSize();
	}
	
	public void setFontSize(float size) {
		this.inside.label.setFontSize(size);
	}
	
	public void setFontStyle(FontStyle style) {
		this.inside.label.setFontStyle(style);
	}

	public void setText(String string) {
		this.inside.label.setText(string);
	}
}
