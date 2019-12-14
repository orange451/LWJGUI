package lwjgui.style;

import lwjgui.scene.Context;

public abstract class Background {
	public abstract void render(Context context, double boundsX, double boundsY, double boundsW, double boundsH, float[] cornerRadii);
}
