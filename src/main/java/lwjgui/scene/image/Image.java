package lwjgui.scene.image;

import static org.lwjgl.BufferUtils.createByteBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
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
	
	public Image() {
		
	}

	protected int getImage() {
		Context context = LWJGUI.getCurrentContext();
		if ( context == null )
			return -1;
		
		if ( desiredPath == null )
			return -1;
		
		long nvg = context.getNVG();

		if ( !imageReferences.containsKey(nvg)) {
			ByteBuffer data = null;
			
			try {
				data = ioResourceToByteBuffer(desiredPath, 4 * 1024);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if ( data == null ) 
				return -1;
			
			// Get image
			int img = NanoVG.nvgCreateImageMem(nvg, 0, data);
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
	
	private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
		ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
		buffer.flip();
		newBuffer.put(buffer);
		return newBuffer;
	}
	
	@SuppressWarnings("deprecation")
	protected static URL ioResourceGetURL( String resource ) {
		URL url = Image.class.getClassLoader().getResource(resource);
		if (url == null) {
			try {
				url = new File(resource).toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		}

		return url;
	}
	
	protected static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
		if ( resource == null )
			return null;

		// Get URL for file
		URL url = ioResourceGetURL( resource );

		ByteBuffer buffer = createByteBuffer(bufferSize);
		InputStream source = url.openStream();

		if ( source == null )
			throw new FileNotFoundException(resource);

		try {
			ReadableByteChannel rbc = Channels.newChannel(source);
			try {
				while ( true ) {
					int bytes = rbc.read(buffer);
					if ( bytes == -1 )
						break;
					if ( buffer.remaining() == 0 )
						buffer = resizeBuffer(buffer, buffer.capacity() * 2);
				}
			} finally {
				rbc.close();
			}
		} finally {
			source.close();
		}

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
