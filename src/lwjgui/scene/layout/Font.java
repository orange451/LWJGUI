package lwjgui.scene.layout;

import static org.lwjgl.nanovg.NanoVG.nvgCreateFontMem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import lwjgui.Context;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIWindow;

public class Font {
	public static Font SANS = new Font("sans", "Roboto-Regular.ttf", "Roboto-Bold.ttf", "Roboto-Light.ttf");

	
	
	private String name;
	private String fontNameNormal;
	private String fontNameBold;
	private String fontNameLight;
	private HashMap<Long,String> fontDataNormal = new HashMap<Long,String>();
	private HashMap<Long,String> fontDataBold = new HashMap<Long,String>();
	private HashMap<Long,String> fontDataLight = new HashMap<Long,String>();
	private ArrayList<ByteBuffer> bufs = new ArrayList<ByteBuffer>();
	
	private Font(String name, String normal, String bold, String light) {
		this.fontNameNormal = normal;
		this.fontNameBold = bold;
		this.fontNameLight = light;
		this.name = name;
	}

	private static InputStream inputStream(String path) throws IOException {
		InputStream stream;
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			stream = new FileInputStream(file);
		} else {
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		}
		return stream;
	}
	
	private static byte[] toByteArray(InputStream stream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		try {
			while ((nRead = stream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toByteArray();
	}

	public static ByteBuffer resourceToByteBuffer(String path) throws IOException {
		ByteBuffer data = null;
		InputStream stream = inputStream(path);
		if (stream == null) {
			throw new FileNotFoundException(path);
		}
		byte[] bytes = toByteArray(stream);
		data = ByteBuffer.allocateDirect(bytes.length).order(ByteOrder.nativeOrder()).put(bytes);
		data.flip();
		return data;
	}

	private void loadFont(String loadName, String suffix, HashMap<Long, String> map) {
		if ( loadName == null ) {
			return;
		}
		LWJGUIWindow window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		Context context = window.getContext();
		long vg = context.getNVG();
		
		try {
			String path = "lwjgui/scene/layout/" + loadName;
			ByteBuffer buf = resourceToByteBuffer(path);
			nvgCreateFontMem(vg, name+suffix, buf, 0);
			map.put(vg,name+suffix);
			bufs.add(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getFont() {
		for (int i = 0; i < bufs.size(); i++) {
			bufs.get(i);
		}
		return getFont(FontStyle.REGULAR);
	}

	public String getFont(FontStyle style) {
		LWJGUIWindow window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		Context context = window.getContext();
		long vg = context.getNVG();

		HashMap<Long,String> using = fontDataNormal;
		if ( fontDataNormal.get(vg) == null ) {
			loadFont(fontNameNormal, "-Regular", fontDataNormal);
			loadFont(fontNameBold, "-Bold", fontDataBold);
			loadFont(fontNameLight, "-Light", fontDataLight);
		}

		if ( style == FontStyle.BOLD && fontDataBold.get(vg) != null )
			using = fontDataBold;
		if ( style == FontStyle.LIGHT && fontDataLight.get(vg) != null )
			using = fontDataLight;
		
		return using.get(vg);
	}
}