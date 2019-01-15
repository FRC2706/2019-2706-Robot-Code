package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Sendables;
import ca.team2706.frc.robot.commands.ArcadeDriveWithJoystick;
import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.SendableImpl;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Subsystem that controls the driving of the robot as well as certain sensors that are used for driving
 */
public class DriveBase extends Subsystem {

    private final WPI_TalonSRX leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor;
    private final DifferentialDrive robotDriveBase;

    private final PigeonIMU gyro;

    /**
     * Creates a drive base, and initializes all required sensors and motors
     */
    public DriveBase() {
        // TODO: Configure motors from fluid config or from file
        leftFrontMotor = new WPI_TalonSRX(Config.LEFT_FRONT_DRIVE_MOTOR_ID);
        leftBackMotor = new WPI_TalonSRX(Config.LEFT_BACK_DRIVE_MOTOR_ID);
        rightFrontMotor = new WPI_TalonSRX(Config.RIGHT_FRONT_DRIVE_MOTOR_ID);
        rightBackMotor = new WPI_TalonSRX(Config.RIGHT_BACK_DRIVE_MOTOR_ID);

        leftFrontMotor.configFactoryDefault(Config.CAN_LONG);
        leftBackMotor.configFactoryDefault(Config.CAN_LONG);
        rightFrontMotor.configFactoryDefault(Config.CAN_LONG);
        rightBackMotor.configFactoryDefault(Config.CAN_LONG);

        leftFrontMotor.configPeakCurrentLimit(2, Config.CAN_LONG);
        leftBackMotor.configPeakCurrentLimit(2, Config.CAN_LONG);
        rightFrontMotor.configPeakCurrentLimit(2, Config.CAN_LONG);
        rightBackMotor.configPeakCurrentLimit(2, Config.CAN_LONG);

        leftFrontMotor.setInverted(Config.INVERT_LEFT_DRIVE);
        leftFrontMotor.setInverted(Config.INVERT_RIGHT_DRIVE);

        leftBackMotor.follow(leftFrontMotor);
        rightBackMotor.follow(rightFrontMotor);

        leftBackMotor.setInverted(InvertType.FollowMaster);
        leftBackMotor.setInverted(InvertType.FollowMaster);

        enableCurrentLimit(Config.DRIVEBASE_CURRENT_LIMIT);

        robotDriveBase = new DifferentialDrive(leftFrontMotor, rightFrontMotor);
        robotDriveBase.setRightSideInverted(false);
        robotDriveBase.setSafetyEnabled(false);

        gyro = new PigeonIMU(Config.GYRO_ID);

        // TODO: Also output data to logging/smartdashboard
        addChild("Left Front Motor", leftFrontMotor);
        addChild("Left Back Motor", leftBackMotor);
        addChild("Right Front Motor", rightFrontMotor);
        addChild("Right Back Motor", rightBackMotor);

        addChild(robotDriveBase);

        addChild("Gyroscope", Sendables.newPigeonSendable(gyro));

        addChild("Left Encoder", Sendables.newTalonEncoderSendable(leftFrontMotor));
        addChild("Right Encoder", Sendables.newTalonEncoderSendable(rightFrontMotor));

        setOpenLoopMode();
    }

    public void stop() {
        leftFrontMotor.stopMotor();
        rightFrontMotor.stopMotor();
    }

    private void selectEncodersStandard() {
        leftFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    }

    public void setOpenLoopMode() {
        stop();
        selectEncodersStandard();
        reset();
    }

    public void enableCurrentLimit(boolean enable) {
        leftFrontMotor.enableCurrentLimit(enable);
        leftBackMotor.enableCurrentLimit(enable);
        rightFrontMotor.enableCurrentLimit(enable);
        rightBackMotor.enableCurrentLimit(enable);

    }

    private Command defaultCommand;

    @Override
    // Have the default command set from OI
    protected void initDefaultCommand() {
        // TODO: Move to OI

        if(defaultCommand == null) {
             defaultCommand = new ArcadeDriveWithJoystick(new Joystick(0), 4, 5);
        }
        setDefaultCommand(defaultCommand);
    }

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
        return leftFrontMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP;
    }

    /**
     * Get the distance travelled by the right encoder in feet
     *
     * @return The distance of the right encoder
     */
    public double getRightDistance() {
        return rightFrontMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP;
    }

    /**
     * Get the speed of the left encoder in feet per second
     *
     * @return The speed of the left encoder
     */
    public double getLeftSpeed() {
        return leftFrontMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10;
    }

    /**
     * Get the speed of the right encoder in feet per second
     *
     * @return The speed of the right encoder
     */
    public double getRightSpeed() {
        return rightFrontMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10;
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
        leftFrontMotor.getSensorCollection().setQuadraturePosition(0, Config.CAN_SHORT);
        rightFrontMotor.getSensorCollection().setQuadraturePosition(0, Config.CAN_SHORT);
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
