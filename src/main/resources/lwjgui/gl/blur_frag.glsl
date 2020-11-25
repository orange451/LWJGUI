#version 330

uniform sampler2D colorSampler;
uniform vec4 uColor;
uniform float uBlurSize;
uniform vec2 uTexelSize;
uniform vec4 uCornerRadii;

in vec2 passTexCoord;
in vec4 passColor;

out vec4 outColor;

float roundCorner( vec2 edge, vec2 cornerCenter, float radius, vec2 coords ) {
	edge /= uTexelSize;
	coords /= uTexelSize;
	
	float len1 = length(coords - cornerCenter);
	float len2 = length(coords - edge);
	float temp = (len1 < len2) ? 1.0 : 0.0;
	return (len1 > radius) ? temp : 1.0;
}

void main(void) {
   vec4 result = vec4(0.0);
   
   vec2 hlim = vec2(float(-uBlurSize) * 0.5 + 0.5);
   for (int i = 0; i < int(uBlurSize); ++i) {
      for (int j = 0; j < int(uBlurSize); ++j) {
         vec2 offset = (hlim + vec2(float(i), float(j))) * uTexelSize;
         result += vec4(texture(colorSampler, passTexCoord + offset).rgb,1.0);
      }
   }
   
	// Calculate final color
	vec4 final = result / float(uBlurSize*uBlurSize);
	final = final * vec4(1,1,1,uColor.a);
	final = final + vec4(uColor.rgb,0.0);
	
	// Apply round corner logic
	vec4 baseSample = texture(colorSampler, passTexCoord);
	final = mix( baseSample, final, 
		min( 
			roundCorner( vec2(0.0), vec2( uCornerRadii.w ), uCornerRadii.w, passTexCoord ),
			min(
				roundCorner( vec2(0.0, 1.0), vec2(0, 1.0 / uTexelSize.y) - vec2( -uCornerRadii.x, uCornerRadii.x ), uCornerRadii.x, passTexCoord ),
				min(
					roundCorner( vec2(1.0, 0.0), vec2(1.0 / uTexelSize.x, 0.0) - vec2( uCornerRadii.z, -uCornerRadii.z ), uCornerRadii.z, passTexCoord ),
					roundCorner( vec2(1.0), (1.0 / uTexelSize) - vec2( uCornerRadii.y ), uCornerRadii.y, passTexCoord )
				)
			)
		)
	);
	
	// Output to screen
	outColor = final;
}
