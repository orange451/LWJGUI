package lwjgui.paint;

import java.lang.reflect.Field;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;

public class Color {
	public static final Color WHITE = new Color(255, 255, 255).immutable(true);
	public static final Color LIGHT_GRAY = new Color(211, 211, 211).immutable(true);
	public static final Color SILVER = new Color(192, 192, 192).immutable(true);
	public static final Color GRAY = new Color(128, 128, 128).immutable(true);
	public static final Color DIM_GRAY = new Color(169, 169, 169).immutable(true);
	public static final Color DARK_GRAY = new Color(64, 64, 64).immutable(true);
	public static final Color MIDNIGHT = new Color(24, 24, 24).immutable(true);
	public static final Color LIGHT_BLACK = new Color(10, 10, 10).immutable(true);
	public static final Color WHITE_SMOKE = new Color(245, 245, 245).immutable(true);
	public static final Color BLACK = new Color(0, 0, 0).immutable(true);
	public static final Color RED = new Color(255, 0, 0).immutable(true);
	public static final Color PINK = new Color(255, 175, 175).immutable(true);
	public static final Color ORANGE = new Color(255, 200, 0).immutable(true);
	public static final Color YELLOW = new Color(255, 255, 0).immutable(true);
	public static final Color LIGHT_YELLOW = new Color(255, 238, 158).immutable(true);
	public static final Color LIGHT_BLUE = new Color(158, 238, 255).immutable(true);
	public static final Color LIME = new Color(0, 255, 0).immutable(true);
	public static final Color GREEN = new Color("#008000").immutable(true);
	public static final Color MAGENTA = new Color(255, 0, 255).immutable(true);
	public static final Color CYAN = new Color(0, 255, 255).immutable(true);
	public static final Color BLUE = new Color(0, 0, 255).immutable(true);
	public static final Color AQUA = new Color(3, 158, 211).immutable(true);
	public static final Color CORAL = new Color("#FF7F50").immutable(true);
	public static final Color VIOLET = new Color("#ee82ee").immutable(true);
	public static final Color INDIGO = new Color("#4b0082").immutable(true);
	public static final Color TRANSPARENT = new Color(255, 255, 255, 0).immutable(true);

	private int rgba;
	private NVGColor nvg;
	private Vector4f vector;

	private boolean immutable = false;

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue values
	 * in the range (0 - 255). The actual color used in rendering depends on finding
	 * the best match given the color space available for a given output device.
	 * Alpha is defaulted to 255.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0 to
	 *                                  255, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGB
	 */
	public Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	/**
	 * Creates an sRGB color with the specified red, green, blue, and alpha values
	 * in the range (0 - 255).
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code>,
	 *                                  <code>b</code> or <code>a</code> are outside
	 *                                  of the range 0 to 255, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the alpha component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getAlpha
	 * @see #getRGB
	 */
	public Color(int r, int g, int b, int a) {
		set(r, g, b, a);
	}

	/**
	 * Creates an opaque sRGBA color with the specified combined RGBA value
	 * consisting of the alpha component in bits 31-24, red component in bits 16-23,
	 * the green component in bits 8-15, and the blue component in bits 0-7. The
	 * actual color used in rendering depends on finding the best match given the
	 * color space available for a particular output device.
	 *
	 * @param rgba the combined RGBA components
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getAlpha
	 * @see #getRGBA
	 */
	public Color(int rgba) {
		set(rgba);
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue values
	 * in the range (0.0 - 1.0). Alpha is defaulted to 1.0. The actual color used in
	 * rendering depends on finding the best match given the color space available
	 * for a particular output device.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 */
	public Color(float r, float g, float b) {
		this((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue values
	 * in the range (0.0 - 1.0). Alpha is defaulted to 1.0. The actual color used in
	 * rendering depends on finding the best match given the color space available
	 * for a particular output device.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 */
	public Color(double r, double g, double b) {
		this((int) (r * 255), (int) (g * 255), (int) (b * 255));
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue values
	 * in the range (0.0 - 1.0). Alpha is defaulted to 1.0. The actual color used in
	 * rendering depends on finding the best match given the color space available
	 * for a particular output device.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 */
	public Color(double r, double g, double b, double a) {
		this((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
	}

	/**
	 * Creates a Color from a given HEX value.
	 * 
	 * @param hex e.g. #FFFFFF for white.
	 */
	public Color(String hex) throws NumberFormatException {
		hex = hex.toUpperCase();
		
		if ( !hex.startsWith("#") )
			hex = "#"+hex;
		
		if ( hex.length() == 9 ) {
			set(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16), Integer.valueOf(hex.substring(7, 9), 16));
		} else if ( hex.length() >= 7 ) {
			set(Integer.valueOf(hex.substring(1, 3), 16), Integer.valueOf(hex.substring(3, 5), 16), Integer.valueOf(hex.substring(5, 7), 16), 255);
		} else if ( hex.length() >= 4 ) {
			String hexr = hex.substring(1,2);
			hexr += hexr;
			String hexg = hex.substring(2,3);
			hexg += hexg;
			String hexb = hex.substring(3,4);
			hexb += hexb;
			
			set(Integer.valueOf(hexr, 16),
				Integer.valueOf(hexg, 16),
				Integer.valueOf(hexb, 16),
				255);
		} else {
			throw new NumberFormatException("Cannot parse hex code: \"" + hex + "\"");
		}
	}

	/**
	 * Creates a Color from another Color (a copy).
	 * 
	 * @param color - the color to copy
	 */
	public Color(Color color) {
		set(color);
	}
	
	/**
	 * Fills the user-provided array with the HSB representation of the specified RGB color
	 * @param r
	 * @param g
	 * @param b
	 * @param hsbvals
	 * @return
	 */
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;

        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
            saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
            saturation = 0;
        if (saturation == 0)
            hue = 0;
        else {
            float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax)
                hue = bluec - greenc;
            else if (g == cmax)
                hue = 2.0f + redc - bluec;
            else
                hue = 4.0f + greenc - redc;
            hue = hue / 6.0f;
            if (hue < 0)
                hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }
    
    /**
     * returns the RGB representation of the color derived from the user-specified HSB
     * @param hue
     * @param saturation
     * @param brightness
     * @return
     */
    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)java.lang.Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
            case 0:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (t * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 1:
                r = (int) (q * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (p * 255.0f + 0.5f);
                break;
            case 2:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (brightness * 255.0f + 0.5f);
                b = (int) (t * 255.0f + 0.5f);
                break;
            case 3:
                r = (int) (p * 255.0f + 0.5f);
                g = (int) (q * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 4:
                r = (int) (t * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (brightness * 255.0f + 0.5f);
                break;
            case 5:
                r = (int) (brightness * 255.0f + 0.5f);
                g = (int) (p * 255.0f + 0.5f);
                b = (int) (q * 255.0f + 0.5f);
                break;
            }
        }
        return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
    }
    
    /**
     * Creates a new color based on the supplied HSV.
     * @param hue
     * @param saturation
     * @param vibrance/brightness
     * @return
     */
	public static Color fromHSB(float hue, float saturation, float brightness) {
		return new Color(0xff000000 | Color.HSBtoRGB(hue, saturation, brightness));
	}


	/**
	 * Sets whether or not this Color is immutable. Immutable Colors cannot be
	 * recycled, and any setters used will instead return a new Color object rather
	 * than internally modifying this color.
	 * 
	 * @param immutable
	 * @return
	 */
	public Color immutable(boolean immutable) {
		this.immutable = immutable;
		return this;
	}

	/**
	 * Creates a copy of this Color, however customization options such as immutable
	 * are not copied.
	 * 
	 * @return - the copy of this Color.
	 */
	public Color copy() {
		return new Color(this);
	}

	/**
	 * Sets a Color from another Color (turns this Color into a copy of the given
	 * color)
	 * 
	 * @param color - the color to copy
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color set(Color color) {
		return set(color.getRGBA());
	}

	/**
	 * Sets an opaque sRGB color with the specified red, green, and blue values in
	 * the range (0.0 - 1.0). The actual color used in rendering depends on finding
	 * the best match given the color space available for a particular output
	 * device. Useful for when a Color object needs to be recycled.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the alpha component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color set(float r, float g, float b, float a) {
		return set((int) (r * 255), (int) (g * 255), (int) (b * 255), (int) (a * 255));
	}

	/**
	 * Sets the alpha value in the range (0.0 - 1.0). Useful for when a Color object
	 * needs to be recycled.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color alpha(float a) {
		return set(getRed(), getGreen(), getBlue(), (int) (a * 255));
	}

	/**
	 * Sets the red value in the range (0.0 - 1.0). Useful for when a Color object
	 * needs to be recycled.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color red(float r) {
		return set((int) (r * 255), getGreen(), getBlue(), getAlpha());
	}

	/**
	 * Sets the green value in the range (0.0 - 1.0). Useful for when a Color object
	 * needs to be recycled.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color green(float g) {
		return set(getRed(), (int) (g * 255), getBlue(), getAlpha());
	}

	/**
	 * Sets the blue value in the range (0.0 - 1.0). Useful for when a Color object
	 * needs to be recycled.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code> or
	 *                                  <code>b</code> are outside of the range 0.0
	 *                                  to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGBA
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color blue(float b) {
		return set(getRed(), getGreen(), (int) (b * 255), getAlpha());
	}

	/**
	 * Sets an sRGB color with the specified red, green, blue, and alpha values in
	 * the range (0 - 255). Useful for when a Color object needs to be recycled.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code>,
	 *                                  <code>b</code> or <code>a</code> are outside
	 *                                  of the range 0 to 255, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the alpha component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getAlpha
	 * @see #getRGBA
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color set(int r, int g, int b, int a) {
		// Set the new color value
		int rgb = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);

		// Ensure that the color is valid
		testColorValueRange(r, g, b, a);

		return set(rgb);
	}

	/**
	 * Sets an opaque sRGBA color with the specified combined RGBA value consisting
	 * of the alpha component in bits 31-24, red component in bits 16-23, the green
	 * component in bits 8-15, and the blue component in bits 0-7. The actual color
	 * used in rendering depends on finding the best match given the color space
	 * available for a particular output device.
	 *
	 * @param rgba the combined RGBA components
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getAlpha
	 * @see #getRGB
	 * 
	 * @return this Color object if the Color is not mutable, or a new Color object
	 *         if this Color is set to be immutable.
	 */
	public Color set(int rgba) {
		if (immutable) {
			return new Color(rgba);
		} else {
			this.rgba = rgba;

			// Create the NVGColor/update it
			float fR = getRed() / 255.0f;
			float fG = getGreen() / 255.0f;
			float fB = getBlue() / 255.0f;
			float fA = getAlpha() / 255.0f;

			if (nvg == null) {
				nvg = NVGColor.create();
			}

			nvg.r(fR);
			nvg.g(fG);
			nvg.b(fB);
			nvg.a(fA);

			// Create the vector/update it
			if (vector == null) {
				vector = new Vector4f(fR, fG, fB, fA);
			} else {
				vector.set(fR, fG, fB, fA);
			}

			return this;
		}
	}

	/**
	 * Returns a NanoVG color with the same component values.
	 * 
	 * @return
	 */
	public NVGColor getNVG() {
		return nvg;
	}

	/**
	 * Returns the vector4f value with the same component values.
	 * 
	 * @return
	 */
	public Vector4f getVector() {
		return vector;
	}

	/**
	 * Returns the red component in the range 0-255 in the default sRGB space.
	 * 
	 * @return the red component.
	 * @see #getRGB
	 */
	public int getRed() {
		return (getRGBA() >> 16) & 0xFF;
	}

	public float getRedF() {

		return getRed() / 255f;

	}

	/**
	 * Returns the green component in the range 0-255 in the default sRGB space.
	 * 
	 * @return the green component.
	 * @see #getRGB
	 */
	public int getGreen() {
		return (getRGBA() >> 8) & 0xFF;
	}

	public float getGreenF() {

		return getGreen() / 255f;

	}

	/**
	 * Returns the blue component in the range 0-255 in the default sRGB space.
	 * 
	 * @return the blue component.
	 * @see #getRGB
	 */
	public int getBlue() {
		return (getRGBA() >> 0) & 0xFF;
	}

	public float getBlueF() {

		return getBlue() / 255f;

	}

	/**
	 * Returns the alpha component in the range 0-255.
	 * 
	 * @return the alpha component.
	 * @see #getRGB
	 */
	public int getAlpha() {
		return (getRGBA() >> 24) & 0xff;
	}

	public float getAlphaF() {

		return getAlpha() / 255f;

	}

	/**
	 * Returns the RGB value representing the color in the default sRGB
	 * {@link ColorModel}. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7
	 * are blue).
	 * 
	 * @return the RGB value of the color in the default sRGB
	 *         <code>ColorModel</code>.
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @since JDK1.0
	 */
	public int getRGBA() {
		return rgba;
	}

	public Color brighter(double factor) {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		int alpha = getAlpha();

		/*
		 * From 2D group: 1. black.brighter() should return grey 2. applying brighter to
		 * blue will always return blue, brighter 3. non pure color (non zero rgb) will
		 * eventually return white
		 */
		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i, alpha);
		}
		if (r > 0 && r < i) r = i;
		if (g > 0 && g < i) g = i;
		if (b > 0 && b < i) b = i;

		return new Color(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255), Math.min((int) (b / factor), 255), alpha);
	}

	private static final double FACTOR = 0.9;

	/**
	 * Creates a new <code>Color</code> that is a brighter version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a brighter version of this
	 * <code>Color</code>. The {@code alpha} value is preserved. Although
	 * <code>brighter</code> and <code>darker</code> are inverse operations, the
	 * results of a series of invocations of these two methods might be inconsistent
	 * because of rounding errors.
	 * 
	 * @return a new <code>Color</code> object that is a brighter version of this
	 *         <code>Color</code> with the same {@code alpha} value.
	 * @since JDK1.0
	 */
	public Color brighter() {
		return brighter(FACTOR);
	}

	/**
	 * Creates a new <code>Color</code> that is a darker version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a darker version of this
	 * <code>Color</code>. The {@code alpha} value is preserved. Although
	 * <code>brighter</code> and <code>darker</code> are inverse operations, the
	 * results of a series of invocations of these two methods might be inconsistent
	 * because of rounding errors.
	 * 
	 * @return a new <code>Color</code> object that is a darker version of this
	 *         <code>Color</code> with the same {@code alpha} value.
	 * @since JDK1.0
	 */
	public Color darker() {
		return new Color(Math.max((int) (getRed() * FACTOR), 0), Math.max((int) (getGreen() * FACTOR), 0), Math.max((int) (getBlue() * FACTOR), 0), getAlpha());
	}

	public String toString() {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		return "Color:{" + r + "," + g + "," + b + "}";
	}

	public void getColorComponents(float[] array) {
		if (array.length < 3) return;

		array[0] = getRed() / 255f;
		array[1] = getGreen() / 255f;
		array[2] = getBlue() / 255f;
	}

	/**
	 * Returns whether or not the RGB values of the given color match this one.
	 * 
	 * @param color
	 * @return
	 */
	public boolean rgbMatches(Color color) {
		return (getRed() == color.getRed() && getGreen() == color.getGreen() && getBlue() == color.getBlue());
	}

	/**
	 * Blends the to colors, gradually fading the "from" color to the "to" color
	 * based on the given normalized multiplier (a 0-1 value, where 1 is full
	 * transitioned).
	 * 
	 * @param from  - starting color
	 * @param to    - what color to transition to
	 * @param store - the Color object to store the blended colors into (to prevent
	 *              making garbage)
	 * @param mult  - how much of the transition is completed (0 = 100% the from
	 *              color, 1 = 100% the to color)
	 * @return
	 */
	public static Color blend(Color from, Color to, Color store, double mult) {
		// May not be the best approach, but if it looks good, then whatever.
		int r1 = from.getRed();
		int g1 = from.getGreen();
		int b1 = from.getBlue();
		int a1 = from.getAlpha();

		int r2 = to.getRed();
		int g2 = to.getGreen();
		int b2 = to.getBlue();
		int a2 = to.getAlpha();

		store.set((int) mix(r1, r2, mult), (int) mix(g1, g2, mult), (int) mix(b1, b2, mult), (int) mix(a1, a2, mult));

		return store;
	}

	public static double mix(double x, double y, double a) {
		return x + (y - x) * a;
	}

	private static void testColorValueRange(int r, int g, int b, int a) {
		boolean rangeError = false;
		String badComponentString = "";

		if (a < 0 || a > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Alpha";
		}
		if (r < 0 || r > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Red";
		}
		if (g < 0 || g > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Green";
		}
		if (b < 0 || b > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Blue";
		}
		if (rangeError == true) {
			throw new IllegalArgumentException("Color parameter outside of expected range: " + badComponentString);
		}
	}

	public static Color match(String string) {
		String matchName = string.toLowerCase();
		
		Field[] declaredFields = Color.class.getDeclaredFields();
		for (Field field : declaredFields) {
		    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
		    	if ( field.getName().toLowerCase().replace("_", "").equals(matchName)) {
		    		try {
						return (Color) field.get(null);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
		    	}
		    }
		}
		
		return null;
	}
}
