package lwjgui.scene.layout;

import org.joml.Vector2f;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Orientation;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.Node;

public class GridView extends FillableRegion {
	private Orientation orientation = Orientation.HORIZONTAL;
	private ObservableList<Node> items = new ObservableList<Node>();
	
	private Vector2f lastSize = new Vector2f();

	private DirectionalBox internalBox;
	private float spacing = 4;
	
	public GridView() {
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
	
	public float getSpacing() {
		return this.spacing;
	}
	
	public void setSpacing(float spacing) {
		this.spacing = spacing;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		float wid = (float) this.getWidth();
		float hei = (float) this.getHeight();
		
		if ( wid != lastSize.x || hei != lastSize.y ) {
			lastSize.set(wid, hei);
			rebuild();
			position(parent);
		}
	}
	
	protected void rebuild() {
		this.getChildren().clear();
		
		this.internalBox = new VBox();
		if ( this.orientation.equals(Orientation.VERTICAL ) )
			this.internalBox = new HBox();
		this.internalBox.setSpacing(spacing);
		this.internalBox.setFillToParentWidth(true);
		this.internalBox.setFillToParentHeight(true);
		this.getChildren().add(this.internalBox);
		
		DirectionalBox current = new HBox();
		if ( this.orientation.equals(Orientation.VERTICAL ) )
			current = new VBox();
		current.setSpacing(spacing);
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
			currentLen += cLen + spacing;
			
			if ( currentLen <= maxLen || current.getChildren().size() == 0) {
				current.getChildren().add(item);
			} else {
				currentLen = 0;
				current = new HBox();
				if ( this.orientation.equals(Orientation.VERTICAL ) )
					current = new VBox();
				current.setSpacing(spacing);
				this.internalBox.getChildren().add(current);
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
