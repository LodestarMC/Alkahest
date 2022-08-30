package team.lodestar.alkahest.core.alchemy;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;
import team.lodestar.alkahest.core.path.PotionPathData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PotionMap {
    public List<PotionPathData> map;

    public static PotionMap EMPTY = new PotionMap(List.of());

    public PotionMap(List<PotionPathData> map) {
        this.map = map;
    }

    public List<PotionPathData> getMap() {
        return map;
    }

    public void copy(PotionMap map){
        this.map = new ArrayList<>(map.getMap());
    }

    public boolean equals(PotionMap map){
        for (int i = 0; i < this.map.size(); i++) {
            if(!this.map.get(i).equals(map.getMap().get(i))){
                return false;
            }
        }
        return true;
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

    public void clear(){
        map.clear();
    }

    public CompoundTag toNbt(){
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for(PotionPathData data : map){
            list.add(data.toNbt());
        }
        tag.put("map", list);
        return tag;
    }

    public static PotionMap fromNbt(CompoundTag tag){
        ListTag list = tag.getList("map", 10);
        List<PotionPathData> map = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            CompoundTag dataTag = list.getCompound(i);
            PotionPathData data = PotionPathData.fromNbt(dataTag);
            map.add(data);
        }
        return new PotionMap(map);
    }
}
