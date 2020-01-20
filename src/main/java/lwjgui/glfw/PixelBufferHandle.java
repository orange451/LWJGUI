package lwjgui.glfw;

import org.lwjgl.glfw.GLFW;

public final class PixelBufferHandle {

	private int redBits = 8;
	private int redBitsAccum = 0;
	private int greenBits = 8;
	private int greenBitsAccum = 0;
	private int blueBits = 8;
	private int blueBitsAccum = 0;
	private int alphaBits = 8;
	private int alphaBitsAccum = 0;
	private int depthBits = 24;
	private int stencilBits = 8;
	private int auxBuffers = 0;
	private int samples = 0;
	private int refreshRate = GLFW.GLFW_DONT_CARE;
	private int stereo = GLFW.GLFW_FALSE;
	private int srgbCapable = GLFW.GLFW_FALSE;
	private int doubleBuffer = GLFW.GLFW_TRUE;

	public int getRedBits() {
		return redBits;
	}

	public void setRedBits(int redBits) {
		this.redBits = this.checkInputDC(redBits);
	}

	public int getRedBitsAccum() {
		return redBitsAccum;
	}

	public void setRedBitsAccum(int redBitsAccum) {
		this.redBitsAccum = this.checkInputDC(redBitsAccum);
	}

	public int getGreenBits() {
		return greenBits;
	}

	public void setGreenBits(int greenBits) {
		this.greenBits = this.checkInputDC(greenBits);
	}

	public int getGreenBitsAccum() {
		return greenBitsAccum;
	}

	public void setGreenBitsAccum(int greenBitsAccum) {
		this.greenBitsAccum = this.checkInputDC(greenBitsAccum);
	}

	public int getBlueBits() {
		return blueBits;
	}

	public void setBlueBits(int blueBits) {
		this.blueBits = this.checkInputDC(blueBits);
	}

	public int getBlueBitsAccum() {
		return blueBitsAccum;
	}

	public void setBlueBitsAccum(int blueBitsAccum) {
		this.blueBitsAccum = this.checkInputDC(blueBitsAccum);
	}

	public int getAlphaBits() {
		return alphaBits;
	}

	public void setAlphaBits(int alphaBits) {
		this.alphaBits = this.checkInputDC(alphaBits);
	}

	public int getAlphaBitsAccum() {
		return alphaBitsAccum;
	}

	public void setAlphaBitsAccum(int alphaBitsAccum) {
		this.alphaBitsAccum = this.checkInputDC(alphaBitsAccum);
	}

	public int getDepthBits() {
		return depthBits;
	}

	public void setDepthBits(int depthBits) {
		this.depthBits = this.checkInputDC(depthBits);
	}

	public int getStencilBits() {
		return stencilBits;
	}

	public void setStencilBits(int stencilBits) {
		this.stencilBits = this.checkInputDC(stencilBits);
	}

	public int getAuxBuffers() {
		return auxBuffers;
	}

	public void setAuxBuffers(int auxBuffers) {
		this.auxBuffers = this.checkInput(auxBuffers);
	}

	public int getSamples() {
		return samples;
	}

	public void setSamples(int samples) {
		this.samples = this.checkInput(samples);
	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public void setRefreshRate(int refreshRate) {
		this.refreshRate = this.checkInputDC(refreshRate);
	}

	public int getStereo() {
		return stereo;
	}

	public void setStereo(int stereo) {
		this.stereo = this.checkBoolean(stereo);
	}

	public int getSRGBCapable() {
		return srgbCapable;
	}

	public void setSrgbCapable(int srgbCapable) {
		this.srgbCapable = this.checkBoolean(srgbCapable);
	}

	public int getDoubleBuffer() {
		return doubleBuffer;
	}

	public void setDoubleBuffer(int doubleBuffer) {
		this.doubleBuffer = this.checkBoolean(doubleBuffer);
	}

	private int checkInputDC(int input) {
		if (input < 0)
			if (input != GLFW.GLFW_DONT_CARE)
				return GLFW.GLFW_DONT_CARE;
		return input;
	}

	private int checkBoolean(int input) {
		if (input <= 0)
			return GLFW.GLFW_FALSE;
		else
			return GLFW.GLFW_TRUE;
	}

	private int checkInput(int input) {
		if (input >= 0)
			return input;
		else
			return 0;
	}

	@Override
	public String toString() {
		return "PixelBufferHandle [redBits=" + redBits + ", redBitsAccum=" + redBitsAccum + ", greenBits=" + greenBits
				+ ", greenBitsAccum=" + greenBitsAccum + ", blueBits=" + blueBits + ", blueBitsAccum=" + blueBitsAccum
				+ ", alphaBits=" + alphaBits + ", alphaBitsAccum=" + alphaBitsAccum + ", depthBits=" + depthBits
				+ ", stencilBits=" + stencilBits + ", auxBuffers=" + auxBuffers + ", samples=" + samples
				+ ", refreshRate=" + refreshRate + ", stereo=" + (stereo == GLFW.GLFW_TRUE ? "true" : "false")
				+ ", srgbCapable=" + (srgbCapable == GLFW.GLFW_TRUE ? "true" : "false") + ", doubleBuffer="
				+ (doubleBuffer == GLFW.GLFW_TRUE ? "true" : "false") + "]";
	}
}
