#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normals;
in vec4 bones;
in vec4 boneWeights;

out vec2 pass_textureCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void) {


    gl_Position = projectionMatrix * viewMatrix * transformationMatrix * vec4(position,1.0);
    pass_textureCoords = textureCoords;
}