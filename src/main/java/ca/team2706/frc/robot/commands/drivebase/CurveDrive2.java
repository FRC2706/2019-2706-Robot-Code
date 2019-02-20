package ca.team2706.frc.robot.commands.drivebase;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

import java.util.function.Supplier;

public class CurveDrive2 extends FollowTrajectory{
    public CurveDrive2(double speed, Waypoint[] waypoints) {
        this(()->speed, waypoints);
    }

    public CurveDrive2(Supplier<Double> speed, Waypoint[] waypoints) {
        super(speed, generateTrajectory(waypoints));
    }

    private static Trajectory generateTrajectory(Waypoint[] waypoints) {
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
        Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.01, 1.7, 1.7, 60.0);

// Generate the trajectory
        return Pathfinder.generate(waypoints, config);
    }
}
