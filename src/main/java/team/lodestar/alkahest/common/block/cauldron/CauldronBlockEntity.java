package team.lodestar.alkahest.common.block.cauldron;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.lodestar.alkahest.core.listeners.ItemPathDataListener;
import team.lodestar.alkahest.core.path.Path;
import team.lodestar.alkahest.registry.common.BlockEntityRegistration;
import team.lodestar.lodestone.helpers.BlockHelper;
import team.lodestar.lodestone.systems.blockentity.ItemHolderBlockEntity;
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory;

public class CauldronBlockEntity extends ItemHolderBlockEntity {
    public Path path = new Path();
    public CauldronBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    public CauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistration.CAULDRON.get(), pos, state);
        inventory = new LodestoneBlockEntityInventory(16, 1) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(level, pos);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        path.clear();
        if(!inventory.isEmpty()){
            for(ItemStack stack : inventory.nonEmptyItemStacks){
                if(stack.hasTag()){
                    CompoundTag tag = stack.getTag();
                    if(tag.contains("path")){
                        Path addPath = Path.fromNBT(tag.getCompound("path"));
                        path.addPath(addPath);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        if (path != null) {
            compound.put("path", path.toNBT());
        }
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        path = Path.fromNBT(compound.getCompound("path"));
        super.load(compound);
    }
}
