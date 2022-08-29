package team.lodestar.alkahest.core.path;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PathProgressData {
    float progress;
    List<Direction> path;

    public PathProgressData(float progress, List<Direction> path) {
        this.progress = progress;
        this.path = path;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public List<Direction> getPath() {
        return path;
    }

    public void setPath(List<Direction> path) {
        this.path = path;
    }

    public void invert(){
        List<Direction> newPath = new ArrayList<>();
        for(Direction dir : path){
            newPath.add(dir.getOpposite());
        }
        path = newPath;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("progress", progress);
        ListTag list = new ListTag();
        for(Direction direction : path){
            CompoundTag directionCompound = new CompoundTag();
            directionCompound.putString("direction", direction.getName().toLowerCase(Locale.ROOT));
            list.add(directionCompound);
        }
        tag.put("path", list);
        return tag;
    }

    public static PathProgressData fromNBT(CompoundTag tag) {
        float progress = tag.getFloat("progress");
        ListTag list = tag.getList("path", 10);
        List<Direction> path = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            CompoundTag directionCompound = list.getCompound(i);
            path.add(Direction.byName(directionCompound.getString("direction")));
        }
        return new PathProgressData(progress, path);
    }
}
