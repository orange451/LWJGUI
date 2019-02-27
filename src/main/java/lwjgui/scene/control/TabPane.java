package lwjgui.scene.control;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.event.ChangeEvent;
import lwjgui.event.ElementCallback;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
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
	
	//private TabDragEvent tabDragEvent;
	private EventHandler<ChangeEvent<Tab>> selectionChangeEvent;
	
	public TabPane() {
		this.setFillToParentHeight(true);
		this.setFillToParentWidth(true);
		
		this.tabs = new ObservableList<Tab>();
		this.flag_clip = true;
		this.setAlignment(Pos.TOP_LEFT);
		this.setPrefSize(100, 100);
		this.setMinHeight(28);
		
		this.internal = new TabPaneInternal();
		this.children.add(internal);
		
		this.tabButtons = new TabPaneButtonBox();
		this.internal.getChildren().add(tabButtons);
		
		this.contentPane = new StackPane() {
			{
				flag_clip = true;
			}
		};
		this.internal.getChildren().add(contentPane);
		
		this.tabs.setAddCallback(new ElementCallback<Tab>() {
			@Override
			public void onEvent(Tab changed) {
				tabButtons.getChildren().add(changed.button);
				changed.tabPane = TabPane.this;
				
				changed.button.setOnMousePressed(event -> {
					select(changed);
					event.consume();
				});
				
				changed.button.setOnMouseReleased(event -> {
					select(changed);
					event.consume();
				});
				
				changed.button.setOnMouseDragged(event -> {
					System.out.println("Not implemented yet");
				});
			}
		});		
		this.tabs.setRemoveCallback(new ElementCallback<Tab>() {
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
		// Limit content pane size
		contentPane.setMaxSize(getWidth(), getHeight()-tabButtons.getHeight());
		contentPane.setPrefSize(contentPane.getMaxWidth(), contentPane.getMaxHeight());
		
		if ( tabs == null || tabs.size() == 0) {
			deselect();
		}
		
		// Find missing tab
		while ( (currentTab == null || !tabs.contains(currentTab) ) && tabs.size() > 0 ) {
			currentTabIndex--;
			
			if ( currentTabIndex < 0 )
				currentTabIndex = 0;
			if ( currentTabIndex > tabs.size()-1 )
				currentTabIndex = tabs.size()-1;
			
			select(tabs.get(currentTabIndex));
		}

		super.position(parent);
	}
	
	protected int getTabIndex(Tab tab) {
		for (int i = 0; i < tabs.size(); i++) {
			if ( tabs.get(i).equals(tab) )
				return i;
		}
		return -1;
	}
	
	protected void deselect() {
		currentTab = null;
		currentTabIndex = 0;
		this.contentPane.getChildren().clear();
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
		
		if ( selectionChangeEvent != null ) {
			boolean consumed = EventHelper.fireEvent(selectionChangeEvent, new ChangeEvent<Tab>(currentTab, tab));
			if ( consumed )
				return;
		}
		
		currentTab = tab;
		this.contentPane.getChildren().clear();
		this.contentPane.getChildren().add(tab.getContent());
		currentTabIndex = getTabIndex(tab);
		
		if ( stored && currentTab.lastSelected != null ) {
			cached_context.setSelected(currentTab.lastSelected);
		}
	}
	
	public Tab getSelected() {
		return currentTab;
	}

	@Override
	public void render(Context context) {
		// Render internal pane
		this.clip(context);
		internal.render(context);
		
		for (int i = 0; i < tabs.size(); i++) {
			Tab tab = tabs.get(i);
			TabButton button = tab.button;
			button.pressed = currentTab.equals(tab);
		}
	}
	
	static class TabPaneInternal extends VBox {
		TabPaneInternal() {
			this.flag_clip = true;
			this.setFillToParentHeight(true);
			this.setFillToParentWidth(true);
			this.setAlignment(Pos.TOP_LEFT);
		}
		
		@Override
		public void render(Context context) {
			super.render(context);
			
			this.clip(context);
			// Dropshadow
			long vg = context.getNVG();
			float x = (float) getX();
			float y = (float) getY();
			float w = (float) getWidth();
			NVGPaint bg = NanoVG.nvgLinearGradient(vg, x, y-16, x, y+6, Theme.current().getShadow().getNVG(), Color.TRANSPARENT.getNVG(), NVGPaint.calloc());
			NanoVG.nvgBeginPath(vg);
			NanoVG.nvgRect(vg, x, y, w, 6);
			NanoVG.nvgFillPaint(vg, bg);
			NanoVG.nvgFill(vg);
		}
	}
	
	static class TabPaneButtonBox extends HBox {
		TabPaneButtonBox() {
			this.flag_clip = true;
			this.setFillToParentWidth(true);
			this.setSpacing(2);
			this.setPadding(new Insets(6,4,0,4));
			this.setPrefHeight(28);
			this.setMinHeight(this.getPrefHeight());
		}
		
		@Override
		public void render(Context context) {
			LWJGUIUtil.fillRect(context, getX(), getY(), getWidth(), getHeight(), Theme.current().getBackgroundAlt());
			LWJGUIUtil.fillRect(context, getX(), getY()+getHeight()-1, getWidth(), 1, Theme.current().getControlOutline());
			for (int i = 0; i < children.size(); i++) {
				clip(context);
				children.get(i).render(context);
			}
		}
	}

	/*public void setTabDraggedEvent(TabDragEvent event) {
		this.tabDragEvent = event;
	}*/
	
	public void setOnSelectionChange( EventHandler<ChangeEvent<Tab>> event ) {
		this.selectionChangeEvent = event;
	}
}
