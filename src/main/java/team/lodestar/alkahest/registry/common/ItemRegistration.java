package team.lodestar.alkahest.registry.common;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.alkahest.Alkahest;

public class ItemRegistration {
    public static DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Alkahest.MODID);

    public static final RegistryObject<Item> MORTAR = ITEMS.register("mortar", () -> new BlockItem(BlockRegistration.MORTAR.get(), properties(CreativeModeTab.TAB_BREWING)));

    public static Item.Properties properties(CreativeModeTab tab) {
        return new Item.Properties().tab(tab);
    }
}
