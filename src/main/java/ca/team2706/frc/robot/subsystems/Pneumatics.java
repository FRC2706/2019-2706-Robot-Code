package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.commands.intake.arms.RaiseArmsSafely;
import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.function.Consumer;

/**
 * Subsystem for controlling pneumatics involved in intake.
 * This means the arms pneumatics and the plunger are controlled here.
 */
public class Pneumatics extends Subsystem {
    private static Pneumatics currentInstance;

    private DoubleSolenoid intakeLiftSolenoid;
    private DoubleSolenoid hatchEjectorSolenoid;

    private Compressor compressor;

    /**
     * The current status of the intake arms, whether they're in hatch mode or in cargo mode.
     */
    private IntakeMode mode;

    /**
     * True if the plunger is out, false otherwise.
     */
    private boolean plungerExtended = false;

    /**
     * Different states that the intake subsystem can be in, either
     * inhaling hatches or inhaling cargo.
     */
    public enum IntakeMode {
        CARGO, HATCH
    }

    /**
     * Initializes a new IntkaePneumatics instance if not already initialized.
     */
    public static SubsystemStatus init() {
        if (currentInstance == null) {
            currentInstance = new Pneumatics();
        }

        return SubsystemStatus.OK;
    }

    /**
     * Gets the current Pneumatics instance.
     *
     * @return The current Pneumatics instance.
     */
    public static Pneumatics getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Constructs a new Pneumatics with default DoubleSolenoids.
     */
    private Pneumatics() {
        this(new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID),
                new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID));
    }

    private final Consumer<RobotState> listener;

    /**
     * Constructs a new Pneumatics with the given double solenoids.
     *
     * @param intakeLiftSolenoid   The intake lift double solenoid.
     * @param hatchEjectorSolenoid The hatch ejector (plunger) solenoid.
     */
    private Pneumatics(final DoubleSolenoid intakeLiftSolenoid, final DoubleSolenoid hatchEjectorSolenoid) {
        this.intakeLiftSolenoid = intakeLiftSolenoid;
        this.hatchEjectorSolenoid = hatchEjectorSolenoid;

        addChild("Intake", intakeLiftSolenoid);
        addChild("Hatch", hatchEjectorSolenoid);

        // Need to make sure that the robot's state is known at the beginning.
        listener = this::onRobotStateChange;
        Robot.setOnStateChange(listener);

        compressor = new Compressor();
    }

    /**
     * Called when the robot's state changes.
     *
     * @param robotState The robot's state.
     */
    private void onRobotStateChange(RobotState robotState) {
        if (robotState == RobotState.TELEOP) {
            // Make sure the arms are in the right state for teleop (only useful during drive practice)
            if (getMode() == null) {
                if (Intake.getInstance().isCargoInMechanism()) {
                    mode = IntakeMode.CARGO;
                } else {
                    new RaiseArmsSafely().start();
                }
            }
            Robot.removeStateListener(listener);
        }
        // If we start in auto, assume robot was configured properly to start in hatch mode.
        else if (robotState == RobotState.AUTONOMOUS) {
            mode = IntakeMode.HATCH;
            Robot.removeStateListener(listener);
        }
    }

    /**
     * Gets the current intake mode, either hatch or cargo.
     *
     * @return The intake's current mode.
     */
    public Pneumatics.IntakeMode getMode() {
        return mode;
    }

    /**
     * Lowers the intake arms, in preparation for inhaling cargo.
     */
    public void lowerArms() {
        // We don't want to lower the intake onto the plunger.
        if (isPlungerStowed()) {
            intakeLiftSolenoid.set(DoubleSolenoid.Value.kReverse);
            mode = IntakeMode.CARGO;
        }
    }

    /**
     * Raises the intake arms in preparation for manipulating hatches.
     */
    public void raiseArms() {
        intakeLiftSolenoid.set(DoubleSolenoid.Value.kForward);
        mode = IntakeMode.HATCH;
    }

    /**
     * Turns off the intake arms pneumatics.
     */
    public void stopArmsPneumatics() {
        intakeLiftSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /**
     * Extends the hatch deployment cylinder
     */
    public void deployPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kReverse);
        plungerExtended = true;
    }

    /**
     * Retracts the hatch deployment cylinder
     */
    public void retractPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
        plungerExtended = false;
    }

    /**
     * Turns the pneumatics for the plunger piston off.
     */
    public void stopPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /**
     * Turns the pneumatics compressor on or off as desired.
     * @param state The desired state, true for on or false for off.
     */
    public void setCompressorState(final boolean state) {
        compressor.setClosedLoopControl(state);
    }

    /**
     * Determines if the hatch ejector (plunger) is in the inward position (stowed) or in the outward position (not stowed).
     *
     * @return True if the plunger is stowed, false otherwise.
     */
    public boolean isPlungerStowed() {
        return !plungerExtended;
    }

    @Override
    protected void initDefaultCommand() {
    }
}
