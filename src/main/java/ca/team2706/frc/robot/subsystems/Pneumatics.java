package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.commands.intake.arms.RaiseArmsSafely;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.pneumatics.PneumaticPiston;
import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.function.Consumer;

/**
 * Subsystem for controlling pneumatics involved in intake.
 * This means the arms pneumatics and the plunger are controlled here.
 */
public class Pneumatics extends Subsystem {
    private static Pneumatics currentInstance;

    private PneumaticPiston intakeLiftSolenoid;
    private PneumaticPiston hatchEjectorSolenoid;

    /**
     * Different states that the intake subsystem can be in, either
     * inhaling hatches or inhaling cargo.
     */
    public enum IntakeMode {
        CARGO, HATCH
    }

    /**
     * Initializes a new IntakePneumatics instance if not already initialized.
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
        this(new PneumaticPiston(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID, null), // TODO make sure null doesn't crash it.
                new PneumaticPiston(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID, PneumaticState.STOWED));
    }

    private final Consumer<RobotState> listener;

    /**
     * Constructs a new Pneumatics with the given double solenoids.
     *
     * @param intakeLiftSolenoid   The intake lift double solenoid.
     * @param hatchEjectorSolenoid The hatch ejector (plunger) solenoid.
     */
    private Pneumatics(final PneumaticPiston intakeLiftSolenoid, final PneumaticPiston hatchEjectorSolenoid) {
        this.intakeLiftSolenoid = intakeLiftSolenoid;
        this.hatchEjectorSolenoid = hatchEjectorSolenoid;

        addChild("Intake Arms", intakeLiftSolenoid);
        addChild("Hatch", hatchEjectorSolenoid);

        // Need to make sure that the robot's state is known at the beginning.
        listener = this::onRobotStateChange;
        Robot.setOnStateChange(listener);
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
                    intakeLiftSolenoid.forceSetState(PneumaticState.DEPLOYED);
                } else {
                    new RaiseArmsSafely().start();
                }
            }
            Robot.removeStateListener(listener);
        }
        // If we start in auto, assume robot was configured properly to start in hatch mode.
        else if (robotState == RobotState.AUTONOMOUS) {
            intakeLiftSolenoid.forceSetState(PneumaticState.STOWED);
            Robot.removeStateListener(listener);
        }
    }

    /**
     * Gets the current intake mode, either hatch or cargo.
     *
     * @return The intake's current mode.
     */
    public Pneumatics.IntakeMode getMode() {
        final Pneumatics.IntakeMode mode;
        final PneumaticState armsMode = getArmsState();
        if (armsMode == null) {
            mode = null;
        } else {
            switch (getArmsState()) {
                case DEPLOYED:
                    mode = IntakeMode.CARGO;
                    break;
                case STOWED:
                    mode = IntakeMode.HATCH;
                    break;
                default:
                    mode = null;
                    break;
            }
        }

        return mode;
    }

    /**
     * Moves the arms to the given position.
     *
     * @param desiredState The new arms position.
     */
    public void moveArms(PneumaticState desiredState) {
        if (isPlungerStowed()) {
            intakeLiftSolenoid.set(desiredState);
        }
    }

    /**
     * Turns off the intake arms pneumatics.
     */
    public void stopArmsPneumatics() {
        intakeLiftSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /**
     * Gets the state position of the intake arms.
     *
     * @return The state position of the arms.
     */
    public PneumaticState getArmsState() {
        return intakeLiftSolenoid.getState();
    }

    /**
     * Moves the plunger to the given position.
     *
     * @param newState The new plunger position.
     */
    public void movePlunger(PneumaticState newState) {
        hatchEjectorSolenoid.set(newState);
    }

    /**
     * Turns the pneumatics for the plunger piston off.
     */
    public void stopPlunger() {
        hatchEjectorSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /**
     * Determines if the hatch ejector (plunger) is in the inward position (stowed) or in the outward position (not stowed).
     *
     * @return True if the plunger is stowed, false otherwise.
     */
    public boolean isPlungerStowed() {
        return getPlungerState() == PneumaticState.STOWED;
    }

    /**
     * Gets the plunger's piston state.
     *
     * @return The plunger piston state.
     */
    public PneumaticState getPlungerState() {
        return hatchEjectorSolenoid.getState();
    }

    @Override
    protected void initDefaultCommand() {
    }
}
