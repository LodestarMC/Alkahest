package team.lodestar.alkahest.registry.common;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.common.item.GenericCrushedItem;
import team.lodestar.alkahest.common.item.PestleItem;

public class ItemRegistration {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Alkahest.MODID);

    public static final RegistryObject<Item> GENERIC_CRUSHED = ITEMS.register("generic_crushed", () -> new GenericCrushedItem(properties(CreativeModeTab.TAB_BREWING)));

    public static final RegistryObject<Item> MORTAR = ITEMS.register("mortar", () -> new BlockItem(BlockRegistration.MORTAR.get(), properties(CreativeModeTab.TAB_BREWING)));
    public static final RegistryObject<Item> CAULDRON = ITEMS.register("cauldron", () -> new BlockItem(BlockRegistration.CAULDRON.get(), properties(CreativeModeTab.TAB_BREWING)));
    public static final RegistryObject<Item> PESTLE = ITEMS.register("pestle", () -> new PestleItem(properties(CreativeModeTab.TAB_BREWING)));

    public static Item.Properties properties(CreativeModeTab tab) {
        return new Item.Properties().tab(tab);
    }
}
