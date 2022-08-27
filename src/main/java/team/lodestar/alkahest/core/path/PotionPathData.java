package team.lodestar.alkahest.core.path;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PotionPathData {
    public List<MobEffectInstance> effects;
    public Vec3 location;
    public float radius;

    public PotionPathData(List<MobEffectInstance> effects, Vec3 location, float radius) {
        this.effects = effects;
        this.location = location;
        this.radius = radius;
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
}
