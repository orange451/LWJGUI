package lwjgui.scene.image;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;

public class Image {
	private HashMap<Long,Integer> imageReferences = new HashMap<Long,Integer>();

	private int width = -1;
	private int height = -1;
	
	private String desiredPath;

	public Image(String filePath) {
		this.desiredPath = filePath;
	}

	protected int getImage() {
		Context context = LWJGUI.getCurrentContext();
		if ( context == null )
			return -1;
		
		if ( desiredPath == null )
			return -1;
		
		long nvg = context.getNVG();

		if ( !imageReferences.containsKey(nvg)) {
			
			// Get image
			int img = NanoVG.nvgCreateImage(nvg, desiredPath, 0);
			int[] w = new int[1];
			int[] h = new int[1];
			NanoVG.nvgImageSize(nvg, img, w, h);
			width = w[0];
			height = h[0];
			
			// Store to references
			imageReferences.put(nvg, img);
		}
		
		// Return
		return imageReferences.get(nvg);
	}

	protected boolean isLoaded() {
		return getImage() != -1;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public void dispose() {
		for (Map.Entry<Long, Integer> entry : imageReferences.entrySet()) {
			NanoVG.nvgDeleteImage(entry.getKey(), entry.getValue());
		}
		imageReferences.clear();
	}
}
