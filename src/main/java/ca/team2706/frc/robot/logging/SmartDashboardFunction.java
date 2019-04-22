package ca.team2706.frc.robot.logging;

public interface SmartDashboardFunction<T>  {
    boolean put(String key, T data);
}
