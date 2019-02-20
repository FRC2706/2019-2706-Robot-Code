package ca.team2706.frc.robot.commands.drivebase;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.modifiers.TankModifier;

import java.util.function.Supplier;

public class FollowTrajectory extends MotionProfile2Wheel{

    public FollowTrajectory(double speed, Trajectory trajectory) {
        this(()->speed, trajectory);
    }

    public FollowTrajectory(Supplier<Double> speed, Trajectory trajectory) {
        super(speed, generateDualTrajectory(trajectory));
    }

    private static DualTalonTrajectory generateDualTrajectory(Trajectory trajectory) {
        // The distance between the left and right sides of the wheelbase is 0.6m
        double wheelbase_width = 0.6;

// Create the Modifier Object
        TankModifier modifier = new TankModifier(trajectory);

// Generate the Left and Right trajectories using the original trajectory
// as the centre
        modifier.modify(wheelbase_width);

        Trajectory left  = modifier.getLeftTrajectory();       // Get the Left Side
        Trajectory right = modifier.getRightTrajectory();      // Get the Right Side

        DualTalonTrajectory dualTalonTrajectory = new DualTalonTrajectory(new double [trajectory.segments.length], new double [trajectory.segments.length], new double [trajectory.segments.length], new double [trajectory.segments.length], new double[trajectory.segments.length],new int [trajectory.segments.length],trajectory.segments.length);
        for (int i = 0; i < trajectory.segments.length; i++) {
            dualTalonTrajectory.pos[i] = left.segments[i].position*3.281;
            dualTalonTrajectory.vel[i] = left.segments[i].velocity*3.281;
            dualTalonTrajectory.pos2[i] = right.segments[i].position*3.281;
            dualTalonTrajectory.vel2[i] = right.segments[i].velocity*3.281;
            dualTalonTrajectory.heading[i] = Pathfinder.r2d(trajectory.segments[i].heading);
            dualTalonTrajectory.time[i] = (int)(trajectory.segments[i].dt*1000.0);
        }

        return dualTalonTrajectory;
    }
}
