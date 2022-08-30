package team.lodestar.alkahest.core.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AddReloadListenerEvent;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.alchemy.PotionMap;
import team.lodestar.alkahest.core.path.PotionPathData;
import team.lodestar.lodestone.helpers.util.Color;

import java.util.*;

public class PotionPathDataListener extends SimpleJsonResourceReloadListener {
    public static Map<ResourceLocation, PotionPathData> POTION_PATH_DATA = new HashMap<>();
    public static PotionMap potionMap = new PotionMap(new ArrayList<>());
    private static final Gson GSON = new Gson();
    public PotionPathDataListener() {
        super(GSON, "potion_path_data");
    }

    public static void register(AddReloadListenerEvent event) {
        event.addListener(new PotionPathDataListener());
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        POTION_PATH_DATA.clear();
        potionMap.clear();
        Alkahest.LOGGER.info("Loading potion path data...");
        for(int i = 0; i < pObject.size(); i++){
            ResourceLocation key = (ResourceLocation) pObject.keySet().toArray()[i];
            JsonObject object = pObject.get(key).getAsJsonObject();
            String name = object.getAsJsonPrimitive("name").getAsString();
            ResourceLocation resourceLocation = new ResourceLocation(name);
            Alkahest.LOGGER.info("Loading path data for " + name);
            if(POTION_PATH_DATA.containsKey(resourceLocation)){
                Alkahest.LOGGER.warn("Duplicate path data for potion {}", resourceLocation);
            }
            JsonArray effects = object.getAsJsonArray("effects");
            List<MobEffectInstance> effectList = new ArrayList<>();
            for(JsonElement effect : effects){
                if(effect.isJsonObject()){
                    JsonObject effectObject = effect.getAsJsonObject();
                    String effectName = effectObject.getAsJsonPrimitive("name").getAsString();
                    int duration = effectObject.getAsJsonPrimitive("duration").getAsInt();
                    int amplifier = effectObject.getAsJsonPrimitive("amplifier").getAsInt();
                    MobEffectInstance mobEffect = new MobEffectInstance(Registry.MOB_EFFECT.get(new ResourceLocation(effectName)), duration, amplifier);
                    effectList.add(mobEffect);
                }
            }
            JsonObject location = object.getAsJsonObject("location");
            int x = location.getAsJsonPrimitive("x").getAsInt();
            int y = location.getAsJsonPrimitive("y").getAsInt();
            int z = location.getAsJsonPrimitive("z").getAsInt();
            Vec3 vec3 = new Vec3(x, y, z);
            float radius = object.getAsJsonPrimitive("radius").getAsFloat();
            JsonObject color = object.getAsJsonObject("color");
            int r = color.getAsJsonPrimitive("r").getAsInt();
            int g = color.getAsJsonPrimitive("g").getAsInt();
            int b = color.getAsJsonPrimitive("b").getAsInt();
            int a = color.getAsJsonPrimitive("a").getAsInt();
            MobEffectCategory category = MobEffectCategory.valueOf(object.getAsJsonPrimitive("category").getAsString().toUpperCase(Locale.ROOT));
            PotionPathData potionPathData = new PotionPathData(effectList, vec3, radius, new Color(r,g,b,a), category);
            POTION_PATH_DATA.put(resourceLocation, potionPathData);
            List<PotionPathData> potionPathDataList = new ArrayList<>();
            for(PotionPathData potionPathData1 : POTION_PATH_DATA.values()){
                potionMap.add(potionPathData1);
            }
            Alkahest.LOGGER.info("Loaded path data for " + name + " with " + effectList.size() + " effects: " + effectList + " at " + vec3 + " with radius " + radius);
        }
    }
}
