package lwjgui.scene.image;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;

public class Image {
	private int width = -1;
	private int height = -1;

	private int image = -1;

	private String desiredPath;

	private boolean loaded;

	private CachedImage cached;

	public Image(String filePath) {
		this.desiredPath = filePath;
	}

	public Image() {

	}

	public void init() {
		if (loaded)
			return;
		Context context = LWJGUI.getCurrentContext();
		if (context == null)
			return;

		if (desiredPath == null)
			return;

		cached = context.getImageCaching().loadImage(desiredPath);
		width = cached.getWidth();
		height = cached.getHeight();
		image = cached.getImage();
		loaded = true;
	}

	public void dispose() {
		if (!loaded)
			return;
		loaded = false;
		cached.references--;
	}

	protected int getImage() {
		return image;
	}

	protected boolean isLoaded() {
		return loaded;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
