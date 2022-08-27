package team.lodestar.alkahest.common.block.mortar;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.recipe.MortarRecipe;
import team.lodestar.alkahest.registry.common.BlockEntityRegistration;
import team.lodestar.alkahest.registry.common.ItemRegistration;
import team.lodestar.alkahest.registry.common.ItemTagRegistry;
import team.lodestar.lodestone.helpers.BlockHelper;
import team.lodestar.lodestone.systems.blockentity.ItemHolderBlockEntity;
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory;

import java.util.Optional;

public class MortarBlockEntity extends ItemHolderBlockEntity {
    public LodestoneBlockEntityInventory output;
    boolean isSpinning = false;
    Player lastInteracted;

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
//        if(player.getItemInHand(hand).isEmpty() && player.isCrouching() && !inventory.isEmpty()){
//            super.onUse(player, hand);
//            return InteractionResult.SUCCESS;
//        }
//        if(player.getItemInHand(hand).is(Items.STICK) && !inventory.isEmpty() && !player.isCrouching()){
//            level.playSound(null, worldPosition, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS, 1f, 1f);
//            //addPercentage(inventory.getStackInSlot(0));
//            isSpinning = true;
//            lastInteracted = player;
//            return InteractionResult.SUCCESS;
//        }
//        super.onUse(player, hand);
//        return InteractionResult.SUCCESS;
        if(player.getItemInHand(hand).is(ItemRegistration.PESTLE.get())){
            return InteractionResult.PASS;
        }
        return super.onUse(player, hand);
    }

    @Override
    public void tick() {
        super.tick();
        if(isSpinning){
            if(lastInteracted != null && lastInteracted.getItemInHand(InteractionHand.MAIN_HAND).is(Items.STICK)){
                if(!lastInteracted.isCrouching()){
                    addPercentage(inventory.getStackInSlot(0));
                    isSpinning = false;
                    lastInteracted = null;
                }
            }
        }
    }

    public void addPercentage(ItemStack stack){
        if(stack.is(ItemTagRegistry.CRUSHABLES)){
            ItemStack crushedStack = stack.is(ItemRegistration.GENERIC_CRUSHED.get()) ? stack : new ItemStack(ItemRegistration.GENERIC_CRUSHED.get(), stack.getCount());
            int crushedAmount = stack.is(ItemRegistration.GENERIC_CRUSHED.get()) ? Math.min(stack.getTag().getInt("crush") + 1, 100) : 1;
            crushedStack.getOrCreateTag().putInt("crush", crushedAmount);
            if(!stack.is(ItemRegistration.GENERIC_CRUSHED.get())){
                crushedStack.getOrCreateTag().putString("item", stack.getHoverName().getString());
            }
            inventory.setStackInSlot(0, crushedStack);
        }
    }
//    private static boolean hasRecipe(MortarBlockEntity entity){
//        Level level = entity.level;
//        SimpleContainer inventory = new SimpleContainer(entity.inventory.getSlots());
//        for(int i = 0; i < entity.inventory.getSlots(); i++){
//            inventory.setItem(i, entity.inventory.extractItem(i, 1, true));
//        }
//        Optional<MortarRecipe> match = level.getRecipeManager().getRecipeFor(MortarRecipe.Type.INSTANCE, inventory, level);
//        return match.isPresent();
//    }
//
//    private static void craftItem(MortarBlockEntity entity){
//        Level level = entity.level;
//        SimpleContainer inventory = new SimpleContainer(entity.inventory.getSlots());
//        for(int i = 0; i < entity.inventory.getSlots(); i++){
//            inventory.setItem(i, entity.inventory.extractItem(i, 1, false));
//        }
//        Optional<MortarRecipe> match = level.getRecipeManager().getRecipeFor(MortarRecipe.Type.INSTANCE, inventory, level);
//        Alkahest.LOGGER.info(match.toString());
//
//        if(match.isPresent()) {
//            for(int i = 0; i < entity.inventory.nonEmptyItemAmount; i++){
//                entity.inventory.extractItem(i, 1, false);
//            }
//            entity.output.setStackInSlot(0, match.get().getResultItem());
//            entity.progress = 0;
//            entity.spins = 0;
//            entity.cooldown = 0;
//        }

//    }

    @Override
    public void onBreak(@Nullable Player player) {
        inventory.dumpItems(level, worldPosition);
        output.dumpItems(level, worldPosition);
    }
}
