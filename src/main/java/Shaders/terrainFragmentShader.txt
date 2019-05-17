#version 400 core

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D terrainTexture;

void main(void) {

    vec2 tiledCoords = pass_textureCoords;      // * 4 for 4*4 tiles

    out_Color = texture(terrainTexture, tiledCoords);

}