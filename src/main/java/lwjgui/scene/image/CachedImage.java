package lwjgui.scene.image;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;

public class CachedImage {
	private int width = -1;
	private int height = -1;

	private int image = -1;

	private String desiredPath;

	int references;

	public CachedImage(String filePath) {
		this.desiredPath = filePath;
	}

	public void init() {
		Context context = LWJGUI.getCurrentContext();
		long nvg = context.getNVG();

		ByteBuffer data = null;

		try {
			data = Context.ioResourceToByteBuffer(desiredPath, 4 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (data == null)
			return;

		// Get image
		image = NanoVG.nvgCreateImageMem(nvg, 0, data);
		memFree(data);
		int[] w = new int[1];
		int[] h = new int[1];
		NanoVG.nvgImageSize(nvg, image, w, h);
		width = w[0];
		height = h[0];
	}

	public void dispose() {
		Context context = LWJGUI.getCurrentContext();
		NanoVG.nvgDeleteImage(context.getNVG(), image);
	}

	public int getImage() {
		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getPath() {
		return desiredPath;
	}

}
