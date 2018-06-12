#version 330

uniform sampler2D colorSampler;

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

void main(void) {
	vec4 color = vec4(1.0)*passColor;//texture(colorSampler, passTexCoord);
	outColor = color;
}
