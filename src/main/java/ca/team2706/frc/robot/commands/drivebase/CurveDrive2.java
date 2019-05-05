package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.util.function.Supplier;

/**
 * Class that takes waypoints and creates a path to follow them
 */
public class CurveDrive2 extends FollowTrajectory {

    /**
     * Takes waypoints and turns it into trajectories for following
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles he number of cycles to complete after finishing the motion profile
     * @param waypoints     The array of waypoints
     */
    public CurveDrive2(boolean direction, int minDoneCycles, Waypoint[] waypoints) {
        this(() -> direction, () -> minDoneCycles, waypoints);
    }

    /**
     * Takes waypoints and turns it into trajectories for following
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles he number of cycles to complete after finishing the motion profile
     * @param waypoints     The array of waypoints
     */
    public CurveDrive2(Supplier<Boolean> direction, Supplier<Integer> minDoneCycles, Waypoint[] waypoints) {
        super(direction, minDoneCycles, generateTrajectory(waypoints));
    }

    /**
     * Generates a trajectory from the waypoints for the robot to follow
     *
     * @param waypoints The waypoints in the path for the robot to follow
     * @return A trajectory for the robot to follow
     */
    private static Trajectory generateTrajectory(Waypoint[] waypoints) {

        Waypoint[] newWaypoints = new Waypoint[waypoints.length];

        for (int i = 0; i < waypoints.length; i++) {
            newWaypoints[i] = waypoints[i];
            newWaypoints[i].y *= -1;
        }

        // Create the Trajectory Configuration
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.01, Config.PATHFINDING_VELOCITY.value(), Config.PATHFINDING_ACCELERATION.value(), Config.PATHFINDING_JERK.value());

        // Generate the trajectory
        return Pathfinder.generate(waypoints, config);
    }
}
