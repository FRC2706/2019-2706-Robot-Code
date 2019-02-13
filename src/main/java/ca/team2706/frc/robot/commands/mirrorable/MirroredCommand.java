package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.Command;

/**
 * A command that can be mirrored in a certain way
 */
public abstract class MirroredCommand extends Command implements IMirrorable<Command> {

    private boolean mirrored;

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
