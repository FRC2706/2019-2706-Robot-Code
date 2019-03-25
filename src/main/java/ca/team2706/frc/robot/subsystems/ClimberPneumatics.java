package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * ClimberPneumatics subsystem for controlling the climber pneumatics.
 */
public class ClimberPneumatics extends Subsystem {

    private static ClimberPneumatics currentInstance;


    // Pistons that extend and push the robot onto the platform.
    private DoubleSolenoid leftPusher, rightPusher;

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
                new DoubleSolenoid(Config.CLIMBER_LEFT_PUSHER_FORWARD_ID, Config.CLIMBER_LEFT_PUSHER_BACKWARD_ID),
                new DoubleSolenoid(Config.CLIMBER_RIGHT_PUSHER_FORWARD_ID, Config.CLIMBER_RIGHT_PUSHER_BACKWARD_ID));
    }

    /**
     * Constructs a new climber instance with the given climber motor.
     *
     * @param rightPusher The right pusher double solenoid.
     * @param leftPusher  The left pusher double solenoid.
     */
    public ClimberPneumatics(final DoubleSolenoid rightPusher, final DoubleSolenoid leftPusher) {
        this.rightPusher = rightPusher;
        this.leftPusher = leftPusher;

        addChild("Left Pusher", this.leftPusher);
        addChild("Right Pusher", this.rightPusher);
    }

    /**
     * Does the final stage in climbing by pushing out the climber pistons to mount the robot on the third level.
     */
    public void pushRobot() {
        leftPusher.set(DoubleSolenoid.Value.kForward);
        rightPusher.set(DoubleSolenoid.Value.kForward);
    }

    /**
     * Retracts the climbing pistons.
     */
    public void retractPushers() {
        leftPusher.set(DoubleSolenoid.Value.kReverse);
        rightPusher.set(DoubleSolenoid.Value.kReverse);
    }

    /**
     * Stops the climber pneumatics.
     */
    public void stopPneumatics() {
        leftPusher.set(DoubleSolenoid.Value.kOff);
        rightPusher.set(DoubleSolenoid.Value.kOff);
    }

    @Override
    protected void initDefaultCommand() {
    }
}
