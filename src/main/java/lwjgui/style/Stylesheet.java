package lwjgui.style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lwjgui.scene.Node;

public class Stylesheet {
	private String source;
	private HashMap<String, StyleSelector> idToStyleSelector = new HashMap<>();
	private HashMap<StyleSelector, StyleData> styleData = new HashMap<>();

	public Stylesheet(String css) {
		this.source = css;
	}
	
	/**
	 * Apply generic styling to a node via its classes/tag
	 * @param node
	 */
	public void applyStyling(Node node) {
		// Start list of operations
		Map<String, StyleOperationValue> declarations = new HashMap<>();
		
		// JUST FOR NORMAL SELECTOR STYLING FIRST
		{
			// Apply styling for the DOM TAG
			computeStyling(node, StyleSelectorType.TAG, node.getElementType(), declarations, false);
			
			// Apply styling for the class
			ArrayList<String> classList = node.getClassList();
			for (int i = 0; i < classList.size(); i++) {
				String claz = classList.get(i);
				computeStyling(node, StyleSelectorType.CLASS, claz, declarations, false);
			}
		}
		
		// NEXT JUST FOR PSEUDO CLASSES
		{
			// Apply styling for the DOM TAG
			computeStyling(node, StyleSelectorType.TAG, node.getElementType(), declarations, true);
			
			// Apply styling for the class
			ArrayList<String> classList = node.getClassList();
			for (int i = 0; i < classList.size(); i++) {
				String claz = classList.get(i);
				computeStyling(node, StyleSelectorType.CLASS, claz, declarations, true);
			}
		}
		
		applyStyling(node, declarations);
	}

	/**
	 * Apply styling to a node usined the specified element tag
	 * @param node
	 * @param forceElementType
	 */
	public void applyStyling(Node node, String forceElementType) {
		Map<String, StyleOperationValue> declarations = new HashMap<>();
		computeStyling(node, StyleSelectorType.TAG, forceElementType, declarations, false);
		computeStyling(node, StyleSelectorType.TAG, forceElementType, declarations, true);
		applyStyling( node, declarations );
	}
	
	private void applyStyling(Node node, Map<String, StyleOperationValue> declarations) {
		// Iterate over operations and apply
		Iterator<Entry<String, StyleOperationValue>> iterator = declarations.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<String, StyleOperationValue> val = iterator.next();
			val.getValue().process(node);
		}
	}

	private boolean computeStyling(Node node, StyleSelectorType type, String id, Map<String, StyleOperationValue> combinedDeclarations, boolean justPseudoClasses) {
		StyleSelector selector = idToStyleSelector.get(id);
		if ( selector == null )
			return false;
		
		if ( selector.getType() != type )
			return false;
		
		StyleData data = styleData.get(selector);
		if ( data == null )
			return false;
		
		// Apply the styling!
		if ( justPseudoClasses ) {
			List<String> pseudoClasses = data.getPseudoClassOrder();
			for (int i = 0; i < pseudoClasses.size(); i++) {
				String pseudoClass = pseudoClasses.get(i);
				if ( pseudoClass.equals("normal") )
					continue;
				
				// Add this pseudoClasses declarations to the combined list
				if ( isPseudoClassActive(node, pseudoClass) )
					computeStyling(data, pseudoClass, combinedDeclarations);
			}
		} else {
			if ( isPseudoClassActive(node, PseudoClass.NORMAL.getName()) ) {
				computeStyling(data, PseudoClass.NORMAL.getName(), combinedDeclarations);
			}
		}
		
		return true;
	}
	
	/**
	 * Returns whether a pseudoClass is active.
	 * @param node
	 * @param pseudoClass
	 * @return
	 */
	private boolean isPseudoClassActive(Node node, String pseudoClass) {
		PseudoClass enumClass = PseudoClass.match(pseudoClass);
		if ( enumClass == null )
			return false;
		
		return enumClass.isActive(node);
	}

	/**
	 * Adds a style data to the combined declarations. (Can combine multiple pseudo classes, but only keep the most recent entries).
	 * @param data
	 * @param methodType
	 * @param combinedDeclarations
	 */
	private void computeStyling(StyleData data, String methodType, Map<String, StyleOperationValue> combinedDeclarations) {
		List<StyleOperationValue> declarations = data.getDeclarationData(methodType);
		if ( declarations.size() <= 0 )
			return;
		
		for (int i = 0; i < declarations.size(); i++) {
			StyleOperationValue op = declarations.get(i);
			combinedDeclarations.put(op.getName(), op);
		}
	}

	/**
	 * Compule Stylesheet
	 */
	public boolean compile() throws StylesheetCompileError {
		String newSource = source.replaceAll("[?<=\\/\\*](.*)[?=\\*\\/]", "");
		try {
			StringBuilder currentSelector = new StringBuilder();
			for (int i = 0; i < newSource.length(); i++) {
				char c = newSource.charAt(i);
	
				if (c == '{') {
					List<StyleSelector> selectors = parseSelectors(currentSelector.toString());
					currentSelector.setLength(0);
					if (selectors == null)
						continue;
	
					StringBuilder content = new StringBuilder();
					for (int j = i; j < newSource.length(); j++) {
						char cc = newSource.charAt(j);
	
						if (cc == '}') {
							// Parse content
							parseContent(selectors, content.toString());
							i = j;
							break;
						} else {
							content.append(cc);
						}
					}
				} else {
					// Read selector string
					currentSelector.append(c);
				}
			}
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			throw new StylesheetCompileError();
		}
	}

	/**
	 * Parse all content for a selector
	 * @param selectors
	 * @param content
	 */
	private void parseContent(List<StyleSelector> selectors, String content) {
		System.out.println("Found selectors (" + selectors.size() + "): " + Arrays.toString(selectors.toArray()));

		HashMap<Object, StyleVarArgs> data = new HashMap<>();

		String currentKey = null;
		StringBuilder t = new StringBuilder();
		for (int i = 1; i < content.length(); i++) {
			char c = content.charAt(i);

			// Store key
			if (c == ':') {
				currentKey = t.toString().trim();
				t.setLength(0);
				continue;
			}

			// End key
			if (c == ';') {
				String currentVal = t.toString().trim();
				StyleVarArgs val = parseArgs(currentVal);
				if ( val != null ) {
					if ( data.containsKey(currentKey) ) {
						data.get(currentKey).add(val);
					} else {
						data.put(currentKey, val);
					}
				}
				currentKey = null;
				t.setLength(0);
				continue;
			}

			// Continue reading
			t.append(c);
		}
		
		// In case there was an unfinished key...
		if ( t.length() > 0 && currentKey != null ) {
			String currentVal = t.toString().trim();
			StyleVarArgs val = parseArgs(currentVal);
			if ( val != null ) {
				if ( data.containsKey(currentKey) ) {
					data.get(currentKey).add(val);
				} else {
					data.put(currentKey, val);
				}
			}
			t.setLength(0);
		}
		
		for (int i = 0; i < selectors.size(); i++) {
			StyleSelector selector = selectors.get(i);
			StyleSelector key = idToStyleSelector.get(selector.selector);
			if ( key == null ) {
				idToStyleSelector.put(selector.selector, selector);
				key = selector;
			}
			
			StyleData sData = styleData.get(key);
			if ( sData == null ) {
				sData = new StyleData();
				styleData.put(selector, sData);
			}

			final StyleData sDataFinal = sData;

			data.entrySet().forEach(entry -> {
				System.out.println(selector.selector + " :: '" + entry.getKey() + "' = '" + entry.getValue() + "'");
				StyleOperation op = StyleOperations.match(entry.getKey().toString());
				
				if ( op != null ) {
					//StyleOperationValue opValue = new StyleOperationValue(op, entry.getValue());
					//operations.add(opValue);
					sDataFinal.addDeclarationData(selector.getModifier(), new StyleOperationValue(op, entry.getValue())); 
				}
			});
		}
	}
	
	/**
	 * Take a css value and parse it into a list of args
	 * @param content
	 * @return
	 */
	private StyleVarArgs parseArgs(String content) {
		content = content.replace(", ", ",");
		
		StyleVarArgs arguments = new StyleVarArgs();
		
		ArrayList<Object> temp = new ArrayList<Object>();
		String current = "";
		boolean inFunction = false;
		StyleFunction sFunc = null;
		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			
			if ( (c == ' ' || i+1 == content.length()) && !inFunction ) {
				if ( i+1 == content.length() && !inFunction )
					current += c;
				
				// Get the value from the param
				String t = current.trim();
				current = "";
				Object o = parseVal(t);
				
				// Add current params as an argument, and reset.
				if ( o != null )
					temp.add(o);
				
				// If this is the last character, Add current params as an argument, and reset.
				if ( i+1 == content.length() && !inFunction ) {
					StyleParams params = new StyleParams(temp.toArray(new Object[temp.size()]));
					if ( params.size() > 0 ) {
						arguments.add(params);
						temp.clear();
					}
				}
			} else {
				if ( c == '(' ) {
					inFunction = true;
					sFunc = new StyleFunction(current.trim());
					current = "";
					continue;
				} else if ( c == ')' ) {
					inFunction = false;
					StyleVarArgs argFunc = parseArgs(current.trim().replace(" ", "").replace(",", " "));
					if ( argFunc.size() > 0 ) {
						sFunc.args = argFunc;
						temp.add(sFunc);
						sFunc = null;
					}
					current = "";
					continue;
				} else if ( c == ',' && !inFunction ) { // Add current params as an argument, and reset.
					// Get the value from the param
					String t = current.trim();
					current = "";
					Object o = parseVal(t);
					
					// Add current params as an argument, and reset.
					if ( o != null )
						temp.add(o);
					
					arguments.add(new StyleParams(temp.toArray(new Object[temp.size()])));
					temp.clear();
					current = "";
				} else {
					current = current + c;
				}
			}
		}
		return arguments;
	}

	/**
	 * Try to parse string to a value (number, percent, string).
	 * 
	 * @param value
	 * @return
	 */
	private Object parseVal(String value) {
		Object t = parseNumber(value);
		if (t != null)
			return t;

		t = parsePercent(value);
		if (t != null)
			return t;

		return value;
	}

	/**
	 * Try to parse string to number.
	 * 
	 * @param value
	 * @return
	 */
	private Number parseNumber(String value) {
		if (!value.endsWith("px"))
			return null;
		
		value = value.substring(0,value.length()-2);
		try {
			Number t = Double.parseDouble(value);
			return t;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Try to parse string to percentage.
	 * 
	 * @param value
	 * @return
	 */
	private Percentage parsePercent(String value) {
		if (!value.endsWith("%"))
			return null;

		value = value.substring(0, value.length() - 1);
		Number num = parseNumber(value + "px");
		if (num == null)
			return null;

		return new Percentage(num.doubleValue());
	}

	/**
	 * Turn string of selectors into list.
	 * @param selectorString
	 * @return
	 */
	private List<StyleSelector> parseSelectors(String selectorString) {
		List<StyleSelector> ret = new ArrayList<>();
		selectorString = selectorString.trim();
		String[] t = selectorString.contains(",") ? selectorString.split("\\,") : new String[] { selectorString };
		for (int i = 0; i < t.length; i++) {
			String t2 = t[i].trim();
			ret.add(new StyleSelector(t2));
		}

		return ret;
	}

	/**
	 * This class defines the order in which CSS pseudo classes are fired. It also contines the data (routine) of the pseudo class.
	 * @author Andrew
	 *
	 */
	class StyleData {

		private HashMap<String, List<StyleOperationValue>> routines = new HashMap<>();
		private List<String> routineOrder = new ArrayList<>();

		public void addDeclarationData(String pseudoClass, StyleOperationValue styleOperationValue) {
			if ( !this.routines.containsKey(pseudoClass) ) {
				this.routines.put(pseudoClass, new ArrayList<>());
				this.routineOrder.add(pseudoClass);
			}
			
			this.routines.get(pseudoClass).add(styleOperationValue);
		}
		
		public List<String> getPseudoClassOrder() {
			return this.routineOrder;
		}

		public List<StyleOperationValue> getDeclarationData(String pseudoClass) {
			return this.routines.get(pseudoClass);
		}
	}

	class Percentage {
		private double percent;

		public Percentage(double percent) {
			this.percent = percent;
		}

		public double getPercent() {
			return percent;
		}

		public double getValue() {
			return percent / 100d;
		}
		
		public String toString() {
			return percent + "%";
		}
	}

	class StyleSelector {
		private String selector;
		private StyleSelectorType type;
		private String modifier = "normal"; // Useful for :hover support

		public StyleSelector(String selector) {
			// Parse out event
			if ( selector.contains(":") ) {
				String[] t = selector.split(":", 2);
				selector = t[0];
				this.modifier = t[1];
			}
			
			// Parse out class
			if ( selector.startsWith(".") ) {
				selector = selector.substring(1);
				type = StyleSelectorType.CLASS;
			} else {
				type = StyleSelectorType.TAG;
			}
				
			// Set selector
			this.selector = selector;
		}
		
		public String getModifier() {
			return this.modifier;
		}

		public StyleSelectorType getType() {
			return this.type;
		}

		public String getSelector() {
			return this.selector;
		}

		@Override
		public String toString() {
			return getSelector();
		}
		
		public int hashCode() {
			return selector.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			if ( !(o instanceof StyleSelector) )
				return false;
			
			StyleSelector s = (StyleSelector)o;
			if ( !s.selector.contentEquals(selector) )
				return false;
			
			return true;
		}
	}

	enum StyleSelectorType {
		TAG, CLASS;
	}
}

abstract class DataCallback<T, E> {
	public abstract T callback(E object);
}

/**
 * Pseudo Class enum list. Used to check if a pseudo class is active or not.
 * @author Andrew
 *
 */
enum PseudoClass {
	NORMAL("normal", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return true;
		}
	}),
	HOVER("hover", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isHover();
		}
	}),
	FOCUS("focus", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isSelected() || node.isClicked();
		}
	}),
	SELECT("select", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isSelected();
		}
	}),
	ACTIVE("active", new DataCallback<Boolean, Node>() {
		@Override
		public Boolean callback(Node node) {
			return node.isClicked();
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

/**
 * Class used to define a function name and which args were passed to it through user-supplied CSS.
 * Can be an argument when checking StyleVarArgs in StyleOperations
 * @author Andrew
 *
 */
class StyleFunction {
	protected StyleVarArgs args;
	private String name;
	
	public StyleFunction(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public StyleVarArgs getArgs() {
		return this.args;
	}
	
	@Override
	public String toString() {
		return name + "(" + args + ")";
	}
}

/**
 * Class used to define each grouping of parameters that represent the values for a css property. Most properties will have 1 Var Arg with multiple params.<br>
 * i.e.<br>
 * padding: 16px 24px						<b>--> This has 1 Var Arg, with 2 params.<br></b>
 * box-shadow: 0px 0px, 32px 32px 32px red	<b>--> This has 2 var args. The first has 2 params, the second has 4 params.</b>
 * @author Andrew
 *
 */
class StyleVarArgs {
	private List<StyleParams> params = new ArrayList<>();
	
	public StyleVarArgs(StyleParams...params) {
		for (int i = 0; i < params.length; i++) {
			this.params.add(params[i]);
		}
	}
	
	public void add(StyleVarArgs val) {
		for (int i = 0; i < val.size(); i++) {
			this.add(val.get(i));
		}
	}

	public void add(StyleParams styleParams) {
		params.add(styleParams);
	}

	public int size() {
		return params.size();
	}
	
	public StyleParams get(int index) {
		return params.get(index);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(params.toArray(new Object[params.size()]));
	}
}

/**
 * Class used to represent parameters in a argument.
 * @author Andrew
 *
 */
class StyleParams {
	private List<Object> values = new ArrayList<Object>();
	
	public StyleParams(Object...objects) {
		for (int i = 0; i < objects.length; i++) {
			values.add(objects[i]);
		}
	}
	
	public int size() {
		return values.size();
	}
	
	public Object get(int index) {
		return values.get(index);
	}
	
	@Override
	public String toString() {
		return Arrays.toString(values.toArray(new Object[values.size()]));
	}
}

/**
 * This class maps a property to a style operation. It's implemented when giving java the ability to interface with CSS.
 * @author Andrew
 *
 */
abstract class StyleOperation {
	private String name;
	
	public StyleOperation(String key) {
		this.name = key;
		StyleOperations.operations.put(key, this);
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
		operation.process(node, value);
	}
	
	@Override
	public String toString() {
		return operation + " " + value;
	}
}
