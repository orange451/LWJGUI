package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;

import lwjgui.collections.ObservableList;
import lwjgui.event.ChangeEvent;
import lwjgui.event.KeyEvent;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Pos;
import lwjgui.scene.Node;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class TreeView<E> extends TreeBase<E> {
	private VBox internalBox;
	private int indentWidth = 18;
	
	protected ObservableList<TreeItem<E>> selectedItems;
	protected ObservableList<TreeItem<E>> visibleItems;
	private TreeItem<E> lastSelected;
	private boolean autoSelectChildren;
	
	public TreeView() {
		this.internalBox = new VBox();
		this.internalBox.setFillToParentWidth(true);
		this.children.add(internalBox);
		
		this.flag_clip = true;
		this.setPrefSize(1, 1);
		this.setAlignment(Pos.TOP_LEFT);
		
		this.selectedItems = new ObservableList<TreeItem<E>>();
		this.visibleItems = new ObservableList<TreeItem<E>>();
		
		this.setMousePressedEvent(new MouseEvent() {
			@Override
			public void onEvent(int button) {
				clearSelectedItems();
			}
		});
		
		this.setOnKeyPressed(new KeyEvent() {
			@Override
			public void onEvent(int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				if ( this.isConsumed() )
					return;
				
				if ( !isDecendentSelected() )
					return;
				
				int index = getItemIndex(getLastSelectedItem());
				index = Math.min(visibleItems.size()-1, Math.max(0, index));
				
				// Up and down arrows
				if ( key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_DOWN ) {
					int end = index;
					if ( key == GLFW.GLFW_KEY_UP )
						end--;
					if ( key == GLFW.GLFW_KEY_DOWN )
						end++;
					end = Math.min(visibleItems.size()-1, Math.max(0, end));

					if ( isShiftDown ) {
						selectItems(new IndexRange(end,index));
					} else {
						clearSelectedItems();
						selectItem(visibleItems.get(end));
					}
				}
				
				// Enter key
				if ( key == GLFW.GLFW_KEY_ENTER ) {
					visibleItems.get(index).setExpanded(!visibleItems.get(index).isExpanded());
				}
			}
		});
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);

		visibleItems.clear();
		internalBox.getChildren().clear();
		addChildren(0, this);
		
		if ( !this.isDecendentSelected() )
			lastSelected = null;
	}
	
	private void addChildren(int indent, TreeBase<E> root) {
		if ( root instanceof TreeItem && !((TreeItem<E>)root).isExpanded() )
			return;
		
		ObservableList<TreeItem<E>> itm = root.getItems();
		for (int i = 0; i < itm.size(); i++) {
			TreeItem<E> child = (TreeItem<E>)itm.get(i);
			TreeNode<E> node = root.getNode(child);
			node.root = this;
			node.setPrefWidth(internalBox.getWidth());
			node.setInset(indent*indentWidth);
			
			internalBox.getChildren().add(node);
			visibleItems.add(child);
			addChildren(indent+1, child);
		}
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
	}
	
	/**
	 * Clears the current item selection list.
	 */
	public void clearSelectedItems() {
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

abstract class TreeBase<E> extends StackPane {
	protected ObservableList<TreeItem<E>> items;
	protected ObservableList<TreeNode<E>> nodes;
	
	public TreeBase() {
		items = new ObservableList<TreeItem<E>>();
		nodes = new ObservableList<TreeNode<E>>();
		
		items.setAddCallback(new ChangeEvent<TreeItem<E>>() {
			@Override
			public void onEvent(TreeItem<E> changed) {
				nodes.add(new TreeNode<E>(changed));
			}
		});
		items.setRemoveCallback(new ChangeEvent<TreeItem<E>>() {
			@Override
			public void onEvent(TreeItem<E> changed) {
				nodes.remove(getNode(changed));
			}
		});
	}

	protected TreeNode<E> getNode(TreeItem<E> item) {
		for (int i = 0; i < nodes.size(); i++) {
			if ( nodes.get(i).item.equals(item) ) {
				return nodes.get(i);
			}
		}
		return null;
	}

	/**
	 * 
	 * @return Returns the direct children of this tree. Adding to this list will add them to the tree.
	 */
	public ObservableList<TreeItem<E>> getItems() {
		return items;
	}
}