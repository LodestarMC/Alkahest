package team.lodestar.alkahest.core.path;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.setup.LodestoneRenderTypeRegistry;

import java.util.ArrayList;
import java.util.List;

public class IngredientPathUtils {
    // Get the current progress of the path
    public static Vec3 getCurrentVector(Path path){
        List<List<Direction>> directions = path.directionMap.keySet().stream().toList();
        Vec3 vec = Vec3.ZERO;
        for(List<Direction> dir : directions){
            for(Direction d : dir){
                vec = vec.add(Vec3.atLowerCornerOf(d.getNormal()).scale(path.directionMap.get(dir)/(100f/path.directionMap.size())));
            }
        }
        return vec;
    }

    public static List<Vec3> passedPoints(Path path){
        List<List<Direction>> directions = path.directionMap.keySet().stream().toList();
        List<Vec3> points = new ArrayList<>();
        points.add(Vec3.ZERO);
        Vec3 vec = Vec3.ZERO;
        for(List<Direction> dir : directions){
            if(path.directionMap.get(dir) == Math.round(100f/path.directionMap.size())){
                for(Direction d : dir){
                    vec = vec.add(Vec3.atLowerCornerOf(d.getNormal()));
                }
                points.add(vec);
            }
        }
        return points;
    }
}
