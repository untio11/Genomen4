#version 430

layout(local_size_x = 32, local_size_y = 32) in;
layout(rgba32f, binding = 0) uniform image2D img_output;

layout(location = 0) uniform vec3  camera;
layout(location = 1) uniform float fov;
layout(location = 2) uniform mat3  transform;
layout(location = 3) uniform ivec2 chunk_info; // # horizontal chunks, # vertical chunks,
layout(location = 4) uniform vec3  player_light;
layout(location = 5) uniform vec3  enemy_light;

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

layout(std430, binding = 6) buffer TopLefts {
    vec2 toplefts[];
};

layout(std430, binding = 7) buffer ChunkDimensions {
    vec4 chunk_dimensions[];
};

ivec2 pixel_coords;
ivec2 dimensions;
vec4 debugcolor;
float view_distance = 6.0;
float enemy_light_range = 3.5;


vec3 discriminant(vec3 ray, vec3 source, vec3 target, float sphere_radius) {
    vec3 omc = source - target;
    float b = dot(omc, ray);
    float c = dot(omc, omc) - sphere_radius * sphere_radius;
    return vec3(b, c, (b * b - c));
}

bool intersectsBoundingSphere(vec3 origin, vec3 ray, vec3 top_left, vec3 chunk_size) {
    vec3 center = top_left + 0.5 * chunk_size;
    vec3 disc = discriminant(ray, origin, center, length(center - top_left));
    return !(disc.z < 0);
}

vec4 phong(vec3 point, vec3 normal, vec3 light_source, vec4 base_color, float shininess) {
    vec4 result = 0.0 * base_color; // ambient light initial color
    vec3 L = (light_source - point); // Direction from point to light source

    result += max(dot(normal, L) / (length(normal) * length(L)), 0.0) * base_color;
    result += pow(max(dot(normal, normalize(L + normalize(point - camera))), 0.0), shininess) * base_color;

    return result;
}

vec4 shadowBounce(vec3 origin, vec3 light_source) {

    float u, v, t; // u and v are for interpolating, t is to find the intersection P
    vec3 v01, v02; // two edges starting at v0
    vec3 pvec, tvec, qvec; // Three stupid names
    float det; // Determinant
    vec3 direction;

    for (int j = 0; j < chunk_info.x * chunk_info.y; j++) {
        for (int i = offsets[j]; i < offsets[j + 1]; i++) {
            if (chunk_dimensions[j].y < 0.5) break;
            direction = normalize(light_source - origin);
            if (!(intersectsBoundingSphere(origin, direction, vec3(toplefts[j].x, 0.0, toplefts[j].y), chunk_dimensions[j].xyz))) break;

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
            if (t < 0.0 || t > length(light_source - origin)) { // Triangle is behind us, or intersection happens behind the lightsource
                continue;
            }

            return vec4(0.0, 0.0, 0.0, 1.0);
        }
    }

    return vec4(1.0, 1.0, 1.0, 1.0);
}

vec3 getRay() {
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

    float enemy_distance, player_distance;
    vec4 enemy_contribution, player_contribution;

    float closest = 1.0 / 0.0;

    for (int j = 0; j < chunk_info.x * chunk_info.y; j++) {
        for (int i = offsets[j]; i < offsets[j + 1]; i++) {
            if (!(intersectsBoundingSphere(camera, ray, vec3(toplefts[j].x, 0.0, toplefts[j].y), chunk_dimensions[j].xyz))) break;
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

            closest = t;
            intersection = camera + t * ray;

            color = vec4(0.0, 0.0, 0.0, 1.0);
            base_color = vec4((parsed_colors[indices[(i * 3) + 0]] * (1 - u - v) + parsed_colors[indices[(i * 3) + 1]] * u + parsed_colors[indices[(i * 3) + 2]] * v).xyz, 1.0);

            enemy_distance = length(enemy_light - intersection);
            enemy_contribution = vec4(0.0, 0.0, 0.0, 1.0);
            player_contribution = vec4(0.0, 0.0, 0.0, 1.0);
            normal = normalize(cross(v01, v02));
            player_distance = length(player_light - intersection);

            if (enemy_distance < enemy_light_range) {
                enemy_contribution += phong(intersection, normal, enemy_light, base_color, 5.0);
                enemy_contribution *= sqrt(enemy_light_range - enemy_distance) / sqrt(enemy_light_range);
                enemy_contribution *= vec4(1.0, 0.6, 0.2, 1.0) * 0.8;
                enemy_contribution *= shadowBounce(intersection + 0.0001 * normal, enemy_light);
            }

            if (player_distance < view_distance) {
                player_contribution += phong(intersection, normal, player_light, base_color, 5.0);
                player_contribution *= sqrt(view_distance - player_distance) / sqrt(view_distance);
            }

            color = player_contribution + enemy_contribution;
            color *= shadowBounce(intersection + 0.0001 * normal, player_light - vec3(0.0, 0.5, 0.0));
        }
    }

    return color;
}

void main() {
    // Get (x,y) position of this pixel in the texture (index in global work group)
    pixel_coords = ivec2(gl_GlobalInvocationID.xy);
    dimensions = imageSize(img_output);
    imageStore(img_output, pixel_coords, trace());
}
