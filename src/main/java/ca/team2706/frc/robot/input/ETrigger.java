package ca.team2706.frc.robot.input;

import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * Enhanced trigger with additional features that allow for commands
 * to be run as long as the trigger is active.
 */
public abstract class ETrigger extends Trigger {

    /**
     * Starts the given command whenever the trigger becomes active and cancels it when the trigger becomes inactive.
     *
     * @param command the command to run.
     */
    public void runWhileActive(Command command) {
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
}
