package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import jaci.pathfinder.Trajectory;

import java.util.Arrays;
import java.util.function.Supplier;

/**
 * Drives in a straight line to a position
 */
public class MotionProfile2Wheel extends Command {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed;
    private final Supplier<Integer> minDoneCycles;

    private final double[] posLeft;
    private final double[] velLeft;
    private final double[] posRight;
    private final double[] velRight;
    private final double[] heading;
    private final int[] time;
    private final int size;

    private int doneCycles;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed         The maximum speed of the robot
     */
    public MotionProfile2Wheel(double speed, int minDoneCycles, double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
            this(() -> speed, () -> minDoneCycles, posLeft, velLeft, posRight, velRight, heading, time, size);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed         The maximum speed of the robot
     */
    public MotionProfile2Wheel(Supplier<Double> speed, Supplier<Integer> minDoneCycles, double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.posLeft = posLeft;
        this.velLeft = velLeft;
        this.posRight = posRight;
        this.velRight = velRight;
        this.heading = heading;
        this.time = time;
        this.size = size;
        this.minDoneCycles = minDoneCycles;
    }

    MotionProfile2Wheel(Supplier<Double> speed, Supplier<Integer> minDoneCycles, DualTalonTrajectory dualTalonTrajectory) {
        this(speed, minDoneCycles, dualTalonTrajectory.posLeft, dualTalonTrajectory.velLeft, dualTalonTrajectory.posRight, dualTalonTrajectory.velRight,  dualTalonTrajectory.heading, dualTalonTrajectory.time, dualTalonTrajectory.size);
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().pushMotionProfile2Wheel(posLeft, velLeft, heading, time, size, posRight, velRight);
        DriveBase.getInstance().setMotionProfile2Wheel();

        doneCycles = 0;
    }

    @Override
    public void execute() {
            DriveBase.getInstance().runMotionProfile2Wheel(speed.get());
    }

    @Override
    protected boolean isFinished() {
        if(DriveBase.getInstance().isFinishedMotionProfile2Wheel()) {
            doneCycles++;
        }

        return doneCycles >= minDoneCycles.get();
    }

    @Override
    public void end(){
        DriveBase.getInstance().setDisabledMode();
    }

    static class DualTalonTrajectory {

        final double[] posLeft;
        final double[] velLeft;
        final double[] posRight;
        final double[] velRight;
        final double[] heading;
        final int[] time;
        final int size;

        DualTalonTrajectory(double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
            this.posLeft = posLeft;
            this.velLeft = velLeft;
            this.posRight = posRight;
            this.velRight = velRight;
            this.heading = heading;
            this.time = time;
            this.size = size;
        }
    }
}

