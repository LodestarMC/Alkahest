package team.lodestar.alkahest.core.path;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import team.lodestar.lodestone.helpers.util.Color;

import java.util.ArrayList;
import java.util.List;

public class PotionPathData {
    public List<MobEffectInstance> effects;
    public Vec3 location;
    public float radius;
    public Color color;
    public MobEffectCategory category;

    public PotionPathData(List<MobEffectInstance> effects, Vec3 location, float radius, Color color, MobEffectCategory category) {
        this.effects = effects;
        this.location = location;
        this.radius = radius;
        this.color = color;
        this.category = category;
    }

    public boolean equals(PotionPathData data){
        if(!this.effects.equals(data.effects)){
            return false;
        }
        if(!this.location.equals(data.location)){
            return false;
        }
        if(this.radius != data.radius){
            return false;
        }
        if(!this.color.equals(data.color)){
            return false;
        }
        if(this.category != data.category){
            return false;
        }
        return true;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for(MobEffectInstance effect : effects){
            CompoundTag effectTag = new CompoundTag();
            effectTag.putString("id", ForgeRegistries.MOB_EFFECTS.getKey(effect.getEffect()).toString());
            effectTag.putInt("duration", effect.getDuration());
            effectTag.putInt("amplifier", effect.getAmplifier());
            list.add(effectTag);
        }
        tag.put("effects", list);
        CompoundTag locationTag = new CompoundTag();
        locationTag.putDouble("x", location.x);
        locationTag.putDouble("y", location.y);
        locationTag.putDouble("z", location.z);
        tag.put("location", locationTag);
        tag.putFloat("radius", radius);
        tag.putInt("color", color.getRGB());
        tag.putInt("alpha", color.getAlpha());
        tag.putInt("category", category.ordinal());
        return tag;
    }

    public static PotionPathData fromNbt(CompoundTag tag) {
        ListTag list = tag.getList("effects", 10);
        List<MobEffectInstance> effects = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            CompoundTag effectTag = list.getCompound(i);
            String id = effectTag.getString("id");
            int duration = effectTag.getInt("duration");
            int amplifier = effectTag.getInt("amplifier");
            MobEffectInstance effect = new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(id)), duration, amplifier);
            effects.add(effect);
        }
        CompoundTag locationTag = tag.getCompound("location");
        Vec3 location = new Vec3(locationTag.getDouble("x"), locationTag.getDouble("y"), locationTag.getDouble("z"));
        float radius = tag.getFloat("radius");
        int intcol = tag.getInt("color");
        Color color = new Color(intcol);
        color.setAlpha(tag.getInt("alpha"));
        MobEffectCategory category = MobEffectCategory.values()[tag.getInt("category")];
        return new PotionPathData(effects, location, radius, color, category);
    }

    public List<MobEffectInstance> getEffects() {
        return effects;
    }

    public void setEffects(List<MobEffectInstance> effects) {
        this.effects = effects;
    }

    public Vec3 getLocation() {
        return location;
    }

    public boolean isInRadius(Vec3 location){
        return this.location.distanceTo(location) <= radius;
    }

    public void setLocation(Vec3 location) {
        this.location = location;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
