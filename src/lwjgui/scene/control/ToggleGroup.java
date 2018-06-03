package lwjgui.scene.control;

import lwjgui.collections.ObservableList;

public class ToggleGroup {
	private ObservableList<Toggle> toggleables = new ObservableList<Toggle>();
	
	public void add(Toggle b) {
		this.toggleables.add(b);
	}
	
	public void remove(Toggle b) {
		this.toggleables.remove(b);
	}

	public void select(Toggle b) {
		for (int i = 0; i < toggleables.size(); i++) {
			Toggle t = toggleables.get(i);
			
			if ( !t.equals(b) ) {
				t.setSelected(false);
			}
		}
	}
	
	
}
