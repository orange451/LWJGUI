package lwjgui.gl;

public class BlurShader extends GenericShader {

	public BlurShader() {
		super(
			BlurShader.class.getResource("blur_vert.glsl"),
			BlurShader.class.getResource("blur_frag.glsl")
		);
	}
}
