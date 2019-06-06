package Graphics.RenderEngine;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.Tile;
import GameState.TileType;
import GameState.World;
import Graphics.Models.ActorModel;
import Graphics.Models.BaseModel;
import Graphics.Models.TerrainModel;
import Graphics.Terrains.TerrainGenerator;
import org.apache.commons.lang3.ArrayUtils;
import org.bytedeco.opencv.presets.opencv_core;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.stream.Stream;

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
    private static Chunk[][] chunks;

    private static Loader loader;
    private Camera camera;
    private World world;
    private List<ActorModel> entities;
    private List<TerrainModel> terrain_list;
    private Map<String, TerrainModel> terrain_map;
    private Map<Integer, List<TerrainModel>> texture_to_terrainlist_map;
    private static final Map<TileType, Integer> terrain_type_to_texture_map;
    private static final int backupTexture;

    static {
        loader = new Loader();
        terrain_type_to_texture_map = new HashMap<>();
        // create textures
        int water = loader.loadTexture("water");
        int sand =  loader.loadTexture("sand");
        int grass = loader.loadTexture("grass");
        int tree =  loader.loadTexture("tree");
        int shore = loader.loadTexture("shore");
        backupTexture = loader.loadTexture("black");

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
    }

    private void generateChunks() {
        x_chunks = (int) Math.ceil((float) World.getInstance().getWidth()  / (float) CHUNK_WIDTH);
        y_chunks = (int) Math.ceil((float) World.getInstance().getHeight() / (float) CHUNK_HEIGHT);

        int x_spillover = (x_chunks * CHUNK_WIDTH - World.getInstance().getWidth());
        int y_spillover = (y_chunks * CHUNK_HEIGHT - World.getInstance().getHeight());

        int left_spillover = (int) Math.floor(x_spillover / 2);
        int top_spillover =  (int) Math.floor(y_spillover / 2);

        chunks = new Chunk[x_chunks][y_chunks];
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
        List<TerrainModel> tiles = new ArrayList<>();
        int world_width = World.getInstance().getWidth();
        int world_height = World.getInstance().getHeight();
        int water_texture = terrain_type_to_texture_map.getOrDefault(TileType.WATER, backupTexture);

        for (int c = x * CHUNK_WIDTH - leftpad; c < (x * CHUNK_WIDTH - leftpad) + CHUNK_WIDTH; c++) {
            for (int r = y * CHUNK_HEIGHT - toppad; r < (y * CHUNK_HEIGHT - toppad) + CHUNK_HEIGHT; r++) {
                if (r < 0 || c < 0 || r > world_height || c > world_width) {

                    tiles.add(TerrainGenerator.generateTerrain(World.getInstance().getTile(c, r), water_texture, loader));

                } else {
                    tiles.add(terrain_map.get(String.format("(%d,%d)", c, r)));
                }
            }
        }

        return new Chunk(tiles);
    }

    /**
     * Return the chunks that should be in the viewport. From testing, there seem to be 23 _tiles_ in the horizontal direction
     * and 13 _tiles_ in the vertical direction. Generally there are 4x5 chunks, worst case there are 5x6 chunks, and near
     * the edges even 3x6 is possible or even smaller (maybe).
     * @param x The x component of the middle of the current viewport in world space coordinates.
     * @param y The y component of the middle of the current viewport in world space coordinates.
     * @return An array of chunks that should at least be partly visible on screen.
     */
    public Chunk[] getVisibileChunks(float x, float y) {
        int left_x_chunk = Math.max((int) ((x - X_TILES_TO_EDGE) / CHUNK_WIDTH), 0);
        int right_x_chunk = Math.min((int) ((x + X_TILES_TO_EDGE) / CHUNK_WIDTH), x_chunks);
        int top_y_chunk = Math.min((int) ((y + Y_TILES_TO_EDGE) / CHUNK_HEIGHT), y_chunks);
        int bottom_y_chunk = Math.max((int) ((y - Y_TILES_TO_EDGE) / CHUNK_HEIGHT), 0);
        Chunk[] result = new Chunk[(1 + right_x_chunk - left_x_chunk) * (1 + top_y_chunk - bottom_y_chunk)];

        int counter = 0;
        for (int i = left_x_chunk; i < right_x_chunk; i++) {
            for (int j = bottom_y_chunk; j < top_y_chunk; j++) {
                result[counter++] = chunks[i][j];
            }
        }

        return result;
    }

    private void initActors(Actor[] actors) {
        for (Actor actor : actors) {
            //BaseModel playerBase = OBJLoader.loadObjModelInVao("player", loader); // TODO: get model and texture for thief
            BaseModel playerBase = AnimModelLoader.loadAnimModelInVao("res/gnomeModel.dae", loader);
            int playerTexture = loader.loadTexture("gnomeTexture");
            playerBase.setTexture(playerTexture);
            //playerBase.setScale(0.1f);

            entities.add(new ActorModel(actor, playerBase));
        }
    }

    /**
     * Create the mapping from Terraintexture to a list of all terrains that have that texture, and add these Terrains
     * to the complete terrain_list. Only to be called once per map to generate the terrain.
     */
    private void initTileMap() { // TODO: can we make this static?
        texture_to_terrainlist_map.clear();
        Tile tile;
        int texture;

        for(int x = 0; x < World.getInstance().getWidth(); x++) {
            for (int y = 0; y < World.getInstance().getHeight(); y++) {
                // get texture form hashmap, if it isn't there, use the backupTexture
                tile = World.getInstance().getTile(x, y);
                texture = terrain_type_to_texture_map.getOrDefault(tile.getType(), backupTexture);
                TerrainModel terrain = TerrainGenerator.generateTerrain(tile, texture, loader);

                // add to terrainList, which will be processed in loop
                terrain_list.add(terrain);
                terrain_map.put(String.format("(%d,%d)", x, y), terrain);
                processTerrain(terrain);
            }
        }
    }

    /**
     * Puts the given terrain in the texture to terrain list map
     * @param terrain The terrain to be added to the map.
     */
    private void processTerrain(TerrainModel terrain) {
        int texture = terrain.getTextureID();
        List<TerrainModel> terrainBatch = texture_to_terrainlist_map.get(texture);

        if (terrainBatch != null) {
            terrainBatch.add(terrain);
        } else {
            List<TerrainModel> newBatch = new ArrayList<>();
            newBatch.add(terrain);
            texture_to_terrainlist_map.put(texture, newBatch);
        }
    }

    public void cleanUp() {
        loader.cleanUp();
    }

    public List<ActorModel> getEntities() {
        return entities;
    }

    /**
     * To fetch all the terrain objects in the scene
     * @return The list of all terrain objects in the scene
     */
    public List<TerrainModel> getTerrainList() {
        return terrain_list;
    }

    public Map<Integer, List<TerrainModel>> getTexture_to_terrainlist_map() {
        return texture_to_terrainlist_map;
    }

    public Camera getCamera() {
        return camera;
    }

    public class Chunk {
        List<TerrainModel> data;
        private int vertex_count;
        private int tringle_count;
        private int coordinate_amount;
        private float[] coordinate_stream;

        Chunk(List<TerrainModel> tiles) {
            this.data = tiles;
            this.vertex_count = computeVertexCount();
            this.tringle_count = vertex_count / 3;

            Stream<Float> stream = Stream.of();
            for (TerrainModel tile : tiles) {
                Float[] tile_coordinates = ArrayUtils.toObject(tile.getPosition_data());
                stream = Stream.concat(stream, Arrays.stream(tile_coordinates));
            }

            this.coordinate_stream = ArrayUtils.toPrimitive(stream.toArray(Float[]::new));
            this.coordinate_amount = coordinate_stream.length;
        }

        public float[] getCoordinateStream() {
            return coordinate_stream;
        }

        public int getVertexCount() {
            return vertex_count;
        }

        public int getTringleCount() {
            return tringle_count;
        }

        public int getCoordinate_amount() {
            return coordinate_amount;
        }

        private int computeVertexCount() {
            int sum = 0;

            for (TerrainModel tile : data) {
                sum += tile.getVertexCount();
            }

            return sum;
        }
    }
}
