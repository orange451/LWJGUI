package lwjgui.scene.layout;

import lwjgui.Context;
import lwjgui.scene.Node;

public class BorderPane extends Pane {
	private Node top;
	private Node center;
	private Node bottom;
	private Node left;
	private Node right;
	
	private VBox internalVBox;
	private HBox internalHBox;
	
	public BorderPane() {
		this.setFillToParentHeight(true);
		this.setFillToParentWidth(true);
		
		this.internalVBox = new VBox();
		this.internalVBox.setBackground(null);
		this.internalVBox.setFillToParentHeight(true);
		this.internalVBox.setFillToParentWidth(true);
		
		this.internalHBox = new HBox();
		this.internalHBox.setBackground(null);
		this.internalHBox.setFillToParentHeight(true);
		this.internalHBox.setFillToParentWidth(true);
		
		update();
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		layoutChildren();
	}
	
	protected void layoutChildren() {
		this.internalVBox.position(this);
	}
	
	@Override
	public void render(Context context) {
		super.render(context);
		this.internalVBox.render(context);
	}
	
	public void setTop(Node node) {
		this.top = node;
		update();
	}
	
	public void setBottom(Node node) {
		this.bottom = node;
		update();
	}
	
	public void setCenter(Node node) {
		this.center = node;
		update();
	}
	
	public void setLeft(Node node) {
		this.left = node;
		update();
	}

	public void setRight(Node node) {
		this.right = node;
		update();
	}
	
	private void update() {
		this.internalHBox.getChildren().clear();
		this.internalVBox.getChildren().clear();
		
		this.internalHBox.setAlignment(this.getAlignment());
		this.internalVBox.setAlignment(this.getAlignment());
		
		if ( top != null ) {
			this.internalVBox.getChildren().add(top);
		}
		
		if ( center != null || left != null || right != null ) {
			this.internalVBox.getChildren().add(this.internalHBox);
			
			if ( this.left != null ) {
				
				this.internalHBox.getChildren().add(sp(left, false));
			}
			
			if ( this.center != null ) {
				this.internalHBox.getChildren().add(sp(center, true));
			}
			
			if ( this.right != null ) {
				this.internalHBox.getChildren().add(sp(right, false));
			}
		}
		
		if ( bottom != null ) {
			this.internalVBox.getChildren().add(bottom);
		}
	}

	private StackPane sp(Node node, boolean fitWid) {
		StackPane ret = new StackPane();
		ret.setFillToParentHeight(true);
		ret.setFillToParentWidth(fitWid);
		ret.setAlignment(this.alignment);
		ret.getChildren().add(node);
		ret.setPrefWidth(node.getPrefWidth());
		ret.setBackground(null);
		ret.setPrefHeight(1);
		
		return ret;
	}
}
