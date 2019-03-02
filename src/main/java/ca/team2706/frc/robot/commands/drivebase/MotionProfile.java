package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Drives in a straight line to a position
 */
public class MotionProfile extends Command {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed;
    private final Supplier<Integer> minDoneCycles;

    private final double[] pos;
    private final double[] vel;
    private final double[] heading;
    private final int[] time;
    private final int size;

    private int doneCycles;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed The maximum speed of the robot
     */
    public MotionProfile(double speed, int minDoneCycles, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        this(() -> speed, () -> minDoneCycles, pos, vel, heading, time, size);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed The maximum speed of the robot
     */
    public MotionProfile(Supplier<Double> speed, Supplier<Integer> minDoneCycles, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.pos = pos;
        this.vel = vel;
        this.heading = heading;
        this.time = time;
        this.size = size;

        this.minDoneCycles = minDoneCycles;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().pushMotionProfile1Wheel(speed.get() >= 0, pos, vel, heading, time, size);
        DriveBase.getInstance().setMotionProfile();

        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().runMotionProfile(Math.abs(speed.get()));
    }

    @Override
    protected boolean isFinished() {
        if (DriveBase.getInstance().isFinishedMotionProfile()) {
            doneCycles++;
        }

        return doneCycles >= minDoneCycles.get();
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }
}

