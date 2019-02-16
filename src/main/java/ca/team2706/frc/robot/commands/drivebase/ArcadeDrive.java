package ca.team2706.frc.robot.commands.drivebase;

import java.util.function.Supplier;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Drives the robot using values for driving forward and rotation
 */
public abstract class ArcadeDrive extends Command {

    private final Supplier<Double> forwardVal;
    private final Supplier<Double> rotateVal;
    private final boolean squareInputs;
    private final boolean initBrake;

    /**
     * Creates the arcade drive
     *
     * @param forwardVal   The values to use for driving forward
     * @param rotateVal    The values to use for driving rotation
     * @param squareInputs Whether to square the forward and rotation values
     * @param initBrake    Whether to start and end the command in brake or coast mode
     */
    protected ArcadeDrive(Supplier<Double> forwardVal, Supplier<Double> rotateVal,
                          boolean squareInputs, boolean initBrake) {
        // Ensure that this command is the only one to run on the drive base
        // Requires must be included to use this command as a default command for the drive base
        requires(DriveBase.getInstance());

        this.forwardVal = forwardVal;
        this.rotateVal = rotateVal;
        this.squareInputs = squareInputs;
        this.initBrake = initBrake;
    }

    @Override
    public void initialize() {
        // Prepare for driving by human
        DriveBase.getInstance().setOpenLoopVoltageMode();

        DriveBase.getInstance().setBrakeMode(initBrake);
    }

    @Override
    public void execute() {
        // Pass values to drive base to make the robot move
        DriveBase.getInstance().arcadeDrive(forwardVal.get(), rotateVal.get(), squareInputs);
    }

    @Override
    public abstract boolean isFinished();

    @Override
    public void end() {
        // Go back to disabled mode
        DriveBase.getInstance().setDisabledMode();

        // Ensure brake mode is same as when starting command since it may have been changed
        DriveBase.getInstance().setBrakeMode(initBrake);
    }
}
