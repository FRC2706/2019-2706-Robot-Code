package ca.team2706.frc.robot;

/**
 * Essentially the same thing as {@code Consumer<RobotState>}
 */
@FunctionalInterface
public interface StateConsumer {
    void accept(RobotState state);

}
