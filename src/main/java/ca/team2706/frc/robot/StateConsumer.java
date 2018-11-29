package ca.team2706.frc.robot;

@FunctionalInterface
public interface StateConsumer {
    void accept(RobotState state);

}
