package lwjgui.scene.control;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.Color;
import lwjgui.LWJGUIUtil;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Font;
import lwjgui.scene.layout.FontStyle;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class Tab {
	protected TabButton button;
	private Node content;
	protected Node lastSelected;
	protected TabPane tabPane;
	
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

	class TabButton extends StackPane {
		protected HBox internal;
		protected Label label;
		protected boolean pressed;
		protected Label x;
		
		protected TabButton(String name) {
			this.internal = new HBox();
			this.internal.setBackground(null);
			this.internal.setSpacing(4);
			this.getChildren().add(internal);
			
			this.label = new Label(name);
			this.label.setMouseTransparent(true);
			this.label.setFontSize(16);
			this.internal.getChildren().add(label);
			
			this.x = new Label("\u2715");
			this.x.setFontSize(16);
			this.internal.getChildren().add(x);
			
			this.x.setMouseReleasedEvent(new MouseEvent() {
				@Override
				public void onEvent(int button) {
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
			
			return (this.isDecendentHovered() && GLFW.glfwGetMouseButton(cached_context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS);
		}
		
		protected boolean isHovered() {
			if ( cached_context == null )
				return false;
			
			if ( cached_context.isSelected(x) )
				return false;
			
			return this.isDecendentHovered();
		}
		
		private void buttonMask(long vg, float x, float y, float w, float h, float rOffset) {
			int corner = 3;
			NanoVG.nvgRoundedRectVarying(vg, x, y, w, h, (float)corner+rOffset, (float)corner+rOffset, 0, 0);
		}
		
		@Override
		public void render(Context context) {
			long vg = context.getNVG();
			int x = (int) this.getAbsoluteX();
			int y = (int) this.getAbsoluteY();
			int w = (int) this.getWidth();
			int h = (int) this.getHeight();
			
			internal.setMouseReleasedEvent(this.mouseReleasedEvent);
			
			// Background
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x,y,w,h,1);
			NanoVG.nvgFillColor(vg, Theme.currentTheme().getControlOutline().getNVG());
			NanoVG.nvgFill(vg);
			
			// Draw main background
			Color c1 = isPressed()?Theme.currentTheme().getControlOutline():(pressed?Theme.currentTheme().getControl():(isHovered()?Theme.currentTheme().getControlHover():Theme.currentTheme().getControl()));
			Color c2 = pressed?Theme.currentTheme().getPane():Theme.currentTheme().getControlOutline();
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y, x, y+h*3, c1.getNVG(), c2.getNVG(), NVGPaint.calloc());
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x+1,y+1,w-2,h-1,0);
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
			
			// Draw dark line show this tab button is not selected
			if ( !pressed ) {
				LWJGUIUtil.fillRect(context, getAbsoluteX(), getAbsoluteY()+getHeight()-1, getWidth(), 1, Theme.currentTheme().getControlOutline());
			}
			
			// Change color of X button
			boolean xpressed = context.isHovered(this.x) && GLFW.glfwGetMouseButton(context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
			Color c = Theme.currentTheme().getSelectionPassive();
			if ( context.isHovered(this.x) )
				c = Theme.currentTheme().getControlOutline();
			if ( this.isPressed() || xpressed )
				c = Theme.currentTheme().getText();
			this.x.setTextFill(c);
			
			// Render internal stuff
			super.render(context);
		}
	}
}
