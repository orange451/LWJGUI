#version 120

uniform sampler2D colorSampler;
uniform vec4 uColor;
uniform float uBlurSize;
uniform vec2 uTexelSize;
uniform vec4 uCornerRadii;

varying vec4 passColor;

void main(void) {
	vec4 result = vec4(0.0);
	
	vec2 hlim = vec2(float(-uBlurSize) * 0.5 + 0.5);
	for (int i = 0; i < uBlurSize; ++i) {
		for (int j = 0; j < uBlurSize; ++j) {
			vec2 offset = (hlim + vec2(float(i), float(j))) * uTexelSize;
			result += vec4(texture2D(colorSampler, gl_TexCoord[0].st + offset).rgb,1.0);
		}
	}
	
	// Calculate final color
	vec4 final = result / float(uBlurSize*uBlurSize);
	final = final * vec4(1,1,1,uColor.a);
	final = final + vec4(uColor.rgb,0.0);
	
	// Output to screen
	gl_FragColor = final;
}
