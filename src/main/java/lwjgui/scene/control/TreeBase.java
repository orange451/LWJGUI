package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.event.ElementCallback;
import lwjgui.scene.layout.StackPane;

public abstract class TreeBase<E> extends StackPane {
	protected ObservableList<TreeItem<E>> items;
	protected ObservableList<TreeNode<E>> nodes;
	
	public TreeBase() {
		items = new ObservableList<TreeItem<E>>();
		nodes = new ObservableList<TreeNode<E>>();
		
		items.setAddCallback(new ElementCallback<TreeItem<E>>() {
			@Override
			public void onEvent(TreeItem<E> changed) {
				nodes.add(new TreeNode<E>(changed));
			}
		});
		items.setRemoveCallback(new ElementCallback<TreeItem<E>>() {
			@Override
			public void onEvent(TreeItem<E> changed) {
				nodes.remove(getNode(changed));
			}
		});
	}

	protected TreeNode<E> getNode(TreeItem<E> item) {
		for (int i = 0; i < nodes.size(); i++) {
			if ( i >= nodes.size() )
				continue;
			
			TreeNode<E> node = nodes.get(i);
			if ( node == null )
				continue;
			
			if ( item.equals(node.item) )
				return nodes.get(i);
		}
		return null;
	}
	
	public boolean isExpanded() {
		return true;
	}

	/**
	 * 
	 * @return Returns the direct children of this tree. Adding to this list will add them to the tree.
	 */
	public ObservableList<TreeItem<E>> getItems() {
		return items;
	}
}