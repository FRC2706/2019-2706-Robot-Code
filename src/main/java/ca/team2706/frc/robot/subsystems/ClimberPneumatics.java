package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.pneumatics.PneumaticPiston;
import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * ClimberPneumatics subsystem for controlling the climber pneumatics.
 */
public class ClimberPneumatics extends Subsystem {
    private static ClimberPneumatics currentInstance;


    // Pistons that extend and push the robot onto the platform.
    private final PneumaticPiston backPusher, frontPusher;

    /**
     * Gets the current instance of the climber subsystem
     *
     * @return The current climber instance.
     */
    public static ClimberPneumatics getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new instance of the climber subsystem, if it has not been not been initialized.
     *
     * @return The subsystem's status after initialization.
     */
    public static SubsystemStatus init() {
        if (currentInstance == null) {
            currentInstance = new ClimberPneumatics();
        }

        return SubsystemStatus.OK;
    }

    /**
     * Constructs a new climber instance with default talon motors and such.
     */
    private ClimberPneumatics() {
        this(
                new PneumaticPiston(Config.CLIMBER_FRONT_PUSHER_FORWARD_ID, Config.CLIMBER_FRONT_PUSHER_BACKWARD_ID, PneumaticState.STOWED, false),
                new PneumaticPiston(Config.CLIMBER_BACK_PUSHER_FORWARD_ID, Config.CLIMBER_BACK_PUSHER_BACKWARD_ID, PneumaticState.STOWED, false));
    }

    /**
     * Constructs a new climber instance with the given climber motor.
     *
     * @param backPusher  The left pusher double solenoid.
     * @param frontPusher The front pusher double solenoid.
     */
    public ClimberPneumatics(final PneumaticPiston backPusher, final PneumaticPiston frontPusher) {
        this.backPusher = backPusher;
        this.frontPusher = frontPusher;

        addChild("Back Pusher", this.backPusher);
        addChild("Front Pusher", this.frontPusher);
    }

    @Override
    public void periodic() {
        super.periodic();
    }

    /**
     * Moves the back piston to the desired state.
     *
     * @param desiredState The desired piston state.
     */
    public void moveBackPiston(final PneumaticState desiredState) {
        Log.d("Back climber " + desiredState.name());
        backPusher.set(desiredState);
    }

    /**
     * Moves the front piston to the desired state
     *
     * @param desiredState The desired piston state.
     */
    public void moveFrontPiston(final PneumaticState desiredState) {
        Log.d("Front climber " + desiredState.name());
        frontPusher.set(desiredState);
    }

    /**
     * Gets the state of the front pistons.
     *
     * @return The piston state.
     */
    public PneumaticState getFrontState() {
        return frontPusher.getState();
    }

    /**
     * Gets the state of the back pistons.
     *
     * @return The piston state.
     */
    public PneumaticState getBackState() {
        return backPusher.getState();
    }

    /**
     * Stops the climber pneumatics.
     */
    public void stopPneumatics() {
        backPusher.set(DoubleSolenoid.Value.kOff);
        frontPusher.set(DoubleSolenoid.Value.kOff);
    }

    @Override
    protected void initDefaultCommand() {
    }
}
