package lwjgui.gl;

public class BoxShadowShader extends GenericShader {

	public BoxShadowShader() {
		super(
			Thread.currentThread().getContextClassLoader().getResource("lwjgui/gl/box_shadow_vert.glsl"),
			Thread.currentThread().getContextClassLoader().getResource("lwjgui/gl/box_shadow_frag.glsl")
		);
	}
}
