package team.lodestar.alkahest.registry.common;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.recipe.MortarRecipe;

public class RecipeRegistration {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Alkahest.MODID);

    public static final RegistryObject<RecipeSerializer<MortarRecipe>> MORTAR = RECIPE_SERIALIZERS.register("mortar", () -> MortarRecipe.Serializer.INSTANCE);
}
