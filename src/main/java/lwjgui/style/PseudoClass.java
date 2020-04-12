package lwjgui.style;

import lwjgui.scene.Node;
import lwjgui.scene.control.Control;

/**
 * Pseudo Class enum list. Used to check if a pseudo class is active or not.
 * @author Andrew
 *
 */
public enum PseudoClass {
	/**
	 * Default pseudo class. Does not need to be explicitly mentioned.
	 */
	DEFAULT("default", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return true;
		}
	}),
	
	/**
	 * Hover pseudo class. Active when the node is hovered.
	 */
	HOVER("hover", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isHover();
		}
	}),
	
	/**
	 * Focus pseudo class. Active when node is selected or clicked.
	 */
	FOCUS("focus", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isSelected() || node.isClicked();
		}
	}),
	
	/**
	 * Select pseudo class. Active when node is just selected.
	 */
	SELECT("select", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isSelected();
		}
	}),
	
	/**
	 * Active pseudo class. Active when node is just clicked.
	 */
	ACTIVE("active", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isClicked();
		}
	}),
	
	/**
	 * Disabled pseudo class. Active when node is disabled. Only nodes descendant of {@link Control} can be disabled.
	 */
	DISABLED("disabled", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			if ( !(node instanceof Control) )
				return false;
			
			return ((Control)node).isDisabled();
		}
	});
	
	private DataCallback<Boolean, Node> callback;
	private String className;
	
	private PseudoClass(String name, DataCallback<Boolean, Node> callback) {
		this.callback = callback;
		this.className = name;
	}
	
	public String getName() {
		return this.className;
	}

	public Boolean isActive(Node node) {
		return this.callback.callback(node);
	}
	
	public static PseudoClass match(String name) {
		PseudoClass[] classes = PseudoClass.values();
		for (int i = 0; i < classes.length; i++) {
			if ( classes[i].className.equals(name) )
				return classes[i];
		}
		
		return null;
	}
}