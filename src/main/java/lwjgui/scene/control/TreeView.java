package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.collections.ObservableList;
import lwjgui.event.CallbackEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.VBox;

public class TreeView<E> extends TreeBase<E> {
	private VBox internalBox;
	private int indentWidth = 18;
	
	protected ObservableList<TreeItem<E>> selectedItems;
	protected ObservableList<TreeItem<E>> visibleItems;
	private TreeItem<E> lastSelected;
	private boolean autoSelectChildren;

	protected EventHandler<CallbackEvent<TreeItem<E>>> onSelectItemEvent;
	protected EventHandler<CallbackEvent<TreeItem<E>>> onDeselectItemEvent;
	
	protected boolean needsRefresh = true;
	
	public TreeView() {
		this.internalBox = new VBox();
		this.children.add(internalBox);
		
		this.flag_clip = true;
		this.setPrefSize(1, 1);
		this.setAlignment(Pos.TOP_LEFT);
		
		this.selectedItems = new ObservableList<TreeItem<E>>();
		this.visibleItems = new ObservableList<TreeItem<E>>();
		
		this.setOnMousePressedInternal(event -> {
			if ( event.button != GLFW.GLFW_MOUSE_BUTTON_LEFT )
				return;
			clearSelectedItems();
		});
		
		this.setOnKeyPressedInternal(event -> {
			if ( event.isConsumed() )
				return;
			
			if ( !isDescendentSelected() )
				return;
			
			int index = getItemIndex(getLastSelectedItem());
			index = Math.min(visibleItems.size()-1, Math.max(0, index));
			int key = event.getKey();
			
			// Up and down arrows
			if ( key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_DOWN ) {
				int end = index;
				if ( key == GLFW.GLFW_KEY_UP )
					end--;
				if ( key == GLFW.GLFW_KEY_DOWN )
					end++;
				end = Math.min(visibleItems.size()-1, Math.max(0, end));

				if ( event.isShiftDown ) {
					selectItems(new IndexRange(end,index));
				} else {
					clearSelectedItems();
					selectItem(visibleItems.get(end));
				}
			}
			
			// Enter key
			if ( key == GLFW.GLFW_KEY_ENTER ) {
				visibleItems.get(index).setExpanded(!visibleItems.get(index).isExpanded());
				needsRefresh = true;
			}
		});
	}
	
	@Override
	protected void position(Node parent) {
		for (int i = 0; i < items.size(); i++) {
			items.get(i).setTree(this);
		}
		
		this.internalBox.setPrefWidth(0);
		for (int i = 0; i < visibleItems.size(); i++) {
			TreeItem<E> item = visibleItems.get(i);
			TreeNode<E> node = getNode(item);
			if ( node != null ) {
				node.setPrefWidth(0);
			}
		}

		// Refresh visible Item list
		if ( needsRefresh ) {
			needsRefresh = false;
			visibleItems.clear();
			internalBox.getChildren().clear();
			addChildren(0, this);
		}
		
		// Deselect if not selected
		if ( !this.isDescendentSelected() )
			lastSelected = null;
		
		// Super positioning
		super.position(parent);
		
		// Size internal box
		this.internalBox.setPrefWidth(TreeView.this.getWidth());
		
		// Update sizes
		for (int i = 0; i < internalBox.getChildren().size(); i++) {
			Node node = internalBox.getChildren().get(i);
			node.setPrefWidth(TreeView.this.getWidth());
		}
	}
	
	private void addChildren(int indent, TreeBase<E> root) {
		if ( root instanceof TreeItem && !((TreeItem<E>)root).isExpanded() )
			return;
		
		ObservableList<TreeNode<E>> itm = root.nodes;
		for (int i = 0; i < itm.size(); i++) {
			if ( i >= itm.size() )
				continue;
			
			TreeNode<E> nde = itm.get(i);
			if ( nde == null )
				continue;
			
			TreeItem<E> child = nde.getItem();
			if ( child == null )
				continue;
			
			TreeNode<E> node = root.getNode(child);
			if ( node == null )
				continue;
			
			node.root = this;
			node.setPrefWidth(TreeView.this.getWidth());
			node.setInset(indent*indentWidth);
			
			internalBox.getChildren().add(node);
			visibleItems.add(child);
			addChildren(indent+1, child);
		}
	}
	
	public void setOnSelectItem(EventHandler<CallbackEvent<TreeItem<E>>> event) {
		this.onSelectItemEvent = event;
	}
	
	public void setOnDeselectItem(EventHandler<CallbackEvent<TreeItem<E>>> event) {
		this.onDeselectItemEvent = event;
	}
	
	protected int getItemIndex(TreeItem<E> item) {
		if ( item == null )
			return -1;
		
		for (int i = 0; i < visibleItems.size(); i++) {
			if ( visibleItems.get(i).equals(item) )
				return i;
		}
		
		return -1;
	}

	/**
	 * Return a list of all the currently selected items in this tree view.
	 * @return
	 */
	public ObservableList<TreeItem<E>> getSelectedItems() {
		return this.selectedItems;
	}
	
	/**
	 * Return the last selected item in this tree view.
	 * @return
	 */
	public TreeItem<E> getLastSelectedItem() {
		return this.lastSelected;
	}
	
	/**
	 * Select a range of items. This is based off of what is currently visible in the view.<br>
	 * For example:<br>
	 * - Item 1<br>
	 * - Item 2<br>
	 * - - Item 6<br>
	 * - Item 3<br>
	 * selectItems(new IndexRange(1,2)) will select: Item 2 and Item 6.
	 * @param range
	 */
	public void selectItems(IndexRange range) {
		range.normalize();
		for (int i = range.getStart(); i <= range.getEnd(); i++) {
			TreeItem<E> item = visibleItems.get(i);
			this.selectItem(item);
		}
	}

	/**
	 * Select an individual item from the tree view.<br>
	 * If autoSelectChildren is set to true, it will recursively select child items
	 * ONLY if they are not visible.
	 * @param item
	 */
	public void selectItem(TreeItem<E> item) {
		if ( this.isItemSelected(item) )
			return;
		
		if ( autoSelectChildren && (!item.isExpanded() || !visibleItems.contains(item)) ) {
			ObservableList<TreeItem<E>> itm = item.getItems();
			for (int i = 0; i < itm.size(); i++) {
				selectItem(itm.get(i));
			}
		}
		
		lastSelected = item;
		this.selectedItems.add(item);
		
		if ( onSelectItemEvent != null ) {
			EventHelper.fireEvent(this.onSelectItemEvent, new CallbackEvent<TreeItem<E>>(item));
		}
	}
	
	/**
	 * Clears the current item selection list.
	 */
	public void clearSelectedItems() {
		for (int i = 0; i < selectedItems.size(); i++) {
			if ( onDeselectItemEvent != null ) {
				EventHelper.fireEvent(this.onDeselectItemEvent, new CallbackEvent<TreeItem<E>>(selectedItems.get(i)));
			}
		}
		this.selectedItems.clear();
	}
	
	/**
	 * Deselects a specific item from the tree view.
	 * @param item
	 */
	public void deselectItem(TreeItem<E> item) {
		if ( autoSelectChildren && (!item.isExpanded() || !visibleItems.contains(item)) ) {
			ObservableList<TreeItem<E>> itm = item.getItems();
			for (int i = 0; i < itm.size(); i++) {
				deselectItem(itm.get(i));
			}
		}
		
		this.selectedItems.remove(item);
		
		if ( onDeselectItemEvent != null ) {
			EventHelper.fireEvent(this.onDeselectItemEvent, new CallbackEvent<TreeItem<E>>(item));
		}
	}
	
	/**
	 * 
	 * @param item
	 * @return Returns whether or not this item is currently selected.
	 */
	public boolean isItemSelected(TreeItem<E> item) {
		return this.selectedItems.contains(item);
	}
	
	/**
	 * Toggles the autoSelectChildren variable.<br>
	 * See {@link #selectItems(IndexRange)}
	 * @param autoSelectChildren
	 */
	public void setAutoSelectChildren(boolean autoSelectChildren) {
		this.autoSelectChildren = autoSelectChildren;
	}
}