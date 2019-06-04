package Graphics.RenderEngine;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.Tile;
import GameState.TileType;
import GameState.World;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.Terrains.Terrain;
import Graphics.Textures.ModelTexture;
import Graphics.Textures.TerrainTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Convert the state of the world to models for the renderer.
 */
public class Scene {
    private static final int CHUNK_WIDTH = 6;
    private static final int CHUNK_HEIGHT = 3;
    private static final int X_TILES_TO_EDGE = 12;
    private static final int Y_TILES_TO_EDGE = 7;
    private int x_chunks;
    private int y_chunks;

    private static Loader loader;
    private Camera camera;
    private World world;
    private List<Model> entities;
    private List<Terrain> terrain_list;
    private Map<String, Terrain> terrain_map;
    private Map<TerrainTexture, List<Terrain>> texture_to_terrainlist_map;
    private static final Map<TileType, TerrainTexture> terrain_type_to_texture_map;
    private static final TerrainTexture backupTexture;

    static {
        loader = new Loader();
        terrain_type_to_texture_map = new HashMap<>();
        // create textures
        TerrainTexture water = new TerrainTexture(loader.loadTexture("water"));
        TerrainTexture sand = new TerrainTexture(loader.loadTexture("sand"));
        TerrainTexture grass = new TerrainTexture((loader.loadTexture("grass")));
        TerrainTexture tree = new TerrainTexture((loader.loadTexture("tree")));
        TerrainTexture shore = new TerrainTexture(loader.loadTexture("shore"));
        backupTexture = new TerrainTexture((loader.loadTexture("black")));

        // put them in the hashmap
        terrain_type_to_texture_map.put(TileType.WATER, water);
        terrain_type_to_texture_map.put(TileType.SAND, sand);
        terrain_type_to_texture_map.put(TileType.GRASS, grass);
        terrain_type_to_texture_map.put(TileType.TREE, tree);
        terrain_type_to_texture_map.put(TileType.SHORE_N, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NS, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_E, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_ES, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NE, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NES, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_W, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_SW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NSW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_EW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_ESW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NEW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_NESW, shore);
        terrain_type_to_texture_map.put(TileType.SHORE_S, shore);
    }

    /**
     * Create a scene from the given world. An OpenGL context has to be current for this to work.
     * @param world The world to make a scene from
     */
    public Scene(World world) {
        this.world = world;
        this.entities = new ArrayList<>();
        this.terrain_list = new ArrayList<>();
        this.terrain_map = new HashMap<>();
        this.texture_to_terrainlist_map = new HashMap<>();
        init();
    }

    /**
     * Load the world and initialize the scene by loading all the world tiles into Terrain objects and creating the
     * player models.
     * @throws IllegalStateException when the world is not initialized yet
     */
    public void init() throws IllegalStateException {
        initTileMap();
        initActors(world.getActors());
        generateChunks();
        camera = world.getCamera();
        getVisibileChunks(camera.getPosition().x, camera.getPosition().z);
    }

    private void generateChunks() {
        x_chunks = (int) Math.ceil((float) World.getInstance().getWidth()  / (float) CHUNK_WIDTH);
        y_chunks = (int) Math.ceil((float) World.getInstance().getHeight() / (float) CHUNK_HEIGHT);

        int x_spillover = (x_chunks * CHUNK_WIDTH - World.getInstance().getWidth());
        int y_spillover = (y_chunks * CHUNK_HEIGHT - World.getInstance().getHeight());

        int left_spillover = (int) Math.floor(x_spillover / 2);
        int top_spillover =  (int) Math.floor(y_spillover / 2);

        Chunk[][] chunks = new Chunk[x_chunks][y_chunks];
        for (int x = 0; x < x_chunks; x++) {
            for (int y = 0; y < y_chunks; y++) {
                chunks[x][y] = fillChunk(x, y, top_spillover, left_spillover);
            }
        }
    }

    /**
     * Fill the chunk (x,y) with the appropriate data.
     * @param x Horizontal index of the chunk
     * @param y Vertical index of the chunk
     * @param toppad Amount of water tiles to be added at the top
     * @param leftpad Amount of water tiles to be added at the left
     */
    private Chunk fillChunk(int x, int y, int toppad, int leftpad) {
        List<Terrain> tiles = new ArrayList<>();
        int world_width = World.getInstance().getWidth();
        int world_height = World.getInstance().getHeight();

        for (int c = x * CHUNK_WIDTH - leftpad; c < (x * CHUNK_WIDTH - leftpad) + CHUNK_WIDTH; c++) {
            for (int r = y * CHUNK_HEIGHT - toppad; r < (y * CHUNK_HEIGHT - toppad) + CHUNK_HEIGHT; r++) {
                if (r < 0 || c < 0 || r > world_height || c > world_width) {
                    tiles.add(new Terrain(r, c, 0, loader, terrain_type_to_texture_map.getOrDefault(TileType.WATER, backupTexture)));
                } else {
                    tiles.add(terrain_map.get(String.format("(%d,%d)", c, r)));
                }
            }
        }

        return new Chunk(tiles);
    }

    /**
     * Return the chunks that should be in the viewport. From testing, there seem to be 23 _tiles_ in the horizontal direction
     * and 13 _tiles_ in the vertical direction. Best case these are 4x5 chunks, worst case there are 5x6 chunks.
     * @param x The x component of the middle of the current viewport in world space coordinates.
     * @param y The y component of the middle of the current viewport in world space coordinates.
     * @return A 2D array of chunks with indices x,y.
     */
    public Chunk[][] getVisibileChunks(float x, float y) {
        int left_x_chunk = Math.max((int) ((x - X_TILES_TO_EDGE) / CHUNK_WIDTH), 0);
        int bottom_y_chunk = Math.max((int) ((y - Y_TILES_TO_EDGE) / CHUNK_HEIGHT), 0);
        int right_x_chunk = Math.min((int) ((x + X_TILES_TO_EDGE) / CHUNK_WIDTH), x_chunks);
        int top_y_chunk = Math.min((int) ((y + Y_TILES_TO_EDGE) / CHUNK_HEIGHT), y_chunks);
        return null;
    }

    private void initActors(Actor[] actors) {
        for (Actor actor : actors) {
            RawModel playerModel = OBJLoader.loadObjModel("player", loader); // TODO: get model and texture for thief
            ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
            TexturedModel texturedPlayer = new TexturedModel(playerModel, playerTexture);
            Model actorModel = new Model(actor, texturedPlayer, 0.2f);
            entities.add(actorModel);
        }
    }

    /**
     * Create the mapping from Terraintexture to a list of all terrains that have that texture, and add these Terrains
     * to the complete terrain_list. Only to be called once per map to generate the terrain.
     */
    private void initTileMap() { // TODO: can we make this static?
        texture_to_terrainlist_map.clear();
        TileType tileType;

        for(int r = 0; r < World.getInstance().getWidth(); r++) {
            for (int c = 0; c < World.getInstance().getHeight(); c++) {
                // get texture form hashmap, if it isn't there, use the backupTexture
                tileType = world.getTileType(r,c);

                int height = 0; // TODO: Height can probably be decided inside the Terrain class too
                if (tileType == TileType.TREE) {
                    height = 1;
                }

                Terrain terrain = new Terrain(r, c, height, loader, terrain_type_to_texture_map.getOrDefault(tileType, backupTexture));
                // add to terrainList, which will be processed in loop
                terrain_list.add(terrain);
                terrain_map.put(String.format("(%d,%d)", r, c), terrain);
                processTerrain(terrain);
            }
        }
    }

    /**
     * Puts the given terrain in the texture to terrain list map
     * @param terrain The terrain to be added to the map.
     */
    private void processTerrain(Terrain terrain) {
        TerrainTexture texture = terrain.getTexture();
        List<Terrain> terrainBatch = texture_to_terrainlist_map.get(texture);
        if (terrainBatch != null) {
            terrainBatch.add(terrain);
        } else {
            List<Terrain> newBatch = new ArrayList<>();
            newBatch.add(terrain);
            texture_to_terrainlist_map.put(texture, newBatch);
        }
    }

    public void cleanUp() {
        loader.cleanUp();
    }

    public List<Model> getEntities() {
        return entities;
    }

    /**
     * To fetch all the terrain objects in the scene
     * @return The list of all terrain objects in the scene
     */
    public List<Terrain> getTerrainList() {
        return terrain_list;
    }

    public Map<TerrainTexture, List<Terrain>> getTexture_to_terrainlist_map() {
        return texture_to_terrainlist_map;
    }

    public Camera getCamera() {
        return camera;
    }

    private class Chunk {
        List<Terrain> data;

        Chunk(List<Terrain> tiles) {
            this.data = tiles;
        }

        long getVertexCount() {
            long sum = 0;

            for (Terrain tile : data) {
                sum += tile.getModel().getVertexCount();
            }

            return sum;
        }
    }
}
