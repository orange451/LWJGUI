#version 120

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;

attribute vec3 inPos;
attribute vec2 inTexCoord;
attribute vec4 inColor;

varying vec4 passColor;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * worldMatrix * vec4(inPos,1.0);
	gl_TexCoord[0].st = gl_MultiTexCoord0.xy;

	passColor = inColor;
}
