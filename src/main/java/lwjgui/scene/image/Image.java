package lwjgui.scene.image;

import static org.lwjgl.system.MemoryUtil.memFree;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;

public class Image {

	private int width = -1;
	private int height = -1;

	private String desiredPath;

	private boolean loaded;

	private int image = -1;

	public Image(String filePath) {
		this.desiredPath = filePath;
	}

	public Image() {
		//
	}

	protected int getImage() {
		if (loaded)
			return image;
		Context context = LWJGUI.getThreadWindow().getContext();
		if (context == null)
			return -1;

		if (desiredPath == null)
			return -1;

		long nvg = context.getNVG();

		ByteBuffer data = null;

		try {
			data = Context.ioResourceToByteBuffer(desiredPath, 4 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (data == null)
			return -1;

		// Get image
		image = NanoVG.nvgCreateImageMem(nvg, 0, data);
		memFree(data);
		int[] w = new int[1];
		int[] h = new int[1];
		NanoVG.nvgImageSize(nvg, image, w, h);
		width = w[0];
		height = h[0];
		
		context.loadImage(this);

		loaded = true;
		return image;
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
		if ( loaded ) {
			loaded = false;
			Context context = LWJGUI.getThreadWindow().getContext();
			NanoVG.nvgDeleteImage(context.getNVG(), image);
		}
		
		image = -1;
	}
}
