package lwjgui.font;

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
import org.lwjgl.nanovg.NanoVG;

import lwjgui.LWJGUI;
import lwjgui.scene.Context;
import lwjgui.scene.Window;

public class Font {
	public static Font SANS = new Font("lwjgui/scene/layout/", "Roboto-Regular.ttf", "Roboto-Bold.ttf", "Roboto-Italic.ttf", "Roboto-Light.ttf");
	public static Font COURIER = new Font("lwjgui/scene/layout/", "Courier-New-Regular.ttf", "Courier-New-Bold.ttf", "Courier-New-Italic.ttf", null);
	public static Font CONSOLAS = new Font("lwjgui/scene/layout/", "Consolas-Regular.ttf", "Consolas-Bold.ttf", "Consolas-Italic.ttf", null);
	public static Font ARIAL = new Font("lwjgui/scene/layout/", "Arial-Unicode.ttf");

	private static ByteBuffer fallbackSansEmoji;
	private static ByteBuffer fallbackRegularEmoji;
	private static ByteBuffer fallbackArial;
	private static ByteBuffer fallbackEntypo;
	
	static {
		try {
			fallbackSansEmoji		= resourceToByteBuffer("lwjgui/scene/layout/OpenSansEmoji.ttf");
			fallbackRegularEmoji	= resourceToByteBuffer("lwjgui/scene/layout/NotoEmoji-Regular.ttf");
			fallbackArial			= resourceToByteBuffer("lwjgui/scene/layout/Arial-Unicode.ttf");
			fallbackEntypo			= resourceToByteBuffer("lwjgui/scene/layout/entypo.ttf");
		}catch(Exception e) {
			//
		}
	}
	
	private String fontPath;
	private String fontNameRegular;
	private String fontNameBold;
	private String fontNameLight;
	private String fontNameItalic;
	private HashMap<Long,String> fontDataRegular = new HashMap<Long,String>();
	private HashMap<Long,String> fontDataBold = new HashMap<Long,String>();
	private HashMap<Long,String> fontDataLight = new HashMap<Long,String>();
	private HashMap<Long,String> fontDataItalic = new HashMap<Long,String>();
	private ArrayList<ByteBuffer> bufs = new ArrayList<ByteBuffer>();
	
	/**
	 * Creates a new font with the given settings.
	 * 
	 * @param fontPath - the folder path of the font files
	 * @param regularFileName - the filename of the regular font TTF (e.g. fontName.ttf)
	 * @param boldFileName - the filename of the bold font TTF
	 * @param italicFileName - the filename of the italic font TTF
	 * @param lightFileName - the filename of the light font TTF
	 */
	public Font(String fontPath, String regularFileName, String boldFileName, String italicFileName, String lightFileName) {
		this.fontPath = fontPath;
		this.fontNameRegular = regularFileName;
		this.fontNameBold = boldFileName;
		this.fontNameLight = lightFileName;
		this.fontNameItalic = italicFileName;
	}	
	/**
	 * Creates a new font with the given settings. Only the regular font is set and made available.
	 * 
	 * @param fontPath - the folder path of the font files
	 * @param regularFileName - the filename of the regular font TTF (e.g. fontName.ttf)
	 */
	
	public Font(String fontPath, String regularFileName) {
		this.fontPath = fontPath;
		this.fontNameRegular = regularFileName;
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

	/**
	 * Loads the resource via ByteBuffer
	 * @param path
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * Loads a given Font
	 * @param fontPath
	 * @param loadName
	 * @param suffix
	 * @param map
	 */
	private void loadFont(String fontPath, String loadName, HashMap<Long, String> map) {
		if ( loadName == null ) {
			return;
		}
		Window window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		Context context = window.getContext();
		long vg = context.getNVG();
		int fontCallback;
		
		try {
			String path = fontPath + loadName;
			
			// Create normal font
			ByteBuffer buf = resourceToByteBuffer(path);
			fontCallback = nvgCreateFontMem(vg, loadName, buf, 0);
			map.put(vg, loadName);
			bufs.add(buf);
			
			// Fallback emoji font
			addFallback(vg, fontCallback, fallbackSansEmoji);
			addFallback(vg, fontCallback, fallbackRegularEmoji);
			addFallback(vg, fontCallback, fallbackArial);
			addFallback(vg, fontCallback, fallbackEntypo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addFallback(long vg, int fontCallback, ByteBuffer fontData) {
		NanoVG.nvgAddFallbackFontId(vg, fontCallback, nvgCreateFontMem(vg, "emoji", fontData, 0));
		bufs.add(fontData);
	}

	public String getFont() {
		for (int i = 0; i < bufs.size(); i++) {
			bufs.get(i);
		}
		return getFont(FontStyle.REGULAR);
	}

	/**
	 * Gets the font with the given style. If the font hasn't been loaded yet, this function will do so.
	 * 
	 * @param style
	 * @return
	 */
	public String getFont(FontStyle style) {
		Window window = LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext());
		if ( window == null )
			return null;
		
		Context context = window.getContext();
		long vg = context.getNVG();

		HashMap<Long,String> using = fontDataRegular;
		if ( fontDataRegular.get(vg) == null ) {
			loadFont(fontPath, fontNameRegular, fontDataRegular);
			loadFont(fontPath, fontNameBold, fontDataBold);
			loadFont(fontPath, fontNameLight, fontDataLight);
			loadFont(fontPath, fontNameItalic, fontDataItalic);
		}

		if ( style == FontStyle.BOLD && fontDataBold.get(vg) != null )
			using = fontDataBold;
		if ( style == FontStyle.LIGHT && fontDataLight.get(vg) != null )
			using = fontDataLight;
		if ( style == FontStyle.ITALIC && fontDataItalic.get(vg) != null )
			using = fontDataItalic;
		
		return using.get(vg);
	}
	
	public float[] getTextBounds(Context context, String string, FontStyle style, float size) {
		float[] bounds = new float[4];
		
		if (context == null) {
			return bounds;
		}
		
		String font = getFont(style);
		
		if (font == null) {
			return bounds;
		}
		
		NanoVG.nvgFontSize(context.getNVG(), size);
		NanoVG.nvgFontFace(context.getNVG(), font);
		NanoVG.nvgTextAlign(context.getNVG(), NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
		
		if (string != null) {
			NanoVG.nvgTextBounds(context.getNVG(), 0, 0, string, bounds);
		}
		
		return bounds;
	}
}