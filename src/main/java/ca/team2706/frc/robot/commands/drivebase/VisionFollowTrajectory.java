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
                new Waypoint(0, 0, Pathfinder.d2r(90)),
                // Final position/heading of robot: in front of target
                //new Waypoint(vRobotToFinal_RobotX, vRobotToFinal_RobotY, angRobotHeadingFinalRad_Robot),
                new Waypoint(left ? -1 : 1, 4, Pathfinder.d2r(90)),
        };

        Trajectory trajectory =  Pathfinder.generate(points, config);
        /*
         * Headings in trajectory must each be converted into the robot motion control system's frame
         * whose x-y axes are the same as our robot frame but whose heading along the y-axis
         * is at 0 degrees with  positive heading clockwise (as compared to our robot frame whose
         * heading along the y-axis is 90 degrees with positive heading counter-clockwise).
         */
        double PI_OVER_2 = Math.PI / 2.0;
        for (int i = 0; i < trajectory.length(); i++) {
            trajectory.segments[i].heading = PI_OVER_2 - trajectory.segments[i].heading;
        }


        System.out.println("DAV: Trajectory length: " + trajectory.length());
        for (int i = 0; i < trajectory.length(); i++)
        {
            String str =
                    trajectory.segments[i].x + "," +
                            trajectory.segments[i].y + "," +
                            trajectory.segments[i].heading;

            System.out.println(str);
        }

        System.out.println("DAV: Trajectory generated");

        return trajectory;

    }
}
