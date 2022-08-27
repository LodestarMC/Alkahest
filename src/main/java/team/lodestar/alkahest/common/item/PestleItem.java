package team.lodestar.alkahest.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import team.lodestar.alkahest.common.block.mortar.MortarBlockEntity;

public class PestleItem extends Item {
    public PestleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(pContext.getLevel().getBlockEntity(pContext.getClickedPos()) != null){
            if(pContext.getLevel().getBlockEntity(pContext.getClickedPos()) instanceof MortarBlockEntity mbe){
                mbe.addPercentage(mbe.inventory.getStackInSlot(0));
                return InteractionResult.SUCCESS;
            }
        }
        return super.useOn(pContext);
    }
}
