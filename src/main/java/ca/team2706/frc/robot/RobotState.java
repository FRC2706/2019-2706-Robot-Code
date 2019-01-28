package ca.team2706.frc.robot;

/**
 * Enum for the various states that the robot may be in.
 */
public enum RobotState {

    /**
     * Robot is being initialized
     */
    ROBOT_INIT,

    /**
     * Robot is being disabled
     */
    DISABLED,

    /**
     * Robot is entering autonomous mode
     */
    AUTONOMOUS,

    /**
     * Robot is entering teleop mode
     */
    TELEOP,

    /**
     * Robot is entering test mode
     */
    TEST,

    /**
     * Robot is shutting down
     */
    SHUTDOWN
}
