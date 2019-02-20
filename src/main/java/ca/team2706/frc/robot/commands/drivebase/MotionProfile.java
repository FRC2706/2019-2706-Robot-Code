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

    private final double[] pos;
    private final double[] vel;
    private final double[] heading;
    private final int[] time;
    private final int size;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed         The maximum speed of the robot
     */
    public MotionProfile(double speed, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        this(() -> speed, pos, vel, heading, time, size);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed         The maximum speed of the robot
     */
    public MotionProfile(Supplier<Double> speed, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.pos = pos;
        this.vel = vel;
        this.heading = heading;
        this.time = time;
        this.size = size;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().pushMotionProfile1Wheel(pos, vel, heading, time, size);
        DriveBase.getInstance().setMotionProfile();
    }

    @Override
    public void execute() {
            DriveBase.getInstance().runMotionProfile(speed.get());
    }

    @Override
    protected boolean isFinished() {
       return DriveBase.getInstance().isFinishedMotionProfile();
    }

    @Override
    public void end(){
        DriveBase.getInstance().setDisabledMode();
    }
}

