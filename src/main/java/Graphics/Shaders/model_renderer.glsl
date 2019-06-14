#version 430

layout(local_size_x = 32, local_size_y = 32) in;
layout(rgba32f, binding = 0) uniform image2D img_output;

layout(location = 0) uniform vec3  camera;
layout(location = 1) uniform float fov;
layout(location = 2) uniform mat3  camera_transform;
layout(location = 3) uniform int   offset;
layout(location = 4) uniform mat4  model_transform;
layout(location = 5) uniform int   tringle_count;

layout(std430, binding = 1) buffer ModelData {
    vec4 vertices[];
};

layout(std430, binding = 2) buffer IndexData {
    int indices[];
};

layout(std430, binding = 3) buffer BoundingSpheres {
    vec4 spheres[];
};

ivec2 pixel_coords;
ivec2 dimensions;

vec3 discriminant(vec3 ray, vec3 source, vec3 target, float sphere_radius) {
    vec3 omc = source - target;
    float b = dot(omc, ray);
    float c = dot(omc, omc) - sphere_radius * sphere_radius;
    return vec3(b, c, (b * b - c));
}

bool intersectsBoundingSphere(vec3 origin, vec3 ray, vec4 sphere) {
    vec3 disc = discriminant(ray, origin, sphere.xyz, sphere.w);
    if (min(-disc.x + sqrt(disc.z), -disc.x - sqrt(disc.z)) < 0) {
        return false;
    } else {
        return true;
    }
}

vec3 getRay() {
    // Map pixel coordinates to normalized space: [-1,1]^2 (sorta, taking care of aspect ratio)
    float x = (float(pixel_coords.x * 2.0 - dimensions.x) / (dimensions.x)) * (16.0/9.0) + camera.x;
    float y = (float(pixel_coords.y * 2.0 - dimensions.y) / dimensions.y) + camera.y;
    float z = camera.z + fov;

    return normalize(vec3(x, y, z) - camera) * camera_transform;
}

vec4 trace() {
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
    vec3 ray = getRay();

    float u, v, t; // u and v are for interpolating, t is to find the intersection P
    vec3 v01, v02; // two edges starting at v0
    vec3 pvec, tvec, qvec; // Three stupid names
    float det; // Determinant

    vec3 normal, intersection; // Normal of the triangle, the intersection point and base color for shading

    float closest = 1.0 / 0.0;

    for (int i = 0; i < tringle_count; i++) {
        if (intersectsBoundingSphere(camera, ray, spheres[i])) {
            return vec4(0.0, 1.0, 0.5, 1.0);
        }
        v01  = vertices[indices[(i * 3) + 1]].xyz - vertices[indices[(i * 3) + 0]].xyz;
        v02  = vertices[indices[(i * 3) + 2]].xyz - vertices[indices[(i * 3) + 0]].xyz;
        pvec = cross(ray, v02);
        det  = dot(v01, pvec);

        if (abs(det) < 0.000001) { // Too close to parallel. If we remove abs(), we get backface culling
            continue;
        }

        tvec = camera - vertices[indices[(i * 3) + 0]].xyz;
        u    = dot(tvec, pvec) / det;
        if (u < 0.0 || u > 1.0) {
            continue;
        }

        qvec = cross(tvec, v01);
        v = dot(ray, qvec) / det;
        if (v < 0.0 || v + u > 1.0) {
            continue;
        }

        t = dot(v02, qvec) / det;
        if (t >= closest || t < 0.0) { // Not the closest, or triangle is behind us
            continue;
        }

        closest = t;

        color = vec4(1.0, 0.0, 0.0, 1.0);
    }

    return color;
}

void main() {
    // Get (x,y) position of this pixel in the texture (index in global work group)
    pixel_coords = ivec2(gl_GlobalInvocationID.xy);
    dimensions = imageSize(img_output);
    vec4 color = trace();

    imageStore(img_output, pixel_coords, color);
}
