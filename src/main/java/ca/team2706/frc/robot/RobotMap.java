package ca.team2706.frc.robot;

public class RobotMap {

    // ID of the robot that code is running on
    private static final int ROBOT_ID = getRobotId();

    // If constants are robot specific, make them = robotSpecific([Competition], [Practice], [Simulation])

    public static final int SELECTOR_ID = robotSpecific(0, 1, 2);
    public static final String CAMERA_IP = robotSpecific("10.27.6.240", "10.27.6.240", "127.0.0.1");



    // End constant declarations

    private static int getRobotId() {
        // TODO: Get robot ID from filesystem
        // TODO: Log robot ID

        return 0;
    }

    @SafeVarargs
    private static <T> T robotSpecific(T x1, T... rest) {
        // TODO: Could tie into higher level configuration manager here

        // Return the first value if the robot id doesn't fall between second and last index
        if(ROBOT_ID < 1 || ROBOT_ID > rest.length) {
            return x1;
        }
        else {
            return rest[ROBOT_ID - 1];
        }
    }
}
