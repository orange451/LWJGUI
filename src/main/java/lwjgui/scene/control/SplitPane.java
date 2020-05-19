package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joml.Vector2d;
import org.joml.Vector4d;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.glfw.input.MouseHandler;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Cursor;
import lwjgui.scene.Node;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class SplitPane extends Control {
	private Orientation orientation;
	private ArrayList<Divider> dividers = new ArrayList<Divider>();
	private ArrayList<DividerNode> divider_nodes = new ArrayList<DividerNode>();
	private HashMap<Divider,Integer> divider_cache = new HashMap<Divider,Integer>();
	private ObservableList<Node> items = new ObservableList<Node>();
	private StackPane divider_holder;
	private int dividerThickness = 6;

	protected static Map<Node,Boolean> divider_resize = new HashMap<>();

	private Divider grabbedDivider;
	private Divider hovered;

	public SplitPane() {
		this.setAlignment(Pos.TOP_LEFT);

		this.divider_holder = new StackPane();
		this.divider_holder.setFillToParentHeight(true);
		this.divider_holder.setFillToParentWidth(true);
		this.divider_holder.setBackgroundLegacy(null);
		this.children.add(divider_holder);

		this.items.setAddCallback(new ElementCallback<Node>() {
			@Override
			public void onEvent(Node changed) {
				if ( divider_resize.get(changed) == null ) {
					divider_resize.put(changed, true);
				}
				recalculateDividers();
			}
		});

		this.items.setRemoveCallback(new ElementCallback<Node>() {
			@Override
			public void onEvent(Node changed) {
				recalculateDividers();
			}
		});

		this.setOnMouseReleased(event -> {
			System.out.println("A");
		});

		this.setOrientation(Orientation.VERTICAL);
	}

	@Override
	public String getElementType() {
		return "splitpane";
	}

	private double lastLen = 0;
	@Override
	protected void position(Node parent) {
		grabDividers();

		super.position(parent);
		
		double curLen = getWidth();
		if ( !this.orientation.equals(Orientation.VERTICAL) ) {
			curLen = getHeight();
		}
		
		if ( curLen != lastLen ) {
			onSizeChange(lastLen);
			this.resize();
			this.updateChildrenLocalRecursive();
			lastLen = curLen;
		}
	}

	private void clickDividers() {
		if ( hovered == null ) {
			return;
		}

		MouseHandler mh = window.getMouseHandler();
		double mx = mh.getX();
		double my = mh.getY();

		grabbedDivider = hovered;
		mouseGrabLocation.set(mx, my);
	}

	private Divider getDividerUnderMouse() {
		MouseHandler mh = window.getMouseHandler();
		double mx = mh.getX();
		double my = mh.getY();
		
		Node hoveredNode = window.getContext().getHovered();
		if ( hoveredNode != null && hoveredNode != this && !hoveredNode.isDescendentOf(this) )
			return null;

		for (int i = 0; i < dividers.size(); i++) {
			Divider d = dividers.get(i);
			Vector4d bounds = getDividerBounds(d);
			if ( mx > bounds.x && mx < bounds.x+bounds.z && my > bounds.y && my < bounds.y+bounds.w) {
				return d;
			}
		}

		return null;
	}

	private boolean click = false;
	private boolean released = true;
	private Vector2d mouseGrabLocation = new Vector2d();
	private void grabDividers() {

		// Get mouse pressed
		MouseHandler mh = window.getMouseHandler();

		// Check if we're clicking
		if ( !click && mh.isButtonPressed(0) && released )
			click = true;
		else if ( click && mh.isButtonPressed(0)) {
			released = false;
			click = false;
		} else if ( !mh.isButtonPressed(0) )
			released = true;

		if ( click ) {
			clickDividers();
		}

		if ( grabbedDivider == null )
			return;

		// If mouse not pressed, not holding divider
		if ( !mh.isButtonPressed(0) ) {
			grabbedDivider = null;
			return;
		}

		// Get mouse coordinates
		double mx = mh.getX() - mouseGrabLocation.x;
		double my = mh.getY() - mouseGrabLocation.y;

		// If we're holding onto a divider
		double pChange = pixelSpaceToDividerSpace(mx);
		if ( this.orientation == Orientation.HORIZONTAL ) 
			pChange = pixelSpaceToDividerSpace(my);

		this.setDividerPosition(divider_cache.get(grabbedDivider), grabbedDivider.position+pChange);
		
		// Update new mouse location
		Vector4d bounds = this.getDividerBounds(grabbedDivider);
		if ( mh.getX() > bounds.x && mh.getX() < bounds.x + bounds.z && mh.getY() > bounds.y && mh.getY() < bounds.y + bounds.w )
			mouseGrabLocation.add(mx, my);

		for (int i = 0; i < 4; i++) {
			this.updateChildren();
			this.resize();
		}
	}

	protected void onSizeChange(double lastLength) {
		for (int i = 0; i < divider_nodes.size(); i++) {
			DividerNode d = divider_nodes.get(i);

			Divider leftDivider = null;
			Divider rightDivider = null;

			// Has a left divider
			if ( i > 0 ) {
				leftDivider = this.dividers.get(i-1);
			}
			// Has a right divider
			if ( i < divider_nodes.size()-1 ) {
				rightDivider = this.dividers.get(i);
			}

			// Check if the divider needs to be resized
			boolean resize = true;
			if ( d.getChildren().size() > 0 ) {
				resize = divider_resize.get(d.getChildren().get(0));
			}

			// If not (static divider), then make sure it stays the same length
			if ( !resize ) {
				resizeDiv( leftDivider, d, lastLength );
				resizeDiv( rightDivider, d, lastLength );
			}
		}
	}

	private void resizeDiv(Divider div, DividerNode node, double lastLength) {
		if ( div == null || lastLength == 0)
			return;

		double lastOff = this.pixelSpaceToDividerSpace(div.position*lastLength);
		int divIndex = getDividerIndex(div);
		if ( divIndex == dividers.size()-1) {
			lastOff = 1.0 - this.pixelSpaceToDividerSpace((1.0-div.position)*lastLength);
		}
		setDividerPosition(divIndex, lastOff);
	}

	private int getDividerIndex(Divider div) {
		for (int i = 0; i < dividers.size(); i++) {
			if ( dividers.get(i).equals(div) ) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected void resize() {		
		super.resize();

		float filledLen = 0;
		double maxLen = this.getWidth();
		if ( this.orientation.equals(Orientation.HORIZONTAL) ) {
			maxLen = this.getHeight();
		}

		for (int i = 0; i < divider_nodes.size(); i++) {
			DividerNode d = this.divider_nodes.get(i);
			double left = 0;
			double right = 1;
			double subt = 0;
			Divider leftDivider = null;
			Divider rightDivider = null;

			// Has a left divider
			if ( i > 0 ) {
				leftDivider = this.dividers.get(i-1);
				left = leftDivider.position;
				subt += dividerThickness/2f;
			}
			// Has a right divider
			if ( i < divider_nodes.size()-1 ) {
				rightDivider = this.dividers.get(i);
				right = rightDivider.position;
				subt += dividerThickness/2f;
			}

			// Calculate length of divider node
			double len = ((right-left)*maxLen) - subt;
			double t = Math.ceil(len-0.25); // Round up to eliminate rounding issues.

			if ( d.getChildren().size() > 0 )
				d.setAlignment(d.getChildren().get(0).getAlignment());

			if ( this.orientation.equals(Orientation.VERTICAL) ) {
				d.setFillToParentWidth(false);
				d.setFillToParentHeight(true);
				d.setPrefWidth(t);
				d.setMinWidth(t);
				d.setMaxWidth(t);
				d.setLocalPosition(divider_holder, filledLen, 0);
			} else {
				d.setFillToParentWidth(true);
				d.setFillToParentHeight(false);
				d.setPrefHeight(t);
				d.setMinHeight(t);
				d.setMaxHeight(t);
				d.setLocalPosition(divider_holder, 0, filledLen);
			}
			
			//this.updateChildrenLocalRecursive();

			filledLen += len + dividerThickness;
		}
	}

	public ObservableList<Node> getItems() {
		return items;
	}

	public void setOrientation( Orientation orientation ) {
		this.orientation = orientation;

		// Re add dividers into holder
		this.recalculateDividers();
	}

	@Override
	public boolean isResizeable() {
		return false;
	}

	private Vector4d getDividerBounds(Divider d) {
		double percent = d.getPosition();
		int dividerWidth = dividerThickness;
		int dividerHeight = (int) getHeight();
		int dividerX = (int) ((getX() + getWidth()*percent)-(dividerThickness/2d));
		int dividerY = (int) getY();
		if ( orientation.equals(Orientation.HORIZONTAL) ) {
			dividerWidth = (int) getWidth();
			dividerHeight = dividerThickness;
			dividerX = (int) getX();	
			dividerY = (int) ((getY() + getHeight()*percent)-(dividerThickness/2d));
		}

		return new Vector4d(dividerX, dividerY, dividerWidth, dividerHeight);
	}

	private void recalculateDividers() {
		this.divider_cache.clear();
		ArrayList<Divider> t = new ArrayList<Divider>();
		int amtDiv = this.items.size()-1;
		for (int i = 0; i < amtDiv; i++) {
			Divider d = new Divider();
			d.setPosition((i+1)/(double)(amtDiv+1));
			t.add(d);
			this.divider_cache.put(d, i);
		}
		dividers = t;

		synchronized(divider_nodes) {
			this.divider_holder.getChildren().clear();
			this.divider_nodes.clear();
			for (int i = 0; i < items.size(); i++) {
				DividerNode dn = new DividerNode(items.get(i));
				this.divider_nodes.add(dn);
				this.divider_holder.getChildren().add(dn);
			}
		}

		resize();
		/*ObservableList<Node> n = new ObservableList<Node>();
		ObservableList<Node> old = this.children;
		for (int i = 0; i < old.size(); i++) {
			n.add(old.get(i));
		}
		for (int i = 0; i < t.size(); i++) {
			n.add(new DividerNode(t.get(i)));
		}
		this.divider_cache = n;*/
	}

	private double pixelSpaceToDividerSpace(double mx) {
		double maxLen = getWidth();
		if ( this.orientation.equals(Orientation.HORIZONTAL) )
			maxLen = getHeight();

		return mx/maxLen;
	}

	@Deprecated
	private Divider lastHovered;
	
	@Override
	public void render(Context context) {
		if ( !isVisible() )
			return;
		
		long vg = context.getNVG();

		for (int i = 0; i < children.size(); i++) {
			// Clip to my bounds
			clip(context);

			// Draw child
			Node child = children.get(i);
			child.render(context);
		}

		clip(context);
		for (int i = 0; i < dividers.size(); i++) {
			Divider divider = dividers.get(i);
			Vector4d bounds = getDividerBounds(divider);

			// Main bar
			hovered = getDividerUnderMouse();
			Color col = Theme.current().getControlOutline();
			NanoVG.nnvgBeginPath(vg);
			NanoVG.nvgFillColor(vg, col.getNVG());
			NanoVG.nvgRect(vg, (int)bounds.x, (int)bounds.y, (int)bounds.z, (int)bounds.w);
			NanoVG.nvgFill(vg);

			Cursor desiredCursor = orientation.equals(Orientation.VERTICAL)?Cursor.HRESIZE:Cursor.VRESIZE;
			if ( hovered != null ) {
				getScene().setCursor(desiredCursor);
			} else if ( lastHovered != null ) {
				if ( getScene().getCursor().equals(desiredCursor) ) {
					getScene().setCursor(Cursor.NORMAL);
				}
			}
			lastHovered = hovered;

			// Inner Gradient
			NanoVG.nvgTranslate(vg, (int)bounds.x, (int)bounds.y);
			try (MemoryStack stack = stackPush()) {
				if ( this.orientation.equals(Orientation.VERTICAL) ) {
					NVGPaint bg = NanoVG.nvgLinearGradient(vg, 0, 0, (int)bounds.z, 0, Theme.current().getPane().getNVG(), Theme.current().getBackgroundAlt().getNVG(), NVGPaint.callocStack(stack));
					NanoVG.nvgBeginPath(vg);
					NanoVG.nvgRect(vg, 1, 0, (int)bounds.z-2,(int)bounds.w);
					NanoVG.nvgFillPaint(vg, bg);
					NanoVG.nvgFill(vg);
				} else {
					NVGPaint bg = NanoVG.nvgLinearGradient(vg, 0, 0, 0, (int)bounds.w, Theme.current().getPane().getNVG(), Theme.current().getBackgroundAlt().getNVG(), NVGPaint.callocStack(stack));
					NanoVG.nvgBeginPath(vg);
					NanoVG.nvgRect(vg, 0, 1, (int)bounds.z,(int)bounds.w-2);
					NanoVG.nvgFillPaint(vg, bg);
					NanoVG.nvgFill(vg);
				}
			}
			NanoVG.nvgTranslate(vg, (int)-bounds.x, (int)-bounds.y);
		}

		Color outlineColor = Theme.current().getControlOutline();
		NanoVG.nvgBeginPath(vg);
		NanoVG.nvgRect(vg, (int)this.getX(), (int)this.getY(), (int)getWidth(), (int)getHeight());
		NanoVG.nvgStrokeColor(vg, outlineColor.getNVG());
		NanoVG.nvgStrokeWidth(vg, 1f);
		NanoVG.nvgStroke(vg);
	}
	
	public double getDividerThickness() {
		return this.dividerThickness;
	}
	
	public void setDividerThickness(double thickness) {
		this.dividerThickness = (int)thickness;
	}

	public void setDividerPosition( int index, double position ) {
		Divider d = dividers.get(index);

		// Get left/right dividers
		Divider left = null;
		Divider right = null;
		if ( index > 0 ) 
			left = dividers.get(index-1);
		if ( index < dividers.size()-1 )
			right = dividers.get(index+1);

		// Get divider thickness in divider space
		double dthick = pixelSpaceToDividerSpace(dividerThickness);

		// Get min max bounds
		double minPos = dthick/2d;
		double maxPos = 1-dthick/2d;
		if ( left != null )
			minPos = left.position+dthick;
		if ( right != null )
			maxPos = right.position-dthick;

		// Clamp position
		position = Math.min( maxPos, Math.max(minPos, position) );

		// Set position
		d.position = position;
	}

	/**
	 * Marks if a divider should be resized with its parent.
	 * If true, the ratio of the divider position stays the same no matter what size.
	 * If false, the divider stays in the same absolute position when parent is resized.
	 * @param node
	 * @param bool
	 */
	public static void setResizableWithParent(Node node, boolean bool) {
		divider_resize.put(node, bool);
	}

	/**
	 * Represents a single divider in the SplitPane.
	 * @since JavaFX 2.0
	 */
	public static class Divider {
		private double position = 0.5;
		public final void setPosition(double value) {
			position = value;
		}

		public final double getPosition() {
			return position;
		}
	}

	static class DividerNode extends StackPane {

		public DividerNode(Node node) {
			this.setAlignment(Pos.TOP_LEFT);
			this.getChildren().add(node);
			this.setFillToParentWidth(true);
			this.setFillToParentHeight(true);
			this.flag_clip = true;
		}

		@Override
		public boolean isResizeable() {
			return false;
		}
		
		@Override
		public void render(Context context) {
			this.clip(context, 0);
			super.render(context);
		}
	}
}
