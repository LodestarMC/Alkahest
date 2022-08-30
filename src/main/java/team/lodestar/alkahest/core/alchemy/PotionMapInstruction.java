package team.lodestar.alkahest.core.alchemy;

import java.util.function.UnaryOperator;

public class PotionMapInstruction {
    public UnaryOperator<PotionMap> instruction;
    public String name;

    public PotionMapInstruction(UnaryOperator<PotionMap> instruction, String name) {
        this.instruction = instruction;
        this.name = name;
    }
}
