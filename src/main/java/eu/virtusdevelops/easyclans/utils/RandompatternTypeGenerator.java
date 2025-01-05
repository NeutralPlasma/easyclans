package eu.virtusdevelops.easyclans.utils;

import org.bukkit.block.banner.PatternType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandompatternTypeGenerator {
    private static final Random PRNG = new Random();
    private final List<PatternType> patternTypes;

    public RandompatternTypeGenerator() {
        // Collect all known PatternType instances
        patternTypes = new ArrayList<>();
        patternTypes.add(PatternType.BASE);
        patternTypes.add(PatternType.SQUARE_BOTTOM_LEFT);
        patternTypes.add(PatternType.SQUARE_BOTTOM_RIGHT);
        patternTypes.add(PatternType.SQUARE_TOP_LEFT);
        patternTypes.add(PatternType.SQUARE_TOP_RIGHT);
        patternTypes.add(PatternType.STRIPE_BOTTOM);
        patternTypes.add(PatternType.STRIPE_TOP);
        patternTypes.add(PatternType.STRIPE_LEFT);
        patternTypes.add(PatternType.STRIPE_RIGHT);
        patternTypes.add(PatternType.STRIPE_CENTER);
        patternTypes.add(PatternType.STRIPE_MIDDLE);
        patternTypes.add(PatternType.STRIPE_DOWNRIGHT);
        patternTypes.add(PatternType.STRIPE_DOWNLEFT);
        patternTypes.add(PatternType.SMALL_STRIPES);
        patternTypes.add(PatternType.CROSS);
        patternTypes.add(PatternType.STRAIGHT_CROSS);
        patternTypes.add(PatternType.TRIANGLE_BOTTOM);
        patternTypes.add(PatternType.TRIANGLE_TOP);
        patternTypes.add(PatternType.TRIANGLES_BOTTOM);
        patternTypes.add(PatternType.TRIANGLES_TOP);
        patternTypes.add(PatternType.DIAGONAL_LEFT);
        patternTypes.add(PatternType.DIAGONAL_UP_RIGHT);
        patternTypes.add(PatternType.DIAGONAL_UP_LEFT);
        patternTypes.add(PatternType.DIAGONAL_RIGHT);
        patternTypes.add(PatternType.CIRCLE);
        patternTypes.add(PatternType.RHOMBUS);
        patternTypes.add(PatternType.HALF_VERTICAL);
        patternTypes.add(PatternType.HALF_HORIZONTAL);
        patternTypes.add(PatternType.HALF_VERTICAL_RIGHT);
        patternTypes.add(PatternType.HALF_HORIZONTAL_BOTTOM);
        patternTypes.add(PatternType.BORDER);
        patternTypes.add(PatternType.CURLY_BORDER);
        patternTypes.add(PatternType.CREEPER);
        patternTypes.add(PatternType.GRADIENT);
        patternTypes.add(PatternType.GRADIENT_UP);
        patternTypes.add(PatternType.BRICKS);
        patternTypes.add(PatternType.SKULL);
        patternTypes.add(PatternType.FLOWER);
        patternTypes.add(PatternType.MOJANG);
        patternTypes.add(PatternType.GLOBE);
        patternTypes.add(PatternType.PIGLIN);
        patternTypes.add(PatternType.FLOW);
        patternTypes.add(PatternType.GUSTER);
    }

    public PatternType randomPatternType() {
        return patternTypes.get(PRNG.nextInt(patternTypes.size()));
    }
}
