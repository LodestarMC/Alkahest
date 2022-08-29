package team.lodestar.alkahest.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import team.lodestar.alkahest.core.path.Path;
import team.lodestar.alkahest.core.render.StringHelper;

import java.util.List;
import java.util.Locale;

public class GenericCrushedItem extends Item {
    public GenericCrushedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getBarWidth(ItemStack pStack) {
        if(pStack.hasTag() && pStack.getTag() != null){
            Path path = Path.fromNBT((CompoundTag) pStack.getTag().get("path"));
            return (int) (path.getFlatProgress() / 100f * 13);
        }
        return 0;
    }

    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return true;
    }

    @Override
    public int getBarColor(ItemStack pStack) {
        if(pStack.hasTag() && pStack.getTag() != null){
            return pStack.getTag().getInt("middleColor");
        }
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(pStack.hasTag() && pStack.getTag() != null){
            String modifier = StringHelper.capitalizeWords(Path.fromNBT(pStack.getTag().getCompound("path")).getModifier().toString().toLowerCase(Locale.ROOT).replaceAll("_", " "));
            pTooltipComponents.add(new TextComponent(modifier).withStyle(ChatFormatting.GOLD));
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public Component getName(ItemStack pStack) {
        if(pStack.hasTag()){
            Path path = Path.fromNBT((CompoundTag) pStack.getTag().get("path"));
            String itemName = Registry.ITEM.get(ResourceLocation.tryParse(pStack.getTag().getString("item"))).getDescriptionId();
            return new TextComponent("Crushed ").append(new TranslatableComponent(itemName)).append(new TextComponent(" " + path.getProgress()).withStyle(s -> s.withColor(pStack.getTag().getInt("middleColor"))));
        }
        return super.getName(pStack);
    }
}
