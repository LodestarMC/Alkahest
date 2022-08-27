package team.lodestar.alkahest.core.path;

import net.minecraft.core.Direction;

import java.util.List;
import java.util.Set;

public record DirectionData(Path directions) {
    public Direction getMainDirection() {
        return directions.directionMap.keySet().stream().toList().get(0).get(0);
    }
    public List<Direction> getModDirections() {
        return directions.directionMap.keySet().stream().toList().get(0).subList(1, directions.directionMap.keySet().stream().toList().get(0).size());
    }
    public Set<List<Direction>> getDirections() {
        return directions.directionMap.keySet();
    }

    public Float getProgress(List<Direction> dir){
        return directions.directionMap.get(dir);
    }
}
