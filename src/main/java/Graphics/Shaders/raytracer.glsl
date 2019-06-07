#version 430

layout(local_size_x = 32, local_size_y = 32) in;
layout(rgba32f, binding = 0) uniform image2D img_output;

layout(location = 0) uniform vec3  camera;
layout(location = 1) uniform float fov;
layout(location = 2) uniform mat3  transform;
layout(location = 3) uniform int   tringle_amount;

layout(std430, binding = 1) buffer VertexPositions { // Contains the vertices of all visible tringles
    vec4 parsed_positions[];
};

layout(std430, binding = 2) buffer VertexNormals {
    vec4 parsed_normals[];
};

layout(std430, binding = 3) buffer ColorCoods {
    vec4 parsed_colors[];
};

layout(std430, binding = 4) buffer Indices {
    int indices[];
};

layout(std430, binding = 5) buffer Offsets { // Offsets to find the start of each chunk
    int offsets[];
};

ivec2 pixel_coords;

vec4 phong(vec3 point, vec3 normal, vec3 light_source, vec4 base_color, float shininess) {
    vec4 result = 0.0 * base_color; // ambient light initial color

    vec3 L = (light_source - point); // Direction from point to light source
    float distance = length(L);
    float angle = max(dot(normal, L) / (length(normal) * length(L)), 0.0); // Angle between normal and light source

    // Diffuse contribution
    result += angle * base_color;

    vec3 E = normalize(point - camera); // viewdirection
    vec3 halfway = normalize(L + E);
    float spec = pow(max(dot(normal, halfway), 0.0), shininess);
    result += spec * base_color;
    return result * max((sqrt(15.0 - distance)/sqrt(15.0)), 0.2);
}
vec4 shadowBounce(vec3 origin, vec3 light_source) {
    float distance = length(light_source - origin); // origin + distance * direction should be light source
    vec3 direction = normalize(light_source - origin);

    float u, v, t; // u and v are for interpolating, t is to find the intersection P
    vec3 v01, v02; // two edges starting at v0
    vec3 pvec, tvec, qvec; // Three stupid names
    float det; // Determinant

    for (int i = 0; i < tringle_amount; i++) {
        v01  = parsed_positions[indices[(i * 3) + 1]].xyz - parsed_positions[indices[(i * 3) + 0]].xyz;
        v02  = parsed_positions[indices[(i * 3) + 2]].xyz - parsed_positions[indices[(i * 3) + 0]].xyz;
        pvec = cross(direction, v02);
        det = dot(v01, pvec);

        if (abs(det) < 0.000001) { // Too close to parallel. If we remove abs(), we get backface culling
            continue;
        }

        tvec = origin - parsed_positions[indices[(i * 3) + 0]].xyz;
        u = dot(tvec, pvec) / det;
        if (u < 0.0 || u > 1.0) {
            continue;
        }

        qvec = cross(tvec, v01);
        v = dot(direction, qvec) / det;
        if (v < 0.0 || v + u > 1.0) {
            continue;
        }

        t = dot(v02, qvec) / det;
        if (t < 0.0 || t > distance) { // Triangle is behind us, or intersection happens behind the lightsource
            continue;
        }

        return vec4(0.65, 0.65, 0.65, 1.0);
    }

    return vec4(1.0, 1.0, 1.0, 1.0);
}

vec3 getRay() {
    ivec2 dimensions = imageSize(img_output);
    // Map pixel coordinates to normalized space: [-1,1]^2 (sorta, taking care of aspect ratio)
    float x = (float(pixel_coords.x * 2.0 - dimensions.x) / (dimensions.x)) * (16.0/9.0) + camera.x;
    float y = (float(pixel_coords.y * 2.0 - dimensions.y) / dimensions.y) + camera.y;
    float z = camera.z + fov;

    return normalize(vec3(x, y, z) - camera) * transform;
}

vec4 trace() {
    vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
    vec3 ray = getRay();

    float u, v, t; // u and v are for interpolating, t is to find the intersection P
    vec3 v01, v02; // two edges starting at v0
    vec3 pvec, tvec, qvec; // Three stupid names
    float det; // Determinant

    vec3 normal, intersection; // Normal of the triangle, the intersection point and base color for shading
    vec4 base_color;

    float closest = 1.0 / 0.0;

    for (int i = 0; i < tringle_amount; i++) {
        v01  = parsed_positions[indices[(i * 3) + 1]].xyz - parsed_positions[indices[(i * 3) + 0]].xyz;
        v02  = parsed_positions[indices[(i * 3) + 2]].xyz - parsed_positions[indices[(i * 3) + 0]].xyz;
        pvec = cross(ray, v02);
        det  = dot(v01, pvec);

        if (abs(det) < 0.000001) { // Too close to parallel. If we remove abs(), we get backface culling
            continue;
        }

        tvec = camera - parsed_positions[indices[(i * 3) + 0]].xyz;
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

        color = vec4(0.0, 0.0, 0.0, 1.0);
        closest = t;
        base_color = vec4((parsed_colors[indices[(i * 3) + 0]] * (1 - u - v) +
                      parsed_colors[indices[(i * 3) + 1]] * u +
                      parsed_colors[indices[(i * 3) + 2]] * v).xyz, 1.0
        );
        normal = normalize(cross(v01, v02));
        intersection = camera + t * ray;
        color += phong(intersection, normal, vec3(camera.x, 1.0, camera.z), base_color, 2.0);
        color *= shadowBounce(intersection + 0.0001 * normal, vec3(camera.x, 1.0, camera.z));
    }

    return color;
}

void main() {
    // Get (x,y) position of this pixel in the texture (index in global work group)
    pixel_coords = ivec2(gl_GlobalInvocationID.xy);

    imageStore(img_output, pixel_coords, trace());
}
