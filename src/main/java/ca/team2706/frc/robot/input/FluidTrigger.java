package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import edu.wpi.first.wpilibj.GenericHID;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Trigger which uses fluid bindings as part of its conditions.
 * This allows for very specific conditions which trigger actions.
 */
public abstract class FluidTrigger extends ETrigger {
    private final Map<FluidConstant<String>, XboxValue> bindings = new HashMap<>();

    /**
     * Constructs a fluid trigger with the following bindings that will be updated and usable in the conditions.
     *
     * @param bindings The fluid constant bindings.
     */
    public FluidTrigger(FluidConstant<String>... bindings) {
        if (bindings == null || bindings.length <= 0) {
            throw new IllegalArgumentException("Bindings cannot be null or empty for FluidTrigger.");
        }

        Arrays.stream(bindings).forEach(fluidConstant -> {
            this.bindings.put(fluidConstant, XboxValue.getXboxValueFromFluidConstant(fluidConstant));
            fluidConstant.addChangeListener((oldValue, newValue) -> this.bindings.replace(fluidConstant, XboxValue.getXboxValueFromNTKey(newValue)));
        });
    }

    /**
     * Gets the XboxValue binding associated with the provided fluid constant. This binding
     * must have been included in the constructor arguments for this object.
     *
     * @param fluidConstant The fluid constant with which the XboxValue is associated with.
     * @return The XboxValue binding.
     */
    public XboxValue getBinding(final FluidConstant<String> fluidConstant) {
        return bindings.get(fluidConstant);
    }

    /**
     * Determines if any of the provided buttons/triggers/axis are active (being pressed).
     *
     * @param controller        The controller to be checked.
     * @param minAxisActivation The minimum required activation for the button/trigger to be considered pressed.
     * @param values            The XboxValues to be checked.
     * @return True if any of the buttons/triggers/axis are considered pressed, false otherwise.
     */
    public static boolean areAnyActive(GenericHID controller, final double minAxisActivation, XboxValue... values) {
        return Arrays.stream(values).anyMatch(xboxValue -> FluidButton.determineIfActivated(controller, xboxValue.getPort(), xboxValue.getInputType(), minAxisActivation));
    }

    /**
     * Determines if any of the provided buttons/triggers/axis are active (being pressed) with default minimum
     * axis activation.
     *
     * @param controller The controller to be checked.
     * @param values     The XboxValues to be checked.
     * @return True if any of the buttons/triggers/axis are considered pressed, false otherwise.
     * @see #areAnyActive(GenericHID, double, XboxValue...)
     */
    public static boolean areAnyActive(GenericHID controller, XboxValue... values) {
        return areAnyActive(controller, FluidButton.DEFAULT_MIN_AXIS_ACTIVATION, values);
    }

    /**
     * Determines if all of the provided buttons/triggers/axis are active (being pressed).
     *
     * @param controller        The controller to be checked.
     * @param minAxisActivation The minimum required activation for the button/trigger to be considered pressed.
     * @param values            The XboxValues to be checked.
     * @return True if any of the buttons/triggers/axis are considered pressed, false otherwise.
     */
    public static boolean areAllActive(GenericHID controller, final double minAxisActivation, XboxValue... values) {
        return Arrays.stream(values).allMatch(xboxValue -> FluidButton.determineIfActivated(controller, xboxValue.getPort(), xboxValue.getInputType(), minAxisActivation));
    }

    /**
     * Determines if any of the provided buttons/triggers/axis are active (being pressed) with default minimum
     * axis activation.
     *
     * @param controller The controller to be checked.
     * @param values     The XboxValues to be checked.
     * @return True if any of the buttons/triggers/axis are considered pressed, false otherwise.
     * @see #areAllActive(GenericHID, double, XboxValue...)
     */
    public static boolean areAllActive(GenericHID controller, XboxValue... values) {
        return areAllActive(controller, FluidButton.DEFAULT_MIN_AXIS_ACTIVATION, values);
    }
}
