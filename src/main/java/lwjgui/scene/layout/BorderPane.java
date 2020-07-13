package lwjgui.scene.layout;

import lwjgui.collections.ObservableList;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;

public class BorderPane extends Pane {
	private Node top;
	private Node center;
	private Node bottom;
	private Node left;
	private Node right;
	
	private VBox internalVBox;
	private HBox internalHBox;
	
	private double spacing;
	
	public BorderPane() {
		this.setFillToParentHeight(true);
		this.setFillToParentWidth(true);
		
		this.internalVBox = new VBox();
		this.internalVBox.setAlignment(Pos.CENTER);
		this.internalVBox.setBackgroundLegacy(null);
		this.internalVBox.setFillToParentHeight(true);
		this.internalVBox.setFillToParentWidth(true);
		this.children.add(this.internalVBox);
		
		this.internalHBox = new HBox();
		this.internalHBox.setAlignment(Pos.CENTER);
		this.internalHBox.setBackgroundLegacy(null);
		this.internalHBox.setFillToParentHeight(true);
		this.internalHBox.setFillToParentWidth(true);
		
		update();
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		/*if ( center != null ) {
			float height = 0;
			int v = 0;
			if ( top != null ) {
				height += top.getHeight();
				v++;
			}
			if ( bottom != null ) {
				height += bottom.getHeight();
				v++;
			}
			
			//center.setMaxHeight((getHeight()-height)-(spacing*v));
			
			float width = 0;
			int h = 0;
			if ( left != null ) {
				width += left.getWidth();
				h++;
			}
			if ( right != null ) {
				width += right.getWidth();
				h++;
			}
			
			//center.setMaxWidth((getWidth()-width)-(spacing*h));
		}*/
		updateChildren();
	}

	@Override
	public String getElementType() {
		return "borderpane";
	}
	
	public void setSpacing( double spacing ) {
		this.spacing = spacing;
		update();
	}
	
	@Override
	public ObservableList<Node> getChildren() {
		return new ObservableList<Node>(children);
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
		
		this.internalHBox.setSpacing(spacing);
		this.internalVBox.setSpacing(spacing);
		
		// Top
		if ( top != null )
			this.internalVBox.getChildren().add(top);
		
		// Center box
		this.internalVBox.getChildren().add(this.internalHBox);
		
		// Left side
		if ( left != null )
			this.internalHBox.getChildren().add(sp(left, false));
		
		// Center
		this.internalHBox.getChildren().add(sp(center, true));
		
		// Right side
		if ( right != null )
			this.internalHBox.getChildren().add(sp(right, false));
		
		// Bottom
		if ( bottom != null )
			this.internalVBox.getChildren().add(bottom);
	}

	private StackPane sp(Node node, boolean fitWid) {
		StackPane ret = new StackPane();
		ret.setPrefSize(0, 0);
		ret.setFillToParentHeight(true);
		ret.setFillToParentWidth(fitWid);
		ret.setAlignment(this.alignment);
		if ( node != null ) {
			ret.getChildren().add(node);
			ret.setPrefWidth(node.getPrefWidth());
		}
		ret.setBackgroundLegacy(null);
		ret.setPrefHeight(1);
		
		return ret;
	}
}
