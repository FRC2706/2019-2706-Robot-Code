package ca.team2706.frc.robot.commands.mirrorable;

/**
 * Something that can be mirrored into a command
 */
public interface IMirrorable<T> {

    /**
     * Mirrors something and returns it
     *
     * @return The mirrored object
     */
    T mirror();

    /**
     * Gets the command
     *
     * @return The command
     */
    T get();
}
