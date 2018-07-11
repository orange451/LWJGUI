package lwjgui.scene.control;

public interface Toggle {
	/**
	 * Sets whether or not this button is selected.
	 * <br>
	 * If it belongs to a ToggleGroup, only one button can be selected at a time.
	 * @param b
	 */
	public void setSelected(boolean b);
	
	/**
	 * 
	 * @return Returns whether or not the button is selected.
	 */
	public boolean isSelected();
}
