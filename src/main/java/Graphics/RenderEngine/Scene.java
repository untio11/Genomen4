package Graphics.RenderEngine;

import GameState.Entities.Camera;
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
    private static Loader loader;
    private Camera camera;
    private World world;
    private List<Model> entities;
    private List<Terrain> terrain_list;
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
        backupTexture = new TerrainTexture((loader.loadTexture("black")));

        // put them in the hashmap
        terrain_type_to_texture_map.put(TileType.WATER, water);
        terrain_type_to_texture_map.put(TileType.SAND, sand);
        terrain_type_to_texture_map.put(TileType.GRASS, grass);
        terrain_type_to_texture_map.put(TileType.TREE, tree);
    }

    /**
     * Create a scene from the given world
     * @param world The world to make a scene from
     */
    public Scene(World world) {
        this.world = world;
        this.entities = new ArrayList<>();
        this.terrain_list = new ArrayList<>();
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

        RawModel playerModel = OBJLoader.loadObjModel("player", loader);
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
        //TexturedModel texturedPlayer = new TexturedModel(playerModel, playerTexture);
        //world.getFather().setModel(texturedPlayer); // TODO: the world doesn't need to know the texture, that is to be kept here.

        Model fatherModel = new Model(world.getFather(), playerModel, playerTexture, 0.1f);
        entities.add(fatherModel);


        camera = new Camera(world.getFather());
        //world.getFather().setScale(0.1f); // Again, the world should not be concerned with how the player model looks
    }

    /**
     * Create the mapping from Terraintexture to a list of all terrains that have that texture, and add these Terrains
     * to the complete terrain_list. Only to be called once per map to generate the terrain.
     */
    private void initTileMap() { // TODO: can we make this static?
        texture_to_terrainlist_map.clear();
        TileType tileType;
        for(int r = 0; r< World.getInstance().getWidth(); r++) {
            for (int c = 0; c < World.getInstance().getHeight(); c++) {
                // get texture form hashmap, if it isn't there, use the backupTexture
                tileType = world.getTileType(r,c);

                int height = 0; // TODO: Height can probably be decided inside the Terrain class too
                if (tileType == TileType.TREE) { // TODO: Fix this to only make trees high
                    height = 1;
                }

                Terrain terrain = new Terrain(r, c, height, loader, terrain_type_to_texture_map.getOrDefault(tileType, backupTexture));
                // add to terrainList, which will be processed in loop
                terrain_list.add(terrain);
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
}
