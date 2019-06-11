package Graphics.RenderEngine;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.Entities.LightSource;
import GameState.Tile;
import GameState.TileType;
import GameState.World;
import Graphics.Models.ActorModel;
import Graphics.Models.BaseModel;
import Graphics.Models.TerrainModel;
import Graphics.RenderEngine.RayTracing.RayTracer;
import Graphics.Terrains.TerrainGenerator;
import com.sun.istack.internal.Nullable;
import javafx.scene.effect.Light;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

/**
 * Convert the state of the world to models for the renderer.
 */
public class Scene {
    private static final int CHUNK_WIDTH = 6;
    private static final int CHUNK_HEIGHT = 3;
    private static final int X_TILES_TO_EDGE = 7;
    private static final int Y_TILES_TO_EDGE = 4;
    private int x_chunks;
    private int y_chunks;
    private static Chunk[][] chunks;
    private static int[] old_left_top = {-1, -1}, old_right_bot = {-1, -1};

    private static Loader loader;
    private Camera camera;
    private World world;
    private List<ActorModel> entities;
    private List<TerrainModel> terrain_list;
    private LightSource[] lights = new LightSource[2];
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
        initLights();
    }

    private void initLights() {
        int counter = 0;
        for (ActorModel actorModel : entities) {
            lights[counter] = new LightSource();
            actorModel.getActor().add(lights[counter++]);
        }
    }

    public Chunk[] getChunks() {
        Chunk[] result = new Chunk[chunks.length * chunks[0].length];
        int counter = 0;
        for (int i = 0; i < chunks.length; i++) {
            for (int j = 0; j < chunks[i].length; j++) {
                result[counter++] = chunks[i][j];
            }
        }

        return result;
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
                if (r < 0 || c < 0 || r > world_height - 1 || c > world_width - 1) {
                    tiles.add(TerrainGenerator.generateTerrain(new Tile(TileType.WATER, new int[]{r,c}, 0), water_texture, loader));
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
     * @return An array of chunks that should at least be partly visible on screen. Null if no new visible chunks.
     */
    public Chunk[] getVisibileChunks(float x, float y) {
        int top_chunk  = Math.max((int) Math.floor((y - Y_TILES_TO_EDGE) / CHUNK_HEIGHT), 0);
        int left_chunk = Math.max((int) Math.floor((x - X_TILES_TO_EDGE) / CHUNK_WIDTH), 0);

        if (left_chunk == old_left_top[0] && top_chunk == old_left_top[1]) return null;
        old_left_top[0] = left_chunk;
        old_left_top[1] = top_chunk;

        int bottom_chunk = Math.min((int) Math.ceil((y + Y_TILES_TO_EDGE) / CHUNK_HEIGHT), y_chunks);
        int right_chunk  = Math.min((int) Math.ceil((x + X_TILES_TO_EDGE) / CHUNK_WIDTH), x_chunks);

        if (right_chunk == old_right_bot[0] && bottom_chunk == old_right_bot[1]) return null;
        old_right_bot[0] = right_chunk;
        old_right_bot[1] = bottom_chunk;

        Chunk[] result = new Chunk[(right_chunk - left_chunk) * (bottom_chunk - top_chunk)];
        RayTracer.chunk_count[0] = (right_chunk - left_chunk);
        RayTracer.chunk_count[1] = (bottom_chunk - top_chunk);
        int counter = 0;

        for (int i = left_chunk; i < right_chunk; i++) {
            for (int j = top_chunk; j < bottom_chunk; j++) {
                result[counter++] = chunks[i][j];
            }
        }

        return result;
    }

    public List<ActorModel> getVisibleActors(Chunk[] visible_chunks) {
        float[] top_left = visible_chunks[0].top_left;
        // Copy like this, otherwise we get weird bugs
        float[] bottom_right = {visible_chunks[visible_chunks.length - 1].top_left[0],
                visible_chunks[visible_chunks.length - 1].top_left[1]};
        bottom_right[0] += CHUNK_WIDTH;
        bottom_right[1] += CHUNK_HEIGHT;

        List<ActorModel> result = new ArrayList<>();

        for (ActorModel actorModel : entities) {
            Actor actor = actorModel.getActor();
            float[] actor_pos = new float[] {actor.get3DPosition().x, actor.get3DPosition().y};

            if (insideBounds(actor_pos, top_left, bottom_right)) {
                result.add(actorModel);
            }
        }

        return result;
    }

    public List<LightSource> getVisibleLights(Chunk[] visible_chunks) {
        float[] top_left = visible_chunks[0].top_left;
        // Copy like this, otherwise we get weird bugs
        float[] bottom_right = {visible_chunks[visible_chunks.length - 1].top_left[0],
                visible_chunks[visible_chunks.length - 1].top_left[1]};
        bottom_right[0] += CHUNK_WIDTH;
        bottom_right[1] += CHUNK_HEIGHT;

        List<LightSource> result = new ArrayList<>();

        for (LightSource light : lights) {

            float[] light_pos = new float[] {light.getPosition().x, light.getPosition().z};

            if (insideBounds(light_pos, top_left, bottom_right)) {
                result.add(light);
            }
        }

        return result;
    }

    private boolean insideBounds(float[] target, float[] top_left, float[] bottom_right) {
        return ((top_left[0] <= target[0] && target[0] < bottom_right[0]) &&
                (top_left[1] <= target[1] && target[1] < bottom_right[1]));
    }

    private void initActors(Actor[] actors) {
        for (Actor actor : actors) {
            BaseModel playerBase = OBJLoader.loadObjModelInVao("player", loader); // TODO: get model and texture for thief
            int playerTexture = loader.loadTexture("playerTexture");
            playerBase.setTexture(playerTexture);
            playerBase.setScale(0.2f);

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
        private float[] normal_stream;
        private float[] color_stream;
        private int[] index_stream;
        private float[] top_left = new float[] {Float.MAX_VALUE, Float.MAX_VALUE};
        private ActorModel[] actors = new ActorModel[2];
        private LightSource[] lights = new LightSource[2];

        Chunk(List<TerrainModel> tiles) {
            this.data = tiles;
            this.vertex_count = computeVertexCount();
            this.tringle_count = vertex_count / 3;

            Stream<Float> coordinate_stream = Stream.of();
            Stream<Integer> index_stream = Stream.of();
            Stream<Float> color_stream = Stream.of();
            int index_counter = 0;

            for (int i = 0; i < tiles.size(); i++) {
                TerrainModel tile = tiles.get(i);

                top_left[0] = Math.min(top_left[0], tile.getX());
                top_left[1] = Math.min(top_left[1], tile.getY());

                Integer[] tile_indices = ArrayUtils.toObject(tile.getIndexData());
                for (int j = 0; j < tile_indices.length; j++) {
                    tile_indices[j] += index_counter;
                }
                index_counter += tile.getPosition_data().length / 4; // Actual amount of vertices defined

                coordinate_stream = Stream.concat(coordinate_stream, Arrays.stream(ArrayUtils.toObject(tile.getPosition_data())));
                color_stream = Stream.concat(color_stream, Arrays.stream(ArrayUtils.toObject(tile.getColorData())));
                index_stream = Stream.concat(index_stream, Arrays.stream(tile_indices));
            }

            this.coordinate_stream = ArrayUtils.toPrimitive(coordinate_stream.toArray(Float[]::new));
            this.index_stream = ArrayUtils.toPrimitive(index_stream.toArray(Integer[]::new));
            this.coordinate_amount = this.coordinate_stream.length;
            this.color_stream = ArrayUtils.toPrimitive(color_stream.toArray(Float[]::new));
        }

        public float[] getColorStream() {
            return color_stream;
        }

        public float[] getCoordinateStream() {
            return coordinate_stream;
        }

        public int[] getIndex_stream() {
            return index_stream;
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

        public float[] getTopLeft() {
            return top_left;
        }
    }
}
