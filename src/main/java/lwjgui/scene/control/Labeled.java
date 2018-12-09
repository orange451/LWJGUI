package lwjgui.scene.control;

import java.awt.Point;

import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.HBox;

public abstract class Labeled extends Control {
	protected GraphicLabel graphicLabel;
	private Node graphic;
	
	class GraphicLabel {
		protected HBox holder;
		protected Point offset = new Point();
		protected Label label;
		public Pos alignment;
		
		GraphicLabel() {
			alignment = Pos.CENTER;
			
			holder = new HBox();
			holder.setSpacing(4);
			holder.setFillToParentWidth(false);
			
			label = new Label("Label");
			holder.getChildren().add(label);
			
			holder.setBackground(null);
		}

		public void render(Context context) {
			if ( graphic != null ) {
				int minSize = (int) label.getFontSize();
				graphic.setPrefSize(minSize, minSize);
				graphic.setMaxSize(minSize, minSize);
			}
			
			holder.setAlignment(alignment);
			holder.position(Labeled.this);
			holder.offset(offset.x, offset.y);
			holder.render(context);
		}

		public double getMaximumPotentialWidth() {
			double x = Labeled.this.getPadding().getWidth();
			if ( holder.getChildren().size() == 2 )
				x += holder.getChildren().get(0).getPrefWidth();
			return label.getPrefWidth() + (holder.getSpacing()*holder.getChildren().size()-1) + x; 
		}

		public double getMaximumPotentialHeight() {
			return label.getHeight(); 
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
	
	@Override
	protected void position( Node parent ) {
		this.setPrefWidth(this.graphicLabel.getMaximumPotentialWidth());
		this.setPrefHeight(this.graphicLabel.getMaximumPotentialHeight());
		this.graphicLabel.holder.position(this);
		//this.graphicLabel.holder.offset(graphicLabel.offset.x, graphicLabel.offset.y);
		//this.graphicLabel.label.position(this);
		super.position(parent);
	}
	
	@Override
	public void render(Context context) {
		this.graphicLabel.render(context);
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
