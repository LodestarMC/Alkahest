package team.lodestar.alkahest.registry.common;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.common.block.mortar.MortarBlock;

public class BlockRegistration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Alkahest.MODID);

    public static final RegistryObject<Block> MORTAR = BLOCKS.register("mortar", () -> new MortarBlock<>(Block.Properties.of(Material.STONE)).setBlockEntity(BlockEntityRegistration.MORTAR));
}
