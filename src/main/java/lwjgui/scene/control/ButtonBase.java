package lwjgui.scene.control;

import java.awt.Point;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.MouseEvent;
import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.style.Background;
import lwjgui.style.BackgroundLinearGradient;
import lwjgui.style.BackgroundSolid;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;
import lwjgui.style.ColorStop;
import lwjgui.style.StyleBackground;
import lwjgui.style.StyleBorder;
import lwjgui.style.StyleBoxShadow;
import lwjgui.theme.Theme;

public abstract class ButtonBase extends Labeled implements StyleBorder,StyleBackground,StyleBoxShadow {
	protected EventHandler<ActionEvent> buttonEvent;
	protected EventHandler<ActionEvent> buttonInternalEvent;
	
	protected double textOffset;
	
	private Background background;
	private Color borderColor;
	private float[] borderRadii;
	private float borderWidth;
	private BorderStyle borderStyle;
	private ObservableList<BoxShadow> boxShadows = new ObservableList<>();
	
	public ButtonBase(String name) {
		super();
		this.setText(name);
		
		this.setMinSize(12, 24);
		this.setPadding(new Insets(4,6,4,6));
		
		this.setBorderRadii(2.5f);
		this.setBorderStyle(BorderStyle.SOLID);
		this.setBorderWidth(1);
		this.setBorderColor(Theme.current().getControlOutline());
		
		this.setText(name);
		this.setFontSize(16);
		
		// Fire the click event when we're clicked
		this.setOnMouseReleasedInternal( new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if ( isDisabled() )
					return;
				
				if ( event.button == 0 ) {
					EventHelper.fireEvent(buttonInternalEvent, new ActionEvent());
					EventHelper.fireEvent(buttonEvent, new ActionEvent());
				}
			}
		});
		
		// If the button is selected, and enter is pressed. Fire the click event
		this.setOnKeyPressedInternal( (event) -> {
			if ( event.getKey() == GLFW.GLFW_KEY_ENTER ) {
				if ( this.cached_context.isSelected(this) ) {
					EventHelper.fireEvent(buttonInternalEvent, new ActionEvent());
					EventHelper.fireEvent(buttonEvent, new ActionEvent());
				}
			}
		});
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */
	public void setBackgroundLegacy(Color color) {
		setBackground( new BackgroundSolid(color) );
	}
	
	/**
	 * Set the background color of this node.
	 * <br>
	 * If set to null, then no background will draw.
	 * @param color
	 */	
	public void setBackground(Background color) {
		this.background = color;
	}
	
	/**
	 * Get the current background color of this node.
	 * @return
	 */
	public Background getBackground() {
		return this.background;
	}
	
	@Override
	public void setBorderStyle(BorderStyle style) {
		this.borderStyle = style;
	}

	@Override
	public BorderStyle getBorderStyle() {
		return this.borderStyle;
	}

	@Override
	public float[] getBorderRadii() {
		return borderRadii;
	}

	@Override
	public void setBorderRadii(float radius) {
		this.setBorderRadii(radius, radius, radius, radius);
	}

	@Override
	public void setBorderRadii(float cornerTopLeft, float cornerTopRight, float cornerBottomRight, float cornerBottomLeft) {
		this.borderRadii = new float[] {cornerTopLeft, cornerTopRight, cornerBottomRight, cornerBottomLeft};
	}

	@Override
	public void setBorderColor(Color color) {
		this.borderColor = color;
	}

	@Override
	public Color getBorderColor() {
		return this.borderColor;
	}

	@Override
	public void setBorderWidth(float width) {
		this.borderWidth = width;
	}

	@Override
	public float getBorderWidth() {
		return this.borderWidth;
	}

	@Override
	public ObservableList<BoxShadow> getBoxShadowList() {
		return this.boxShadows;
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}
	
	/*
	@Override
	public Vector2d getAvailableSize() {
		return new Vector2d(getMaxWidth(),getMaxHeight());
	}*/
	
	protected boolean isPressed() {
		if ( cached_context == null )
			return false;
		
		if ( isDisabled() )
			return false;
		
		return cached_context.isHovered(this) && GLFW.glfwGetMouseButton(cached_context.getWindowHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
	}

	@Override
	public void render(Context context) {
		clip(context, 0);
		
		// SETUP BUTTON COLOR
		Color buttonColor = isPressed()?Theme.current().getControlOutline():((context.isHovered(this)&&!isDisabled())?Theme.current().getControlHover():Theme.current().getControl());
		this.setBackground(new BackgroundLinearGradient(90, new ColorStop(buttonColor, 0), new ColorStop(Theme.current().getControlOutline(), 3)));
		
		// SETUP BUTTON OUTLINE
		Color outlineColor = (context.isSelected(this)&&context.isFocused()&&!isDisabled())?Theme.current().getSelection():Theme.current().getControlOutline();
		this.setBorderColor(outlineColor);
		
		// Weird inset outline???
		this.getBoxShadowList().clear();
		if ( !this.isDisabled() ) {
			Color c2 = Theme.current().getControlAlt();
			if ( context.isHovered(this) )
				c2 = Theme.current().getControlHover().darker();
			if ( isPressed() )
				c2 = buttonColor.darker();
			this.getBoxShadowList().add(new BoxShadow(0, 0, 2, 1, c2, true));
		}
		
		// SETUP SELECTION GRAPHIC
		if ( context.isSelected(this) && context.isFocused() ) {
			Color sel = Theme.current().getSelection();
			if ( isDisabled() )
				sel = Theme.current().getSelectionPassive();

			this.getBoxShadowList().add(new BoxShadow(0, 0, 3, 1, sel));
			this.getBoxShadowList().add(new BoxShadow(0, 0, 1.5f, 2, sel.alpha(0.2f), true));
		}
		
		
		// Draw drop shadows
		for (int i = 0; i < getBoxShadowList().size(); i++) {
			BoxShadow shadow = getBoxShadowList().get(i);
			if ( shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, this.getBorderRadii(), (int) getX(), (int) getY(), (int)getWidth(), (int)getHeight());
		}
		
		// Draw border
		if ( this.getBorderStyle() != BorderStyle.NONE && this.getBorderWidth() > 0 && this.getBorderColor() != null ) {
			LWJGUIUtil.drawBorder(context, getX(), getY(), getWidth(), getHeight(), this.getBorderWidth(), this.getBackground(), this.getBorderColor(), this.getBorderRadii() );
		}
		
		// Draw background
		if ( getBackground() != null ) {
			getBackground().render(context, getX(), getY(), getWidth(), getHeight(), getBorderRadii());
		}
		
		// Draw inset shadows
		for (int i = 0; i < getBoxShadowList().size(); i++) {
			BoxShadow shadow = getBoxShadowList().get(i);
			if ( !shadow.isInset() )
				continue;
			LWJGUIUtil.drawBoxShadow(context, shadow, this.getBorderRadii(), (int) getX(), (int) getY(), (int)getWidth(), (int)getHeight());
		}
		
		// Text color?
		if ( isDisabled() ) {
			this.setTextFill(Theme.current().getShadow());
		} else {
			this.setTextFill(Theme.current().getText());
		}
		
		// Draw children
		this.offset(textOffset, 0);
		super.render(context);
		this.offset(-textOffset, 0);
	}
	
	protected Point getDrawSize() {
		return new Point((int)getWidth(), (int)getHeight());
	}

	public void setOnAction(EventHandler<ActionEvent> event) {
		this.buttonEvent = event;
	}

	protected void setOnActionInternal(EventHandler<ActionEvent> event) {
		this.buttonInternalEvent = event;
	}

}
