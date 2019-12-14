#version 330

uniform vec4 box;
uniform vec2 window;
uniform float sigma;

layout(location = 0) in vec3 inPos;
layout(location = 1) in vec2 inTexCoord;
layout(location = 2) in vec4 inColor;

out vec2 vertex;

void main() {
  float padding = 3.0 * sigma;
  vertex = mix(box.xy - padding, box.zw + padding, inPos.xy);
  gl_Position = vec4(vertex / window * 2.0 - 1.0, 0.0, 1.0);
}