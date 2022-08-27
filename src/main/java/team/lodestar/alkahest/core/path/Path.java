package team.lodestar.alkahest.core.path;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.*;

public class Path {
    Map<List<Direction>, Float> directionMap;

    public Path(){
        directionMap = new HashMap<>();
    }

    public void add(List<Direction> directions, float progress) {
        directionMap.put(directions, progress);
    }

    public void add(List<Direction> directions) {
        directionMap.put(directions, 0f);
    }

    public void progress(){
        float perDir = 100f / directionMap.size();
        for(List<Direction> directions : directionMap.keySet()){
            if(directionMap.get(directions) < perDir){
                directionMap.put(directions, directionMap.get(directions) + 1);
                return;
            }
        }
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for(List<Direction> directions : directionMap.keySet()){
            ListTag dir = new ListTag();
            for(Direction d : directions){
                CompoundTag dirTag = new CompoundTag();
                dirTag.putString("direction", d.getName().toLowerCase(Locale.ROOT));
                dir.add(dirTag);
            }
            CompoundTag dirTag = new CompoundTag();
            dirTag.put("directions", dir);
            dirTag.putFloat("progress", directionMap.get(directions));
            list.add(dirTag);
        }
        tag.put("directionsList", list);
        return tag;
    }

    public static Path fromNBT(CompoundTag tag) {
        Path path = new Path();
        ListTag list = tag.getList("directionsList", 10);
        for(int i = 0; i < list.size(); i++){
            CompoundTag dirTag = list.getCompound(i);
            List<Direction> directions = new ArrayList<>();
            ListTag dir = dirTag.getList("directions", 10);
            for (int j = 0; j < dir.size(); j++) {
                CompoundTag directionCompound = dir.getCompound(j);
                directions.add(Direction.byName(directionCompound.getString("direction")));
            }
            path.add(directions, dirTag.getFloat("progress"));
        }
        return path;
    }

    public String getProgress(){
        StringBuilder progress = new StringBuilder();
        float percent = 0;
        for(List<Direction> directions : directionMap.keySet()){
            percent += directionMap.get(directions);
        }
        progress.append(Math.min(100, percent));
        return progress.toString().toUpperCase(Locale.ROOT);
    }

}
