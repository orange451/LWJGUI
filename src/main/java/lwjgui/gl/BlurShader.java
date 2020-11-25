package lwjgui.gl;

public class BlurShader extends GenericShader {

	public BlurShader() {
		super(
			Thread.currentThread().getContextClassLoader().getResource("lwjgui/gl/blur_vert.glsl"),
			Thread.currentThread().getContextClassLoader().getResource("lwjgui/gl/blur_frag.glsl")
		);
	}
}
