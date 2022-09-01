package team.lodestar.alkahest.core.alchemy;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;
import team.lodestar.alkahest.core.path.PotionPathData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PotionMap {
    public List<PotionPathData> map;

    public static PotionMap EMPTY = new PotionMap(List.of());

    public PotionMap(List<PotionPathData> map) {
        this.map = map;
    }

    public List<PotionPathData> getMap() {
        return map;
    }

    //
    public Vec3 getLowestPoint(){
        Vec3 lowest = Vec3.ZERO;
        for(PotionPathData data : map){
            if(data.location.y() < lowest.y()){
                lowest = data.location;
            }
        }
        return lowest;
    }

    public boolean deepEquals(PotionMap map){
        for(int i = 0; i < this.map.size(); i++){
            if(!this.map.get(i).equals(map.getMap().get(i))){
                return false;
            }
        }
        return true;
    }

    public static <T> boolean lazyEquals(List<T> list1, List<T> list2){
        return new TreeSet<>(new HashSet<>(list1)).equals(new TreeSet<>(new HashSet<>(list2)));
    }

    public void copy(PotionMap map){
        this.map = new ArrayList<>(map.getMap());
    }

    public PotionMap clone(){
        List<PotionPathData> newMap = new ArrayList<>();
        for(PotionPathData data : map){
            newMap.add(data.clone());
        }
        return new PotionMap(newMap);
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
