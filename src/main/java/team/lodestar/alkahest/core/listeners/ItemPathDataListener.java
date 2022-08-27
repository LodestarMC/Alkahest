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
import team.lodestar.alkahest.core.alchemy.Element;
import team.lodestar.alkahest.core.path.DirectionData;
import team.lodestar.alkahest.core.path.ItemPathData;
import team.lodestar.alkahest.core.path.Path;

import java.util.*;

public class ItemPathDataListener extends SimpleJsonResourceReloadListener {
    public static Map<Item, ItemPathData> ITEM_PATH_DATA = new HashMap<>();
    private static final Gson GSON = (new GsonBuilder()).create();
    public ItemPathDataListener() {
        super(GSON, "item_path_data");
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new ItemPathDataListener());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ITEM_PATH_DATA.clear();
        Alkahest.LOGGER.info("Loading item path data...");
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
            if (ITEM_PATH_DATA.containsKey(item)){
                Alkahest.LOGGER.warn("Duplicate path data for item {}", resourceLocation);
            }
            JsonArray path = object.getAsJsonArray("path");
            Path p = new Path();
            for(int j = 0; j < path.size(); j++) {
                JsonElement direction = path.get(j);
                if(direction.isJsonArray()){
                    JsonArray dir = direction.getAsJsonArray();
                    List<Direction> directions = new ArrayList<>();
                    for(JsonElement dirElement : dir) {
                        if(dirElement.isJsonPrimitive()){
                            String dirName = dirElement.getAsJsonPrimitive().getAsString();
                            Direction direction1 = Direction.byName(dirName);
                            if(direction1 == null) {
                                Alkahest.LOGGER.warn("Could not find direction {} for item {}", dirName, item.getName(item.getDefaultInstance()).getString());
                                continue;
                            }
                            directions.add(direction1);
                        }
                    }
                    p.add(directions);
                } else {
                    if(Direction.byName(direction.getAsString()) != null) {
                        p.add(Collections.singletonList(Direction.byName(direction.getAsString())));
                    } else {
                        Alkahest.LOGGER.warn("Invalid direction {} for item {}", direction.getAsString(), item.getName(item.getDefaultInstance()).getString());
                    }
                }
            }
            JsonObject elements = object.getAsJsonObject("elements");
            Map<Element, Float> elementData = new HashMap<>();
            for(Map.Entry<String, JsonElement> entry : elements.entrySet()) {
                Element element = Element.byName(entry.getKey());
                if(element == null) {
                    Alkahest.LOGGER.warn("Invalid element for item {}", resourceLocation);
                    continue;
                }
                elementData.put(element, Math.round(entry.getValue().getAsFloat() * 10) / 10.0f);
            }
            ItemPathData itemPathData = new ItemPathData(new DirectionData(p), elementData);
            ITEM_PATH_DATA.put(item, itemPathData);
            Alkahest.LOGGER.info("Path data for " + item.getName(item.getDefaultInstance()).getString() + " loaded, Path: " + itemPathData.dirs.getDirections() + ", Elements: " + itemPathData.getElementsString());
        }
    }
}
