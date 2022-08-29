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
        List<List<Direction>> directions = path.getAllDirections();
        Vec3 vec = Vec3.ZERO;
        for(List<Direction> dir : directions){
            for(Direction d : dir){
                vec = vec.add(Vec3.atLowerCornerOf(d.getNormal()).scale(path.getProgress(dir)/(100f/path.directionMap.size())));
            }
        }
        return vec;
    }

    public static List<Vec3> passedPoints(Path path){
        List<List<Direction>> directions = path.getAllDirections();
        List<Vec3> points = new ArrayList<>();
        points.add(Vec3.ZERO);
        Vec3 vec = Vec3.ZERO;
        for(List<Direction> dir : directions){
            if(path.getProgress(dir) == Math.round(100f/path.directionMap.size())){
                for(Direction d : dir){
                    vec = vec.add(Vec3.atLowerCornerOf(d.getNormal()));
                }
                points.add(vec);
            }
        }
        return points;
    }

    public static Vec3 getVecAtPoint(List<Direction> directions, Path path){
        Vec3 vec = Vec3.ZERO;
        for(Direction d : directions){
            vec = vec.add(Vec3.atLowerCornerOf(d.getNormal()).multiply((double)path.getProgress(directions)/100f, (double)path.getProgress(directions)/100f, (double)path.getProgress(directions)/100f));
        }
        return vec;
    }

    public static List<Vec3> addPathToNodeList(List<Vec3> nodes, Path path){
        List<List<Direction>> directions = path.getAllDirections();
        Vec3 vec = Vec3.ZERO;
        if(nodes.size() > 0){
            vec = nodes.get(nodes.size()-1);
        }
        for(List<Direction> dir : directions){
            vec = vec.add(getVecAtPoint(dir, path));
            nodes.add(vec);
        }
        return nodes;
    }

    public static List<Vec3> pathPoints(Path path){
        List<List<Direction>> directions = path.getAllDirections();
        List<Vec3> points = new ArrayList<>();
        Vec3 vec = Vec3.ZERO;
        for(List<Direction> dir : directions){
            vec = vec.add(getVecAtPoint(dir, path));
            points.add(vec);
        }
        return points;
    }
}
