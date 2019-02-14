package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * A command that can be mirrored in a certain way
 */
public abstract class MirroredCommand extends Command implements IMirrorable<Command> {

    private boolean mirrored;

    public MirroredCommand() {
        super();
    }

    public MirroredCommand(String name) {
        super(name);
    }

    public MirroredCommand(double timeout) {
        super(timeout);
    }

    public MirroredCommand(Subsystem subsystem) {
        super(subsystem);
    }

    public MirroredCommand(String name, Subsystem subsystem) {
        super(name, subsystem);
    }

    public MirroredCommand(double timeout, Subsystem subsystem) {
        super(timeout, subsystem);
    }

    public MirroredCommand(String name, double timeout) {
        super(name, timeout);
    }

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
