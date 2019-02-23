package ca.team2706.frc.robot.commands.drivebase;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Supplier;

public class FollowTrajectoryFromFile extends FollowTrajectory {
    public FollowTrajectoryFromFile(double speed, int minDoneCycles, Path trajectoryDir) {
        this(()->speed, ()->minDoneCycles, trajectoryDir);
    }

    public FollowTrajectoryFromFile(Supplier<Double> speed, Supplier<Integer> minDoneCycles, Path trajectoryDir) {
        super(speed, minDoneCycles, readFromFile(trajectoryDir.resolve("middle.csv")),
                readFromFile(trajectoryDir.resolve("left.csv")),
                readFromFile(trajectoryDir.resolve("right.csv")));
    }

    private static Trajectory readFromFile(Path trajectory) {
        try {
            return Pathfinder.readFromCSV(trajectory.toFile());
        } catch (IOException e) {
           throw new RuntimeException(e);
        }
    }


}
