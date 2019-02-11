package lwjgui.scene.layout;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.geometry.HPos;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Region;

public class GridPane extends Pane {
	private VBox internalVBox;
	
	private Node[][] elements;
	private NodePair[][] elementsInternal;
	private ColumnConstraint[] constraints;
	private int maxX;
	private int maxY;
	
	private int hgap;
	private int vgap;
	
	private boolean modified;
	
	private static int MAX_SIZE = 1024;
	
	public GridPane() {
		this.elements = new Node[MAX_SIZE][MAX_SIZE];
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
				Node selected = LWJGUI.getCurrentContext().getSelected();
				int col = getColumn(selected);
				int row = getRow(selected);
				
				Node e = null;
				int tries = 0;
				while ( e == null && tries < 32 ) {
					tries++;
					if ( event.isShiftDown ) {
						row--;
					} else {
						row++;
					}
					row = Math.min(maxY-1, Math.max(row, 0));
					e = elements[col][row];
				}
				
				LWJGUI.getCurrentContext().setSelected(e);
			}
		});
	}
	
	public void setHgap(int gap) {
		this.hgap = gap;
		update();
	}
	
	public void setVgap(int gap) {
		this.vgap = gap;
		update();
	}
	
	public int getHgap() {
		return this.hgap;
	}
	
	public int getVgap() {
		return this.vgap;
	}
	
	public void add(Node element, int x, int y) {
		elements[x][y] = element;
		modified = true;
		
		LWJGUI.runLater(()-> {
			//modified = true;
		});
	}
	
	public void clear() {
		maxX = getMaxX();
		maxY = getMaxY();
		for (int i = 0; i < maxX; i++) {
			for (int j = 0; j < maxY; j++) {
				elements[i][j] = null;
			}
		}
		update();
	}
	
	private void update() {
		modified = false;
		
		maxX = getMaxX();
		maxY = getMaxY();
		
		internalVBox.getChildren().clear();
		this.elementsInternal = new NodePair[maxX][maxY];
		
		this.internalVBox.setSpacing(vgap);
		this.internalVBox.setBackground(null);
		
		for (int i = 0; i < maxY; i++) {
			HBox row = new HBox();
			row.setSpacing(hgap);
			row.setBackground(null);
			row.setAlignment(Pos.TOP_LEFT);
			
			for (int j = 0; j < maxX; j++) {
				Node element = elements[j][i];
				if ( element == null ) {
					element = new NodeFiller();
				}
				Node sizer = new NodeFiller();
				elementsInternal[j][i] = new NodePair(element, sizer);
				
				HBox cell = new HBox();
				cell.setBackground(null);
				row.getChildren().add(cell);
				
				if ( element.getAlignment() != null && element.getAlignment().getHpos().equals(HPos.RIGHT) ) {
					cell.getChildren().add(sizer);
					cell.getChildren().add(element);
				} else {
					cell.getChildren().add(element);
					cell.getChildren().add(sizer);
				}
			}
			internalVBox.getChildren().add(row);
		}
	}
	
	@Override
	protected void position(Node parent) {
		if ( modified )
			update();
		
		super.position(parent);
		
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
			int totalWidthWithoutMe = 0;
			for (int j = 0; j < maxX; j++) {
				if ( j == i )
					continue;
				totalWidthWithoutMe += elementsInternal[j][0].nodeReal.getWidth();
				totalWidthWithoutMe += elementsInternal[j][0].nodeFiller.getWidth();
			}
			
			// If this column is marked to grow, use the above value to calculate width
			if ( constraints[i].getHgrow().equals(Priority.ALWAYS) ) {
				desiredColumnWidth = this.getWidth()-totalWidthWithoutMe;
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
						e.nodeReal.setPrefWidth(columnWidth);
						e.nodeFiller.setPrefWidth(0);
					} else {
						int mWid = (int) e.nodeReal.getWidth();
						if ( e.nodeReal instanceof Region )
							mWid += ((Region)e.nodeReal).getPadding().getWidth();
						e.nodeFiller.setPrefWidth(Math.max(0, columnWidth-mWid));
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
					int mHei = (int) e.nodeReal.getHeight();
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
	}
	
	class NodeFiller extends Node {
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
			this.clip(context);
			//LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight(), Color.RED);
		}
	}
}
