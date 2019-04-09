package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Abstract class to extend when using curve drive, allows for basic Command architecture
 * Allows the WPI Curvature Drive to be used with passed values
 */
public abstract class CurvatureDrive extends Command {
    private final Supplier<Double> forwardVal;
    private final Supplier<Double> curveSpeed;
    private final boolean initBrake;
    private final Supplier<Boolean> buttonPress;
    private final boolean squareInputs;

    /**
     * Creates the arcade drive
     *
     * @param forwardVal The values to use for driving forward
     * @param curveSpeed The amount that the robot should curve while driving
     * @param initBrake  Whether to start and end the command in brake or coast mode
     */
    protected CurvatureDrive(Supplier<Double> forwardVal, Supplier<Double> curveSpeed,
                             boolean initBrake, Supplier<Boolean> buttonPress, boolean squareInputs) {
        /*
           Ensure that this command is the only one to run on the drive base
           Requires must be included to use this command as a default command for the drive base
        */
        requires(DriveBase.getInstance());
        this.forwardVal = forwardVal;
        this.curveSpeed = curveSpeed;
        this.initBrake = initBrake;
        this.buttonPress = buttonPress;
        this.squareInputs = squareInputs;
    }

    @Override
    public void initialize() {
        // Prepare for driving by human
        DriveBase.getInstance().setOpenLoopVoltageMode();
        DriveBase.getInstance().setBrakeMode(initBrake);
    }

    @Override
    public void execute() {
        double forward = forwardVal.get();
        double curve = curveSpeed.get();

        if (squareInputs) {
            curve *= curve < 0 ? -curve : curve;
        }

        double rotation = Math.abs(curve) < Config.CONTROLLER_DEADBAND ? 0 : curve;

        boolean override = Math.abs(forward) < Config.CURVATURE_OVERRIDE;

        if (buttonPress.get()) {
            if (!DriveBase.getInstance().isBrakeMode()) {
                DriveBase.getInstance().setBrakeMode(true);
            }

            DriveBase.getInstance().curvatureDrive(forward * 0.6, (override ? rotation / 2.5 : rotation), override);
        } else {
            if (DriveBase.getInstance().isBrakeMode()) {
                DriveBase.getInstance().setBrakeMode(false);
            }

            DriveBase.getInstance().curvatureDrive(forward, (override ? rotation / 2 : rotation), override);
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
