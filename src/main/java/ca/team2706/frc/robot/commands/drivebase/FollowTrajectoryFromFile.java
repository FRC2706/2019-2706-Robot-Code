package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * Follows a trajectory from a CSV file
 */
public class FollowTrajectoryFromFile extends FollowTrajectory {

    /**
     * Follows the trajectory from a file
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles The number of cycles to complete after finishing the motion profile
     * @param trajectory    The trajectory for the robot
     */
    public FollowTrajectoryFromFile(boolean direction, int minDoneCycles, String trajectory) {
        this(() -> direction, () -> minDoneCycles, trajectory);
    }

    /**
     * Follows the trajectory from a file
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles The number of cycles to complete after finishing the motion profile
     * @param trajectory    The trajectory for the robot
     */
    public FollowTrajectoryFromFile(Supplier<Boolean> direction, Supplier<Integer> minDoneCycles, String trajectory) {
        super(direction, minDoneCycles, readFromFile(Config.DEPLOY_DIR.resolve("motion-profiles/output/" + trajectory + ".pf1.csv")),
                readFromFile(Config.DEPLOY_DIR.resolve("motion-profiles/output/" + trajectory + ".left.pf1.csv")),
                readFromFile(Config.DEPLOY_DIR.resolve("motion-profiles/output/" + trajectory + ".right.pf1.csv")));
    }

    /**
     * Reads the trajectory from a file
     *
     * @param trajectory The trajectory of the robot
     * @return The trajectory
     */
    private static Trajectory readFromFile(Path trajectory) {
        try {
            return Pathfinder.readFromCSV(trajectory.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
