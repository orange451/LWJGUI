package lwjgui.style;

import java.util.HashMap;

import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.style.Stylesheet.StyleFunction;

public class StyleOperations {
	protected static HashMap<String, StyleOperation> operations = new HashMap<>();

	private final static String AUTO = "auto";
	private final static String NONE = "none";
	
	public static StyleOperation WIDTH = new StyleOperation("width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(0);
			
			node.setPrefWidth(toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation MIN_WIDTH = new StyleOperation("min-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(0);
			
			node.setMinWidth(toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation MAX_WIDTH = new StyleOperation("max-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(Integer.MAX_VALUE);
			
			node.setMaxWidth(toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation HEIGHT = new StyleOperation("height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(0);
			
			node.setPrefHeight(toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation MIN_HEIGHT = new StyleOperation("min-height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(0);
			
			node.setMinHeight(toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation MAX_HEIGHT = new StyleOperation("max-height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(Integer.MAX_VALUE);
			
			node.setMaxHeight(toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation BORDER_RADIUS = new StyleOperation("border-radius") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			if ( value.get(0).toString().contains(",") ) {
				// Not implemented yet
			} else {
				t.setBorderRadii((float) toNumber(value.get(0)));	
			}
		}
	};
	
	public static StyleOperation BORDER_STYLE = new StyleOperation("border-style") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			BorderStyle bs = BorderStyle.valueOf(value.get(0).toString().toUpperCase());
			if ( bs == null )
				bs = BorderStyle.NONE;
			
			t.setBorderStyle(bs);
		}
	};
	
	public static StyleOperation BORDER_WIDTH = new StyleOperation("border-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			t.setBorderWidth((float)toNumber(value.get(0)));
		}
	};
	
	public static StyleOperation BORDER_COLOR = new StyleOperation("border-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			t.setBorderColor(getColor(value.get(0)));
		}
	};
	
	public static StyleOperation BACKGROUND_COLOR = new StyleOperation("background-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBackground) )
				return;
			
			StyleBackground t = (StyleBackground)node;
			Color color = getColor(value.get(0));
			t.setBackground(new BackgroundSolid(color));
		}
	};
	
	public static StyleOperation BOX_SHADOW = new StyleOperation("box-shadow") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBoxShadow) )
				return;
			
			StyleBoxShadow t = (StyleBoxShadow)node;
			
			if ( value.get(0).equals(NONE) )
				t.getBoxShadowList().clear();
			else {
				t.getBoxShadowList().add(new BoxShadow(0, 0, 0, 4, Color.RED.alpha(0.5f)));
			}
		}
	};

	public static StyleOperation match(String key) {
		return operations.get(key);
	}

	protected static double toNumber(Object value) {
		try {
			return Double.parseDouble(value.toString());
		} catch(Exception e) {
			return 0;
		}
	}

	protected static Color getColor(Object arg) {

		// Color by function
		if ( arg instanceof StyleFunction ) {
			StyleFunction func = (StyleFunction)arg;
			StyleVarArgs funcArgs = func.getArgs();
			
			if ( func.getName().equals("rgb") )
				return new Color(toNumber(funcArgs.get(0))/255d, toNumber(funcArgs.get(1))/255d, toNumber(funcArgs.get(2))/255d);
			
			if ( func.getName().equals("rgba") )
				return new Color(toNumber(funcArgs.get(0))/255d, toNumber(funcArgs.get(1))/255d, toNumber(funcArgs.get(2))/255d, toNumber(funcArgs.get(3)));
		}
		
		String string = arg.toString();
		
		// Color by hex
		if ( string.startsWith("#") ) {
			return new Color(string);
		}

		// Color by name
		Color color = Color.match(string);
		if ( color != null )
			return color;
		
		// Fallback
		return Color.PINK;
	}
}
