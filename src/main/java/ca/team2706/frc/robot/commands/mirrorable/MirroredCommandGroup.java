package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * A command group that when mirrored, mirrors all sub-commands
 */
public class MirroredCommandGroup extends CommandGroup implements IMirrorable<Command> {

    private Set<IMirrorable<? extends Command>> mirroredCommands = new HashSet<>();
    private boolean mirrored;

    /**
     * @see CommandGroup#CommandGroup()
     */
    public MirroredCommandGroup() {
        super();
    }

    /**
     * @see CommandGroup#CommandGroup(String)
     */
    public MirroredCommandGroup(String name) {
        super(name);
    }

    /**
     * Adds a command as sequential and makes it possible to be mirrored
     *
     * @param command The command to add
     */
    public void addMirroredSequential(IMirrorable<? extends Command> command) {
        addSequential(command.get());
        mirroredCommands.add(command);
    }

    /**
     * Adds a command as sequential and makes it possible to be mirrored
     *
     * @param command The command to add
     * @param timeout The time in seconds that the command can run before it gets disabled
     */
    public void addMirroredSequential(IMirrorable<? extends Command> command, double timeout) {
        addSequential(command.get(), timeout);
        mirroredCommands.add(command);
    }

    /**
     * Adds a command in parallel and makes it possible to be mirrored
     *
     * @param command The command to add
     */
    public void addMirroredParallel(IMirrorable<? extends Command> command) {
        addParallel(command.get());
        mirroredCommands.add(command);
    }

    /**
     * Adds a command in parallel and makes it possible to be mirrored
     *
     * @param command The command to add
     * @param timeout The time in seconds that the command can run before it gets disabled
     */
    public void addMirroredParallel(IMirrorable<? extends Command> command, double timeout) {
        addParallel(command.get(), timeout);
        mirroredCommands.add(command);
    }

    /**
     * Gets the mirrored version of this command group by mirroring sub commands (doesn't mirror an already mirrored command)
     */
    public MirroredCommandGroup mirror() {
        if (mirrored) {
            return get();
        }

        mirrored = true;

        for (IMirrorable mirrorable : mirroredCommands) {
            mirrorable.mirror();
        }

        return get();
    }

    @Override
    public MirroredCommandGroup get() {
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
