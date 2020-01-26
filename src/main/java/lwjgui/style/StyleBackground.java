package lwjgui.style;

import lwjgui.collections.ObservableList;

public interface StyleBackground {
	public Background getBackground();
	public void setBackground(Background color);
	
	public ObservableList<Background> getBackgrounds();
}
