package team.lodestar.alkahest.core.recipe;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class DirectionalRecipe {
    public List<Direction> directions;
    // Use this instead of the list of directions, translate from Direction to Vec3i offset when adding to the recipe
    public Vec3i position;
    public ItemStack output;
    public List<Ingredient> ingredients;

    public DirectionalRecipe(List<Direction> directions) {
        this.directions = directions;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public void setDirections(List<Direction> directions) {
        this.directions = directions;
    }

    public boolean isValid() {
        return directions.size() > 0;
    }

    public Vec3i getVector() {
        Vec3i vec = new Vec3i(0, 0, 0);
        for(Direction d : directions){
            vec.offset(d.getStepX(), d.getStepY(), d.getStepZ());
        }
        return vec;
    }

    public ItemStack getOutput() {
        return output;
    }

    public void setOutput(ItemStack output) {
        this.output = output;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean matches(List<Direction> directions) {
        if(directions.size() != this.directions.size()){
            return false;
        }
        for(int i = 0; i < directions.size(); i++){
            if(directions.get(i) != this.directions.get(i)){
                return false;
            }
        }
        return true;
    }

    public boolean matches(DirectionalRecipe recipe) {
        return matches(recipe.getDirections());
    }
}
