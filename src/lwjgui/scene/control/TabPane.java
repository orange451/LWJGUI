package lwjgui.scene.control;

import lwjgui.Color;
import lwjgui.collections.ObservableList;
import lwjgui.event.ChangeEvent;
import lwjgui.event.MouseEvent;
import lwjgui.event.ScrollEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Context;
import lwjgui.scene.Node;
import lwjgui.scene.control.Tab.TabButton;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;
import lwjgui.theme.Theme;

public class TabPane extends Control {
	
	private ObservableList<Tab> tabs;
	
	private Tab currentTab;
	private int currentTabIndex;
	
	private TabPaneInternal internal;
	private TabPaneButtonBox tabButtons;
	private StackPane contentPane;
	
	public TabPane() {
		this.setFillToParentHeight(true);
		this.setFillToParentWidth(true);
		
		this.tabs = new ObservableList<Tab>();
		this.flag_clip = true;
		this.setPrefSize(100, 100);
		
		this.internal = new TabPaneInternal();
		this.children.add(internal);
		
		this.tabButtons = new TabPaneButtonBox();
		this.internal.getChildren().add(tabButtons);
		
		this.contentPane = new StackPane();
		this.flag_clip = true;
		this.contentPane.setFillToParentHeight(true);
		this.contentPane.setFillToParentWidth(true);
		this.internal.getChildren().add(contentPane);
		
		this.setAlignment(Pos.TOP_LEFT);
		
		this.tabs.setAddCallback(new ChangeEvent<Tab>() {
			@Override
			public void onEvent(Tab changed) {
				tabButtons.getChildren().add(changed.button);
				
				changed.button.setMousePressedEvent(new MouseEvent() {
					@Override
					public void onEvent(int button) {
						select(changed);
					}
				});
				
				changed.button.setMouseReleasedEvent(new MouseEvent() {
					@Override
					public void onEvent(int button) {
						this.consume();
					}
				});
			}
		});		
		this.tabs.setRemoveCallback(new ChangeEvent<Tab>() {
			@Override
			public void onEvent(Tab changed) {
				tabButtons.getChildren().remove(changed.button);
			}
		});
	}
	
	public ObservableList<Tab> getTabs() {
		return tabs;
	}
	
	@Override
	protected void position(Node parent) {
		super.position(parent);
		
		if ( tabs == null || tabs.size() == 0) {
			currentTab = null;
			currentTabIndex = 0;
		}
		
		// Find missing tab
		while ( (currentTab == null && tabs.size() > 0) || !tabs.contains(currentTab) ) {
			currentTabIndex--;
			if ( currentTabIndex < 0 )
				currentTabIndex = 0;
			if ( currentTabIndex > tabs.size()-1 )
				currentTabIndex = tabs.size()-1;
			
			select(tabs.get(currentTabIndex));
		}
	}
	
	protected int getTabIndex(Tab tab) {
		for (int i = 0; i < tabs.size(); i++) {
			if ( tabs.get(i).equals(tab) )
				return i;
		}
		return -1;
	}
	
	public void select(Tab tab) {
		if ( tabs.size() == 0 )
			return;
		if ( !tabs.contains(tab) ) {
			System.err.println("Tab does not exist within tab pane");
			return;
		}
		boolean stored = false;
		if ( currentTab != null ) {
			Node selected = cached_context.getSelected();
			if ( selected != null && selected.isDescendentOf(this) ) {
				currentTab.lastSelected = selected;
				stored = true;
			}
		}
		currentTab = tab;
		this.contentPane.getChildren().clear();
		this.contentPane.getChildren().add(tab.getContent());
		currentTabIndex = getTabIndex(tab);
		
		if ( stored && currentTab.lastSelected != null ) {
			cached_context.setSelected(currentTab.lastSelected);
		}
	}

	@Override
	public void render(Context context) {
		// Render internal pane
		this.clip(context);
		internal.render(context);
		
		for (int i = 0; i < tabs.size(); i++) {
			Tab tab = tabs.get(i);
			TabButton button = tab.button;
			button.setBackground(Theme.currentTheme().getSelectionPassive());
			
			if ( currentTab.equals(tab) ) {
				button.setBackground(Theme.currentTheme().getPane());
			}
		}
	}
	
	static class TabPaneInternal extends VBox {
		TabPaneInternal() {
			this.flag_clip = true;
			this.setFillToParentHeight(true);
			this.setFillToParentWidth(true);
			this.setAlignment(Pos.TOP_LEFT);
		}
	}
	
	static class TabPaneButtonBox extends HBox {
		TabPaneButtonBox() {
			this.flag_clip = true;
			this.setFillToParentWidth(true);
			this.setSpacing(3);
			this.setPadding(new Insets(6,4,0,4));
			this.setBackground(Theme.currentTheme().getControlOutline());
		}
	}
}
