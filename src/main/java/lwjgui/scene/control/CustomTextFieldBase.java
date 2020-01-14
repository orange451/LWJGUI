package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;
import lwjgui.scene.Node;

public abstract class CustomTextFieldBase extends TextField {
	private Node leftNode;
	private Node rightNode;
	private float padding = 4;
	
	protected void setLeftNode(Node node) {
		this.leftNode = node;
	}
	
	protected void setRightNode(Node node) {
		this.rightNode = node;
	}
	
	/**
	 * Returns unmodifiable list of children
	 */
	@Override
	protected ObservableList<Node> getChildren() {
		ObservableList<Node> t = new ObservableList<>(super.getChildren());
		if ( leftNode != null )
			t.add(leftNode);
		if ( rightNode != null )
			t.add(rightNode);
		
		return t;
	}
	
	protected Node getLeftNode() {
		return this.leftNode;
	}
	
	protected Node getRightNode() {
		return this.rightNode;
	}
	
	@Override
	protected void position(Node parent) {
		this.updateChildrenPublic();
		super.position(parent);

		float rightRad = (this.getBorderRadii()[1] + this.getBorderRadii()[2]) / 2f;
		float leftRad = (this.getBorderRadii()[0] + this.getBorderRadii()[3]) / 2f;
		
		this.internalScrollPane.setInternalPadding(
			new Insets(
				this.internalScrollPane.getInternalPadding().getTop(),
				rightNode != null ? rightNode.getWidth()+padding+rightRad/2-1:padding-1, 
				this.internalScrollPane.getInternalPadding().getBottom(),
				leftNode != null ? leftNode.getWidth()+padding+leftRad/2:padding
			)
		);
	}
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		if ( leftNode != null ) {
			leftNode.setLocalPosition(this, this.internalScrollPane.getInnerBounds().getX()+padding,this.internalScrollPane.getInnerBounds().getHeight()/2-leftNode.getHeight()/2+1);
		}
		
		if ( rightNode != null )
			rightNode.setLocalPosition(this, this.internalScrollPane.getInnerBounds().getWidth()-rightNode.getWidth()-padding,this.internalScrollPane.getInnerBounds().getHeight()/2f-rightNode.getHeight()/2f+1);

		
		super.render(context);
	}
}
