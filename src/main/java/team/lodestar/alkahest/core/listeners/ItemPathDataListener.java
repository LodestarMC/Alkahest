package team.lodestar.alkahest.core.listeners;

import com.google.gson.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.AddReloadListenerEvent;
import team.lodestar.alkahest.Alkahest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPathDataListener extends SimpleJsonResourceReloadListener {
    public static Map<Item, List<Direction>> PATH_DATA = new HashMap<>();
    private static final Gson GSON = (new GsonBuilder()).create();
    public ItemPathDataListener() {
        super(GSON, "path_data");
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new ItemPathDataListener());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        PATH_DATA.clear();
        Alkahest.LOGGER.info("Loading path data...");
        for(int i = 0; i < pObject.size(); i++) {
            ResourceLocation key = (ResourceLocation) pObject.keySet().toArray()[i];
            JsonObject object = pObject.get(key).getAsJsonObject();
            String name = object.getAsJsonPrimitive("item").getAsString();
            ResourceLocation resourceLocation = new ResourceLocation(name);
            Item item = Registry.ITEM.get(resourceLocation);
            Alkahest.LOGGER.info("Loading path data for " + item.getName(item.getDefaultInstance()).getString());
            if(!Registry.ITEM.containsKey(resourceLocation)) {
                Alkahest.LOGGER.warn("Could not find item " + name + " for path data");
                continue;
            }
            if (PATH_DATA.containsKey(item)){
                Alkahest.LOGGER.warn("Duplicate path data for item {}", resourceLocation);
            }
            JsonArray path = object.getAsJsonArray("path");
            List<Direction> pathData = new ArrayList<>();
            for(JsonElement direction : path) {
                Direction pathDir = Direction.byName(direction.getAsString());
                if(pathDir == null) {
                    Alkahest.LOGGER.warn("Invalid path direction for item {}", resourceLocation);
                    continue;
                }
                pathData.add(pathDir);
            }
            PATH_DATA.put(item, pathData);
            Alkahest.LOGGER.info("Path data for " + item.getName(item.getDefaultInstance()).getString() + " loaded, " + pathData.toString());
        }
    }
}
