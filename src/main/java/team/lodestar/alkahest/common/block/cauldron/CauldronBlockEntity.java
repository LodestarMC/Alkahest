package team.lodestar.alkahest.common.block.cauldron;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.TagTypes;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.alchemy.PotionMap;
import team.lodestar.alkahest.core.alchemy.PotionMapInstruction;
import team.lodestar.alkahest.core.alchemy.PotionMapInstructions;
import team.lodestar.alkahest.core.handlers.AlkahestPacketHandler;
import team.lodestar.alkahest.core.listeners.PotionPathDataListener;
import team.lodestar.alkahest.core.net.ClientboundCauldronPacket;
import team.lodestar.alkahest.core.path.*;
import team.lodestar.alkahest.registry.common.BlockEntityRegistration;
import team.lodestar.lodestone.helpers.BlockHelper;
import team.lodestar.lodestone.systems.blockentity.ItemHolderBlockEntity;
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.IntFunction;

public class CauldronBlockEntity extends ItemHolderBlockEntity {
    public List<Path> paths = new ArrayList();
    public List<PotionPathData> containedPotions = new ArrayList();
    public List<String> containedPotionNames = new ArrayList<>();
    public double progress = 0;
    public double previousVisualPercentage = 0;
    public double visualPercentage = 0;
    public boolean flipped = false;
    public PotionMap unmodifiedPotionMap = PotionMap.EMPTY;
    public PotionMap mutablePotionMap = PotionMap.EMPTY;
    public List<Pair<Integer,IntFunction<PotionMapInstruction>>> instructions = new ArrayList<>();

    public CauldronBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public CauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistration.CAULDRON.get(), pos, state);
        inventory = new LodestoneBlockEntityInventory(1, 1) {
            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                BlockHelper.updateAndNotifyState(level, pos);
            }
        };
    }

    @Override
    public InteractionResult onUse(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).is(Items.POTION)) {
            ItemStack stack = player.getItemInHand(hand);
            List<MobEffectInstance> effects = new ArrayList<>();
            for (PotionPathData data : containedPotions) {
                effects.addAll(data.effects);
            }
            PotionUtils.setCustomEffects(stack, effects);
            stack.setHoverName(new TextComponent("Compound Potion"));
            player.setItemInHand(hand, stack);
            return InteractionResult.SUCCESS;
        }
        inventory.interact(level, player, hand);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(LivingEntity placer, ItemStack stack) {
        unmodifiedPotionMap.copy(PotionPathDataListener.potionMap);
        super.onPlace(placer, stack);
    }

    @Override
    public void tick() {
        super.tick();
        if(!unmodifiedPotionMap.map.equals(PotionPathDataListener.potionMap.map)){
            unmodifiedPotionMap.copy(PotionPathDataListener.potionMap);
            mutablePotionMap = unmodifiedPotionMap;
            for (Pair<Integer, IntFunction<PotionMapInstruction>> instruction : instructions) {
                mutablePotionMap = instruction.getSecond().apply(instruction.getFirst()).instruction.apply(mutablePotionMap);
            }
        }
        if (progress < 4) {
            progress++;
        }
        if (!level.isClientSide) {
            dissolveItem(inventory.getStackInSlot(0), 0);
        }
        previousVisualPercentage = visualPercentage;
        visualPercentage = Mth.lerp(0.5f, visualPercentage, progress / 4);
    }

    // TODO: allow uncrushed items to be added to the cauldron and have first step in path be used
    // TODO: use a helper method to take an itemstack and return an ItemPathData object, look up item data from @link{ItemPathDataListener}
    public void dissolveItem(ItemStack stack, int slot) {
        if (!stack.isEmpty()) {
            if (stack.hasTag()) {
                CompoundTag tag = stack.getTag();
                if (tag.contains("path")) {
                    inventory.clear();
                    Path path = Path.fromNBT(tag.getCompound("path"));
                    PathModifier modifier = path.getModifier();
                    switch (modifier) {
                        case FLIP_ALL -> {
                            for (Path p : paths) {
                                p.getDirectionMap().forEach(PathProgressData::invert);
                            }
                            if (!flipped)
                                path.getDirectionMap().forEach(PathProgressData::invert);
                            paths.add(path);
                            progress = 0;
                            flipped = !flipped;
                        }
                        case FLIP -> {
                            flipped = !flipped;
                            path.getDirectionMap().forEach(PathProgressData::invert);
                            paths.add(path);
                            progress = 0;
                        }
                        default -> {
                            if (flipped) {
                                path.getDirectionMap().forEach(PathProgressData::invert);
                            }
                            paths.add(path);
                            progress = 0;
                        }
                    }
                    PotionMapInstructions.instructions.get(path.getInstruction()).apply(path.getPower()).instruction.apply(mutablePotionMap);
                    instructions.add(Pair.of(path.getPower(), PotionMapInstructions.instructions.get(path.getInstruction())));
                }
            }
        }
        checkPotionIntersections();
        AlkahestPacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 128, this.level.dimension())), new ClientboundCauldronPacket(worldPosition, paths, containedPotionNames));
    }

    // TODO: to ignore next points, store a pair<int, int> that determines the the beginning and end of the path to skip debuffs for
    // TODO: do the same for editing translation distances.
    /*
    TODO: add an extra param to addPathToNodeList for the above for "scale",
     */
    public void checkPotionIntersections() {
        containedPotions.clear();
        containedPotionNames.clear();
        List<Path> path = paths;
        List<Vec3> passPoints = new ArrayList<>();
        for (Path p : path) {
            IngredientPathUtils.addPathToNodeList(passPoints, p);
        }
        for (Vec3 node : passPoints) {
            PotionPathData potion = mutablePotionMap.isInRadius(node);
            if (potion != null) {
                if(!containedPotions.contains(potion)) {
                    containedPotions.add(potion);
                }
                for (MobEffectInstance ef : potion.effects) {
                    String effName = (new TranslatableComponent(ef.getDescriptionId())).getString() + ", Strength: " + ef.getAmplifier() + ", Duration: " + ef.getDuration();
                    if(!containedPotionNames.contains(effName)) {
                        containedPotionNames.add(effName);
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        if (!paths.isEmpty()) {
            ListTag list = new ListTag();
            for (Path p : paths) {
                list.add(p.toNBT());
            }
            compound.put("paths", list);
        }
        if (!containedPotions.isEmpty()) {
            ListTag list = new ListTag();
            for (PotionPathData p : containedPotions) {
                list.add(p.toNbt());
            }
            compound.put("potions", list);
        }
        compound.putDouble("progress", progress);
        compound.putDouble("previousVisualPercentage", previousVisualPercentage);
        compound.putDouble("visualPercentage", visualPercentage);
        compound.putBoolean("flipped", flipped);
        compound.put("unmodifiedPotionMap", unmodifiedPotionMap.toNbt());
        compound.put("mutablePotionMap", mutablePotionMap.toNbt());
        if (!instructions.isEmpty()) {
            ListTag list = new ListTag();
            for (Pair<Integer, IntFunction<PotionMapInstruction>> p : instructions) {
                for (String rl : PotionMapInstructions.instructions.keySet()){
                    if(PotionMapInstructions.instructions.get(rl).equals(p.getSecond())){
                        CompoundTag tag = new CompoundTag();
                        tag.putString("instruction", rl.toString());
                        tag.putInt("power", p.getFirst());
                        list.add(tag);
                    }
                }
            }
            compound.put("instructions", list);
        }
        super.saveAdditional(compound);
    }

    @Override
    public void load(CompoundTag compound) {
        paths.clear();
        if (compound.contains("paths")) {
            ListTag list = compound.getList("paths", 10);
            for (int i = 0; i < list.size(); i++) {
                paths.add(Path.fromNBT(list.getCompound(i)));
            }
        }
        containedPotions.clear();
        if (compound.contains("potions")) {
            ListTag list = compound.getList("potions", 10);
            for (int i = 0; i < list.size(); i++) {
                containedPotions.add(PotionPathData.fromNbt(list.getCompound(i)));
            }
        }
        progress = compound.getDouble("progress");
        previousVisualPercentage = compound.getDouble("previousVisualPercentage");
        visualPercentage = compound.getDouble("visualPercentage");
        flipped = compound.getBoolean("flipped");
        unmodifiedPotionMap = PotionMap.fromNbt(compound.getCompound("unmodifiedPotionMap"));
        mutablePotionMap = PotionMap.fromNbt(compound.getCompound("mutablePotionMap"));
        instructions.clear();
        if (compound.contains("instructions")) {
            ListTag list = compound.getList("instructions", 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                instructions.add(new Pair<>(tag.getInt("power"), PotionMapInstructions.instructions.get(tag.getString("instruction"))));
            }
        }
        super.load(compound);
    }
}
