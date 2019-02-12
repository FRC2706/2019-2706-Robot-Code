package ca.team2706.frc.robot.input;

import edu.wpi.first.wpilibj.GenericHID;

import java.util.Objects;

/**
 * Activates a button from a joystick axis trigger
 */
public class AxisButton extends EButton {

    private final GenericHID input;
    private final int axis;
    private final double minTrigger;
    private final TriggerType triggerType;

    /**
     * Creates an axis button
     *
     * @param input The input that the axis is on
     * @param axis The axis channel
     * @param minTrigger The minimum value of the raw axis
     * @param triggerType Whether the trigger should activate for negative, positive or both ends of the axis
     */
    public AxisButton(GenericHID input, int axis, double minTrigger, TriggerType triggerType) {
        this.input = Objects.requireNonNull(input);
        this.axis = axis;
        this.minTrigger = minTrigger;
        this.triggerType = Objects.requireNonNull(triggerType);
    }

    /**
     * Creates an axis button using an index for {@code triggerType}
     *
     * @param input The input that the axis is on
     * @param axis The axis channel
     * @param minTrigger The minimum value of the raw axis
     * @param triggerType Whether the trigger should activate for negative, positive or both ends of the axis (0, 1, 2)
     */
    public AxisButton(GenericHID input, int axis, double minTrigger, int triggerType) {
        this.input = Objects.requireNonNull(input);
        this.axis = axis;
        this.minTrigger = minTrigger;
        this.triggerType = TriggerType.values()[triggerType];
    }

    @Override
    public boolean get() {
        double val = input.getRawAxis(axis);

        if((triggerType == TriggerType.Positive || triggerType == TriggerType.Both) && val >= minTrigger) {
            return true;
        }
        else return (triggerType == TriggerType.Negative || triggerType == TriggerType.Both) && val <= -minTrigger;
    }

    /**
     * The type of trigger condition for the button to activate
     */
    public enum TriggerType {
        /**
         * Activate when the trigger is positive
         */
        Positive,

        /**
         * Activate when the trigger is negative
         */
        Negative,

        /**
         * Activate when the trigger is positive or negative
         */
        Both;
    }
}
