package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.Pair;
import ca.team2706.frc.robot.config.Config;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;

import java.util.Arrays;
import java.util.function.Supplier;

public class FollowTrajectory extends MotionProfile2Wheel {

    public FollowTrajectory(double speed, int minDoneCycles, Trajectory trajectory) {
        this(() -> speed, () -> minDoneCycles, trajectory);
    }

    public FollowTrajectory(Supplier<Double> speed, Supplier<Integer> minDoneCycles, Trajectory trajectory) {
        super(speed, minDoneCycles, generateDualTrajectory(trajectory, twoTrajectory(trajectory)));
    }

    public FollowTrajectory(double speed, int minDoneCycles, Trajectory trajectory, Trajectory left, Trajectory right) {
        this(() -> speed, () -> minDoneCycles, trajectory, left, right);
    }

    public FollowTrajectory(Supplier<Double> speed, Supplier<Integer> minDoneCycles, Trajectory trajectory, Trajectory left, Trajectory right) {
        super(speed, minDoneCycles, generateDualTrajectory(trajectory, Pair.of(left, right)));
    }

    public static Pair<Trajectory> twoTrajectory(Trajectory trajectory) {
        // The distance between the left and right sides of the wheelbase is 0.6m
        double wheelbase_width = Config.WHEELBASE_WIDTH;

// Create the Modifier Object
        TankModifier modifier = new TankModifier(trajectory);

// Generate the Left and Right trajectories using the original trajectory
// as the centre
        modifier.modify(wheelbase_width);

        Trajectory left = modifier.getLeftTrajectory();       // Get the Left Side
        Trajectory right = modifier.getRightTrajectory();      // Get the Right Side

        return Pair.of(left, right);
    }

    private static DualTalonTrajectory generateDualTrajectory(Trajectory trajectory, Pair<Trajectory> leftRightTrajectory) {

        Trajectory left = leftRightTrajectory.getFirst();
        Trajectory right = leftRightTrajectory.getSecond();

        DualTalonTrajectory dualTalonTrajectory = new DualTalonTrajectory(new double[trajectory.segments.length], new double[trajectory.segments.length], new double[trajectory.segments.length], new double[trajectory.segments.length], new double[trajectory.segments.length], new int[trajectory.segments.length], trajectory.segments.length);
        for (int i = 0; i < trajectory.segments.length; i++) {
            dualTalonTrajectory.posLeft[i] = left.segments[i].position;
            dualTalonTrajectory.velLeft[i] = left.segments[i].velocity;
            dualTalonTrajectory.posRight[i] = right.segments[i].position;
            dualTalonTrajectory.velRight[i] = right.segments[i].velocity;
            dualTalonTrajectory.heading[i] = Pathfinder.r2d(trajectory.segments[i].heading);
            dualTalonTrajectory.time[i] = (int) (trajectory.segments[i].dt * 1000.0);
        }

        // Continuous angles rather than (0, 360]
        unwrap(dualTalonTrajectory.heading);

        return dualTalonTrajectory;
    }

    private static void unwrap(double[] degs) {
        // Assume starting at 0 rather than 360
        double last = 0;
        double lastBound = 0;

        for (int i = 0; i < degs.length; i++) {
            double temp = degs[i];

            degs[i] = last + deltaAngle(lastBound, degs[i]);
            lastBound = temp;

            last = degs[i];
        }
    }

    private static double deltaAngle(double angle1, double angle2) {
        double delta = angle2 - angle1;

        if (delta > 180) {
            delta -= 360;
        } else if (delta < -180) {
            delta += 360;
        }

        return delta;
    }
}
