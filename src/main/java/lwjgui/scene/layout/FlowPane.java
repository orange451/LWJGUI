package lwjgui.scene.layout;

import org.joml.Vector2f;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.Node;

public class FlowPane extends FillableRegion implements Gappable {
	private Orientation orientation = Orientation.HORIZONTAL;
	private ObservableList<Node> items = new ObservableList<Node>();
	
	private Vector2f lastSize = new Vector2f();

	private DirectionalBox internalBox;
	private float hgap = 0;
	private float vgap = 0;
	
	public FlowPane() {
		this(Orientation.HORIZONTAL);
	}
	
	public FlowPane(Orientation orientation) {
		this.setAlignment(Pos.TOP_LEFT);
		this.orientation = orientation;
		
		this.items.setAddCallback(new ElementCallback<Node>() {
			@Override
			public void onEvent(Node object) {
				rebuild();
			}
		});
		this.items.setRemoveCallback(new ElementCallback<Node>() {
			@Override
			public void onEvent(Node object) {
				rebuild();
			}
		});
	}
	
	@Override
	public float getVgap() {
		return this.vgap;
	}

	@Override
	public float getHgap() {
		return this.hgap;
	}

	@Override
	public void setVgap(float spacing) {
		if ( this.vgap == spacing )
			return;
		
		this.vgap = spacing;
		rebuild();
	}

	@Override
	public void setHgap(float spacing) {
		if ( this.hgap == spacing )
			return;
		
		this.hgap = spacing;
		rebuild();
	}
	
	@Override
	protected void position(Node parent) {
		float wid = (float) this.getWidth();
		float hei = (float) this.getHeight();
		
		if ( wid != lastSize.x || hei != lastSize.y ) {
			lastSize.set(wid, hei);
			rebuild();
			position(parent);
		}
		
		super.position(parent);
	}
	
	public void setAlignment(Pos pos) {
		super.setAlignment(pos);
		
		if ( this.internalBox == null )
			return;
		
		this.internalBox.setAlignment(pos);
		for (int i = 0; i < this.internalBox.getChildren().size(); i++) {
			this.internalBox.getChildren().get(i).setAlignment(pos);
		}
	}

	@Override
	public String getElementType() {
		return "flowpane";
	}
	
	protected void rebuild() {
		this.getChildren().clear();
		
		this.internalBox = new VBox();
		if ( this.orientation.equals(Orientation.VERTICAL ) ) {
			this.internalBox = new HBox();
			this.internalBox.setFillToParentHeight(true);
		} else {
			this.internalBox.setFillToParentWidth(true);
		}
		this.internalBox.setBackground(null);
		this.internalBox.setSpacing((this.internalBox instanceof HBox)?hgap:vgap);
		this.internalBox.setAlignment(this.getAlignment());
		this.getChildren().add(this.internalBox);
		
		DirectionalBox current = new HBox();
		if ( this.orientation.equals(Orientation.VERTICAL ) )
			current = new VBox();
		current.setSpacing((current instanceof HBox)?hgap:vgap);
		current.setAlignment(this.getAlignment());
		current.setBackground(null);
		this.internalBox.getChildren().add(current);
		
		float currentLen = 0;
		for (int i = 0; i < items.size(); i++) {
			Node item = items.get(i);
			
			float cLen = (float) item.getWidth();
			float maxLen = (float) this.getWidth();
			if ( this.orientation.equals(Orientation.VERTICAL ) ) {
				cLen = (float) item.getHeight();
				maxLen = (float) this.getHeight();
			}
			currentLen += cLen;
			
			if ( currentLen <= maxLen || current.getChildren().size() == 0) {
				current.getChildren().add(item);
				currentLen += current.getSpacing();
			} else {
				current = new HBox();
				if ( this.orientation.equals(Orientation.VERTICAL ) )
					current = new VBox();
				current.setSpacing((current instanceof HBox)?hgap:vgap);
				current.setAlignment(this.getAlignment());
				current.setBackground(null);
				this.internalBox.getChildren().add(current);
				currentLen = 0;
				
				// Rerun for this item
				i--;
			}
		}
	}

	public Orientation getOrientation() {
		return this.orientation;
	}
	
	public void setOrientation( Orientation orientation ) {
		this.orientation = orientation;
	}
	
	public ObservableList<Node> getItems() {
		return this.items;
	}

	@Override
	public boolean isResizeable() {
		return false;
	}
}
