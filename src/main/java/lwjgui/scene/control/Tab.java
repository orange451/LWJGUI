package lwjgui.scene.control;

import static org.lwjgl.system.MemoryStack.stackPush;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

import lwjgui.LWJGUIUtil;
import lwjgui.event.Event;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.FillableRegion;
import lwjgui.scene.Node;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.theme.Theme;

public class Tab {
	protected TabButton button;
	private Node content;
	protected Node lastSelected;
	protected TabPane tabPane;
	protected boolean userCanClose = true;
	protected String name = "";
	protected boolean dragging;
	
	protected EventHandler<Event> closeRequestEvent;
	
	public Tab(String name, boolean userCanClose) {
		button = new TabButton();
		content = new StackPane();
		this.setText(name);
		this.setUserCanClose(userCanClose);
	}
	
	public Tab(String name) {
		this(name, true);
	}
	
	public Tab() {
		this("Tab");
	}
	
	/**
	 * Sets the flag that controls whether or not the user can close this tab.<br>
	 * A tab that cannot be closed will not have a close graphic.
	 * @param canClose
	 */
	public void setUserCanClose(boolean canClose) {
		this.userCanClose = canClose;
		this.button.refresh();
	}
	
	/**
	 * Set the content node for this tab. See {@link #getContent()}.
	 * @param node
	 */
	public void setContent(Node node) {
		this.content = node;
	}
	
	/**
	 * Set the text for this tab.
	 * @param text
	 */
	public void setText(String text) {
		this.name = text;
		button.label.setText(text);
	}

	/**
	 * Returns the content node for this tab. The content node is drawn when the tab 
	 * is currently selected in a tab pane.
	 * @return
	 */
	public Node getContent() {
		return content;
	}
	
	public void setOnCloseRequest(EventHandler<Event> e) {
		closeRequestEvent = e;
	}
	
	@Override
	public String toString() {
		return "Tab:(" + name + ")";
	}

	class TabButton extends FillableRegion {
		protected HBox internal;
		protected Label label;
		protected boolean pressed;
		protected Label x;
		
		protected TabButton() {
			refresh();
		}

		@Override
		public String getElementType() {
			return "tab";
		}
		
		protected void refresh() {
			this.children.clear();
			
			this.internal = new HBox() {
				{
					this.setOnMousePressedInternal((event)->{
						TabButton.this.onMousePressed(event);
						window.getContext().setSelected(TabButton.this);
					});
					
					this.setOnMouseReleasedInternal((event)->{
						TabButton.this.onMouseReleased(event);
					});

					this.setOnMouseDraggedInternal((event)->{
						TabButton.this.mouseDraggedEventInternal.handle(event);
					});
					
					this.setOnMouseDraggedEndInternal((event)->{
						TabButton.this.mouseDraggedEndEventInternal.handle(event);
					});
				}
			};
			this.internal.setBackgroundLegacy(null);
			this.internal.setSpacing(4);
			this.children.add(internal);
			
			this.label = new Label(name);
			this.label.setMouseTransparent(true);
			this.label.setFontSize(16);
			this.label.setFont(Font.SEGOE);
			this.internal.getChildren().add(label);
			
			this.x = new Label(new String(Character.toChars(0x2716)));
			this.x.setMinWidth(x.getTextWidth());
			this.x.setFontSize(16);
			if ( userCanClose ) {
				this.internal.getChildren().add(x);
			}
			
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
			
			//this.setBackground(null);
			this.setPrefSize(4, 4);
			this.setPadding(new Insets(3,6,3,6));
			
			// On Press
			this.setOnMousePressedInternal(event -> {
				if ( tabPane != null ) {
					tabPane.select(Tab.this);
				}
				event.consume();
				window.getContext().setSelected(Tab.this.button);
			});
			
			// On Release
			this.setOnMouseReleasedInternal(event -> {
				if ( tabPane != null ) {
					tabPane.select(Tab.this);
				}
				event.consume();
			});
			
			// On Drag
			this.setOnMouseDraggedInternal(event -> {
				dragging = true;
				event.consume();
			});
			
			// On Drag End
			this.setOnMouseDraggedEndInternal(event -> {
				dragging = false;
				event.consume();
			});
		}

		protected boolean isPressed() {
			Context context = window.getContext();
			if ( context.isSelected(x) || context.isHovered(x) )
				return false;
			
			if ( tabPane.getSelected().equals(Tab.this) )
				return false;
			
			return this.isDescendentHovered() && window.getMouseHandler().isButtonPressed(0);
		}
		
		protected boolean isHovered() {
			Context context = window.getContext();
			
			if ( context.isSelected(x) )
				return false;
			
			return this.isDescendentHovered();
		}
		
		private void buttonMask(long vg, float x, float y, float w, float h, float rOffset) {
			int corner = 2;
			NanoVG.nvgRoundedRectVarying(vg, x, y, w, h, (float)corner+rOffset, (float)corner+rOffset, 0, 0);
		}
		
		@Override
		public void render(Context context) {
			if ( !isVisible() )
				return;
			
			long vg = context.getNVG();
			int x = (int) this.getX();
			int y = (int) this.getY();
			int w = (int) this.getWidth();
			int h = (int) this.getHeight();
			
			boolean pp = context.getLastPressed() != null && pressed&&context.getLastPressed().isDescendentOf(this);
			
			// Background
			NanoVG.nvgBeginPath(vg);
			buttonMask(vg, x,y,w,h,1);
			NanoVG.nvgFillColor(vg, (pp?Theme.current().getSelection():Theme.current().getControlOutline()).getNVG());
			NanoVG.nvgFill(vg);
			
			// Draw main background
			Color c1 = isPressed()?Theme.current().getControlHover():(pressed?Theme.current().getControl():(isHovered()?Theme.current().getControlHover():Theme.current().getControl()));
			Color c2 = pressed?Theme.current().getPane():Theme.current().getControlAlt();
			try (MemoryStack stack = stackPush()) {
				NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y, x, y+h, c1.getNVG(), c2.getNVG(), NVGPaint.callocStack(stack));
				NanoVG.nvgBeginPath(vg);
				buttonMask(vg, x+1,y+1,w-2,h-1,0);
				NanoVG.nvgFillPaint(vg, bg);
				NanoVG.nvgFill(vg);
			}
			
			// Draw dark line show this tab button is not selected
			if ( !pressed ) {
				LWJGUIUtil.fillRect(context, getX(), getY()+getHeight()-1, getWidth(), 1, Theme.current().getControlOutline());
			}
			
			// Change color of X button
			boolean xpressed = context.isHovered(this.x) && window.getMouseHandler().isButtonPressed(0);
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
				NanoVG.nvgClosePath(vg);
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
