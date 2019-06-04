#version 430

layout(local_size_x = 32, local_size_y = 32) in;
layout(rgba32f, binding = 0) uniform image2D img_output;

layout(location = 0) uniform vec3 camera;
layout(location = 1) uniform float fov;
layout(location = 2) uniform mat3 transform;

ivec2 pixel_coords;

vec3 getRay() {
    ivec2 dimensions = imageSize(img_output);
    // Map pixel coordinates to normalized space: [-1,1]^2 (sorta, taking care of aspect ratio)
    float x = (float(pixel_coords.x * 2.0 - dimensions.x) * 16.0 / (dimensions.x * 9.0)) + camera.x;
    float y = (float(pixel_coords.y * 2.0 - dimensions.y) / dimensions.y) + camera.y;
    float z = camera.z + fov;
    return normalize(vec3(x, y, z) - camera) * transform;
}

void main() {
    // Get (x,y) position of this pixel in the texture (index in global work group)
    pixel_coords = ivec2(gl_GlobalInvocationID.xy);

    imageStore(img_output, pixel_coords, vec4(getRay(), 1.0));
}
