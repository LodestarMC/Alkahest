package team.lodestar.alkahest.core.alchemy;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import team.lodestar.alkahest.core.path.PotionPathData;

import java.util.List;
import java.util.function.Predicate;

public class PotionMap {
    public List<PotionPathData> map;

    public PotionMap(List<PotionPathData> map) {
        this.map = map;
    }

    public List<PotionPathData> getMap() {
        return map;
    }

    public void setMap(List<PotionPathData> map) {
        this.map = map;
    }

    public void add(PotionPathData data){
        map.add(data);
    }

    public PotionPathData getPotionPathData(Predicate<PotionPathData> predicate) {
        return map.stream().filter(predicate).findFirst().orElse(null);
    }

    public PotionPathData isInRadius(Vec3 location) {
        return getPotionPathData(potion -> potion.getLocation().distanceTo(location) < potion.getRadius());
    }

    public PotionPathData getPotionPathDataAt(Vec3 location) {
        return getPotionPathData(potion -> potion.getLocation().equals(location));
    }
}
