package lwjgui.font;


import java.nio.ByteBuffer;

import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

import lwjgui.scene.Context;
import lwjgui.util.FileUtils;

public class Font {
	public static Font SANS = new Font("lwjgui/scene/layout/", "Roboto-Regular.ttf", "Roboto-Bold.ttf", "Roboto-Italic.ttf", "Roboto-Light.ttf");
	public static Font COURIER = new Font("lwjgui/scene/layout/", "Courier-New-Regular.ttf", "Courier-New-Bold.ttf", "Courier-New-Italic.ttf", null);
	public static Font CONSOLAS = new Font("lwjgui/scene/layout/", "Consolas-Regular.ttf", "Consolas-Bold.ttf", "Consolas-Italic.ttf", null);
	public static Font ARIAL = new Font("lwjgui/scene/layout/", "Arial-Unicode.ttf");
	public static Font SEGOE = new Font("lwjgui/scene/layout/", "selawk.ttf", "selawkb.ttf", null, "selawkl.ttf");
	public static Font DINGBAT = new Font("lwjgui/scene/layout/", "ErlerDingbats.ttf");

	public static ByteBuffer fallbackSansEmoji;
	public static ByteBuffer fallbackRegularEmoji;
	public static ByteBuffer fallbackArial;
	public static ByteBuffer fallbackEntypo;
	
	static {
		try {
			fallbackSansEmoji = Context.ioResourceToByteBuffer("lwjgui/scene/layout/OpenSansEmoji.ttf", 1024 * 1024);
			fallbackRegularEmoji = Context.ioResourceToByteBuffer("lwjgui/scene/layout/NotoEmoji-Regular.ttf", 1024 * 1024);
			fallbackArial = Context.ioResourceToByteBuffer("lwjgui/scene/layout/Arial-Unicode.ttf", 1024 * 1024);
			fallbackEntypo = Context.ioResourceToByteBuffer("lwjgui/scene/layout/entypo.ttf", 1024 * 1024);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dispose() {
		MemoryUtil.memFree(fallbackSansEmoji);
		MemoryUtil.memFree(fallbackRegularEmoji);
		MemoryUtil.memFree(fallbackArial);
		MemoryUtil.memFree(fallbackEntypo);
	}
	
	private String fontPath;
	private String fontNameRegular;
	private String fontNameBold;
	private String fontNameLight;
	private String fontNameItalic;
	private ByteBuffer preloadedData;
	
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
	
	/**
	 * Creates a new font with the given settings. Only the regular font is set and made available.
	 * @param fontFile
	 */
	public Font(String fontFile) {
		this(FileUtils.getFileDirectoryFromPath(fontFile), FileUtils.getFileNameFromPath(fontFile));
	}
	
	/**
	 * Creates a new font with the given settings. Only the regular font is set and made available.
	 * @param fontName
	 * @param data
	 */
	public Font(String fontName, ByteBuffer data) {
		this.fontPath = null;
		this.preloadedData = data;
	}

	public String getFontPath() {
		return fontPath;
	}

	public String getFontNameRegular() {
		return fontNameRegular;
	}

	public String getFontNameBold() {
		return fontNameBold;
	}

	public String getFontNameLight() {
		return fontNameLight;
	}

	public String getFontNameItalic() {
		return fontNameItalic;
	}

	public String getFont() {
		return getFont(FontStyle.REGULAR);
	}

	/**
	 * Gets the font with the given style. If the font hasn't been loaded yet, this function will do so.
	 * 
	 * @param style
	 * @return
	 */
	public String getFont(FontStyle style) {
		switch (style) {
			case BOLD:
				return fontNameBold;
			case ITALIC:
				return fontNameItalic;
			case LIGHT:
				return fontNameLight;
			case REGULAR:
				return fontNameRegular;
		}

		return fontNameRegular;
	}
	
	public float[] getTextBounds(Context context, String string, FontStyle style, double size, float[] bounds) {		
		if (context == null)
			return bounds;
		
		if (!context.isFontLoaded(this) )
			context.loadFont(this);
		
		String font = getFont(style);
		if (font == null)
			return bounds;
		
		NanoVG.nvgFontSize(context.getNVG(), (float)size);
		NanoVG.nvgFontFace(context.getNVG(), font);
		NanoVG.nvgTextAlign(context.getNVG(), NanoVG.NVG_ALIGN_LEFT|NanoVG.NVG_ALIGN_TOP);
		
		if (string != null) {
			NanoVG.nvgTextBounds(context.getNVG(), 0, 0, string, bounds);
		}
		
		return bounds;
	}

	public ByteBuffer getInternalByteBuffer() {
		return this.preloadedData;
	}
}