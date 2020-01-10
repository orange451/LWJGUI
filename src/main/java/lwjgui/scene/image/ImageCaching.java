package lwjgui.scene.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageCaching {

	private Map<String, CachedImage> images = new HashMap<>();

	public void update() {
		List<CachedImage> toRemove = new ArrayList<>();
		for (CachedImage image : images.values()) {
			if (image.references <= 0)
				toRemove.add(image);
		}
		for (CachedImage image : toRemove) {
			images.remove(image.getPath());
			image.dispose();
		}
	}

	public CachedImage loadImage(String path) {
		CachedImage image;
		if (images.containsKey(path)) {
			image = images.get(path);
			image.references++;
		} else {
			image = new CachedImage(path);
			image.init();
			image.references++;
			images.put(path, image);
		}
		return image;
	}

	public void dispose() {
		for (CachedImage image : images.values()) {
			image.dispose();
		}
		images.clear();
	}

}
