package team.lodestar.alkahest.core.path;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.crafting.Ingredient;
import team.lodestar.alkahest.core.alchemy.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ItemPathData {
    public final DirectionData dirs;
    public List<Vec3i> nodes;
    public final Map<Element, Float> elements;
    public final int color;

    public ItemPathData(DirectionData dirs, Map<Element, Float> pElements, int pColor) {
        this.dirs = dirs;
        this.elements = pElements;
        calculateNodes();
        this.color = pColor;
    }

    public static String getPathString(DirectionData pPath) {
        StringBuilder pathString = new StringBuilder();
        for(List<Direction> dir : pPath.getDirections()) {
            if(dir.size() > 1){
                pathString.append("[ ");
                for(Direction d : dir) {
                    pathString.append(d.getName().toLowerCase(Locale.ROOT)).append(" ");
                }
                pathString.append("] ").append(pPath.getProgress(dir)).append("% ");
            } else {
                pathString.append(dir.get(0).getName().toLowerCase(Locale.ROOT)).append(" ").append(pPath.getProgress(dir)).append("% ");
            }
        }
        return pathString.toString().toUpperCase(Locale.ROOT);
    }

    public static String getElementsString(Map<Element, Float> pElements) {
        StringBuilder elementsString = new StringBuilder();
        for(Element element : pElements.keySet()) {
            elementsString.append(element.name());
            elementsString.append(" ");
            elementsString.append(pElements.get(element));
            elementsString.append(" ");
        }
        return elementsString.toString().toUpperCase(Locale.ROOT);
    }

    public String getPathString() {
        return getPathString(this.dirs);
    }

    public String getElementsString() {
        return getElementsString(this.elements);
    }

    public void calculateNodes(){
        nodes = new ArrayList<>();
        nodes.add(Vec3i.ZERO);
        Vec3i localNode = Vec3i.ZERO;
        for(List<Direction> dir : dirs.getDirections()) {
            for(Direction d : dir) {
                localNode = localNode.offset(d.getStepX(), d.getStepY(), d.getStepZ());
            }
            System.out.println(localNode);
            nodes.add(localNode);
        }
    }
}
