package lwjgui.style;

import lwjgui.scene.Node;

/**
 * This class maps a property to a style operation. It's implemented when giving java the ability to interface with CSS.
 * @author Andrew
 *
 */
public abstract class StyleOperation {
	private String name;
	
	public StyleOperation(String key) {
		this.name = key;
		StyleOperationDefinitions.operations.put(key, this);
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public abstract void process(Node node, StyleVarArgs value);
}

/**
 * This class maps a style operation and user-supplied CSS arguments. 
 * @author Andrew
 *
 */
class StyleOperationValue {
	private StyleOperation operation;
	private StyleVarArgs value;
	
	public StyleOperationValue(StyleOperation operation, StyleVarArgs value) {
		this.value = value;
		this.operation = operation;
	}
	
	public String getName() {
		return this.operation.getName();
	}

	public void process(Node node) {
		if ( value.size() <= 0 )
			return;
		operation.process(node, value);
	}
	
	@Override
	public String toString() {
		return operation + " " + value;
	}
}
