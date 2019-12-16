package lwjgui.style;

import java.util.HashMap;

import lwjgui.paint.Color;
import lwjgui.scene.Node;

public class StyleOperations {
	protected static HashMap<String, StyleOperation> operations = new HashMap<>();

	private final static String AUTO = "auto";
	private final static String NONE = "none";
	
	public static StyleOperation WIDTH = new StyleOperation("width") {
		@Override
		public void process(Node node, Object value) {
			if ( value.equals(AUTO) )
				value = 0;
			
			node.setPrefWidth(getNumber(value));
		}
	};
	
	public static StyleOperation MIN_WIDTH = new StyleOperation("min-width") {
		@Override
		public void process(Node node, Object value) {
			if ( value.equals(AUTO) )
				value = 0;
			
			node.setMinWidth(getNumber(value));
		}
	};
	
	public static StyleOperation MAX_WIDTH = new StyleOperation("max-width") {
		@Override
		public void process(Node node, Object value) {
			if ( value.equals(AUTO) )
				value = Integer.MAX_VALUE;
			
			node.setMaxWidth(getNumber(value));
		}
	};
	
	public static StyleOperation HEIGHT = new StyleOperation("height") {
		@Override
		public void process(Node node, Object value) {
			if ( value.equals(AUTO) )
				value = 0;
			
			node.setPrefHeight(getNumber(value));
		}
	};
	
	public static StyleOperation MIN_HEIGHT = new StyleOperation("min-height") {
		@Override
		public void process(Node node, Object value) {
			if ( value.equals(AUTO) )
				value = 0;
			
			node.setMinHeight(getNumber(value));
		}
	};
	
	public static StyleOperation MAX_HEIGHT = new StyleOperation("max-height") {
		@Override
		public void process(Node node, Object value) {
			if ( value.equals(AUTO) )
				value = Integer.MAX_VALUE;
			
			node.setMaxHeight(getNumber(value));
		}
	};
	
	public static StyleOperation BORDER_RADIUS = new StyleOperation("border-radius") {
		@Override
		public void process(Node node, Object value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			if ( value.toString().contains(",") ) {
				// Not implemented yet
			} else {
				t.setBorderRadii((float) getNumber(value));	
			}
		}
	};
	
	public static StyleOperation BORDER_STYLE = new StyleOperation("border-style") {
		@Override
		public void process(Node node, Object value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			BorderStyle bs = BorderStyle.valueOf(value.toString().toUpperCase());
			if ( bs == null )
				bs = BorderStyle.NONE;
			
			t.setBorderStyle(bs);
		}
	};
	
	public static StyleOperation BORDER_WIDTH = new StyleOperation("border-width") {
		@Override
		public void process(Node node, Object value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			t.setBorderWidth((float)getNumber(value));
		}
	};
	
	public static StyleOperation BORDER_COLOR = new StyleOperation("border-color") {
		@Override
		public void process(Node node, Object value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			t.setBorderColor(getColor(value.toString()));
		}
	};
	
	public static StyleOperation BACKGROUND_COLOR = new StyleOperation("background-color") {
		@Override
		public void process(Node node, Object value) {
			if ( !(node instanceof StyleBackground) )
				return;
			
			StyleBackground t = (StyleBackground)node;
			Color color = getColor(value.toString());
			t.setBackground(new BackgroundSolid(color));
		}
	};
	
	public static StyleOperation BOX_SHADOW = new StyleOperation("box-shadow") {
		@Override
		public void process(Node node, Object value) {
			if ( !(node instanceof StyleBoxShadow) )
				return;
			
			StyleBoxShadow t = (StyleBoxShadow)node;
			
			if ( value.equals(NONE) )
				t.getBoxShadowList().clear();
		}
	};

	public static StyleOperation match(String key) {
		return operations.get(key);
	}

	protected static double getNumber(Object value) {
		try {
			return Double.parseDouble(value.toString());
		} catch(Exception e) {
			return 0;
		}
	}

	protected static Color getColor(String string) {
		
		// Color by function
		if ( (string.startsWith("rgb(") || string.startsWith("rgba(")) && string.endsWith(")") ) {
			String str = string.replace("rgba(", "").replace("rgb(", "").replace(")", "");
			String[] split = str.split(",");
			Number[] vals = new Number[split.length];
			for (int i = 0; i < split.length; i++) {
				try {
					Number test = Double.parseDouble(split[i].trim());
					if ( test.intValue() == test.doubleValue() )
						test = test.intValue();
					
					vals[i] = test;
				} catch(Exception e) {
					e.printStackTrace();
					return Color.PINK;
				}
			}
			
			int alpha = 255;
			if ( vals.length == 4 )
				alpha = (int) (vals[3].doubleValue() * 255);
			
			return new Color(vals[0].intValue(), vals[1].intValue(), vals[2].intValue(), alpha);
		}
		
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
