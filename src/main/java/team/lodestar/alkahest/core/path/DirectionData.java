package team.lodestar.alkahest.core.path;

import net.minecraft.core.Direction;

import java.util.List;
import java.util.Set;

public record DirectionData(Path directions) {
    public List<List<Direction>> getDirections() {
        return directions.getAllDirections();
    }

    public Float getProgress(List<Direction> dir){
        for(PathProgressData data : directions.directionMap){
            if(data.getPath().equals(dir)){
                return data.getProgress();
            }
        }
        return 0f;
    }
}
