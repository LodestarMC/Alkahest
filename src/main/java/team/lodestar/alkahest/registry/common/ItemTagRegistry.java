package team.lodestar.alkahest.registry.common;

import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import team.lodestar.alkahest.Alkahest;

public class ItemTagRegistry {
    public static final TagKey<Item> CRUSHABLES = TagKey.create(Registry.ITEM_REGISTRY, Alkahest.alkahest("crushables"));
}
