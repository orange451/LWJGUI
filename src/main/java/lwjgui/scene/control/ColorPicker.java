package lwjgui.scene.control;

import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;

import lwjgui.LWJGUI;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.gl.Renderer;
import lwjgui.paint.Color;
import lwjgui.paint.ColorNameLookup;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.OpenGLPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.theme.Theme;

public class ColorPicker extends ButtonBase {
	private Color color;
	private Color cancelColor;
	private ColorPopup context;
	
	public ColorPicker(Color color) {
		super(color.toString());
		
		this.setOnMouseReleased((event) -> {
			if ( this.isDisabled() )
				return;

			context.show(getScene(), getX(), getY()+getHeight());
		});
		
		this.setColor(color);
		
		context = new ColorPopup();
		context.setAutoHide(false);
	}

	public ColorPicker() {
		this(Color.WHITE);
	}
	
	public void setColor(Color color) {
		if ( color == null )
			return;
		
		this.color = new Color(color);
		this.setText(ColorNameLookup.matchName(color));
		
		StackPane g = new StackPane();
		g.setMouseTransparent(true);
		g.setPrefSize(16, 16);
		g.setBackground(color);
		this.setGraphic(g);
	}
	
	public Color getColor() {
		return this.color;
	}
	
	class ColorPane extends OpenGLPane {
		
		protected int[] colors;
		protected boolean drawn;
		
		public ColorPane() {
			this.setFillToParentHeight(true);
			this.setFillToParentWidth(true);
			this.setAutoClear(false);
			
			this.setRendererCallback(new Renderer() {
				
				private void drawColor( Context context, Color c1, Color c2, double x1, double y1, double x2, double y2, double sx1, double sy1, double sx2, double sy2 ) {
					NVGPaint paint = NanoVG.nvgLinearGradient(context.getNVG(), (float)sx1, (float)sy1, (float)sx2, (float)sy2, c1.getNVG(), c2.getNVG(), NVGPaint.calloc());
					
					NanoVG.nvgBeginPath(context.getNVG());
					NanoVG.nvgRect(context.getNVG(), (int)x1, (int)y1, (int)(x2-x1), (int)(y2-y1));
					NanoVG.nvgFillPaint(context.getNVG(), paint);
					NanoVG.nnvgFill(context.getNVG());
					
					paint.free();
				}
				
				@Override
				public void render(Context context) {
					if ( drawn && colors == null ) {
						int[] temp = new int[context.getWidth()*context.getHeight()];
						GL11.glReadPixels(0, 0, context.getWidth(), context.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, temp);
						colors = temp;
					}
					
					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
					
					float width = context.getWidth()-16;
					float height = context.getHeight();
					
					Color[] colorsList = new Color[] {
							Color.RED,
							Color.YELLOW,
							Color.GREEN,
							Color.CYAN,
							Color.BLUE,
							Color.MAGENTA,
							Color.RED
					};
					
					// Draw rainbow
					for (int i = 0; i < colorsList.length-1; i++) {
						Color c1 = colorsList[i];
						Color c2 = colorsList[i+1];
						
						double totalWidth = width;
						double mWidth = totalWidth/(double)(colorsList.length-1);
						
						double x1 = (i+0)*mWidth;
						double x2 = (i+1)*mWidth+1;
						
						drawColor( context, c1, c2, x1, 0, x2, height, x1, 0, x2, 0 );
					}
					
					// Black To White
					drawColor( context, Color.WHITE, Color.BLACK, width, 0, context.getWidth(), height, 0, height/64f, 0, height-1);
					
					// Draw shading
					double h = context.getHeight()/2.25;
					drawColor( context, Color.WHITE, Color.TRANSPARENT, 0, 0, width, height, 0, 1, 0, h );
					drawColor( context, Color.TRANSPARENT, Color.BLACK, 0, height-h, width, height+1, 0, height-h, 0, height-1 );
					drawn = true;
				}
			});
		}
	}
	
	class ColorPopup extends PopupWindow {
		private ColorPane colorPane;
		private StackPane colorS;
		private Pane colorPick;
		private TextField r;
		private TextField g;
		private TextField b;
		
		private void update(Color newColor) {
			tempColor(newColor);
			r.setText(""+newColor.getRed());
			g.setText(""+newColor.getGreen());
			b.setText(""+newColor.getBlue());
			
			updateFromText();
		}
		
		private void tempColor(Color newColor) {
			colorS.setBackground(newColor);
			colorPick.setBackground(newColor);
			setColor(newColor);
			
			if ( buttonEvent != null ) {
				EventHelper.fireEvent(buttonEvent, new ActionEvent());
			}
		}
		
		private void updateFromText() {
			tempColor(new Color( Integer.parseInt(r.getText()), Integer.parseInt(g.getText()), Integer.parseInt(b.getText()) ));
			
			LWJGUI.runLater(()->{
				Color newColor = colorS.getBackground();
				try {
					int r1 = newColor.getRed();
					int g1 = newColor.getGreen();
					int b1 = newColor.getBlue();
					
					int len = 64;
					int index = -1;
					int[] colors = colorPane.colors;
					if ( colors != null ) {
						for (int i = 0; i < colors.length; i++) {
							int temp = colors[i];
							int r2 = (temp >> 0) & 0xFF;
							int g2 = (temp >> 8) & 0xFF;
							int b2 = (temp >> 16) & 0xFF;
							
							int tLen = Math.abs(r1-r2)+Math.abs(g1-g2)+Math.abs(b1-b2);
							if ( tLen < len ) {
								index = i;
								len = tLen;
							}
						}
					}
					
					if ( index > -1 ) {
						int y = index/(int)colorPane.getWidth();
						int x = index%(int)colorPane.getWidth();
						colorPick.setAbsolutePosition(colorPane.getX()+x-colorPick.getWidth()/2f, colorPane.getY()+(colorPane.getHeight()-y)-colorPick.getHeight()/2f);
						tempColor(newColor);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		
		class ColorNumberField extends TextField {
			private String selValue;
			public ColorNumberField() {
				this.setOnSelected((event)->{
					selValue = getText();
					
					this.deselect();
					LWJGUI.runLater(()->{
						this.selectAll();
					});
				});

				this.setOnDeselected((event)->{
					if ( getText().length() == 0 ) {
						setText(selValue);
					}
					updateFromText();
					this.deselect();
				});
				
				this.setOnKeyPressed((event)->{
					if ( event.getKey() == GLFW.GLFW_KEY_ENTER && isEditing() ) {
						if ( getText().length() == 0 ) {
							setText(selValue);
							this.selectAll();
						}
						updateFromText();
					}
				});
				
				this.setOnTextChange((event)->{
					String newText = ColorNumberField.this.getText();
					if ( newText.length() > 3 ) {
						newText = newText.substring(0, 3);
						this.setText(newText);
					}
					
					if ( newText.length() > 0 ) {
						try {
							double d = Double.parseDouble(newText);
							if ( (int)d != d ) {
								this.setText(""+(int)d);
							}
						} catch (Exception e) {
							this.setText("0");
						}
					}
				});
			}
		}
		
		private void apply() {
			setColor(colorS.getBackground());
			cancelColor.set(color);
			tempColor(color);
		}
		
		public ColorPopup() {
			this.setPadding(new Insets(1));
			this.setPaddingColor(Theme.current().getControlOutline());
			this.setBackground(Theme.current().getBackground());
			this.setAlignment(Pos.TOP_CENTER);
			this.setPrefSize(100, 100);
			
			VBox background = new VBox();
			background.setSpacing(4);
			background.setBackground(null);
			background.setPadding(new Insets(4));
			this.getChildren().add(background);

			int tWid = 48;
			
			colorS = new StackPane();
			colorS.setPrefWidth(tWid);
			colorS.setFillToParentHeight(true);
			colorS.setPadding(new Insets(1));
			colorS.setPaddingColor(Color.DARK_GRAY);
			
			r = new ColorNumberField();
			r.setPrefWidth(tWid);
			r.setPrompt("R");
			r.setText(""+color.getRed());
			
			g = new ColorNumberField();
			g.setPrefWidth(tWid);
			g.setPrompt("G");
			g.setText(""+color.getGreen());
			
			b = new ColorNumberField();
			b.setPrefWidth(tWid);
			b.setPrompt("B");
			b.setText(""+color.getBlue());
			
			GridPane rgbPane = new GridPane();
			rgbPane.setBackground(null);
			rgbPane.setHgap(4);
			rgbPane.add(colorS, 0, 0);
			rgbPane.add(r, 1, 0);
			rgbPane.add(g, 2, 0);
			rgbPane.add(b, 3, 0);
			background.getChildren().add(rgbPane);
			
			StackPane temp = new StackPane();
			temp.setBackground(Color.GRAY);
			temp.setPrefHeight(100);
			temp.setFillToParentWidth(true);
			temp.setPadding(new Insets(1));
			background.getChildren().add(temp);
			
			{
				colorPane = new ColorPane();
				temp.getChildren().add(colorPane);
				
				int pickSize = 6;
				int hs = pickSize/2;
				
				colorPick = new FloatingPane();
				colorPick.setMouseTransparent(true);
				colorPick.setPrefSize(pickSize, pickSize);
				colorPick.setPadding(new Insets(1));
				colorPick.setPaddingColor(Color.DARK_GRAY);
				colorPane.getChildren().add(colorPick);
				
				Runnable forceUpdate = new Runnable() {
					@Override
					public void run() {
						// Bounds
						if ( colorPick.getX()+hs > colorPane.getX()+colorPane.getWidth()-1 )
							colorPick.setAbsolutePosition(colorPane.getX()+colorPane.getWidth()-hs-1, colorPick.getY());
						if ( colorPick.getX()+hs < colorPane.getX() )
							colorPick.setAbsolutePosition(colorPane.getX()-hs, colorPick.getY());
						if ( colorPick.getY()+hs > colorPane.getY()+colorPane.getHeight() )
							colorPick.setAbsolutePosition(colorPick.getX(), colorPane.getY()+colorPane.getHeight()-hs);
						if ( colorPick.getY()+hs < colorPane.getY()+1 )
							colorPick.setAbsolutePosition(colorPick.getX(), colorPane.getY()-hs+1);
						
						
						Vector2i offset = new Vector2i( (int)(colorPick.getX()+hs-colorPane.getX()), (int)colorPane.getHeight()-(int)(colorPick.getY()+hs-colorPane.getY()) );
						int index = (offset.y*(int)colorPane.getWidth())+offset.x;
						int rgb = colorPane.colors[index];
						
						// Swap blue/red channel? Why do I need to do this?
						Color c = new Color((rgb >> 0) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 16) & 0xFF, 255);
						
						// Update the temp color
						colorS.setBackground(c);
						r.setText(""+c.getRed());
						g.setText(""+c.getGreen());
						b.setText(""+c.getBlue());
						colorPick.setBackground(c);
						tempColor(c);
					}
				};
				
				colorPane.setOnMouseDragged((event)->{
					colorPick.setAbsolutePosition(event.getMouseX()-hs, event.getMouseY()-hs);
					forceUpdate.run();
				});
				
				colorPane.setOnMousePressed((event)->{
					colorPick.setAbsolutePosition(event.getMouseX()-3, event.getMouseY()-3);
					this.cached_context.setSelected(colorPane);
					forceUpdate.run();
				});
			}
			
			StackPane temp2 = new StackPane();
			temp2.setBackground(null);
			temp2.setAlignment(Pos.CENTER_RIGHT);
			temp2.setFillToParentWidth(true);
			background.getChildren().add(temp2);
			
			HBox buttons = new HBox();
			buttons.setSpacing(4);
			buttons.setBackground(null);
			temp2.getChildren().add(buttons);

			Button cancel = new Button("Cancel");
			buttons.getChildren().add(cancel);
			
			cancel.setOnAction((event)->{
				this.close();
			});
			
			Button apply = new Button("Apply");
			buttons.getChildren().add(apply);
			
			apply.setOnAction((event)->{
				this.apply();
				this.close();
			});
		}
		
		@Override
		public void close() {
			super.close();
			try {
				setColor(cancelColor);
				
				if ( buttonEvent != null ) {
					EventHelper.fireEvent(buttonEvent, new ActionEvent());
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void show(Scene scene, double absoluteX, double absoluteY) {
			super.show(scene, absoluteX, absoluteY);
			cancelColor = new Color(color);
			update(color);
			
			// Ugly hack to force wait 3 frames before setting color
			LWJGUI.runLater(()->{
				LWJGUI.runLater(()->{
					LWJGUI.runLater(()->{
						ColorPicker.this.context.updateFromText();
					});
				});
			});
		}

		@Override
		public boolean isResizeable() {
			return false;
		}

		@Override
		public void render(Context context) {
			// Setup rendering info
			long vg = context.getNVG();
			int x = (int) getX();
			int y = (int) getY();
			int w = (int) getWidth();
			int h = (int) getHeight();
			
			// Draw Drop Shadow
			this.clip(context,16);
			NVGPaint paint = NanoVG.nvgBoxGradient(vg, x+2,y+3, w-2,h, 4, 8, Theme.current().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.create());
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRect(vg, x-16,y-16, w+32,h+32);
			NanoVG.nvgFillPaint(vg, paint);
			NanoVG.nvgFill(vg);
			
			super.render(context);
		}
		
	}
}
