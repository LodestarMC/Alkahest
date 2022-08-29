package team.lodestar.alkahest.core.path;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Path {
    List<PathProgressData> directionMap;

    public Path() {
        directionMap = new ArrayList<>();
    }

    public boolean isEmpty(){
        return directionMap.isEmpty();
    }

    public void add(List<Direction> directions, float progress) {
        directionMap.add(new PathProgressData(progress, directions));
    }

    public void add(List<Direction> directions) {
        directionMap.add(new PathProgressData(0, directions));
    }

    public void progress() {
        float perDir = 100f / directionMap.size();
        for(PathProgressData data : directionMap){
            if(data.getProgress() < perDir){
                data.setProgress(data.getProgress() + 1);
                return;
            }
        }
    }

    public float getProgress(List<Direction> dir){
        for(PathProgressData data : directionMap){
            if(data.getPath().equals(dir)){
                return data.getProgress();
            }
        }
        return 0f;
    }

    public List<List<Direction>> getAllDirections() {
        List<List<Direction>> directions = new ArrayList<>();
        for(PathProgressData data : directionMap){
            List<Direction> secondDirs = data.getPath();
            directions.add(secondDirs);
        }
        return directions;
    }

    public void addPath(Path path) {
        directionMap.addAll(path.directionMap);
    }

    public void clear() {
        directionMap.clear();
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        for(PathProgressData data : directionMap){
            list.add(data.toNBT());
        }
        tag.put("directionsList", list);
        return tag;
    }

    public static Path fromNBT(CompoundTag tag) {
        Path path = new Path();
        ListTag list = tag.getList("directionsList", 10);
        for(int i = 0; i < list.size(); i++){
            CompoundTag directionCompound = list.getCompound(i);
            path.directionMap.add(PathProgressData.fromNBT(directionCompound));
        }
        return path;
    }

    public String getProgress() {
        StringBuilder progress = new StringBuilder();
        float percent = 0;
        for(PathProgressData data : directionMap){
            percent += data.getProgress();
        }
        progress.append(Math.min(100, percent));
        return progress.toString().toUpperCase(Locale.ROOT);
    }

    public float getFlatProgress() {
        float percent = 0;
        for(PathProgressData data : directionMap){
            percent += data.getProgress();
        }
        return percent;
    }

}
