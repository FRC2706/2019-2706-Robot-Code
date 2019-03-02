package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem for controlling pneumatics involved in intake.
 * This means the arms pneumatics and the plunger are controlled here.
 */
public class IntakePneumatics extends Subsystem {
    private static IntakePneumatics currentInstance;

    private DoubleSolenoid intakeLiftSolenoid;
    private DoubleSolenoid hatchEjectorSolenoid;

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
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new IntakePneumatics();
        }
    }

    /**
     * Gets the current IntakePneumatics instance.
     *
     * @return The current IntakePneumatics instance.
     */
    public static IntakePneumatics getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Constructs a new IntakePneumatics with default DoubleSolenoids.
     */
    private IntakePneumatics() {
        this(new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID),
                new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID));
    }

    /**
     * Constructs a new IntakePneumatics with the given double solenoids.
     *
     * @param intakeLiftSolenoid   The intake lift double solenoid.
     * @param hatchEjectorSolenoid The hatch ejector (plunger) solenoid.
     */
    private IntakePneumatics(final DoubleSolenoid intakeLiftSolenoid, final DoubleSolenoid hatchEjectorSolenoid) {
        this.intakeLiftSolenoid = intakeLiftSolenoid;
        this.hatchEjectorSolenoid = hatchEjectorSolenoid;
    }

    /**
     * Gets the current intake mode, either hatch or cargo.
     *
     * @return The intake's current mode.
     */
    public IntakePneumatics.IntakeMode getMode() {
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
