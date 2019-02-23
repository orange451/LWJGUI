package lwjgui.scene.control.text_input;

class GlyphData { // This class could be avoided if NanoVG author wouldn't ignore me.
	float width;
	float x;
	private String c;
	boolean SPECIAL;
	
	public GlyphData( float x, float width, String car ) {
		this.c = car;
		this.x = x;
		this.width = width;
	}
	
	public GlyphData( float x, float width, String car, boolean special ) {
		this( x, width, car );
		this.SPECIAL = special;
	}
	
	public String character() {
		return c;
	}
	
	public float x() {
		return x;
	}
	
	public float width() {
		return width;
	}
}