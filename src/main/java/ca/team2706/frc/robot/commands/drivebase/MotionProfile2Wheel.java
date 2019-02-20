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

    private final double[] pos;
    private final double[] vel;
    private final double[] pos2;
    private final double[] vel2;
    private final double[] heading;
    private final int[] time;
    private final int size;

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

    MotionProfile2Wheel(Supplier<Double> speed, DualTalonTrajectory dualTalonTrajectory) {
        this(speed, dualTalonTrajectory.pos, dualTalonTrajectory.vel, dualTalonTrajectory.pos2, dualTalonTrajectory.vel2,  dualTalonTrajectory.heading, dualTalonTrajectory.time, dualTalonTrajectory.size);
    }

    @Override
    public void initialize() {
        System.out.println("Right pos: " + Arrays.toString(pos));
        System.out.println("Right vel: " + Arrays.toString(vel));
        System.out.println("Heading: " + Arrays.toString(heading));
        System.out.println("Time: " + Arrays.toString(time));
        System.out.println("Left pos: " + Arrays.toString(pos2));
        System.out.println("Left vel: " + Arrays.toString(vel2));

        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().pushMotionProfile2Wheel(pos, vel, heading, time, size, pos2, vel2);
        DriveBase.getInstance().setMotionProfile2Wheel();
    }

    @Override
    public void execute() {
            DriveBase.getInstance().runMotionProfile2Wheel(speed.get());
    }

    @Override
    protected boolean isFinished() {
       return DriveBase.getInstance().isFinishedMotionProfile2Wheel();
    }

    @Override
    public void end(){
        DriveBase.getInstance().setDisabledMode();
    }

    static class DualTalonTrajectory {

        final double[] pos;
        final double[] vel;
        final double[] pos2;
        final double[] vel2;
        final double[] heading;
        final int[] time;
        final int size;

        DualTalonTrajectory(double[] pos, double[] vel, double[] pos2, double[] vel2, double[] heading, int[] time, int size) {
            this.pos = pos;
            this.vel = vel;
            this.pos2 = pos2;
            this.vel2 = vel2;
            this.heading = heading;
            this.time = time;
            this.size = size;
        }
    }
}

