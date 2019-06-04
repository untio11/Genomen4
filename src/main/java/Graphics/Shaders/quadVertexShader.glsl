#version 430

in vec4 position_in;
out vec2 pass_textureCoords;

void main() {
    gl_Position = vec4(position_in);
    /*
     * Compute texture coordinate by simply
     * interval-mapping from [-1..+1] to [0..1]
     */
    pass_textureCoords = position_in.xy * 0.5 + vec2(0.5, 0.5);
}