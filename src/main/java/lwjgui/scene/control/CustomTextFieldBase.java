package lwjgui.scene.control;

import lwjgui.geometry.Insets;
import lwjgui.scene.Node;

public abstract class CustomTextFieldBase extends TextField {
	private Node leftNode;
	private Node rightNode;
	private float padding = 4;
	
	protected void setLeftNode(Node node) {
		if ( this.leftNode != null ) {
			this.getChildren().remove(node);
		}
		this.leftNode = node;
		this.getChildren().add(node);
	}
	
	protected void setRightNode(Node node) {
		if ( this.rightNode != null ) {
			this.getChildren().remove(node);
		}
		this.rightNode = node;
		this.getChildren().add(node);
	}
	
	protected Node getLeftNode() {
		return this.leftNode;
	}
	
	protected Node getRightNode() {
		return this.rightNode;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		this.internalScrollPane.setPadding(
			new Insets(
				this.internalScrollPane.getPadding().getTop(),
				rightNode != null ? rightNode.getWidth()+padding+cornerRadius/2-1:padding-1, 
				this.internalScrollPane.getPadding().getBottom(),
				leftNode != null ? leftNode.getWidth()+padding+cornerRadius/2:padding
			)
		);
		
		if ( leftNode != null )
			leftNode.setLocalPosition(this, this.internalScrollPane.getPadding().getLeft()/2-leftNode.getWidth()/2+1,this.getHeight()/2-leftNode.getHeight()/2);
		
		if ( rightNode != null )
			rightNode.setLocalPosition(this, this.getWidth()-this.internalScrollPane.getPadding().getRight(),this.getHeight()/2-rightNode.getHeight()/2);
	}
}
