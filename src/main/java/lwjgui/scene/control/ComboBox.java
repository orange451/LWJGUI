package lwjgui.scene.control;

import lwjgui.collections.ObservableList;
import lwjgui.event.ActionEvent;
import lwjgui.event.ElementCallback;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.font.Font;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;

public class ComboBox<T> extends CombinedButton {
	private T value;
	private ObservableList<T> items;

	private Button main;
	private Button arrow;
	private ContextMenu context;
	
	private EventHandler<ActionEvent> actionEvent;
	
	public ComboBox() {
		this(null);
	}
	
	public ComboBox(T defaultValue) {
		items = new ObservableList<T>();
		
		main = new Button("") {
			@Override
			protected void resize() {
				super.resize();
				
				double useWid = ComboBox.this.isFillToParentWidth()?ComboBox.this.getWidth():ComboBox.this.getPrefWidth();
				double minWidth = Math.max(this.size.x, useWid-(internal.getSpacing()+arrow.getWidth()));
				this.size.x = minWidth;

			}
		};
		main.setAlignment(Pos.CENTER_LEFT);
		this.buttons.add(main);
		
		arrow = new Button("\u25bc") {
			@Override
			public boolean isPressed() {
				return super.isPressed() || context.isOpen();
			}
		};
		arrow.setFont(Font.DINGBAT);
		arrow.setFontSize(14);
		arrow.setPadding(new Insets(5.5,5,2,6));
		this.buttons.add(arrow);
		
		setValue(defaultValue);
		
		context = new ContextMenu();
		context.setAutoHide(false);
		items.setAddCallback(new ElementCallback<T>() {
			@Override
			public void onEvent(T object) {
				context.getItems().clear();
				
				for (int i = 0; i < items.size(); i++) {
					T val = items.get(i);
					MenuItem item = new MenuItem(val.toString());
					context.getItems().add(item);
					
					item.setOnAction((event)->{
						setValue(val);
					});
				}
			}
		});
		
		EventHandler<ActionEvent> onClick = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if ( context.isOpen() )
					return;
				
				context.setStylesheet(ComboBox.this.getStylesheet());
				context.setMinWidth(getWidth());
				context.show(getScene(), getX(), getY()+getHeight()-1);
			}	
		};
		
		arrow.setOnAction(onClick);
		main.setOnAction(onClick);
	}


	@Override
	public String getElementType() {
		return "combobox";
	}
	
	public void setValue(T string) {
		this.value = string;
		if ( string != null )
			this.main.setText(string.toString());
		
		if ( actionEvent != null ) {
			EventHelper.fireEvent(actionEvent, new ActionEvent());
		}
	}
	
	public T getValue() {
		return this.value;
	}
	
	public ObservableList<T> getItems() {
		return this.items;
	}

	public void setOnAction(EventHandler<ActionEvent> event) {
		this.actionEvent = event;
	}
}
