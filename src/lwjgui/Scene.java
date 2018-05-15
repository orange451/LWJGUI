package lwjgui;

import lwjgui.scene.layout.Pane;
import lwjgui.theme.Theme;

public class Scene extends Pane {
	
	public Scene() {
		this.setBackground(Theme.currentTheme().getBackground());
	}
	
	@Override
	public boolean isResizeable() {
		return false;
	}

	@Override
	public void render(Context context) {
		position(null);		
		for (int i = 0; i < children.size(); i++) {
			children.get(i).render(context);
		}
	}

}
