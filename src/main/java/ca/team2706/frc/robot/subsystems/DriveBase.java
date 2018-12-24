package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.talon.TalonEncoder;
import ca.team2706.frc.robot.talon.TalonFactory;
import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/**
 * Subsystem that controls the driving of the robot as well as certain sensors that are used for driving
 */
public class DriveBase extends Subsystem {

    private final WPI_TalonSRX leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor;
    private final DifferentialDrive robotDriveBase;

    private final PigeonIMU gyro;
    private final TalonEncoder leftEncoder, rightEncoder;

    /**
     * Creates a drive base, and initializes all required sensors and motors
     */
    public DriveBase() {
        // TODO: Configure motors from fluid config or from file
        leftFrontMotor = TalonFactory.defaultConfig(Config.LEFT_FRONT_DRIVE_MOTOR_ID);
        leftBackMotor = TalonFactory.defaultConfig(Config.LEFT_BACK_DRIVE_MOTOR_ID);
        rightFrontMotor = TalonFactory.defaultConfig(Config.RIGHT_FRONT_DRIVE_MOTOR_ID);
        rightBackMotor = TalonFactory.defaultConfig(Config.RIGHT_BACK_DRIVE_MOTOR_ID);

        robotDriveBase = new DifferentialDrive(new SpeedControllerGroup(leftFrontMotor, leftBackMotor),
                new SpeedControllerGroup(rightFrontMotor, rightBackMotor));
        robotDriveBase.setSafetyEnabled(false);

        gyro = new PigeonIMU(Config.GYRO_ID);
        leftEncoder = new TalonEncoder(leftFrontMotor);
        rightEncoder = new TalonEncoder(rightFrontMotor);

        leftEncoder.setDistancePerPulse(Config.DRIVE_ENCODER_DPP);
        rightEncoder.setDistancePerPulse(Config.DRIVE_ENCODER_DPP);

        // TODO: Also output data to logging/smartdashboard
        addChild("Left Front Motor", leftFrontMotor);
        addChild("Left Back Motor", leftBackMotor);
        addChild("Right Front Motor", rightFrontMotor);
        addChild("Right Back Motor", rightBackMotor);

        addChild(robotDriveBase);

        addChild(gyro);
        addChild("Left Encoder", leftEncoder);
        addChild("Right Encoder", rightEncoder);
    }

    @Override
    // Have the default command set from OI
    protected void initDefaultCommand() {}

    /**
     * Changes whether the drive motors should coast or brake when output is 0
     *
     * @param brake Whether to turn on brake mode or not
     */
    public void setBrakeMode(boolean brake) {
        NeutralMode mode = brake ? NeutralMode.Brake : NeutralMode.Coast;

        leftFrontMotor.setNeutralMode(mode);
        leftBackMotor.setNeutralMode(mode);
        rightFrontMotor.setNeutralMode(mode);
        rightBackMotor.setNeutralMode(mode);
    }

    /**
     * Drives the robot by controlling the speed of the left and right motors individually
     *
     * @param leftSpeed The speed for the left motors
     * @param rightSpeed The speed for the right motors
     * @param squaredInputs Whether to square each of the values
     */
    public void tankDrive(double leftSpeed, double rightSpeed, boolean squaredInputs) {
        robotDriveBase.tankDrive(leftSpeed, rightSpeed, squaredInputs);
    }

    /**
     * Drives the robot by controlling the forward and rotation values
     *
     * @param forwardSpeed The amount to drive forward
     * @param rotateSpeed The amount to rotate
     * @param squaredInputs Whether to square each of the values
     */
    public void arcadeDrive(double forwardSpeed, double rotateSpeed, boolean squaredInputs) {
        robotDriveBase.arcadeDrive(forwardSpeed, rotateSpeed, squaredInputs);
    }

    /**
     * Drives the robot by controlling the forward and amount of curve values
     *
     * @param forwardSpeed The amount to drive forward
     * @param curveSpeed The amount that the robot should curve while driving
     * @param override When true will only use rotation values
     */
    public void curvatureDrive(double forwardSpeed, double curveSpeed, boolean override) {
        robotDriveBase.curvatureDrive(forwardSpeed, curveSpeed, override);
    }

    /**
     * Get the distance travelled by the left encoder in feet
     *
     * @return The distance of the left encoder
     */
    public double getLeftDistance() {
        return leftEncoder.getDistance();
    }

    /**
     * Get the distance travelled by the right encoder in feet
     *
     * @return The distance of the right encoder
     */
    public double getRightDistance() {
        return rightEncoder.getDistance();
    }

    /**
     * Get the speed of the left encoder in feet per second
     *
     * @return The speed of the left encoder
     */
    public double getLeftSpeed() {
        return leftEncoder.getRate();
    }

    /**
     * Get the speed of the right encoder in feet per second
     *
     * @return The speed of the right encoder
     */
    public double getRightSpeed() {
        return rightEncoder.getRate();
    }

    /**
     * Gets the angle that the robot is facing in degrees
     *
     * @return The rotation of the robot
     */
    public double getHeading() {
        return gyro.getFusedHeading();
    }

    /**
     * Resets the encoder values to 0 ticks
     */
    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    /**
     * Resets the gyro to 0 degrees
     */
    public void resetGyro() {
        gyro.setFusedHeading(0, Config.CAN_SHORT);
    }

    /**
     * Resets the encoders and gyro
     */
    public void reset() {
        resetEncoders();
        resetGyro();
    }
}
