package ca.team2706.frc.robot.logging;

public enum PeriodicLogPriority {

    /**
     * Always logs to SmartDashboard, may log to file
     */
     NT_ALWAYS,

    /**
     * May log to SmartDashboard, may log to file
     */
    NT_MAYBE,

    /**
     * Never logs to SmartDashboard, may log to file
     */
    NT_NEVER;

}
