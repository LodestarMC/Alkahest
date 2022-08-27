package team.lodestar.alkahest.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericCrushedItem extends Item {
    public GenericCrushedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        if(pStack.hasTag()){
            //pTooltipComponents.add(new TextComponent(pStack.getTag().getString("item")));
            //pTooltipComponents.add(new TranslatableComponent("item.alkahest.crushed_item.percentage_tooltip").append(pStack.getTag().getInt("crush")+ "%"));
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        if(pStack.hasTag()){
            return new TextComponent("Crushed " + pStack.getTag().getString("item")).append(new TextComponent(pStack.getTag().getInt("crush")+"").withStyle(s -> s.withColor(ChatFormatting.GREEN)));
        }
        return super.getName(pStack);
    }
}
