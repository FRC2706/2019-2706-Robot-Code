package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift to a given setpoint.
 */
public class MoveLiftToSetpointOnPOV extends Command {
    private final GenericHID controller;

    /**
     * The last bound Xbox POV that was bound to something. Will be one of the POV UP, DOWN, LEFT or RIGHT values.
     */
    private XboxValue lastPressedBound;

    /**
     * Current setpoint goal.
     */
    private int currentSetpoint;

    /**
     * Moves the lift to the currently set setpoint, depending on the Intake mode (either cargo setpoints
     * or hatch setpoints).
     *
     * @param controller The controller to be checked.
     */
    public MoveLiftToSetpointOnPOV(final GenericHID controller) {
        requires(Lift.getInstance());
        this.controller = controller;
    }

    @Override
    protected void execute() {
        // Keep track of the old setpoint so we don't spam the Lift with the same change.
        final int oldSetpoint = currentSetpoint;

        // Bound POV that will be used when the POV is pressed.
        final XboxValue boundPOV;

        final XboxValue pressedPOV = getPressedPOV();

        // If none of the bound POVs are being pressed, run the last pressed bound POV.
        if (!(pressedPOV == XboxValue.XBOX_POV_DOWN ||
                pressedPOV == XboxValue.XBOX_POV_LEFT ||
                pressedPOV == XboxValue.XBOX_POV_RIGHT ||
                pressedPOV == XboxValue.XBOX_POV_UP)) {
            boundPOV = lastPressedBound;
        }
        // Otherwise, use the current POV.
        else {
            boundPOV = pressedPOV;
            lastPressedBound = pressedPOV;
        }

        // Decide what to do based on the real good POV being pressed.
        if (boundPOV != null) {
            switch (boundPOV) {
                case XBOX_POV_DOWN:
                    currentSetpoint = 0;
                    break;
                case XBOX_POV_LEFT:
                    currentSetpoint = 1;
                    break;
                case XBOX_POV_UP:
                    currentSetpoint = 2;
                    break;
                case XBOX_POV_RIGHT:
                    currentSetpoint = 3;
                    break;
                default:
                    // If nothing is right, make the setpoint an invalid -1 so nothing happens.
                    currentSetpoint = -1;
                    break;
            }
        }

        // Only set a new setpoint if it has changed.
        if (currentSetpoint != oldSetpoint && currentSetpoint >= 0) {
            Lift.getInstance().moveToSetpoint(1.0, currentSetpoint);
        }
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().hasReachedSetpoint(currentSetpoint);
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
        lastPressedBound = null;
        currentSetpoint = -1;
    }

    /**
     * Determines which of the POV buttons are being pressed.
     *
     * @return The XboxValue enum for the currently pressed POV button.
     */
    private XboxValue getPressedPOV() {
        return getPressedPOV(controller);
    }

    /**
     * Determines which of the POV buttons are being pressed on the given controller.
     *
     * @param controller The controller to be checked.
     * @return The XboxValue enum for the currently pressed POV button.
     */
    private static XboxValue getPressedPOV(GenericHID controller) {
        final XboxValue value;

        if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_DOWN.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_DOWN;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_UP.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_UP;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_RIGHT.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_RIGHT;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_LEFT.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_LEFT;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_DOWN_LEFT.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_DOWN_LEFT;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_DOWN_RIGHT.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_DOWN_RIGHT;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_UP_RIGHT.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_UP_RIGHT;
        } else if (FluidButton.determineIfActivated(controller, XboxValue.XBOX_POV_UP_LEFT.getPort(), XboxValue.XboxInputType.POV)) {
            value = XboxValue.XBOX_POV_UP_LEFT;
        } else {
            value = null;
        }

        return value;
    }
}
