package lwjgui.scene.control;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;

public class ToggleSwitch extends Labeled implements Toggle {
	protected boolean selected;
	
	protected EventHandler<ActionEvent> toggleEvent;
	
	public ToggleSwitch() {
		this("");
	}
	
	public ToggleSwitch( String name ) {
		this(name, false );
	}
	
	public ToggleSwitch( boolean selected ) {
		this("", selected);
	}
	
	public ToggleSwitch( String name, boolean selected ) {
		this.setPrefWidth(80);
		this.setPrefHeight(18);
		
		this.setText(name);
		this.setFontSize(16);
		this.setGraphic(new ToggleSwitchButton());
		this.setSelected(selected);
		this.setContentDisplay(ContentDisplay.RIGHT);
	}
	
	public void setOnActionEvent(EventHandler<ActionEvent> event) {
		this.toggleEvent = event;
	}
	
	/**
	 * 
	 * @return Returns whether or not the button is selected.
	 */
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean b) {
		selected = b;
	}
	
	public String getElementType() {
		return "toggle";
	}
	
	class ToggleSwitchButton extends Pane {
		protected Pane track;
		protected Button button;
		
		public ToggleSwitchButton() {
			this.setAlignment(Pos.CENTER_RIGHT);
			
			this.track = new Pane() {
				{
					this.setAlignment(Pos.CENTER_LEFT);
					Rectangle t = new Rectangle(1, 1, Theme.current().getControl()) {
						@Override
						public void resize() {
							super.resize();
							this.setPrefSize(ToggleSwitch.this.getPrefWidth(), ToggleSwitch.this.getPrefHeight());
							
							if ( isSelected() && !button.isDisabled() ) {
								this.setFill(Theme.current().getSelection());
								this.setStrokeFill(Theme.current().getSelectionAlt());
							} else {
								this.setFill(Theme.current().getControlAlt());
								this.setStrokeFill(Theme.current().getSelectionPassive());
							}
						}
					};
					t.setCornerRadius(5);
					t.setMouseTransparent(true);
					this.getChildren().add(t);
					
					this.setOnMouseReleasedInternal((event)->{
						//if ( ToggleSwitch.this.isDisabled() )
							//return;
						
						EventHelper.fireEvent(button.buttonInternalEvent, new ActionEvent());
					});
				}

				@Override
				public String getElementType() {
					return "toggletrack";
				}
				
				@Override
				public void render(Context context) {
					if ( !isVisible() )
						return;
					
					super.render(context);
					
					if ( ToggleSwitch.this.getPrefWidth() > 50 ) {
						Color color = Theme.current().getShadow();
						if ( !button.isDisabled() ) {
							if ( isSelected() ) {
								color = Theme.current().getBackground();
							} else {
								color = Theme.current().getText();
							}
						}
						
						if ( isSelected() ) {
							double ox = (track.getWidth()-button.getWidth())/2;
							double oy = track.getHeight()/2;
							LWJGUIUtil.drawText(window.getContext(), "ON", Font.SANS, FontStyle.BOLD, 16, color, getX()+ox, getY()+oy, Pos.CENTER);
						} else {
							double ox = button.getWidth()+(track.getWidth()-button.getWidth())/2;
							double oy = track.getHeight()/2;
							LWJGUIUtil.drawText(window.getContext(), "OFF", Font.SANS, FontStyle.BOLD, 16, color, getX()+ox, getY()+oy, Pos.CENTER);
						}
					}
				}
			};
			this.track.setBackgroundLegacy(null);
			this.track.setFillToParentHeight(true);
			this.track.setFillToParentWidth(true);
			this.track.setAlignment(Pos.CENTER_RIGHT);
			this.getChildren().add(track);
			
			this.button = new Button("");
			this.button.setPrefHeight(ToggleSwitch.this.getPrefHeight());
			this.button.setMinHeight(ToggleSwitch.this.getPrefHeight());
			this.button.setPadding(Insets.EMPTY);
			this.button.setOnActionInternal((event)->{
				setSelected(!isSelected());
				
				if ( toggleEvent != null )
					EventHelper.fireEvent(toggleEvent, new ActionEvent());
				
				window.getContext().setSelected(button);
			});
			this.track.getChildren().add(button);
		}

		@Override
		public String getElementType() {
			return "toggle";
		}
		
		@Override
		public void position(Node parent) {
			this.setPrefWidth(ToggleSwitch.this.getPrefWidth());
			this.button.setPrefWidth(this.getPrefWidth()/2);
			
			super.position(parent);
			
			if ( isSelected() ) {
				track.setAlignment(Pos.TOP_RIGHT);
			} else {
				track.setAlignment(Pos.TOP_LEFT);
			}
			
			button.setDisabled(ToggleSwitch.this.isDisabled());
		}
		
		@Override
		public void render(Context context) {
			if ( !isVisible() )
				return;
			
			this.position(this.getParent());
			
			super.render(context);
			
		}
	}
}
