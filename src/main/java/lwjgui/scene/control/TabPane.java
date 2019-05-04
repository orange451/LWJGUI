package lwjgui.scene.control;

import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUIUtil;
import lwjgui.collections.ObservableList;
import lwjgui.event.ChangeEvent;
import lwjgui.event.ElementCallback;
import lwjgui.event.EventHandler;
import lwjgui.event.EventHelper;
import lwjgui.event.TabDragEvent;
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
	
	private EventHandler<TabDragEvent> tabDragEvent;
	private EventHandler<ChangeEvent<Tab>> selectionChangeEvent;
	
	private boolean canDrag = true;
	private int tabDragIndex;
	private boolean isDragging;
	
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
			}
		});		
		this.tabs.setRemoveCallback(new ElementCallback<Tab>() {
			@Override
			public void onEvent(Tab changed) {
				tabButtons.getChildren().remove(changed.button);
			}
		});
	}
	
	/**
	 * Sets whether or not the user can drag tabs around this tab-pane.
	 * @param drag
	 * @return
	 */
	public void setCanDrag(boolean drag) {
		this.canDrag = drag;
	}
	
	/**
	 * Return modifyable list of tabs.
	 * @return
	 */
	public ObservableList<Tab> getTabs() {
		return tabs;
	}
	
	/**
	 * Set the event callback for when a tab is dragged and dropped.
	 * @param event
	 */
	public void setOnTabDroppedEvent(EventHandler<TabDragEvent> event) {
		this.tabDragEvent = event;
	}
	
	/**
	 * Returns the event callback for when a tab is dragged and dropped.
	 * @return
	 */
	public EventHandler<TabDragEvent> getOnTabDroppedEvent() {
		return this.tabDragEvent;
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
	
	/**
	 * Set the current selected tab in this tab-pane.
	 * @param tab
	 */
	public void select(Tab tab) {
		if ( tabs.size() == 0 )
			return;
		if ( !tabs.contains(tab) ) {
			throw new RuntimeException("Tab does not exist within tab pane");
		}
		boolean stored = false;
		if ( currentTab != null ) {
			Node selected = cached_context.getSelected();
			if ( selected != null && selected.isDescendentOf(this) ) {
				currentTab.lastSelected = selected;
				stored = true;
			}
		}
		
		if ( selectionChangeEvent != null && currentTab != tab ) {
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
	
	/**
	 * Return the currently selected tab.
	 * @return
	 */
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
		
		if ( canDrag && currentTab.dragging ) {
			isDragging = true;
			// Get the tab index we're dragging to
			for (int i = 0; i < tabs.size(); i++) {
				Tab tab = tabs.get(i);
				if ( context.getMouseX() > tab.button.getX() )
					tabDragIndex = i;
				if ( context.getMouseX() > tab.button.getX()+tab.button.getWidth()/2 )
					tabDragIndex = i+1;
			}

			// Turn drag index into x coordinate
			float dragX = (float) tabs.get(Math.min(tabDragIndex, tabs.size()-1)).button.getX();
			if ( tabDragIndex >= tabs.size() )
				dragX += tabs.get(tabs.size()-1).button.getWidth();
			
			// Draw
			LWJGUIUtil.fillRect(context, dragX, tabs.get(0).button.getY(), 2, tabs.get(0).button.getHeight(), Color.BLACK);
		}
		
		// Drop tab
		if ( isDragging && !currentTab.dragging ) {
			isDragging = false;
			
			if ( this.isDescendentHovered() ) {
				dropTab();
				EventHelper.fireEvent(tabDragEvent, new TabDragEvent(currentTab));
			}
		}
	}
	
	private void dropTab() {
		int leftIndex = tabDragIndex-1;
		int rightIndex = tabDragIndex+0;
		if ( leftIndex < 0 )
			leftIndex = 0;
		if ( rightIndex >= tabs.size() )
			rightIndex = tabs.size()-1;
		
		// Dragging on far left or far right
		if ( leftIndex == rightIndex ) {
			
			// Left
			if ( leftIndex == 0 ) {
				tabs.remove(currentTab);
				tabs.add(0, currentTab);
				select(currentTab);
			} else if ( leftIndex == tabs.size()-1 ) { // Right
				tabs.remove(currentTab);
				tabs.add(currentTab);
				select(currentTab);
			}
		} else {
			int placeIndex = rightIndex;
			if ( currentTabIndex < rightIndex )
				placeIndex--;
			
			tabs.remove(currentTab);
			tabs.add(placeIndex, currentTab);
		}
		
		this.tabButtons.refresh();
		tabDragIndex = -1;
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
			// Render children
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
	
	class TabPaneButtonBox extends HBox {
		TabPaneButtonBox() {
			this.flag_clip = true;
			this.setFillToParentWidth(true);
			this.setSpacing(1);
			this.setPadding(new Insets(6,4,0,4));
			this.setPrefHeight(28);
			this.setMinHeight(this.getPrefHeight());
		}
		
		protected void refresh() {
			this.children.clear();
			for (int i = 0; i < tabs.size(); i++) {
				this.children.add(tabs.get(i).button);
			}
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
