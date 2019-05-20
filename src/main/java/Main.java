import GameState.Entities.Camera;
import GameState.Entities.Actor;
import GameState.MapGenerator;
import GameState.TileType;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.RenderEngine.Loader;
import Graphics.RenderEngine.MasterRenderer;
import Graphics.RenderEngine.OBJLoader;
import Graphics.Terrains.Terrain;
import Graphics.Textures.ModelTexture;
import Graphics.Textures.TerrainTexture;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import GameState.World;
import Graphics.WindowManager;

public class Main {
    public static void main(String[] args) {
        World.initWorld(60, 60);
        new WindowManager().run();
    }
}

