package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.util.Arrays;

import org.joml.Math;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUI;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.gl.Renderer;
import lwjgui.paint.Color;
import lwjgui.paint.ColorNameLookup;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.Scene;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.OpenGLPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.shape.Circle;
import lwjgui.style.BackgroundLinearGradient;
import lwjgui.style.BackgroundSolid;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.theme.Theme;

public class ColorPicker extends ButtonBase {
	private Color color;
	private Color cancelColor;
	private ColorPopup2 context;
	private boolean supportsAlpha;

	public ColorPicker(Color color) {
		super(color.toString());

		this.setSupportsAlpha(true);

		this.setOnMouseReleased((event) -> {
			if (this.isDisabled())
				return;

			context = new ColorPopup2();
			context.setAutoHide(false);

			context.position(getScene());
			context.render(null);

			double desiredX = getX() + getWidth() / 2 - context.getWidth() / 2;
			double desiredY = getY() + getHeight() + 2;
			double desiredY2 = getY() - context.getHeight() - 2;
			if (desiredY - context.getHeight() > -desiredY2)
				desiredY = desiredY2;

			cancelColor = new Color(getColor());
			context.show(getScene(), desiredX, desiredY);
		});

		this.setColor(color);
	}

	public boolean supportsAlpha() {
		return this.supportsAlpha;
	}

	public void setSupportsAlpha(boolean alpha) {
		this.supportsAlpha = alpha;
	}

	public ColorPicker() {
		this(Color.BLUE);
	}

	@Override
	public String getElementType() {
		return "colorpicker";
	}

	public void setColor(Color color) {
		if (color == null)
			return;

		this.color = new Color(color);
		this.setText(ColorNameLookup.matchName(color));

		StackPane g = new StackPane();
		g.setMouseTransparent(true);
		g.setPrefSize(16, 16);
		g.setBackgroundLegacy(color);
		this.setGraphic(g);
	}

	public Color getColor() {
		return this.color;
	}

	class ColorPopup2 extends PopupWindow {
		private Circle internalCircle;
		private RainbowSlider hueSlider;
		private Slider alphaSlider;
		
		private Color hsvColor;

		private TextField redField;
		private TextField greenField;
		private TextField blueField;
		private TextField alphaField;
		private CustomTextField hexField;

		private boolean ignoreUpdateRGBA;
		private boolean ignoreUpdateHex;
		private boolean calculatingFromText;

		private float[] internalHSV = new float[3];
		private float internalAlpha = 1.0f;

		private final int PADDING = 8;
		private final int WIDTH = 210;

		private void setHue(double hue) {
			internalHSV[0] = (float) hue;
			hueSlider.setValue(1.0 - hue);
			internalCircle.setFill(computeColor());
		}

		private void setSB(double saturation, double brightness) {
			saturation = Math.max(0, Math.min(1, saturation));
			brightness = Math.max(0, Math.min(1, brightness));
			
			internalHSV[1] = (float) saturation;
			internalHSV[2] = (float) brightness;
			internalCircle.setFill(computeColor());
		}

		private void setAlpha(double alpha) {
			if (!supportsAlpha())
				alpha = 1.0f;

			internalAlpha = (float) alpha;
			if (alphaSlider != null)
				alphaSlider.setValue(alpha);
			internalCircle.setFill(computeColor());
		}

		private void setFromRGBAText() {
			ignoreUpdateRGBA = true;
			{
				int r = (int) toNumber(redField.getText());
				int g = (int) toNumber(greenField.getText());
				int b = (int) toNumber(blueField.getText());
				float a = 1.0f;
				if (alphaField != null)
					a = ((int) (toNumber(alphaField.getText()) * 100)) / 100f;

				if (!calculatingFromText) {
					calculatingFromText = true;
					Color.RGBtoHSB(r, g, b, this.internalHSV);
					this.setAlpha(Math.min(1, Math.max(0, a)));
				}

				this.setHue(this.internalHSV[0]);
				this.setSB(this.internalHSV[1], this.internalHSV[2]);
			}
			ignoreUpdateRGBA = false;
			calculatingFromText = false;
		}

		private void setFromHex() {
			if (hexField.getText().length() == 0)
				return;

			ignoreUpdateHex = true;
			{
				try {
					Color color = new Color(hexField.getText());

					if (!calculatingFromText) {
						calculatingFromText = true;
						Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), this.internalHSV);
						this.setAlpha(Math.min(1, Math.max(0, color.getAlphaF())));
					}

					this.setHue(this.internalHSV[0]);
					this.setSB(this.internalHSV[1], this.internalHSV[2]);
				} catch (Exception e) {
					//
				}
			}
			ignoreUpdateHex = false;
			calculatingFromText = false;
		}

		private float toNumber(String text) {
			try {
				return Float.parseFloat(text);
			} catch (Exception e) {
				return 0;
			}
		}

		private final String pad(String s) {
			return (s.length() == 1) ? "0" + s : s;
		}

		private void updateRGBAText() {
			Color col = getColor();
			redField.setText("" + col.getRed());
			greenField.setText("" + col.getGreen());
			blueField.setText("" + col.getBlue());
			if (alphaField != null)
				alphaField.setText("" + internalAlpha);
		}

		private void updateHexText() {
			Color col = getColor();

			String alpha = pad(Integer.toHexString(col.getAlpha()));
			if (!supportsAlpha())
				alpha = "";

			String red = pad(Integer.toHexString(col.getRed()));
			String green = pad(Integer.toHexString(col.getGreen()));
			String blue = pad(Integer.toHexString(col.getBlue()));
			hexField.setText(red + green + blue + alpha);
		}

		private Color computeColor() {
			int rgb = Color.HSBtoRGB(internalHSV[0], internalHSV[1], internalHSV[2]);
			Color col = new Color(0xff000000 | rgb).alpha(internalAlpha);
			
			hsvColor = new Color(Color.HSBtoRGB(internalHSV[0], 1, 1));

			setColor(col);

			if (col.equals(internalCircle.getFill()))
				return internalCircle.getFill();

			if (buttonEvent != null)
				EventHelper.fireEvent(buttonEvent, new ActionEvent());

			if ( !ignoreUpdateRGBA )
				updateRGBAText();

			if (!ignoreUpdateHex)
				updateHexText();
			
			return col;
		}

		@Override
		public void close() {
			super.close();
			setColor(cancelColor);
			try {

				if (buttonEvent != null) {
					EventHelper.fireEvent(buttonEvent, new ActionEvent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void apply() {
			setColor(internalCircle.getFill());
			cancelColor.set(getColor());
		}
		
		private float tween(float a, float b, float ratio) {
			return a + (b-a)*ratio;
		}

		public ColorPopup2() {
			// Set internal colors
			Color.RGBtoHSB(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), internalHSV);
			internalAlpha = getColor().getAlphaF();

			this.setBorderRadii(2);
			this.setBorderStyle(BorderStyle.SOLID);
			this.setBorderWidth(1);
			this.setBorderColor(Theme.current().getControlOutline());

			VBox internalLayout = new VBox();
			internalLayout.setPadding(new Insets(0, 0, 8, 0));
			this.getChildren().add(internalLayout);

			// Temp color pane
			StackPane picker = new StackPane();
			Pane colorPane = new StackPane() {
				@Override
				public void render(Context context) {
					if ( context == null )
						return;
					
					for (int i = 0; i < getHeight(); i++) {
						try (MemoryStack stack = stackPush()) {
							NVGColor c1 = NVGColor.calloc();
							NVGColor c2 = NVGColor.calloc();
							float ratio = i / (float)(getHeight()-1);
							
							c1.r(tween(1, 0, ratio)).g(tween(1, 0, ratio)).b(tween(1, 0, ratio)).a(1);
							c2.r(tween(hsvColor.getRedF(), 0, ratio)).g(tween(hsvColor.getGreenF(), 0, ratio)).b(tween(hsvColor.getBlueF(), 0, ratio)).a(1);
							
							NVGPaint paint = NanoVG.nvgLinearGradient(
									context.getNVG(),
									(float) getX(),
									(float) getY()+i,
									(float) (getX()+getWidth()),
									(float) getY()+i,
									c1, c2,
									NVGPaint.callocStack(stack)
								);

							NanoVG.nvgBeginPath(context.getNVG());
							NanoVG.nvgRect(context.getNVG(), (int) getX(), (int) getY()+i, (int) getWidth(), (int) 1);
							NanoVG.nvgFillPaint(context.getNVG(), paint);
							NanoVG.nnvgFill(context.getNVG());
							
							c1.free();
							c2.free();
						}
					}
					
					super.render(context);
				}
				
				@Override
				public void position(Node parent) {
					super.position(parent);
					
					picker.setLocalPosition(this, internalHSV[1]*getWidth()-picker.getWidth()/2, (1.0-internalHSV[2])*getHeight()-picker.getHeight()/2);
					picker.setBackgroundLegacy(getColor());
				}
			};
			colorPane.setPrefSize(WIDTH, 100);
			internalLayout.getChildren().add(colorPane);
			
			colorPane.setOnMouseDragged((event)->{
				float mx = (float) (event.getMouseX()-getX());
				float my = (float) (event.getMouseY()-getY());
				
				this.setSB(mx/colorPane.getWidth(), 1.0-(my/colorPane.getHeight()));
			});
			
			// Saturation/Brightness modifier
			picker.forceSize(12, 12);
			picker.setBorderRadii(6);
			picker.setBorderWidth(1);
			picker.setBorderStyle(BorderStyle.SOLID);
			picker.setBorderColor(Color.WHITE);
			picker.getBoxShadowList().add(new BoxShadow(0,0,0,2.2f,Color.WHITE_SMOKE.alpha(0.6f)));
			picker.getBoxShadowList().add(new BoxShadow(0,0,0,0.5f,Color.BLACK.alpha(0.75f)));
			colorPane.getChildren().add(picker);

			// Color stuff
			{
				Pane t = new StackPane();
				t.setAlignment(Pos.TOP_LEFT);
				t.setPadding(new Insets(PADDING));
				t.setFillToParentWidth(true);
				internalLayout.getChildren().add(t);

				HBox layout = new HBox();
				layout.setSpacing(6);
				layout.setFillToParentWidth(true);
				t.getChildren().add(layout);

				internalCircle = new Circle(16);
				internalCircle.setFill(getColor());
				layout.getChildren().add(internalCircle);

				Pane t2 = new VBox();
				t2.setFillToParentWidth(true);
				t2.setFillToParentHeight(true);
				layout.getChildren().add(t2);

				// Hue shifter
				{
					StackPane t3 = new StackPane();
					t3.setFillToParentWidth(true);
					t2.getChildren().add(t3);

					if (!supportsAlpha())
						t3.setPadding(new Insets(8, 0, 0, 0));

					hueSlider = new RainbowSlider();
					hueSlider.setOnValueChangedEvent((event) -> {
						double hue = 1.0 - hueSlider.getValue();
						setHue(hue);
					});
					hueSlider.setFillToParentWidth(true);
					t3.getChildren().add(hueSlider);
				}

				// Alpha shifter
				if (supportsAlpha()) {
					StackPane t3 = new StackPane();
					t3.setFillToParentWidth(true);
					t2.getChildren().add(t3);

					alphaSlider = new Slider(0, 1, 1, 0.01);
					alphaSlider.setOnValueChangedEvent((event) -> {
						setAlpha(alphaSlider.getValue());
					});
					alphaSlider.setFillToParentWidth(true);
					t3.getChildren().add(alphaSlider);
				}
			}

			// Text Input
			{
				Pane rgbLayout = new VBox();
				rgbLayout.setAlignment(Pos.CENTER);
				rgbLayout.setFillToParentWidth(true);
				internalLayout.getChildren().add(rgbLayout);

				VBox t1 = new VBox();
				t1.setSpacing(8);
				t1.setAlignment(Pos.CENTER);
				rgbLayout.getChildren().add(t1);

				HBox tt = new HBox();
				tt.setSpacing(6);
				t1.getChildren().add(tt);

				tt.getChildren().add(redField = smolField()); // R
				tt.getChildren().add(greenField = smolField()); // G
				tt.getChildren().add(blueField = smolField()); // B
				if (supportsAlpha())
					tt.getChildren().add(alphaField = smolField()); // A
				updateRGBAText();

				redField.setOnTextChange((event) -> {
					setFromRGBAText();
				});
				greenField.setOnTextChange((event) -> {
					setFromRGBAText();
				});
				blueField.setOnTextChange((event) -> {
					setFromRGBAText();
				});
				if (supportsAlpha()) {
					alphaField.setOnTextChange((event) -> {
						setFromRGBAText();
					});
				}

				HBox hbox = new HBox();
				hbox.setSpacing(6);
				hbox.setFillToParentWidth(true);
				t1.getChildren().add(hbox);

				hexField = new CustomTextField();
				Label label = new Label("#");
				label.setMouseTransparent(true);
				label.setTextFill(label.getTextFill().alpha(0.5f));
				hexField.setLeftNode(label);
				hexField.setFillToParentWidth(true);
				hbox.getChildren().add(hexField);
				updateHexText();

				hexField.setOnTextChange((event) -> {
					this.setFromHex();
				});

				Button cancel = new Button("Cancel");
				hbox.getChildren().add(cancel);
				cancel.setOnAction((event) -> {
					this.close();
				});

				Button ok = new Button("Okay");
				hbox.getChildren().add(ok);
				ok.setOnAction((event) -> {
					this.apply();
					this.close();
				});
			}

			setHue(internalHSV[0]);
			this.getBoxShadowList().add(new BoxShadow(4, 10, 22, 0, Theme.current().getShadow()));
		}

		private TextField smolField() {
			TextField field = new TextField();
			field.setPrefWidth(supportsAlpha() ? 44 : 59);
			return field;
		}
	}

	class RainbowSlider extends Slider {

		public RainbowSlider() {
			super(0, 1, 0.5f, 0);
		}

		@Override
		protected void drawTrack(Context context) {
			super.drawTrack(context);

			float w = (float) this.getInnerBounds().getWidth();
			float h = (float) this.getInnerBounds().getHeight() / 1.5f;
			float x = (float) (getX() + this.getInnerBounds().getX());
			float y = 1 + (float) (getY() + this.getInnerBounds().getY()) + (this.getInnerBounds().getHeight() / 2f)
					- (h / 2f);

			BackgroundLinearGradient b = new BackgroundLinearGradient(0, Color.RED, Color.MAGENTA, Color.BLUE,
					Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED);

			b.render(context, x, y, w, h, new float[] { 3, 3, 3, 3 });
		}
	}

	class ColorPane extends OpenGLPane {

		protected int[] colors;
		protected boolean drawn;

		public ColorPane() {
			this.setFillToParentHeight(true);
			this.setFillToParentWidth(true);
			this.setAutoClear(false);

			this.setRendererCallback(new Renderer() {

				private void drawColor(Context context, Color c1, Color c2, double x1, double y1, double x2, double y2,
						double sx1, double sy1, double sx2, double sy2) {
					try (MemoryStack stack = stackPush()) {
						NVGPaint paint = NanoVG.nvgLinearGradient(context.getNVG(), (float) sx1, (float) sy1,
								(float) sx2, (float) sy2, c1.getNVG(), c2.getNVG(), NVGPaint.callocStack(stack));

						NanoVG.nvgBeginPath(context.getNVG());
						NanoVG.nvgRect(context.getNVG(), (int) x1, (int) y1, (int) (x2 - x1), (int) (y2 - y1));
						NanoVG.nvgFillPaint(context.getNVG(), paint);
						NanoVG.nnvgFill(context.getNVG());
					}
				}

				@Override
				public void render(Context context) {
					if (drawn && colors == null) {
						int[] temp = new int[context.getWidth() * context.getHeight()];
						GL11.glReadPixels(0, 0, context.getWidth(), context.getHeight(), GL11.GL_RGBA,
								GL11.GL_UNSIGNED_BYTE, temp);
						colors = temp;
					}

					GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

					float width = context.getWidth() - 16;
					float height = context.getHeight();

					Color[] colorsList = new Color[] { Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE,
							Color.MAGENTA, Color.RED };

					// Draw rainbow
					for (int i = 0; i < colorsList.length - 1; i++) {
						Color c1 = colorsList[i];
						Color c2 = colorsList[i + 1];

						double totalWidth = width;
						double mWidth = totalWidth / (double) (colorsList.length - 1);

						double x1 = (i + 0) * mWidth;
						double x2 = (i + 1) * mWidth + 1;

						drawColor(context, c1, c2, x1, 0, x2, height, x1, 0, x2, 0);
					}

					// Black To White
					drawColor(context, Color.WHITE, Color.BLACK, width, 0, context.getWidth(), height, 0, height / 64f,
							0, height - 1);

					// Draw shading
					double h = context.getHeight() / 2.25;
					drawColor(context, Color.WHITE, Color.TRANSPARENT, 0, 0, width, height, 0, 1, 0, h);
					drawColor(context, Color.TRANSPARENT, Color.BLACK, 0, height - h, width, height + 1, 0, height - h,
							0, height - 1);
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
			r.setText("" + newColor.getRed());
			g.setText("" + newColor.getGreen());
			b.setText("" + newColor.getBlue());

			updateFromText();
		}

		private void tempColor(Color newColor) {
			colorS.setBackgroundLegacy(newColor);
			colorPick.setBackgroundLegacy(newColor);
			setColor(newColor);

			if (buttonEvent != null) {
				EventHelper.fireEvent(buttonEvent, new ActionEvent());
			}
		}

		private void updateFromText() {
			tempColor(new Color(Integer.parseInt(r.getText()), Integer.parseInt(g.getText()),
					Integer.parseInt(b.getText())));

			LWJGUI.runLater(() -> {
				Color newColor = ((BackgroundSolid) colorS.getBackground()).getColor();
				try {
					int r1 = newColor.getRed();
					int g1 = newColor.getGreen();
					int b1 = newColor.getBlue();

					int len = 64;
					int index = -1;
					int[] colors = colorPane.colors;
					if (colors != null) {
						for (int i = 0; i < colors.length; i++) {
							int temp = colors[i];
							int r2 = (temp >> 0) & 0xFF;
							int g2 = (temp >> 8) & 0xFF;
							int b2 = (temp >> 16) & 0xFF;

							int tLen = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
							if (tLen < len) {
								index = i;
								len = tLen;
							}
						}
					}

					if (index > -1) {
						int y = index / (int) colorPane.getWidth();
						int x = index % (int) colorPane.getWidth();
						colorPick.setAbsolutePosition(colorPane.getX() + x - colorPick.getWidth() / 2f,
								colorPane.getY() + (colorPane.getHeight() - y) - colorPick.getHeight() / 2f);
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
				this.setOnSelected((event) -> {
					selValue = getText();

					this.deselect();
					LWJGUI.runLater(() -> {
						this.selectAll();
					});
				});

				this.setOnDeselected((event) -> {
					if (getText().length() == 0) {
						setText(selValue);
					}
					updateFromText();
					this.deselect();
				});

				this.setOnKeyPressed((event) -> {
					if (event.getKey() == GLFW.GLFW_KEY_ENTER && isEditing()) {
						if (getText().length() == 0) {
							setText(selValue);
							this.selectAll();
						}
						updateFromText();
					}
				});

				this.setOnTextChange((event) -> {
					String newText = ColorNumberField.this.getText();
					if (newText.length() > 3) {
						newText = newText.substring(0, 3);
						this.setText(newText);
					}

					if (newText.length() > 0) {
						try {
							double d = Double.parseDouble(newText);
							if ((int) d != d) {
								this.setText("" + (int) d);
							}
						} catch (Exception e) {
							this.setText("0");
						}
					}
				});
			}
		}

		private void apply() {
			setColor(((BackgroundSolid) colorS.getBackground()).getColor());
			cancelColor.set(color);
			tempColor(color);
		}

		public ColorPopup() {
			this.setPadding(new Insets(1));
			this.setBorderColor(Theme.current().getControlOutline());
			this.setBackgroundLegacy(Theme.current().getBackground());
			this.setAlignment(Pos.TOP_CENTER);
			this.setPrefSize(100, 100);

			VBox background = new VBox();
			background.setSpacing(4);
			background.setBackgroundLegacy(null);
			background.setPadding(new Insets(4));
			this.getChildren().add(background);

			int tWid = 48;

			colorS = new StackPane();
			colorS.setPrefWidth(tWid);
			colorS.setFillToParentHeight(true);
			colorS.setPadding(new Insets(1));
			colorS.setBorderColor(Color.DARK_GRAY);

			r = new ColorNumberField();
			r.setPrefWidth(tWid);
			r.setPrompt("R");
			r.setText("" + color.getRed());

			g = new ColorNumberField();
			g.setPrefWidth(tWid);
			g.setPrompt("G");
			g.setText("" + color.getGreen());

			b = new ColorNumberField();
			b.setPrefWidth(tWid);
			b.setPrompt("B");
			b.setText("" + color.getBlue());

			GridPane rgbPane = new GridPane();
			rgbPane.setBackgroundLegacy(null);
			rgbPane.setHgap(4);
			rgbPane.add(colorS, 0, 0);
			rgbPane.add(r, 1, 0);
			rgbPane.add(g, 2, 0);
			rgbPane.add(b, 3, 0);
			background.getChildren().add(rgbPane);

			StackPane temp = new StackPane();
			temp.setBackgroundLegacy(Color.GRAY);
			temp.setPrefHeight(100);
			temp.setFillToParentWidth(true);
			temp.setPadding(new Insets(1));
			background.getChildren().add(temp);

			{
				colorPane = new ColorPane();
				temp.getChildren().add(colorPane);

				int pickSize = 6;
				int hs = pickSize / 2;

				colorPick = new FloatingPane();
				colorPick.setMouseTransparent(true);
				colorPick.setPrefSize(pickSize, pickSize);
				colorPick.setPadding(new Insets(1));
				colorPick.setBorderColor(Color.DARK_GRAY);
				colorPane.getChildren().add(colorPick);

				Runnable forceUpdate = new Runnable() {
					@Override
					public void run() {
						// Bounds
						if (colorPick.getX() + hs > colorPane.getX() + colorPane.getWidth() - 1)
							colorPick.setAbsolutePosition(colorPane.getX() + colorPane.getWidth() - hs - 1,
									colorPick.getY());
						if (colorPick.getX() + hs < colorPane.getX())
							colorPick.setAbsolutePosition(colorPane.getX() - hs, colorPick.getY());
						if (colorPick.getY() + hs > colorPane.getY() + colorPane.getHeight())
							colorPick.setAbsolutePosition(colorPick.getX(),
									colorPane.getY() + colorPane.getHeight() - hs);
						if (colorPick.getY() + hs < colorPane.getY() + 1)
							colorPick.setAbsolutePosition(colorPick.getX(), colorPane.getY() - hs + 1);

						Vector2i offset = new Vector2i((int) (colorPick.getX() + hs - colorPane.getX()),
								(int) colorPane.getHeight() - (int) (colorPick.getY() + hs - colorPane.getY()));
						int index = (offset.y * (int) colorPane.getWidth()) + offset.x;
						int rgb = colorPane.colors[index];

						// Swap blue/red channel? Why do I need to do this?
						Color c = new Color((rgb >> 0) & 0xFF, (rgb >> 8) & 0xFF, (rgb >> 16) & 0xFF, 255);

						// Update the temp color
						colorS.setBackgroundLegacy(c);
						r.setText("" + c.getRed());
						g.setText("" + c.getGreen());
						b.setText("" + c.getBlue());
						colorPick.setBackgroundLegacy(c);
						tempColor(c);
					}
				};

				colorPane.setOnMouseDragged((event) -> {
					colorPick.setAbsolutePosition(event.getMouseX() - hs, event.getMouseY() - hs);
					forceUpdate.run();
				});

				colorPane.setOnMousePressed((event) -> {
					colorPick.setAbsolutePosition(event.getMouseX() - 3, event.getMouseY() - 3);
					this.cached_context.setSelected(colorPane);
					forceUpdate.run();
				});
			}

			StackPane temp2 = new StackPane();
			temp2.setBackgroundLegacy(null);
			temp2.setAlignment(Pos.CENTER_RIGHT);
			temp2.setFillToParentWidth(true);
			background.getChildren().add(temp2);

			HBox buttons = new HBox();
			buttons.setSpacing(4);
			buttons.setBackgroundLegacy(null);
			temp2.getChildren().add(buttons);

			Button cancel = new Button("Cancel");
			buttons.getChildren().add(cancel);

			cancel.setOnAction((event) -> {
				this.close();
			});

			Button apply = new Button("Apply");
			buttons.getChildren().add(apply);

			apply.setOnAction((event) -> {
				this.apply();
				this.close();
			});
		}

		@Override
		public void close() {
			super.close();
			try {
				setColor(cancelColor);

				if (buttonEvent != null) {
					EventHelper.fireEvent(buttonEvent, new ActionEvent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void show(Scene scene, double absoluteX, double absoluteY) {
			super.show(scene, absoluteX, absoluteY);
			cancelColor = new Color(color);
			update(color);

			// Ugly hack to force wait 3 frames before setting color
			LWJGUI.runLater(() -> {
				LWJGUI.runLater(() -> {
					LWJGUI.runLater(() -> {
						// ColorPicker.this.context.updateFromText();
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
			this.clip(context, 16);
			try (MemoryStack stack = stackPush()) {
				NVGPaint paint = NanoVG.nvgBoxGradient(vg, x + 2, y + 3, w - 2, h, 4, 8,
						Theme.current().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.callocStack(stack));
				NanoVG.nvgBeginPath(vg);
				NanoVG.nvgRect(vg, x - 16, y - 16, w + 32, h + 32);
				NanoVG.nvgFillPaint(vg, paint);
				NanoVG.nvgFill(vg);
			}

			super.render(context);
		}

	}
}
