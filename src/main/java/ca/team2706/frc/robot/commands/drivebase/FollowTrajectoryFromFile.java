package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class FollowTrajectoryFromFile extends FollowTrajectory {
    public FollowTrajectoryFromFile(double speed, int minDoneCycles, String trajectory) {
        this(()->speed, ()->minDoneCycles, trajectory);
    }

    public FollowTrajectoryFromFile(Supplier<Double> speed, Supplier<Integer> minDoneCycles, String trajectory) {
        super(speed, minDoneCycles, readFromFile(Config.DEPLOY_DIR.resolve("motion-profiles/output/" + trajectory + ".pf1.csv")),
                readFromFile(Config.DEPLOY_DIR.resolve("motion-profiles/output/" + trajectory + ".left.pf1.csv")),
                readFromFile(Config.DEPLOY_DIR.resolve("motion-profiles/output/" + trajectory + ".right.pf1.csv")));
    }

    private static Trajectory readFromFile(Path trajectory) {
        try {
            return Pathfinder.readFromCSV(trajectory.toFile());
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }
}
