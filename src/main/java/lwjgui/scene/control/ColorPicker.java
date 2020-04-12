package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.joml.Math;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.paint.ColorNameLookup;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;
import lwjgui.scene.shape.Circle;
import lwjgui.style.BackgroundLinearGradient;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.theme.Theme;

public class ColorPicker extends ButtonBase {
	private Color color;
	private Color cancelColor;
	private ColorPopup2 context;
	private boolean supportsAlpha;
	
	protected EventHandler<ActionEvent> colorUpdateEvent;
	protected EventHandler<ActionEvent> colorApplyEvent;

	public ColorPicker(Color color) {
		super(color.toString());

		this.setSupportsAlpha(true);

		this.setOnMouseReleased((event) -> {
			if (this.isDisabled())
				return;

			context = new ColorPopup2();
			context.setAutoHide(false);

			context.position(getScene());

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
	
	public void setOnColorUpdate(EventHandler<ActionEvent> event) {
		this.colorUpdateEvent = event;
	}
	
	public EventHandler<ActionEvent> getOnColorUpdate() {
		return this.colorUpdateEvent;
	}
	
	public void setOnColorApply(EventHandler<ActionEvent> event) {
		this.colorApplyEvent = event;
	}
	
	public EventHandler<ActionEvent> getOnColorApply() {
		return this.colorApplyEvent;
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

			if (colorUpdateEvent != null)
				EventHelper.fireEvent(colorUpdateEvent, new ActionEvent());

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

				if (colorUpdateEvent != null) {
					EventHelper.fireEvent(colorUpdateEvent, new ActionEvent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void apply() {
			setColor(internalCircle.getFill());
			cancelColor.set(getColor());
			
			try {

				if (colorApplyEvent != null) {
					EventHelper.fireEvent(colorApplyEvent, new ActionEvent());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private float tween(float a, float b, float ratio) {
			return a + (b-a)*ratio;
		}
		
		private void dragColorPicker(double mouseX, double mouseY, double width, double height) {

			float mx = (float) (mouseX-getX());
			float my = (float) (mouseY-getY());
			
			float hue = this.internalHSV[0];
			float s = (float) (mx/width);
			float b = (float) (my/height);
			b = 1.0f - b; // flip brightness

			this.setSB(s, b);
			this.setHue(hue);
			this.setSB(s, b);
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
			
			colorPane.setOnMousePressed((event)->{
				this.window.getContext().setSelected(colorPane);
				dragColorPicker(event.getMouseX(), event.getMouseY(), colorPane.getWidth(), colorPane.getHeight());
			});
			
			colorPane.setOnMouseDragged((event)->{
				dragColorPicker(event.getMouseX(), event.getMouseY(), colorPane.getWidth(), colorPane.getHeight());
			});
			
			// Saturation/Brightness modifier
			picker.forceSize(12, 12);
			picker.setBorderRadii(6);
			picker.setBorderWidth(1);
			picker.setBorderStyle(BorderStyle.SOLID);
			picker.setBorderColor(Color.WHITE);
			picker.getBoxShadowList().add(new BoxShadow(0,1,2,1f,Theme.current().getShadow()));
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
			setAlpha(this.internalAlpha);
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
					Color.CYAN, Color.LIME, Color.YELLOW, Color.RED);

			b.render(context, x, y, w, h, new float[] { 3, 3, 3, 3 });
		}
	}
}
