package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.event.Event;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class Tab {
	protected TabButton button;
	private Node content;
	protected Node lastSelected;
	protected TabPane tabPane;
	
	protected EventHandler<Event> closeRequestEvent;
	
	public Tab(String name) {
		button = new TabButton(name);
		content = new StackPane();
	}
	
	public Tab() {
		this("");
	}
	
	public void setContent(Node node) {
		this.content = node;
	}
	
	public void setText(String text) {
		button.label.setText(text);
	}

	public Node getContent() {
		return content;
	}
	
	public void setOnCloseRequest(EventHandler<Event> e) {
		closeRequestEvent = e;
	}

	class TabButton extends FillableRegion {
		protected HBox internal;
		protected Label label;
		protected boolean pressed;
		protected Label x;
		
		protected TabButton(String name) {
			this.internal = new HBox();
			this.internal.setBackground(null);
			this.internal.setSpacing(4);
			this.children.add(internal);
			
			this.label = new Label(name);
			this.label.setMouseTransparent(true);
			this.label.setFontSize(16);
			this.internal.getChildren().add(label);

			this.x = new Label("\u2715");
			this.x.setFont(Font.DINGBAT);
			this.x.setMinWidth(x.getTextWidth());
			this.x.setFontSize(16);
			this.internal.getChildren().add(x);
			
			this.x.setOnMouseReleased(event -> {
				boolean close = true;
				
				if ( closeRequestEvent != null ) {
					// If close request is consumed, don't close
					if ( EventHelper.fireEvent(closeRequestEvent, new Event()) ) {
						close = false;
					}
				}
				
				if ( close ) {
					tabPane.getTabs().remove(Tab.this);
				}
			});
			
			this.setBackground(null);
			this.setPrefSize(4, 4);
			this.setPadding(new Insets(3,6,3,6));
		}
		
		protected boolean isPressed() {
			if ( cached_context == null )
				return false;
			
			if ( cached_context.isSelected(x) || cached_context.isHovered(x) )
				return false;
			
			if ( tabPane.getSelected().equals(Tab.this) )
				return false;
			
			return (this.isDescendentHovered() && GLFW.glfwGetMouseButton(cached_context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
		}
		
		protected boolean isHovered() {
			if ( cached_context == null )
				return false;
			
			if ( cached_context.isSelected(x) )
				return false;
			
			return this.isDescendentHovered();
		}
		
		private void buttonMask(long vg, float x, float y, float w, float h, float rOffset) {
			int corner = 2;
			NanoVG.nvgRoundedRectVarying(vg, x, y, w, h, (float)corner+rOffset, (float)corner+rOffset, 0, 0);
		}
		
		@Override
		public void render(Context context) {
			long vg = context.getNVG();
			int x = (int) this.getX();
			int y = (int) this.getY();
			int w = (int) this.getWidth();
			int h = (int) this.getHeight();

			internal.setOnMousePressed(this.mousePressedEvent);
			internal.setOnMouseReleased(this.mouseReleasedEvent);
			internal.setOnMouseDragged(this.mouseDraggedEvent);
			
			boolean pp = context.getLastPressed() != null && pressed&&context.getLastPressed().isDescendentOf(this);
			
			// Background
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x,y,w,h,1);
			NanoVG.nvgFillColor(vg, (pp?Theme.current().getSelection():Theme.current().getControlOutline()).getNVG());
			NanoVG.nvgFill(vg);
			
			// Draw main background
			Color c1 = isPressed()?Theme.current().getControlOutline():(pressed?Theme.current().getControl():(isHovered()?Theme.current().getControlHover():Theme.current().getControl()));
			Color c2 = pressed?Theme.current().getPane():Theme.current().getControlAlt();
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y, x, y+h, c1.getNVG(), c2.getNVG(), NVGPaint.calloc());
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x+1,y+1,w-2,h-1,0);
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
			
			// Draw dark line show this tab button is not selected
			if ( !pressed ) {
				LWJGUIUtil.fillRect(context, getX(), getY()+getHeight()-1, getWidth(), 1, Theme.current().getControlOutline());
			}
			
			// Change color of X button
			boolean xpressed = context.isHovered(this.x) && GLFW.glfwGetMouseButton(context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
			Color c = Theme.current().getControlOutline();
			if ( context.isHovered(this.x) )
				c = Theme.current().getText();
			if ( this.isPressed() || xpressed )
				c = Theme.current().getControlOutline();
			this.x.setTextFill(c);
			
			// internal selection graphic
			if ( context.getLastPressed() != null && context.getLastPressed().isDescendentOf(this) && pressed ) {
				Color sel = Theme.current().getSelection();
				Color col = sel.alpha(0.25f);
				
				NanoVG.nvgBeginPath(vg);
				float inset = 1.25f;
				buttonMask(vg, x+inset,y+inset,w-inset*2,h, 0);
				NanoVG.nvgStrokeColor(vg, col.getNVG());
				NanoVG.nvgStrokeWidth(vg, inset*1.25f);
				NanoVG.nvgStroke(vg);
				NanoVG.nnvgClosePath(vg);
			}
			
			for (int i = 0; i < children.size(); i++) {
				children.get(i).render(context);
			}
		}

		@Override
		public boolean isResizeable() {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
