#version 330

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 worldMatrix;

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec4 inColor;

out vec2 passTexCoord;
out vec4 passColor;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * worldMatrix * vec4(inPos,1.0);
	passTexCoord = inTexCoord;
	passColor = inColor;
}
