package ca.team2706.frc.robot.config;

import java.util.HashMap;

/**
 * Xbox controller binding information.
 * Contains the link between the Xbox's buttons' port and the NetworkTables key used to describe the action.
 */
public enum XboxValue {
    // Axis and triggers
    // Left on the Left Stick
    XBOX_LEFT_STICK_X(0, "L_STICK_X", XboxInputType.Axis),
    XBOX_LEFT_STICK_Y(1, "L_STICK_Y", XboxInputType.Axis),
    XBOX_BACK_LEFT_TRIGGER(2, "L_TRIG", XboxInputType.Axis),
    XBOX_BACK_RIGHT_TRIGGER(3, "R_TRIG", XboxInputType.Axis),
    XBOX_RIGHT_STICK_X(4, "R_STICK_X", XboxInputType.Axis),
    XBOX_RIGHT_STICK_Y(5, "R_STICK_Y", XboxInputType.Axis),

    // Buttons
    XBOX_A_BUTTON(1, "A", XboxInputType.Button),
    XBOX_B_BUTTON(2, "B", XboxInputType.Button),
    XBOX_X_BUTTON(3, "X", XboxInputType.Button),
    XBOX_Y_BUTTON(4, "Y", XboxInputType.Button),
    XBOX_LB_BUTTON(5, "LB", XboxInputType.Button),
    XBOX_RB_BUTTON(6, "RB", XboxInputType.Button),
    XBOX_SELECT_BUTTON(7, "SELECT", XboxInputType.Button),
    XBOX_START_BUTTON(8, "START", XboxInputType.Button),
    XBOX_LEFT_AXIS_BUTTON(9, "L_AXIS_BUTTON", XboxInputType.Button),
    XBOX_RIGHT_AXIS_BUTTON(10, "R_AXIS_BUTTON", XboxInputType.Button),

    // POV (The D-PAD on the XBOX Controller)
    XBOX_POV_UP(0, "UP", XboxInputType.POV),
    XBOX_POV_UP_RIGHT(45, "UP_RIGHT", XboxInputType.POV),
    XBOX_POV_RIGHT(90, "RIGHT", XboxInputType.POV),
    XBOX_POV_DOWN_RIGHT(135, "DOWN_RIGHT", XboxInputType.POV),
    XBOX_POV_DOWN(180, "DOWN", XboxInputType.POV),
    XBOX_POV_DOWN_LEFT(225, "DOWN_LEFT", XboxInputType.POV),
    XBOX_POV_LEFT(270, "LEFT", XboxInputType.POV),
    XBOX_POV_UP_LEFT(315, "UP_LEFT", XboxInputType.POV);

    private final String NTString;
    private final int port;
    private final XboxInputType inputType;

    XboxValue(int port, String NTString, XboxInputType inputType) {
        this.NTString = NTString;
        this.port = port;
        this.inputType = inputType;
    }

    /**
     * @return the nTString
     */
    public String getNTString() {
        return NTString;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Gets the input type of the input
     *
     * @return Either Axis, Button, or POV
     */
    public XboxInputType getInputType() {
        return inputType;
    }


    // Create a hashmap of the networktables entry and the
    private static final HashMap<String, XboxValue> nameMap = new HashMap<>();

    static {
        for (XboxValue value : XboxValue.values()) {
            nameMap.put(value.getNTString(), value);
        }
    }

    /**
     * Gets the XboxValue constant with the given NetworkTables key.
     *
     * @param ntKey The NetworkTables key for the constant.
     * @return The constant object.
     */
    public static XboxValue getXboxValueFromNTKey(final String ntKey) {
        return nameMap.get(ntKey);
    }

    /**
     * Gets the XboxValue constant with the given fluid constant.
     * Used for fluid control bindings. Control bindings should be set up so that for every operator/driver
     * action there is a networktables entry with the value being an XboxValue NT string which associates
     * the action with a controller button.
     *
     * @param fluidConstant The fluid constant in managing the binding.
     * @return The XboxValue to which the fluid constant is set.
     */
    public static XboxValue getXboxValueFromFluidConstant(FluidConstant<String> fluidConstant) {
        return getXboxValueFromNTKey(fluidConstant.value());
    }

    /**
     * Gets the controller button's port number from the given fluid constant.
     *
     * @param fluidConstant The fluid constant controlling the binding.
     * @return The port number.
     */
    public static int getPortFromFluidConstant(FluidConstant<String> fluidConstant) {
        return getXboxValueFromFluidConstant(fluidConstant).getPort();
    }

    /**
     * Gets the port for a controller binding from the networktables key for the button.
     *
     * @param ntString The networktables key for the button. Should have come from {@link #getNTString()}
     * @return The port for the button.
     */
    public static int getPortFromNTString(final String ntString) {
        return getXboxValueFromNTKey(ntString).getPort();
    }

    /**
     * The type of input
     */
    public enum XboxInputType {
        /**
         * A raw axis or analog stick
         */
        Axis,

        /**
         * A button on the robot
         */
        Button,

        /**
         * A POV or D-pad on the robot
         */
        POV
    }
}