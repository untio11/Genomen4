#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normals;
in ivec4 bones;
in vec4 boneWeights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform mat4 jointTransforms[12];

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
	
	mat4 boneTransform = jointTransforms[bones[0]] * boneWeights[0];
    boneTransform += jointTransforms[bones[1]] * boneWeights[1];
    boneTransform += jointTransforms[bones[2]] * boneWeights[2];
    boneTransform += jointTransforms[bones[3]] * boneWeights[3];

    vec4 locPos = boneTransform * vec4(position, 1.0);
	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * locPos;

	vec4 locNor = boneTransform * vec4(normals, 0.0);
	pass_normal = locNor.xyz;
	pass_textureCoords = textureCoords;

}