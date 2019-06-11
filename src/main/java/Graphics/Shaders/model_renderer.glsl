#version 430

layout(local_size_x = 32, local_size_y = 32) in;
layout(rgba32f, binding = 0) uniform image2D img_output;

layout(location = 0) uniform vec3  camera;
layout(location = 1) uniform float fov;
layout(location = 2) uniform mat3  transform;
layout(location = 3) uniform int offset;

layout(std430, binding = 1) buffer ModelData {
    vec4 vertices[];
};

ivec2 pixel_coords;
ivec2 dimensions;

vec3 getRay() {
    // Map pixel coordinates to normalized space: [-1,1]^2 (sorta, taking care of aspect ratio)
    float x = (float(pixel_coords.x * 2.0 - dimensions.x) / (dimensions.x)) * (16.0/9.0) + camera.x;
    float y = (float(pixel_coords.y * 2.0 - dimensions.y) / dimensions.y) + camera.y;
    float z = camera.z + fov;

    return normalize(vec3(x, y, z) - camera) * transform;
}

vec4 trace() {
    return vec4(1.0, 0.0, 0.0, 0.2);
}

void main() {
    // Get (x,y) position of this pixel in the texture (index in global work group)
    pixel_coords = ivec2(gl_GlobalInvocationID.xy);
    dimensions = imageSize(img_output);
    vec4 color = vec4(trace().rgb, 1.0);
    vec4 img_color = vec4(imageLoad(img_output, pixel_coords).rgb, 1.0);
    groupMemoryBarrier();

    imageStore(img_output, pixel_coords, ( 0.0 * color ) + ( (1.0) * img_color ));
}
