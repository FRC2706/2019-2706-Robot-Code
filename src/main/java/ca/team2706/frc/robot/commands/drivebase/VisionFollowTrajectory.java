package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class VisionFollowTrajectory extends FollowTrajectory {

    public VisionFollowTrajectory(double speed, int minDoneCycles, boolean left) {
        super(speed, minDoneCycles, generateTrajectory(left));
    }

    private static Trajectory generateTrajectory(boolean left) {
        Trajectory.Config config =
                new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_FAST,
                        Config.TRAJ_DELTA_TIME.value(), Config.VISION_ASSIST_MAX_VELOCITY.value(), Config.VISION_ASSIST_MAX_ACCELERATION.value(),
                        Config.VISION_ASSIST_MAX_JERK.value());
        Waypoint[] points = new Waypoint[]{
                // Initial position/heading of robot: at origin with heading at 90 deg
                new Waypoint(0, 0, Pathfinder.d2r(0)),
                // Final position/heading of robot: in front of target
                //new Waypoint(vRobotToFinal_RobotX, vRobotToFinal_RobotY, angRobotHeadingFinalRad_Robot),
                new Waypoint(left ? -1 : 1, 4, Pathfinder.d2r(0)),
        };
        return Pathfinder.generate(points, config);
    }
}
