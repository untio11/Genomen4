#version 430

layout(local_size_x = 32, local_size_y = 32) in;
layout(rgba32f, binding = 0) uniform image2D img_output;

layout(location = 0) uniform vec3  camera;
layout(location = 1) uniform float fov;
layout(location = 2) uniform mat3  transform;
layout(location = 3) uniform int   tringle_amount;

layout(std430, binding = 1) buffer VertexPositions {
    vec4 parsed_positions[];
};

layout(std430, binding = 2) buffer VertexNormals {
    vec4 parsed_normals[];
};

layout(std430, binding = 3) buffer TexCoods {
    vec2 parsed_tex[];
};

layout(std430, binding = 4) buffer Indices {
    ivec3 indices[];
};

ivec2 pixel_coords;

vec3 getRay() {
    ivec2 dimensions = imageSize(img_output);
    // Map pixel coordinates to normalized space: [-1,1]^2 (sorta, taking care of aspect ratio)
    float x = (float(pixel_coords.x * 2.0 - dimensions.x) / (dimensions.x)) * (16.0/9.0) + camera.x;
    float y = (float(pixel_coords.y * 2.0 - dimensions.y) / dimensions.y) + camera.y;
    float z = camera.z + fov;

    return normalize(vec3(x, y, z) - camera) * transform;
}

void main() {
    // Get (x,y) position of this pixel in the texture (index in global work group)
    pixel_coords = ivec2(gl_GlobalInvocationID.xy);

    imageStore(img_output, pixel_coords, vec4(getRay(), 1.0));
}
