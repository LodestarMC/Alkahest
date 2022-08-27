package team.lodestar.alkahest.core.path;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.helpers.util.Color;

import java.util.List;

public class PotionPathData {
    public List<MobEffectInstance> effects;
    public Vec3 location;
    public float radius;
    public Color color;

    public PotionPathData(List<MobEffectInstance> effects, Vec3 location, float radius, Color color) {
        this.effects = effects;
        this.location = location;
        this.radius = radius;
        this.color = color;
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
