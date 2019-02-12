package lwjgui;

import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;

public class Color {
	public final static Color white		= new Color(255, 255, 255);
	public final static Color WHITE 		= white;

	public final static Color lightGray  = new Color(211, 211, 211);
	public final static Color LIGHT_GRAY = lightGray;

	public final static Color silver = new Color(192, 192, 192);
	public final static Color SILVER = silver;

	public final static Color gray 		= new Color(128, 128, 128);
	public final static Color GRAY 		= gray;

	public final static Color dimgray	= new Color(169, 169, 169);
	public final static Color DIM_GRAY	= dimgray;

	public final static Color darkGray  = new Color(64, 64, 64);
	public final static Color DARK_GRAY = darkGray;

	public final static Color lightBlack  = new Color( 10, 10, 10 );
	public final static Color LIGHT_BLACK = lightBlack;

	public final static Color whiteSmoke  = new Color( 245, 245, 245 );
	public final static Color WHITE_SMOKE = whiteSmoke;

	public final static Color black     = new Color(0, 0, 0);
	public final static Color BLACK 	= black;

	public final static Color red       = new Color(255, 0, 0);
	public final static Color RED 		= red;

	public final static Color pink      = new Color(255, 175, 175);
	public final static Color PINK 		= pink;

	public final static Color orange    = new Color(255, 200, 0);
	public final static Color ORANGE 	= orange;

	public final static Color yellow    = new Color(255, 255, 0);
	public final static Color YELLOW 	= yellow;

	public final static Color lightYellow  = new Color(255, 238, 158);
	public final static Color LIGHT_YELLOW = lightYellow;

	public final static Color lightBlue  = new Color(158, 238, 255);
	public final static Color LIGHT_BLUE = lightBlue;

	public final static Color green     = new Color(0, 255, 0);
	public final static Color GREEN 	= green;

	public final static Color magenta   = new Color(255, 0, 255);
	public final static Color MAGENTA 	= magenta;

	public final static Color cyan      = new Color(0, 255, 255);
	public final static Color CYAN 		= cyan;

	public final static Color blue      = new Color(0, 0, 255);
	public final static Color BLUE 		= blue;

	public final static Color aqua      = new Color(3, 158, 211);
	public final static Color AQUA 		= aqua;
	
	public final static Color coral      = new Color("#FF7F50");
	public final static Color CORAL 	 = coral;

	public final static Color transparent = new Color(255,255,255,0);
	public final static Color TRANSPARENT = transparent;

	private int value;
	private NVGColor nvg;
	private Vector4f vector;

	private static void testColorValueRange(int r, int g, int b, int a) {
		boolean rangeError = false;
		String badComponentString = "";

		if ( a < 0 || a > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Alpha";
		}
		if ( r < 0 || r > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Red";
		}
		if ( g < 0 || g > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Green";
		}
		if ( b < 0 || b > 255) {
			rangeError = true;
			badComponentString = badComponentString + " Blue";
		}
		if ( rangeError == true ) {
			throw new IllegalArgumentException("Color parameter outside of expected range:"
					+ badComponentString);
		}
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green,
	 * and blue values in the range (0 - 255).
	 * The actual color used in rendering depends
	 * on finding the best match given the color space
	 * available for a given output device.
	 * Alpha is defaulted to 255.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code>
	 *        or <code>b</code> are outside of the range
	 *        0 to 255, inclusive
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
	 * Creates an sRGB color with the specified red, green, blue, and alpha
	 * values in the range (0 - 255).
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code>,
	 *        <code>b</code> or <code>a</code> are outside of the range
	 *        0 to 255, inclusive
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
		value = ((a & 0xFF) << 24) |
				((r & 0xFF) << 16) |
				((g & 0xFF) << 8)  |
				((b & 0xFF) << 0);
		testColorValueRange(r,g,b,a);
	}

	/**
	 * Creates an opaque sRGB color with the specified combined RGB value
	 * consisting of the red component in bits 16-23, the green component
	 * in bits 8-15, and the blue component in bits 0-7.  The actual color
	 * used in rendering depends on finding the best match given the
	 * color space available for a particular output device.  Alpha is
	 * defaulted to 255.
	 *
	 * @param rgb the combined RGB components
	 * @see java.awt.image.ColorModel#getRGBdefault
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGB
	 */
	public Color(int rgb) {
		value = 0xff000000 | rgb;
	}

	/**
	 * Creates an opaque sRGB color with the specified red, green, and blue
	 * values in the range (0.0 - 1.0).  Alpha is defaulted to 1.0.  The
	 * actual color used in rendering depends on finding the best
	 * match given the color space available for a particular output
	 * device.
	 *
	 * @throws IllegalArgumentException if <code>r</code>, <code>g</code>
	 *        or <code>b</code> are outside of the range
	 *        0.0 to 1.0, inclusive
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @see #getRGB
	 */
	public Color(float r, float g, float b) {
		this( (int) (r*255), (int) (g*255), (int) (b*255));
	}

	/**
	 * Creates a Color from a given HEX value.
	 * 
	 * @param hex e.g. #FFFFFF for white.
	 */
	public Color(String hex) {
		   this(Integer.valueOf(hex.substring( 1, 3 ), 16 ), 
				Integer.valueOf(hex.substring( 3, 5 ), 16 ), 
				Integer.valueOf(hex.substring( 5, 7 ), 16 ));
		}
	
	/**
	 * Returns a NanoVG color with the same component values.
	 * @return
	 */
	public NVGColor getNVG() {
		if ( nvg == null ) {
			nvg = NVGColor.calloc();
			nvg.r(getRed()/255.0f);
			nvg.g(getGreen()/255.0f);
			nvg.b(getBlue()/255.0f);
			nvg.a(getAlpha()/255.0f);
		}

		return nvg;
	}

	/**
	 * Returns the vector4f value with the same component values.
	 * @return
	 */
	public Vector4f getVector() {
		if ( vector == null ) {
			vector = new Vector4f(getRed()/255.0f, getGreen()/255.0f, getBlue()/255.0f, getAlpha()/255.0f);
		}

		return vector;
	}

	/**
	 * Returns the red component in the range 0-255 in the default sRGB
	 * space.
	 * @return the red component.
	 * @see #getRGB
	 */
	public int getRed() {
		return (getRGB() >> 16) & 0xFF;
	}

	/**
	 * Returns the green component in the range 0-255 in the default sRGB
	 * space.
	 * @return the green component.
	 * @see #getRGB
	 */
	public int getGreen() {
		return (getRGB() >> 8) & 0xFF;
	}

	/**
	 * Returns the blue component in the range 0-255 in the default sRGB
	 * space.
	 * @return the blue component.
	 * @see #getRGB
	 */
	public int getBlue() {
		return (getRGB() >> 0) & 0xFF;
	}

	/**
	 * Returns the alpha component in the range 0-255.
	 * @return the alpha component.
	 * @see #getRGB
	 */
	public int getAlpha() {
		return (getRGB() >> 24) & 0xff;
	}

	/**
	 * Returns the RGB value representing the color in the default sRGB
	 * {@link ColorModel}.
	 * (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are
	 * blue).
	 * @return the RGB value of the color in the default sRGB
	 *         <code>ColorModel</code>.
	 * @see java.awt.image.ColorModel#getRGBdefault
	 * @see #getRed
	 * @see #getGreen
	 * @see #getBlue
	 * @since JDK1.0
	 */
	public int getRGB() {
		return value;
	}

	public Color brighter(double factor) {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		int alpha = getAlpha();

		/* From 2D group:
		 * 1. black.brighter() should return grey
		 * 2. applying brighter to blue will always return blue, brighter
		 * 3. non pure color (non zero rgb) will eventually return white
		 */
		int i = (int)(1.0/(1.0-factor));
		if ( r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i, alpha);
		}
		if ( r > 0 && r < i ) r = i;
		if ( g > 0 && g < i ) g = i;
		if ( b > 0 && b < i ) b = i;

		return new Color(Math.min((int)(r/factor), 255),
				Math.min((int)(g/factor), 255),
				Math.min((int)(b/factor), 255),
				alpha);
	}

	private static final double FACTOR = 0.9;

	/**
	 * Creates a new <code>Color</code> that is a brighter version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a brighter version
	 * of this <code>Color</code>.
	 * The {@code alpha} value is preserved.
	 * Although <code>brighter</code> and
	 * <code>darker</code> are inverse operations, the results of a
	 * series of invocations of these two methods might be inconsistent
	 * because of rounding errors.
	 * @return     a new <code>Color</code> object that is
	 *                 a brighter version of this <code>Color</code>
	 *                 with the same {@code alpha} value.
	 * @see        java.awt.Color#darker
	 * @since      JDK1.0
	 */
	public Color brighter() {
		return brighter(FACTOR);
	}

	/**
	 * Creates a new <code>Color</code> that is a darker version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a darker version of
	 * this <code>Color</code>.
	 * The {@code alpha} value is preserved.
	 * Although <code>brighter</code> and
	 * <code>darker</code> are inverse operations, the results of a series
	 * of invocations of these two methods might be inconsistent because
	 * of rounding errors.
	 * @return  a new <code>Color</code> object that is
	 *                    a darker version of this <code>Color</code>
	 *                    with the same {@code alpha} value.
	 * @see        java.awt.Color#brighter
	 * @since      JDK1.0
	 */
	public Color darker() {
		return new Color(Math.max((int)(getRed()  *FACTOR), 0),
				Math.max((int)(getGreen()*FACTOR), 0),
				Math.max((int)(getBlue() *FACTOR), 0),
				getAlpha());
	}

	public String toString() {
		int r = getRed();
		int g = getGreen();
		int b = getBlue();
		return "Color:{" + r + "," + g + "," + b + "}";
	}

	public void getColorComponents(float[] array) {
		if ( array.length < 3 ) 
			return;

		array[0] = getRed()/255f;
		array[1] = getGreen()/255f;
		array[2] = getBlue()/255f;
	}
}
