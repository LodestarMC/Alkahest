package team.lodestar.alkahest.common.block.mortar;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.recipe.MortarRecipe;
import team.lodestar.alkahest.registry.common.BlockEntityRegistration;
import team.lodestar.lodestone.helpers.BlockHelper;
import team.lodestar.lodestone.systems.blockentity.ItemHolderBlockEntity;
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory;

import java.util.Optional;

public class MortarBlockEntity extends ItemHolderBlockEntity {
    public LodestoneBlockEntityInventory output;
    public MortarRecipe recipe;
    public int progress;
    public int cooldown;
    public int spins;

    public MortarBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MortarBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistration.MORTAR.get(), pos, state);
        inventory = new LodestoneBlockEntityInventory(1, 8) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(level, pos);
            }
        };
        output = new LodestoneBlockEntityInventory(1, 64){
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(level, pos);
            }
        };
    }

    @Override
    public InteractionResult onUse(Player player, InteractionHand hand) {
        if(player.getItemInHand(hand).isEmpty() && !player.isCrouching() && output.isEmpty() && !inventory.isEmpty()){
            level.playSound(null, worldPosition, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1f, 1f);
            if(spins < 5){
                spins++;
            } else {
                craftItem(this);
            }
            return InteractionResult.SUCCESS;
        }
        if(spins > 5){
            spins = 0;
        }
        super.onUse(player, hand);
        if(!output.isEmpty()){
            output.interact(level, player, hand);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean hasRecipe(MortarBlockEntity entity){
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.inventory.getSlots());
        for(int i = 0; i < entity.inventory.getSlots(); i++){
            inventory.setItem(i, entity.inventory.extractItem(i, 1, true));
        }
        Optional<MortarRecipe> match = level.getRecipeManager().getRecipeFor(MortarRecipe.Type.INSTANCE, inventory, level);
        return match.isPresent();
    }

    private static void craftItem(MortarBlockEntity entity){
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.inventory.getSlots());
        for(int i = 0; i < entity.inventory.getSlots(); i++){
            inventory.setItem(i, entity.inventory.extractItem(i, 1, false));
        }
        Optional<MortarRecipe> match = level.getRecipeManager().getRecipeFor(MortarRecipe.Type.INSTANCE, inventory, level);
        Alkahest.LOGGER.info(match.toString());

        if(match.isPresent()) {
            for(int i = 0; i < entity.inventory.nonEmptyItemAmount; i++){
                entity.inventory.extractItem(i, 1, false);
            }
            entity.output.setStackInSlot(0, match.get().getResultItem());
            entity.progress = 0;
            entity.spins = 0;
            entity.cooldown = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(cooldown < 10 && spins > 1){
            cooldown++;
        }
    }

    @Override
    public void onBreak(@Nullable Player player) {
        inventory.dumpItems(level, worldPosition);
        output.dumpItems(level, worldPosition);
    }
}
