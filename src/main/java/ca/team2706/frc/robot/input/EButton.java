package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.config.XboxValue;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Button with additional feature to run and cancel when released
 */
public abstract class EButton extends Button {

    /**
     * Default min axis activation for buttons. This specifies the percent between 0 and 1
     * at which the axis must be pressed in order for it to count as a button press.
     */
    public static final double DEFAULT_MIN_AXIS_ACTIVATION = 0.8;

    /**
     * The POV of the POV button
     */
    public static final int POV_NUMBER = 0;

    /**
     * Starts the given command whenever the button is newly pressed and cancels it when it is released.
     *
     * @param command the command to start
     */
    public void whenHeld(final Command command) {
        new ButtonScheduler() {
            private boolean m_pressedLast = grab();

            @Override
            public void execute() {
                boolean pressed = grab();

                if (!m_pressedLast && pressed) {
                    command.start();
                } else if (m_pressedLast && !pressed) {
                    command.cancel();
                }

                m_pressedLast = pressed;
            }
        }.start();
    }

    private final VarHandle m_sendablePressed = getVarHandle();

    private VarHandle getVarHandle() {
        try {
            return MethodHandles.privateLookupIn(Trigger.class, MethodHandles.lookup())
                    .findVarHandle(Trigger.class, "m_sendablePressed", boolean.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean grab() {
        boolean pressed = false;

        if (m_sendablePressed != null) {
            pressed = (boolean) m_sendablePressed.getVolatile(this);
        }

        return get() || pressed;
    }

    /**
     * Determines if the given button/trigger is considered as being pressed.
     *
     * @param m_joystick        The joystick on which to check.
     * @param joystickPort      The port binding to the button/trigger to check.
     * @param inputType         The input type of the button/trigger to check.
     * @param minAxisActivation The minimum axis activation (minimum value for which the axis is considered
     *                          active).
     * @return True if the axis/button/trigger is considered as pressed, false othwerwise.
     */
    public static boolean determineIfActivated(GenericHID m_joystick,
                                               final int joystickPort,
                                               final XboxValue.XboxInputType inputType,
                                               final double minAxisActivation) {
        final boolean pressed;

        switch (inputType) {
            case Axis:
                pressed = Math.abs(m_joystick.getRawAxis(joystickPort)) >= minAxisActivation;
                break;
            case Button:
                pressed = m_joystick.getRawButton(joystickPort);
                break;
            case POV:
                pressed = m_joystick.getPOV(POV_NUMBER) == joystickPort;
                break;
            default:
                pressed = false;
                break;
        }

        return pressed;
    }

    /**
     * Determines if the given button/trigger is considered as being pressed with the default min axis activation.
     *
     * @param m_joystick   The joystick to be checked.
     * @param joystickPort The port location of the button/trigger on the joystick.
     * @param inputType    The input type of the binding.
     * @return True if the button/trigger/axis is considered as pressed, false otherwise.
     * @see #determineIfActivated(GenericHID, int, XboxValue.XboxInputType, double)
     */
    public static boolean determineIfActivated(GenericHID m_joystick,
                                               final int joystickPort,
                                               final XboxValue.XboxInputType inputType) {
        return determineIfActivated(m_joystick, joystickPort, inputType, DEFAULT_MIN_AXIS_ACTIVATION);
    }
}
