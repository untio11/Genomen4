#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normals;
in ivec4 bones;
in vec4 boneWeights;

out vec2 pass_textureCoords;
out vec3 pass_normal;

uniform mat4 jointTransforms[15];

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){

	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);

	int boneInd[4] = int[4](bones.x, bones.y, bones.z, bones.w);

	for(int i=0; i<4; i++){
		mat4 jointTransform = jointTransforms[bones[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * boneWeights[i];

		vec4 worldNormal = jointTransform * vec4(normals, 0.0);
		totalNormal += worldNormal * boneWeights[i];
	}

	gl_Position = projectionMatrix * viewMatrix * transformationMatrix * totalLocalPos;
	pass_normal = totalNormal.xyz;
	pass_textureCoords = textureCoords;

}
