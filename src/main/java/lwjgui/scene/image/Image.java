package lwjgui.scene.image;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memRealloc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;

public class Image {
	private HashMap<Long, Integer> imageReferences = new HashMap<Long, Integer>();

	private int width = -1;
	private int height = -1;

	private String desiredPath;

	public Image(String filePath) {
		this.desiredPath = filePath;
	}

	public Image() {

	}

	protected int getImage() {
		Context context = LWJGUI.getCurrentContext();
		if (context == null)
			return -1;

		if (desiredPath == null)
			return -1;

		long nvg = context.getNVG();

		if (!imageReferences.containsKey(nvg)) {
			ByteBuffer data = null;

			try {
				data = ioResourceToByteBuffer(desiredPath, 4 * 1024);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (data == null)
				return -1;

			// Get image
			int img = NanoVG.nvgCreateImageMem(nvg, 0, data);
			memFree(data);
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

	protected static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		ByteBuffer buffer;

		File file = new File(resource);
		if (file.isFile()) {
			FileInputStream fis = new FileInputStream(file);
			FileChannel fc = fis.getChannel();

			buffer = memAlloc((int) fc.size() + 1);

			while (fc.read(buffer) != -1)
				;

			fis.close();
			fc.close();
		} else {
			int size = 0;
			buffer = memAlloc(bufferSize);
			try (InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
				if (source == null)
					throw new FileNotFoundException(resource);
				try (ReadableByteChannel rbc = Channels.newChannel(source)) {
					while (true) {
						int bytes = rbc.read(buffer);
						if (bytes == -1)
							break;
						size += bytes;
						if (!buffer.hasRemaining())
							buffer = memRealloc(buffer, size * 2);
					}
				}
			}
			buffer = memRealloc(buffer, size + 1);
		}
		buffer.put((byte) 0);
		buffer.flip();
		return buffer;
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
