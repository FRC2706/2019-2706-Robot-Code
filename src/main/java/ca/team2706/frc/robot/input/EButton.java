package ca.team2706.frc.robot.input;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Button with additional feature to run and cancel when released
 */
public abstract class EButton extends Button {

    /**
     * Starts the given command whenever the button is newly pressed and cancels it when it is released.
     *
     * @param command the command to start
     */
    public void whenPressed(final Command command) {
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
