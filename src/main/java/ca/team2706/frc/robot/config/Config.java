package ca.team2706.frc.robot.config;

import java.util.ArrayList;

/**
 * Config manager for the robot.
 */
public class Config {
    // #### Solid constants ####
    public static final int XBOX_A = 1;

    // #### Fluid constants ####

    /* Control bindings */
    // Driver controls
    public static final FluidConstant<Integer> DRIVER_PRESS_A = constant("Driver Press A", XBOX_A);
    // Operator controls
    public static final FluidConstant<Integer> OPERATOR_PRESS_A = constant("Operator Press A", XBOX_A);

    private static final ArrayList<FluidConstant<?>> constants = new ArrayList<>();
    /**
     * Creates a new integer fluid constant.
     * @param name The name for the constant type.
     * @param initialValue The initialValue of the constant.
     * @return A new FluidConstant object representing the constant.
     */
    private static <A> FluidConstant<A> constant(final String name, final A initialValue) {
        FluidConstant<A> constant = new FluidConstant<>(name, initialValue);
        constants.add(constant);
        return constant;
    }

    /**
     * Sves all the value of the constants to a human-readable (but not machine readable) text file.
     */
    private static void saveConstants() {

    }
}
