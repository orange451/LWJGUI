package lwjgui.scene.control;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.event.ActionEvent;
import lwjgui.event.EventHelper;
import lwjgui.font.Font;
import lwjgui.font.FontStyle;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.shape.Rectangle;
import lwjgui.theme.Theme;

public class ToggleSwitch extends Labeled implements Toggle {
	protected boolean selected;
	
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
		
		this.setText(name);
		this.setGraphic(new ToggleSwitchButton());
		this.setSelected(selected);
		this.setContentDisplay(ContentDisplay.RIGHT);
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
	
	class ToggleSwitchButton extends Pane {
		protected Pane track;
		protected Button button;
		
		public ToggleSwitchButton() {
			this.setPadding(Insets.EMPTY);
			this.setAlignment(Pos.CENTER_RIGHT);
			
			this.track = new Pane() {
				{
					this.setAlignment(Pos.CENTER_LEFT);
					Rectangle t = new Rectangle(getWidth(), getHeight(), Theme.current().getControl()) {
						@Override
						public void resize() {
							this.setPrefSize(track.getWidth(), track.getHeight());
							super.resize();
							
							if ( isSelected() ) {
								this.setFill(Theme.current().getSelection());
								this.setStrokeFill(Theme.current().getSelectionAlt());
							} else {
								this.setFill(Theme.current().getControlAlt());
								this.setStrokeFill(Theme.current().getSelectionPassive());
							}
						}
					};
					t.setCornerRadius(4);
					t.setMouseTransparent(true);
					this.getChildren().add(t);
					
					this.setOnMouseReleasedInternal((event)->{
						//if ( ToggleSwitch.this.isDisabled() )
							//return;
						
						EventHelper.fireEvent(button.buttonInternalEvent, new ActionEvent());
					});
				}
				
				@Override
				public void render(Context context) {
					super.render(context);
					
					if ( isSelected() ) {
						double ox = (track.getWidth()-button.getWidth())/2;
						double oy = track.getHeight()/2;
						LWJGUIUtil.drawText("ON", Font.SANS, FontStyle.BOLD, 18, Theme.current().getBackground(), getX()+ox, getY()+oy, Pos.CENTER);
					} else {
						double ox = button.getWidth()+(track.getWidth()-button.getWidth())/2;
						double oy = track.getHeight()/2;
						LWJGUIUtil.drawText("OFF", Font.SANS, FontStyle.BOLD, 18, Theme.current().getText(), getX()+ox, getY()+oy, Pos.CENTER);
					}
				}
			};
			this.track.setBackground(null);
			this.track.setFillToParentHeight(true);
			this.track.setFillToParentWidth(true);
			this.track.setAlignment(Pos.CENTER_RIGHT);
			this.getChildren().add(track);
			
			this.button = new Button("");
			this.button.setOnActionInternal((event)->{
				setSelected(!isSelected());
				
				LWJGUI.runLater(()->{
					cached_context.setSelected(button);
				});
			});
			this.track.getChildren().add(button);
		}
		
		@Override
		public void position(Node parent) {
			this.setPrefWidth(ToggleSwitch.this.getPrefWidth());
			button.setPrefWidth(this.getPrefWidth()/2);
			super.position(parent);
			
			if ( isSelected() ) {
				track.setAlignment(Pos.TOP_RIGHT);
			} else {
				track.setAlignment(Pos.TOP_LEFT);
			}
		}
		
		@Override
		public void render(Context context) {
			this.position(this.getParent());
			
			super.render(context);
			
		}
	}
}
