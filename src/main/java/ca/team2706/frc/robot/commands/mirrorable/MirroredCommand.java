package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A command that can be mirrored in a certain way
 */
public abstract class MirroredCommand extends Command implements IMirrorable<Command> {

    private boolean mirrored;

    /**
     * @see Command#Command()
     */
    public MirroredCommand() {
        super();
    }

    /**
     * @see Command#Command(String)
     */
    public MirroredCommand(String name) {
        super(name);
    }

    /**
     * @see Command#Command(double)
     */
    public MirroredCommand(double timeout) {
        super(timeout);
    }

    /**
     * @see Command#Command(Subsystem)
     */
    public MirroredCommand(Subsystem subsystem) {
        super(subsystem);
    }

    /**
     * @see Command#Command(String, Subsystem)
     */
    public MirroredCommand(String name, Subsystem subsystem) {
        super(name, subsystem);
    }

    /**
     * @see Command#Command(double, Subsystem)
     */
    public MirroredCommand(double timeout, Subsystem subsystem) {
        super(timeout, subsystem);
    }

    /**
     * @see Command#Command(String, double)
     */
    public MirroredCommand(String name, double timeout) {
        super(name, timeout);
    }

    /**
     * @see Command#Command()
     */
    public MirroredCommand(String name, double timeout, Subsystem subsystem) {
        super(name, timeout, subsystem);
    }

    /**
     * Mirrors the command (doesn't do anything to an already mirrored command)
     */
    public MirroredCommand mirror() {
        this.mirrored = true;
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
