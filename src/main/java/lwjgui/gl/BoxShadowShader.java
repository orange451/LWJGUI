package lwjgui.gl;

public class BoxShadowShader extends GenericShader {

	public BoxShadowShader() {
		super(
			BoxShadowShader.class.getResource("box_shadow_vert.glsl"),
			BoxShadowShader.class.getResource("box_shadow_frag.glsl")
		);
	}
}
