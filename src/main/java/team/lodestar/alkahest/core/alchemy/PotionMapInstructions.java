package team.lodestar.alkahest.core.alchemy;

import com.google.common.collect.BiMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraftforge.registries.ForgeRegistries;
import team.lodestar.alkahest.Alkahest;

import java.util.*;
import java.util.function.IntFunction;

public class PotionMapInstructions {
    public static Map<String, IntFunction<PotionMapInstruction>> instructions = new HashMap<>();
    public static PotionMapInstruction randomFlip(int count) {
        return new PotionMapInstruction(map -> {
            Random random = new Random();
            List<MobEffect> positiveEffects = ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(s -> s.getCategory().equals(MobEffectCategory.BENEFICIAL)).toList();
            for(int c = 0; c < count; c++){
                int index = random.nextInt(map.getMap().size()-1);
                for (int i = 0; i < map.getMap().get(index).effects.size(); i++) {
                    System.out.println(Arrays.toString(map.getMap().get(index).effects.toArray()));
                    if (map.getMap().get(index).effects.get(i).getEffect().getCategory() == MobEffectCategory.HARMFUL) {
                        map.getMap().get(index).effects.get(i).effect = positiveEffects.get(random.nextInt(positiveEffects.size()-1));
                        c++;
                    }
                }
            }
            return map;
        }, "random_flip");
    }
    public static PotionMapInstruction none(int i) {
        return new PotionMapInstruction(map -> map, "none");
    }

    public static void setup(){
        instructions.put("random_flip", PotionMapInstructions::randomFlip);
        instructions.put("none", PotionMapInstructions::none);
    }
}
