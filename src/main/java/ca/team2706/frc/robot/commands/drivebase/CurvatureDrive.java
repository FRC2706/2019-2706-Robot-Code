package ca.team2706.frc.robot.commands.drivebase;

import java.util.function.Supplier;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

public abstract class CurvatureDrive extends Command {
    private final Supplier<Double> forwardVal;
    private final Supplier<Double> curveSpeed;
    private final boolean initBrake;
    private final Supplier<Boolean> buttonPress;

    /**
     * Creates the arcade drive
     *
     * @param forwardVal The values to use for driving forward
     * @param curveSpeed The amount that the robot should curve while driving
     * @param initBrake  Whether to start and end the command in brake or coast mode
     */
    protected CurvatureDrive(Supplier<Double> forwardVal, Supplier<Double> curveSpeed,
                             boolean initBrake, Supplier<Boolean> buttonPress) {
        // Ensure that this command is the only one to run on the drive base
        // Requires must be included to use this command as a default command for the drive base
        requires(DriveBase.getInstance());

        this.forwardVal = forwardVal;
        this.curveSpeed = curveSpeed;
        this.initBrake = initBrake;
        this.buttonPress = buttonPress;
    }

    @Override
    public void initialize() {
        // Prepare for driving by human
        DriveBase.getInstance().setOpenLoopVoltageMode();

        DriveBase.getInstance().setBrakeMode(initBrake);
    }

    @Override
    public void execute() {

        double rotation = (curveSpeed.get() > -0.05 && curveSpeed.get() < 0.05) ? 0 : curveSpeed.get();
        boolean override = ((forwardVal.get() > -0.25 && forwardVal.get() < 0.25));

        if (buttonPress.get()) {
            DriveBase.getInstance().curvatureDrive(forwardVal.get() * 0.25, (override ? rotation / 3.5 : rotation), override);
        } else {
            DriveBase.getInstance().curvatureDrive(forwardVal.get(), (override ? rotation / 2 : rotation), override);
        }
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
