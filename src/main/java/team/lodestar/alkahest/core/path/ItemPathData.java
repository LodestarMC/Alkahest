package team.lodestar.alkahest.core.path;

import net.minecraft.core.Direction;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ItemPathData {
    public final List<Direction> path;
    public final Ingredient item;

    public ItemPathData(List<Direction> pPath, Ingredient pItem) {
        this.path = pPath;
        this.item = pItem;
    }
}
