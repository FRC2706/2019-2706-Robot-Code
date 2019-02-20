package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Drives in a straight line to a position
 */
public class MotionProfile2Wheel extends Command {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed;

    private final double[] pos;
    private final double[] vel;
    private final double[] pos2;
    private final double[] vel2;
    private final double[] heading;
    private final int[] time;
    private final int size;
    private boolean isReady;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed         The maximum speed of the robot
     */
    public MotionProfile2Wheel(double speed, double[] pos, double[] vel, double[] pos2, double[] vel2, double[] heading, int[] time, int size) {
        this(() -> speed, pos, vel, pos2, vel2, heading, time, size);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed         The maximum speed of the robot
     */
    public MotionProfile2Wheel(Supplier<Double> speed, double[] pos, double[] vel, double[] pos2, double[] vel2, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.pos = pos;
        this.vel = vel;
        this.pos2 = pos2;
        this.vel2 = vel2;
        this.heading = heading;
        this.time = time;
        this.size = size;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().pushMotionProfile2Wheel(pos, vel, heading, time, size, pos2, vel2);
        DriveBase.getInstance().setMotionProfile2Wheel();
        isReady = false;
    }

    @Override
    public void execute() {
        if (isReady || (DriveBase.getInstance().motionProfileIsReadyRight() && DriveBase.getInstance().motionProfileIsReadyLeft())) {
            isReady = true;
            DriveBase.getInstance().runMotionProfile2Wheel(speed.get());
        }
    }

    @Override
    protected boolean isFinished() {
       return DriveBase.getInstance().isFinishedMotionProfile2Wheel();
    }

    @Override
    public void end(){
        DriveBase.getInstance().setDisabledMode();
    }
}

