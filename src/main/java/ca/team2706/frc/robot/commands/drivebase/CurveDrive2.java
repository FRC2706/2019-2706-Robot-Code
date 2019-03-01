package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.util.Arrays;
import java.util.function.Supplier;

public class CurveDrive2 extends FollowTrajectory{

    /**
     * Takes waypoints and turns it into trajectories for following
     * @param speed The speed of the robot
     * @param minDoneCycles he number of cycles to complete after finishing the motion profile
     * @param waypoints The array of waypoints
     */
    public CurveDrive2(double speed, int minDoneCycles, Waypoint[] waypoints) {
        this(()->speed, ()->minDoneCycles, waypoints);
    }

    /**
     * Takes waypoints and turns it into trajectories for following
     * @param speed The speed of the robot
     * @param minDoneCycles he number of cycles to complete after finishing the motion profile
     * @param waypoints The array of waypoints
     */
    public CurveDrive2(Supplier<Double> speed, Supplier<Integer> minDoneCycles, Waypoint[] waypoints) {
        super(speed, minDoneCycles, generateTrajectory(waypoints));
    }

    /**
     * Generates a trajectory from the waypoints for the robot to follow
     * @param waypoints The waypoints in the path for the robot to follow
     * @return A trajectory for the robot to follow
     */
    private static Trajectory generateTrajectory(Waypoint[] waypoints) {

        Waypoint[] newWaypoints = new Waypoint[waypoints.length];

        for(int i = 0; i < waypoints.length; i++) {
            newWaypoints[i] = waypoints[i];
            newWaypoints[i].y *= -1;
        }

        // Create the Trajectory Configuration
//
// Arguments:
// Fit Method:          HERMITE_CUBIC or HERMITE_QUINTIC
// Sample Count:        SAMPLES_HIGH (100 000)
//                      SAMPLES_LOW  (10 000)
//                      SAMPLES_FAST (1 000)
// Time Step:           0.05 Seconds
// Max Velocity:        1.7 m/s
// Max Acceleration:    2.0 m/s/s
// Max Jerk:            60.0 m/s/s/s
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.01, Config.PATHFINDING_VELOCITY.value(), Config.PATHFINDING_ACCELERATION.value(), Config.PATHFINDING_JERK.value());

// Generate the trajectory
        return Pathfinder.generate(waypoints, config);
    }
}
