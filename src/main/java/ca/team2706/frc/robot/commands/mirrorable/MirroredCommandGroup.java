package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

import java.lang.reflect.Field;
import java.util.Vector;

/**
 * A command group that when mirrored, mirrors all sub-commands
 */
public class MirroredCommandGroup extends CommandGroup implements IMirrorable<Command> {

    private boolean mirrored;

    /**
     * Gets the mirrored version of this command (doesn't mirror an already mirrored command)
     */
    public MirroredCommandGroup mirror() {
        if (mirrored) {
            return this;
        }

        mirrored = true;

        try {
            Field m_commands_field = CommandGroup.class.getDeclaredField("m_commands");
            m_commands_field.setAccessible(true);
            Vector<?> m_commands = (Vector<?>) m_commands_field.get(this);

            Field commandField = Class.forName("edu.wpi.first.wpilibj.command.CommandGroup$Entry").getDeclaredField("m_command");
            commandField.setAccessible(true);

            for (Object entry : m_commands) {
                Command command = (Command) commandField.get(entry);

                if (command instanceof IMirrorable) {
                    ((IMirrorable) command).mirror();
                }
            }

            commandField.setAccessible(false);
            m_commands_field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * Gets whether the command is mirrored
     *
     * @return True if the command is mirrored and false otherwise
     */
    protected boolean isMirrored() {
        return mirrored;
    }
}
