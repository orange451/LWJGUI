package lwjgui.scene.control;

import java.awt.Point;

import lwjgui.Context;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.HBox;

public abstract class Labeled extends Control {
	protected GraphicLabel graphicLabel;
	private Node graphic;
	
	class GraphicLabel {
		protected HBox holder;
		protected Point offset = new Point();
		private Label label;
		public Pos alignment;
		
		GraphicLabel() {
			alignment = Pos.CENTER;
			
			holder = new HBox();
			holder.setSpacing(2);
			holder.setFillToParentWidth(false);
			
			label = new Label("Label");
			holder.getChildren().add(label);
			
			holder.setBackground(null);
		}

		public void render(Node parent, Context context) {
			holder.position(parent);
			holder.offset(offset.x, offset.y);
			holder.render(context);
		}

		public double getMaximumPotentialWidth() {
			return label.getWidth(); 
		}
		
		protected void update() {
			holder.getChildren().clear();
			
			if ( graphic == null ) {
				holder.getChildren().add(label);
			} else {
				holder.getChildren().add(graphic);
				holder.getChildren().add(label);
			}
		}
	}
	
	public Labeled(String name) {
		this.graphicLabel = new GraphicLabel();
		this.setText(name);
	}
	
	public void setGraphic( Node graphic ) {
		this.graphic = graphic;
		this.graphicLabel.update();
	}
	
	public float getFontSize() {
		return this.graphicLabel.label.getFontSize();
	}
	
	public void setFontSize(float size) {
		this.graphicLabel.label.setFontSize(size);
	}
	
	public void setFontStyle(FontStyle style) {
		this.graphicLabel.label.setFontStyle(style);
	}

	public void setText(String string) {
		this.graphicLabel.label.setText(string);
	}
}
