package lwjgui.scene.control;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.event.ActionEvent;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.paint.ColorNameLookup;
import lwjgui.scene.Context;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class ColorPicker extends ButtonBase {
	private Color color;
	private PopupWindow context;
	
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
		this.color = color;
		this.setText(ColorNameLookup.matchName(color));
		
		StackPane g = new StackPane();
		g.setPrefSize(16, 16);
		g.setBackground(color);
		this.setGraphic(g);
	}
	
	public Color getColor() {
		return this.color;
	}
	
	class ColorPopup extends PopupWindow {
		
		public ColorPopup() {
			this.setPadding(new Insets(1));
			this.setPaddingColor(Theme.current().getControlOutline());
			this.setBackground(Theme.current().getBackground());
			
			StackPane t = new StackPane();
			t.setAlignment(Pos.TOP_CENTER);
			t.setBackground(null);
			t.setPadding(new Insets(8));
			this.getChildren().add(t);
			
			Color[] baseColors = new Color[] {
					Color.WHITE,
					Color.RED,
					Color.ORANGE,
					Color.YELLOW,
					Color.GREEN,
					Color.CYAN,
					Color.AQUA,
					Color.BLUE,
					Color.VIOLET
			};
			
			int stride = 10;
			Color[] pallette = new Color[baseColors.length*stride];
			for (int i = 0; i < baseColors.length; i++) {
				Color base = baseColors[i];
				for (int j = 0; j < stride; j++) {
					int index = i*stride+j;
					double ratio = 1.0-((j)/(double)stride);

					double rr = (base.getRed()/255d) * ratio;
					double gg = (base.getGreen()/255d) * ratio;
					double bb = (base.getBlue()/255d) * ratio;
					
					Color c = new Color((float)rr, (float)gg, (float)bb);
					pallette[index] = c;
				}
			}
			
			GridPane grid = new GridPane();
			grid.setHgap(1);
			grid.setVgap(1);
			for (int i = 0; i < stride; i++) {
				for (int j = 0; j < baseColors.length; j++) {
					StackPane col = new StackPane();
					col.setPadding(new Insets(1));
					col.setMinSize(16, 16);
					grid.add(col, i, j);
					
					int palletteIndex = j*stride+i;
					if ( palletteIndex >= pallette.length )
						palletteIndex = 0;
					

					col.setBackground(pallette[palletteIndex]);
					
					col.setOnMouseClicked((event) -> {
						context.close();
						setColor(col.getBackground());
						
						if ( buttonEvent != null ) {
							EventHelper.fireEvent(buttonEvent, new ActionEvent());
						}
					});
				}
			}
			
			t.getChildren().add(grid);
			
			this.setPrefSize(100, 100);
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
