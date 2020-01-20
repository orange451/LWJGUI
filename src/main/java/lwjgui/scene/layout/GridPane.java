package lwjgui.scene.layout;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.Node;
import lwjgui.scene.Region;

public class GridPane extends Pane implements Gappable {
	private VBox internalVBox;

	private Node[][] elements;
	private Node[][] elementsEmpty;
	private NodePair[][] elementsInternal;
	private ColumnConstraint[] constraints;
	private int maxX;
	private int maxY;
	
	private float hgap;
	private float vgap;
	
	private boolean modified;
	private boolean empty = true;
	
	private static int MAX_SIZE = 1024;
	
	public GridPane() {
		this.elements = new Node[MAX_SIZE][MAX_SIZE];
		this.elementsEmpty = new Node[MAX_SIZE][MAX_SIZE];
		this.constraints = new ColumnConstraint[MAX_SIZE];
		
		// Fill default constraints
		for (int i = 0; i < MAX_SIZE; i++) {
			constraints[i] = new ColumnConstraint();
		}
		
		this.internalVBox = new VBox();
		this.internalVBox.setAlignment(Pos.TOP_LEFT);
		this.children.add(this.internalVBox);
		
		this.setPrefSize(1, 1);
		
		update();
		
		this.setOnKeyPressed(event -> {
			if( !GridPane.this.isDescendentSelected() )
				return;
			
			if ( event.key == GLFW.GLFW_KEY_TAB ) {
				Node selected = window.getContext().getSelected();
				int col = getColumn(selected);
				int row = getRow(selected);
				
				// Search vertically for a node
				Vector2i direction = new Vector2i( 0, event.isShiftDown?-1:1 );
				Node e = searchForNextNode( new Vector2i( col, row ), direction);
				
				// Search horizontally for a node
				if ( e == null ) {
					direction = new Vector2i( event.isShiftDown?-1:1, 0 );
					e = searchForNextNode( new Vector2i( col, row ), direction);
				}
				
				// If we found a node, select it
				if ( e != null ) {
					window.getContext().setSelected(e);
				}
			}
		});
	}

	@Override
	public String getElementType() {
		return "gridpane";
	}
	
	private Node searchForNextNode( Vector2i position, Vector2i direction ) {
		Node ret = null;
		int tries = 0;
		
		int x = position.x;
		int y = position.y;
		
		while ( ret == null && tries < 32 ) {
			x += direction.x;
			y += direction.y;
			if ( x >= 0 && x < maxX && y >= 0 && y < maxY ) {
				ret = elements[x][y];
			}
			tries++;
		}
		
		return ret;
	}
	
	/**
	 * Sets the horizontal space between Nodes added to this pane.
	 * @param gap
	 */
	@Override
	public void setHgap(float gap) {
		if ( gap == this.hgap )
			return;
		
		this.hgap = gap;
		update();
	}
	
	/**
	 * Sets the vertical space between Nodes added to this pane.
	 * @param gap
	 */
	@Override
	public void setVgap(float gap) {
		if ( gap == this.vgap )
			return;
		
		this.vgap = gap;
		update();
	}

	@Override
	public float getHgap() {
		return this.hgap;
	}

	@Override
	public float getVgap() {
		return this.vgap;
	}
	
	/**
	 * Adds the given element at the x/y position in the GridPane. E.G. Add an element at 1, 1 (one grid space right, one grid space down). 
	 * 
	 * The distance between the Nodes is determined by setHgap/setVgap().
	 * 
	 * @param element
	 * @param x
	 * @param y
	 */
	public void add(Node element, int x, int y) {
		elements[x][y] = element;
		modified = true;
		empty = false;
	}
	
	public void clear() {		
		// Should be the fastest way to empty the elements array
		for (int i = 0; i < elementsEmpty.length; i++) {
			System.arraycopy(elementsEmpty[i], 0, elements[i], 0, elementsEmpty[i].length);
		}
		
		empty = true;
		
		update();
	}
	
	private void update() {
		modified = false;
		
		if ( !empty ) {
			maxX = getMaxX();
			maxY = getMaxY();
		} else {
			maxX = 0;
			maxY = 0;
		}
		
		internalVBox.getChildren().clear();
		this.elementsInternal = new NodePair[maxX][maxY];
		
		this.internalVBox.setSpacing(vgap);
		this.internalVBox.setBackgroundLegacy(null);
		
		for (int i = 0; i < maxY; i++) {
			HBox row = new HBox();
			row.setSpacing(hgap);
			row.setBackgroundLegacy(null);
			row.setAlignment(Pos.TOP_LEFT);
			
			for (int j = 0; j < maxX; j++) {
				Node element = elements[j][i];
				if ( element == null )
					element = new NodeFiller();
				
				Node sizer = new NodeFiller();
				elementsInternal[j][i] = new NodePair(element, sizer);
				
				HBox cell = new HBox();
				cell.setBackgroundLegacy(null);
				row.getChildren().add(cell);
				
				cell.getChildren().add(element);
				cell.getChildren().add(sizer);
				
				//cell.setBackground(Color.WHITE.red(i/(float)(maxY-1)).green(j/(float)(maxX-1)));
			}
			internalVBox.getChildren().add(row);
		}
	}
	
	@Override
	protected void position(Node parent) {
		if ( modified )
			update();
		
		super.position(parent);
	}
	
	@Override
	protected void resize() {
		super.resize();

		adjustSize();
		internalVBox.setFillToParentWidth(this.isFillToParentWidth());
		internalVBox.setFillToParentHeight(this.isFillToParentHeight());
		//this.setPrefSize(internalVBox.getWidth(), internalVBox.getHeight());
	}
	
	public void setColumnConstraint(int column, ColumnConstraint constraint) {
		constraints[column] = constraint;
	}
	
	private void adjustSize() {
		// Update column widths
		for (int i = 0; i < maxX; i++) {
			double columnWidth = getColumnWidth(i);
			double desiredColumnWidth = constraints[i].getPrefWidth();
			
			// Get total width of columns except for the current one
			double totalWidthWithoutMe = 0;
			for (int j = 0; j < maxX; j++) {
				if ( j == i )
					continue;
				totalWidthWithoutMe += elementsInternal[j][0].getWidth();
				totalWidthWithoutMe += hgap;
			}
			
			// If this column is marked to grow, use the above value to calculate width
			if ( constraints[i].getHgrow().equals(Priority.ALWAYS) ) {
				desiredColumnWidth = (GridPane.this.getWidth()-Math.ceil(totalWidthWithoutMe));
			}
			
			if ( columnWidth < desiredColumnWidth )
				columnWidth = desiredColumnWidth;
			if ( columnWidth < constraints[i].getMinWidth() )
				columnWidth = constraints[i].getMinWidth();
			if ( columnWidth > constraints[i].getMaxWidth() )
				columnWidth = constraints[i].getMaxWidth();
			
			for (int j = 0; j < maxY; j++) {
				NodePair e = elementsInternal[i][j];
				
				if ( e != null ) {
					if( constraints[i].isFillWidth() ) {
						if ( e.nodeReal instanceof FillableRegion && ((FillableRegion)e.nodeReal).isFillToParentWidth() ) {
							FillableRegion t = (FillableRegion)e.nodeReal;
							t.forceWidth(columnWidth);
							e.nodeFiller.setPrefWidth(0);
							t.getParent().forceWidth(t.getWidth());
						} else {
							e.nodeReal.setPrefWidth(columnWidth);
							e.nodeFiller.setPrefWidth(0);
						}
					} else {
						if ( e.nodeReal instanceof FillableRegion && ((FillableRegion)e.nodeReal).isFillToParentWidth() ) {
							FillableRegion t = (FillableRegion)e.nodeReal;
							t.forceWidth(columnWidth);
							t.setPrefWidth(columnWidth);
							e.nodeFiller.setPrefWidth(0);
							t.getParent().forceWidth(t.getWidth());
						} else {
							int mWid = (int) e.nodeReal.getWidth();
							e.nodeFiller.setPrefWidth(Math.max(0, columnWidth-mWid));
						}
					}
				}
			}
		}
		
		// Update row heights
		for (int i = 0; i < maxY; i++) {
			double rowHeight = getRowHeight(i);
			for (int j = 0; j < maxX; j++) {
				NodePair e = elementsInternal[j][i];
				
				if ( e != null ) {
					int mHei = 0;//(int) e.nodeReal.getHeight();
					if ( e.nodeReal instanceof Region )
						mHei += ((Region)e.nodeReal).getPadding().getHeight();
					
					e.nodeFiller.setPrefHeight(Math.max(0, rowHeight-mHei));
				}
			}
		}
	}
	
	private double getRowHeight(int row) {
		double ret = 0;
		for (int i = 0; i < maxX; i++) {
			Node element = elements[i][row];
			if ( element != null ) {
				if ( element.getHeight() > ret ) {
					ret = element.getHeight();
				}
			}
		}
		return ret;
	}
	
	private int getColumn(Node element) {
		if ( element == null )
			return 0;
		
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				Node e = elements[i][j];
				if ( e != null && (e.equals(element)||element.isDescendentOf(e)) )
					return i;
			}
		}
		return 0;
	}
	
	private int getRow(Node element) {
		if ( element == null )
			return 0;
		
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				Node e = elements[i][j];
				if ( e != null && (e.equals(element)||element.isDescendentOf(e)) )
					return j;
			}
		}
		return 0;
	}

	private double getColumnWidth(int column) {
		double ret = 0;
		for (int i = 0; i < maxY; i++) {
			Node element = elements[column][i];
			if ( element != null ) {
				if ( element.getWidth() > ret ) {
					ret = element.getWidth();
				}
			}
		}
		return ret;
	}

	private int getMaxX() {
		int ret = -1;
		for (int j = 0; j < elements[0].length; j++) {
			for (int i = 0; i < elements.length; i++) {
				if ( elements[i][j] != null && i > ret ) {
					ret = i;
				}
			}
		}
		return ret+1;
	}

	private int getMaxY() {
		int ret = -1;
		for (int j = 0; j < elements.length; j++) {
			for (int i = 0; i < elements[0].length; i++) {
				if ( elements[j][i] != null && i > ret ) {
					ret = i;
				}
			}
		}
		return ret+1;
	}

	@Override
	public boolean isResizeable() {
		// TODO Auto-generated method stub
		return false;
	}
	
	class NodePair {
		Node nodeReal;
		Node nodeFiller;
		
		NodePair(Node a, Node b) {
			nodeReal = a;
			nodeFiller = b;
		}

		public double getWidth() {
			return nodeReal.getWidth() + nodeFiller.getWidth();
		}
	}
	
	class NodeFiller extends StackPane {
		NodeFiller() {
			setMouseTransparent(true);
			setPrefSize(0,0);
		}

		@Override
		public boolean isResizeable() {
			return false;
		}

		@Override
		public void render(Context context) {
			if ( !isVisible() )
				return;
			
			this.clip(context);
		}
	}

	public Node get(int x, int y) {
		return elements[x][y];
	}
}
