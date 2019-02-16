package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Class for easy use of DriveBase commands
 */
public abstract class DriveBaseCloseLoop extends Command {

    /**
     * The acceptable range in feet for the error between the target and actual position
     */
    private double targetRange;

    /**
     * The minimum number of cycles for the robot to be within the target zone before the command ends
     */
    private final Supplier<Integer> minDoneCycles;

    /**
     * The number of cycles that the robot is in the target range for
     */
    private int doneCycles = 0;

    /**
     * Requires the drivebase and holds the minDoneCycles
     *
     * @param minDoneCycles The minimum cycles at the desired position before the robot ends
     * @param targetRange   The range before starting to count done cycles
     */
    protected DriveBaseCloseLoop(int minDoneCycles, double targetRange) {
        this(() -> minDoneCycles, targetRange);
    }

    /**
     * Requires the drivebase and holds the minDoneCycles
     *
     * @param minDoneCycles The minimum cycles at the desired position before the robot ends
     * @param targetRange   The range before starting to count done cycles
     */
    protected DriveBaseCloseLoop(Supplier<Integer> minDoneCycles, double targetRange) {
        requires(DriveBase.getInstance());
        this.minDoneCycles = minDoneCycles;
        this.targetRange = targetRange;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);

        doneCycles = 0;
    }

    @Override
    public boolean isFinished() {
        if (Math.abs(DriveBase.getInstance().getRightError()) <= targetRange) {
            doneCycles++;
        } else {
            doneCycles = 0;
        }

        return doneCycles >= minDoneCycles.get();
    }
}
