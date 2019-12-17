package lwjgui.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.Node;
import lwjgui.scene.Region;
import lwjgui.transition.FillTransition;
import lwjgui.transition.Transition;

public class StyleOperations {
	protected static HashMap<String, StyleOperation> operations = new HashMap<>();

	private final static String AUTO = "auto";
	private final static String NONE = "none";
	
	public static StyleOperation WIDTH = new StyleOperation("width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			node.setPrefWidth(toNumber(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation MIN_WIDTH = new StyleOperation("min-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			node.setMinWidth(toNumber(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation MAX_WIDTH = new StyleOperation("max-width") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(Integer.MAX_VALUE));
			
			node.setMaxWidth(toNumber(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation HEIGHT = new StyleOperation("height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			node.setPrefHeight(toNumber(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation MIN_HEIGHT = new StyleOperation("min-height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(0));
			
			node.setMinHeight(toNumber(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation MAX_HEIGHT = new StyleOperation("max-height") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( value.size() == 0 )
				value = new StyleVarArgs(new StyleParams(Integer.MAX_VALUE));
			
			node.setMaxHeight(toNumber(value.get(0).get(0)));
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
				t.setBorderRadii((float) toNumber(value.get(0).get(0)));	
			}
		}
	};
	
	public static StyleOperation BORDER_STYLE = new StyleOperation("border-style") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			BorderStyle bs = BorderStyle.valueOf(value.get(0).get(0).toString().toUpperCase());
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
			float destBorder = (float)toNumber(value.get(0).get(0));
			float sourceBorder = t.getBorderWidth();
			
			// Border Width transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( destBorder == sourceBorder || transition == null ) {
				t.setBorderWidth(destBorder);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						t.setBorderWidth(tween(sourceBorder, destBorder, progress));
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation BORDER_COLOR = new StyleOperation("border-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBorder) )
				return;
			
			StyleBorder t = (StyleBorder)node;
			t.setBorderColor(getColor(value.get(0).get(0)));
		}
	};
	
	public static StyleOperation BACKGROUND_COLOR = new StyleOperation("background-color") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBackground) )
				return;
			
			StyleBackground t = (StyleBackground)node;
			Color destColor = getColor(value.get(0).get(0));
			Background currentBackground = t.getBackground();
			
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( transition == null || currentBackground == null || !(currentBackground instanceof BackgroundSolid) ) {
				t.setBackground(new BackgroundSolid(destColor));
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				Color sourceColor = new Color(((BackgroundSolid)currentBackground).getColor());
				if ( sourceColor.equals(destColor) )
					return;
				
				Color fillColor = new Color(sourceColor);
				
				// Color transition
				FillTransition tran = new FillTransition(transition.getDurationMillis(), sourceColor, destColor, fillColor);
				tran.play();
				current.add(tran);

				// Apply fill color
				t.setBackground(new BackgroundSolid(fillColor));
			}
		}
	};
	
	public static StyleOperation BOX_SHADOW = new StyleOperation("box-shadow") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof StyleBoxShadow) )
				return;
			
			StyleBoxShadow t = (StyleBoxShadow)node;
			
			// Generate the box shadow
			List<BoxShadow> shadows = new ArrayList<BoxShadow>();
			if ( !value.get(0).get(0).equals(NONE) ) {
				
				for (int i = 0; i < value.size(); i++) {
					StyleParams params = value.get(i);
					
					if ( value.size() == 2 ) {
						shadows.add(new BoxShadow(toNumber(params.get(0)), toNumber(params.get(1)), 0));
					} else if ( params.size() == 3 ) {
						boolean isNumber = isNumber(params.get(2));
						if ( isNumber ) {
							shadows.add(new BoxShadow(
									toNumber(params.get(0)),
									toNumber(params.get(1)),
									toNumber(params.get(2))
							));
						} else {
							shadows.add(new BoxShadow(
									toNumber(params.get(0)),
									toNumber(params.get(1)),
									0,
									getColor(params.get(2))
							));
						}
					} else if ( params.size() == 4 ) {
						boolean isNumber = isNumber(params.get(3));
						if ( isNumber ) {
							shadows.add(new BoxShadow(
									toNumber(params.get(0)),
									toNumber(params.get(1)),
									toNumber(params.get(2)),
									toNumber(params.get(3))
							));
						} else {
							shadows.add(new BoxShadow(
									toNumber(params.get(0)),
									toNumber(params.get(1)),
									toNumber(params.get(2)),
									getColor(params.get(3))
							));
						}
					} else if ( params.size() == 5 ) {
						shadows.add(new BoxShadow(
								toNumber(params.get(0)),
								toNumber(params.get(1)),
								toNumber(params.get(2)),
								toNumber(params.get(3)),
								getColor(params.get(4))
						));
					}
				}
			}
			// Get the style transition for this node with this transition name
			StyleTransition transition = node.getStyleTransition(this.getName());
			
			// If no transition, directly copy in shadows.
			if ( transition == null ) {
				t.getBoxShadowList().clear();
				for (int i = 0; i < shadows.size(); i++) {
					t.getBoxShadowList().add(shadows.get(i));
				}
			} else {
				// Transition already existing shadows
				for (int i = 0; i < Math.min(shadows.size(), t.getBoxShadowList().size()); i++) {
					List<Transition> current = transition.getTransitions();
					if ( current.size() > 0 )
						continue;
					
					BoxShadow sourceShadow = t.getBoxShadowList().get(i);
					BoxShadow destShadow = shadows.get(i);
					
					// Blur transition
					if ( sourceShadow.getBlurRadius() != destShadow.getBlurRadius() ) {
						float a = sourceShadow.getBlurRadius();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setBlurRadius(tween(a, destShadow.getBlurRadius(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Spread transition
					if ( sourceShadow.getSpread() != destShadow.getSpread() ) {
						float a = sourceShadow.getSpread();
						
						Transition tran = new Transition(transition.getDurationMillis()) {
							@Override
							public void tick(double progress) {
								sourceShadow.setSpread(tween(a, destShadow.getSpread(), progress));
							}
						};
						tran.play();
						current.add(tran);
					}
					
					// Color transition
					if ( sourceShadow.getSpread() != destShadow.getSpread() ) {
						sourceShadow.getFromColor().immutable(false);
						Transition tran = new FillTransition(transition.getDurationMillis(), new Color(sourceShadow.getFromColor()), destShadow.getFromColor(), sourceShadow.getFromColor());
						tran.play();
						current.add(tran);
					}
				}
			}
			
			shadows.clear();
		}
	};
	
	public static StyleOperation PADDING = new StyleOperation("padding") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			if ( !(node instanceof Region) )
				return;
			
			Region region = (Region)node;
			Insets dest = null;
			Insets source = region.getPadding();
			
			// Get dest padding
			if ( value.get(0).size() == 1 ) {
				dest = new Insets( toNumber(value.get(0).get(0)) );
			} else if ( value.get(0).size() == 2 ) {
				dest = new Insets( toNumber(value.get(0).get(0)), toNumber(value.get(0).get(1)) );
			} else if ( value.get(0).size() == 4 ) {
				dest = new Insets( toNumber(value.get(0).get(0)), toNumber(value.get(0).get(1)), toNumber(value.get(0).get(2)), toNumber(value.get(0).get(3)) );
			}
			
			// NPE
			if ( dest == null )
				return;
			
			// Transition
			StyleTransition transition = node.getStyleTransition(this.getName());
			if ( dest.equals(source) || transition == null ) {
				region.setPadding(dest);
			} else {
				List<Transition> current = transition.getTransitions();
				if ( current.size() > 0 )
					return;
				
				final Insets destF = dest;
				
				Transition tran = new Transition(transition.getDurationMillis()) {
					@Override
					public void tick(double progress) {
						Insets t = new Insets( tween(source.getTop(), destF.getTop(), progress),
								tween(source.getRight(), destF.getRight(), progress),
								tween(source.getBottom(), destF.getBottom(), progress),
								tween(source.getLeft(), destF.getLeft(), progress));
						
						region.setPadding(t);
					}
				};
				tran.play();
				current.add(tran);
			}
		}
	};
	
	public static StyleOperation TRANSITION = new StyleOperation("transition") {
		@Override
		public void process(Node node, StyleVarArgs value) {
			for (int i = 0; i< value.size(); i++) {
				if ( value.get(i).size() < 2 )
					continue;
				
				String property = value.get(i).get(0).toString().trim();
				long duration = toDuration(value.get(i).get(1));
				
				
				StyleTransition styleTransition = node.getStyleTransition(property);
				if ( styleTransition == null ) {
					styleTransition = new StyleTransition(property, duration, StyleTransitionType.LINEAR);
					node.setStyleTransition(property, styleTransition);
				}
				
				styleTransition.setDuration(duration);
			}
		}
	};

	public static StyleOperation match(String key) {
		return operations.get(key);
	}

	protected static float tween(double value1, double value2, double progress) {
		return (float) (value1 + (value2-value1)*progress);
	}
	
	protected static long toDuration(Object object) {
		String str = object.toString();
		double multiplier = 1.0;
		
		if ( !str.endsWith("ms") )
			multiplier = 1000;
		
		str = str.replace("ms", "").replace("s", "");
		return (long) (toNumber(str) * multiplier);
	}

	protected static float toNumber(Object value) {
		try {
			return (float) Double.parseDouble(value.toString());
		} catch(Exception e) {
			return 0;
		}
	}
	
	protected static boolean isNumber(Object value) {
		try {
			Double.parseDouble(value.toString());
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	protected static Color getColor(Object arg) {

		// Color by function
		if ( arg instanceof StyleFunction ) {
			StyleFunction func = (StyleFunction)arg;
			StyleVarArgs funcArgs = func.getArgs();
			
			if ( func.getName().equals("rgb") )
				return new Color(toNumber(funcArgs.get(0).get(0))/255d, toNumber(funcArgs.get(0).get(1))/255d, toNumber(funcArgs.get(0).get(2))/255d);
			
			if ( func.getName().equals("rgba") )
				return new Color(toNumber(funcArgs.get(0).get(0))/255d, toNumber(funcArgs.get(0).get(1))/255d, toNumber(funcArgs.get(0).get(2))/255d, toNumber(funcArgs.get(0).get(3)));
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
