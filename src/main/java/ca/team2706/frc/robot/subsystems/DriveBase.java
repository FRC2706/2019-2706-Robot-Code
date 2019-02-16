package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Sendables;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.sensors.AnalogSelector;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Subsystem that controls the driving of the robot as well as certain sensors that are used for driving
 */
public class DriveBase extends Subsystem {

    private static DriveBase currentInstance;
    /**
     * Four drive Talons
     */
    private final WPI_TalonSRX leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor;
    /**
     * Logic for controlling robot motion in teleop
     */
    private final DifferentialDrive robotDriveBase;
    /**
     * Gyro to record robot heading
     */
    private final PigeonIMU gyro;
    /**
     * Analog Selector
     */
    private final AnalogSelector selector;
    /*
     * Purple light that goes on the robot
     */
    private final PWM light;
    /**
     * The mode that the robot is driving with
     */
    private DriveMode driveMode;
    /**
     * Indicates whether the robot is in brake mode
     */
    private boolean brakeMode;
    private Command defaultCommand;

    /**
     * Creates a drive base, and initializes all required sensors and motors
     */
    private DriveBase() {
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

        leftFrontMotor.setInverted(Config.INVERT_FRONT_LEFT_DRIVE);
        rightFrontMotor.setInverted(Config.INVERT_FRONT_RIGHT_DRIVE);

        follow();

        if (Config.INVERT_FRONT_LEFT_DRIVE == Config.INVERT_BACK_LEFT_DRIVE) {
            leftBackMotor.setInverted(InvertType.FollowMaster);
        } else {
            leftBackMotor.setInverted(InvertType.OpposeMaster);
        }

        if (Config.INVERT_FRONT_RIGHT_DRIVE == Config.INVERT_BACK_RIGHT_DRIVE) {
            rightBackMotor.setInverted(InvertType.FollowMaster);
        } else {
            rightBackMotor.setInverted(InvertType.OpposeMaster);
        }

        enableCurrentLimit(Config.DRIVEBASE_CURRENT_LIMIT);

        robotDriveBase = new DifferentialDrive(leftFrontMotor, rightFrontMotor);
        robotDriveBase.setRightSideInverted(false);
        robotDriveBase.setSafetyEnabled(false);

        gyro = new PigeonIMU(new TalonSRX(Config.GYRO_TALON_ID));

        selector = new AnalogSelector(Config.SELECTOR_ID);

        light = new PWM(Config.PURPLE_LIGHT);

        light.setRaw(4095);

        // TODO: Also output data to logging/smartdashboard
        addChild("Left Front Motor", leftFrontMotor);
        addChild("Left Back Motor", leftBackMotor);
        addChild("Right Front Motor", rightFrontMotor);
        addChild("Right Back Motor", rightBackMotor);

        addChild(robotDriveBase);

        addChild("Gyroscope", Sendables.newPigeonSendable(gyro));
        addChild("Selector", selector);

        addChild("Left Encoder", Sendables.newTalonEncoderSendable(leftFrontMotor));
        addChild("Right Encoder", Sendables.newTalonEncoderSendable(rightFrontMotor));

        addChild("Merge Light", light);

        setDisabledMode();
        setBrakeMode(false);

        Thread loggingThread = new Thread(this::log);
        loggingThread.setDaemon(true);
        loggingThread.start();
    }

    public static DriveBase getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new drive base object.
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new DriveBase();
        }
    }

    /**
     * Gets the analog selector's index
     *
     * @return The index from 1-12 or 0 if unplugged
     */
    public int getAnalogSelectorIndex() {
        return selector.getIndex();
    }

    /**
     * Gets the drivemode that the robot is currently in
     *
     * @return The current drive mode
     */
    public DriveMode getDriveMode() {
        return driveMode;
    }

    /**
     * Stops the robot
     */
    public void stop() {
        leftFrontMotor.stopMotor();
        rightFrontMotor.stopMotor();
    }

    /**
     * Selects local encoders and the current sensor
     */
    private void selectEncodersStandard() {
        leftFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        leftFrontMotor.configNeutralDeadband(Config.DRIVE_OPEN_LOOP_DEADBAND.value());
        rightFrontMotor.configNeutralDeadband(Config.DRIVE_OPEN_LOOP_DEADBAND.value());
        leftBackMotor.configNeutralDeadband(Config.DRIVE_OPEN_LOOP_DEADBAND.value());
        rightBackMotor.configNeutralDeadband(Config.DRIVE_OPEN_LOOP_DEADBAND.value());
    }

    /**
     * Selects local encoders and the current sensor
     */
    private void selectEncodersSum() {
        leftFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_SHORT);
        rightFrontMotor.configRemoteFeedbackFilter(leftFrontMotor.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 0, Config.CAN_SHORT);

        rightFrontMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, Config.CAN_SHORT);
        rightFrontMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, Config.CAN_SHORT);

        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, Config.CAN_SHORT);
        rightFrontMotor.configSelectedFeedbackCoefficient(0.5, 0, Config.CAN_SHORT);

        leftFrontMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_LEFT.value());
        rightFrontMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_RIGHT.value());

        rightFrontMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, Config.CAN_SHORT);
        rightFrontMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, Config.CAN_SHORT);
        leftFrontMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, Config.CAN_SHORT);

        leftFrontMotor.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value());
        rightFrontMotor.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value());
        leftBackMotor.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value());
        rightBackMotor.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value());

        rightFrontMotor.config_kP(0, Config.DRIVE_CLOSED_LOOP_P.value());
        rightFrontMotor.config_kI(0, Config.DRIVE_CLOSED_LOOP_I.value());
        rightFrontMotor.config_kD(0, Config.DRIVE_CLOSED_LOOP_D.value());

        rightFrontMotor.configClosedLoopPeriod(0, 1, Config.CAN_SHORT);
    }

    /**
     * Selects local encoders, the current sensor and the pigeon
     */
    private void selectEncodersSumWithPigeon() {
        leftFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_SHORT);
        rightFrontMotor.configRemoteFeedbackFilter(leftFrontMotor.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 0, Config.CAN_SHORT);
        rightFrontMotor.configRemoteFeedbackFilter(gyro.getDeviceID(), RemoteSensorSource.GadgeteerPigeon_Yaw, 1, Config.CAN_SHORT);

        rightFrontMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, Config.CAN_SHORT);
        rightFrontMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, Config.CAN_SHORT);

        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, Config.CAN_SHORT);
        rightFrontMotor.configSelectedFeedbackCoefficient(0.5, 0, Config.CAN_SHORT);

        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, 1, Config.CAN_SHORT);

        rightFrontMotor.configSelectedFeedbackCoefficient(1, 1, Config.CAN_SHORT);

        leftFrontMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_LEFT.value());
        rightFrontMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_RIGHT.value());

        rightFrontMotor.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20, Config.CAN_SHORT);
        rightFrontMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20, Config.CAN_SHORT);
        leftFrontMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, Config.CAN_SHORT);
        rightFrontMotor.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20, Config.CAN_SHORT);

        /* Configure neutral deadband */
        rightFrontMotor.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value(), Config.CAN_SHORT);
        leftFrontMotor.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value(), Config.CAN_SHORT);

        rightFrontMotor.config_kP(0, Config.DRIVE_CLOSED_LOOP_P.value());
        rightFrontMotor.config_kI(0, Config.DRIVE_CLOSED_LOOP_I.value());
        rightFrontMotor.config_kD(0, Config.DRIVE_CLOSED_LOOP_D.value());

        rightFrontMotor.config_kP(1, Config.PIGEON_KP.value());
        rightFrontMotor.config_kI(1, Config.PIGEON_KI.value());
        rightFrontMotor.config_kD(1, Config.PIGEON_KD.value());
        rightFrontMotor.config_kF(1, Config.PIGEON_KF.value());

        rightFrontMotor.configClosedLoopPeriod(0, 1, Config.CAN_SHORT);

        rightFrontMotor.configClosedLoopPeriod(1, 1, Config.CAN_SHORT);
        rightFrontMotor.configAuxPIDPolarity(false, Config.CAN_SHORT);

        rightFrontMotor.selectProfileSlot(0, 0);
        rightFrontMotor.selectProfileSlot(1, 1);
    }

    /**
     * Sets the talons to a disabled mode
     */
    public void setDisabledMode() {
        if (driveMode != DriveMode.Disabled) {
            stop();

            reset();

            driveMode = DriveMode.Disabled;
        }
    }

    /**
     * Switches the talons to a mode that is optimal for driving the robot using human input
     */
    public void setOpenLoopVoltageMode() {
        if (driveMode != DriveMode.OpenLoopVoltage) {
            stop();
            selectEncodersStandard();
            reset();

            driveMode = DriveMode.OpenLoopVoltage;
        }
    }

    /**
     * Sets the talons to a disabled mode
     */
    public void setPositionNoGyroMode() {
        if (driveMode != DriveMode.PositionNoGyro) {
            stop();
            selectEncodersSum();
            reset();

            driveMode = DriveMode.PositionNoGyro;
        }
    }

    /**
     * Gets the encoder sum using the pigeon
     */
    public void setPositionGyroMode() {
        if (driveMode != DriveMode.PositionGyro) {
            stop();
            selectEncodersSumWithPigeon();
            reset();

            driveMode = DriveMode.PositionGyro;
        }
    }

    /**
     * Changes whether current limiting should be used
     *
     * @param enable Whether to enable the current or not
     */
    public void enableCurrentLimit(boolean enable) {
        leftFrontMotor.enableCurrentLimit(enable);
        leftBackMotor.enableCurrentLimit(enable);
        rightFrontMotor.enableCurrentLimit(enable);
        rightBackMotor.enableCurrentLimit(enable);

    }

    @Override
    protected void initDefaultCommand() {
    }

    /**
     * Has the slave motors follow the master motors
     */
    private void follow() {
        leftBackMotor.follow(leftFrontMotor);
        rightBackMotor.follow(rightFrontMotor);
    }

    /**
     * Checks whether the robot is in brake mode
     *
     * @return True when the Talons have the neutral mode set to {@code NeutralMode.Brake}
     */
    public boolean isBrakeMode() {
        return brakeMode;
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

        brakeMode = brake;
    }

    /**
     * Drives the robot by controlling the speed of the left and right motors individually
     *
     * @param leftSpeed     The speed for the left motors
     * @param rightSpeed    The speed for the right motors
     * @param squaredInputs Whether to square each of the values
     */
    public void tankDrive(double leftSpeed, double rightSpeed, boolean squaredInputs) {
        setOpenLoopVoltageMode();

        robotDriveBase.tankDrive(leftSpeed, rightSpeed, squaredInputs);
        follow();
    }

    /**
     * Drives the robot by controlling the forward and rotation values
     *
     * @param forwardSpeed  The amount to drive forward
     * @param rotateSpeed   The amount to rotate
     * @param squaredInputs Whether to square each of the values
     */
    public void arcadeDrive(double forwardSpeed, double rotateSpeed, boolean squaredInputs) {
        setOpenLoopVoltageMode();

        robotDriveBase.arcadeDrive(forwardSpeed, rotateSpeed, squaredInputs);
        follow();
    }

    /**
     * Drives the robot by controlling the forward and amount of curve values
     *
     * @param forwardSpeed The amount to drive forward
     * @param curveSpeed   The amount that the robot should curve while driving
     * @param override     When true will only use rotation values
     */
    public void curvatureDrive(double forwardSpeed, double curveSpeed, boolean override) {
        setOpenLoopVoltageMode();

        robotDriveBase.curvatureDrive(forwardSpeed, curveSpeed, override);
        follow();
    }

    /**
     * Goes to a position with the closed loop Talon PIDs using only encoder information
     *
     * @param speed    The speed from 0 to 1
     * @param setpoint The setpoint to go to in feet
     */
    public void setPositionNoGyro(double speed, double setpoint) {
        setPositionNoGyroMode();

        leftFrontMotor.configClosedLoopPeakOutput(0, speed);
        rightFrontMotor.configClosedLoopPeakOutput(0, speed);

        rightFrontMotor.set(ControlMode.Position, setpoint / Config.DRIVE_ENCODER_DPP);
        leftFrontMotor.follow(rightFrontMotor);

        follow();
    }

    /**
     * Goes to a position with the closed loop Talon PIDs using only encoder and gyro
     *
     * @param speed          The speed from 0 to 1
     * @param setpoint       The setpoint to go to in feet
     * @param targetRotation The desired rotation
     */
    public void setPositionGyro(double speed, double setpoint, double targetRotation) {

        setPositionGyroMode();

        leftFrontMotor.configClosedLoopPeakOutput(0, speed);
        rightFrontMotor.configClosedLoopPeakOutput(0, speed);
        leftFrontMotor.configClosedLoopPeakOutput(1, speed);
        rightFrontMotor.configClosedLoopPeakOutput(1, speed);

        rightFrontMotor.set(ControlMode.Position, setpoint / Config.DRIVE_ENCODER_DPP, DemandType.AuxPID, targetRotation);
        leftFrontMotor.follow(rightFrontMotor, FollowerType.AuxOutput1);

        follow();
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
        return Sendables.getPigeonYaw(gyro);
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
        gyro.setYaw(0, Config.CAN_SHORT);
    }

    /**
     * Resets the encoders and gyro
     */
    public void reset() {
        resetEncoders();
        resetGyro();
    }

    /**
     * Gets the error for the left motor
     *
     * @return The error in feet
     */
    public double getLeftError() {
        return leftFrontMotor.getClosedLoopError(0) * Config.DRIVE_ENCODER_DPP;
    }

    /**
     * Gets the error for the right motor
     *
     * @return The error in feet
     */
    public double getRightError() {
        return rightFrontMotor.getClosedLoopError(0) * Config.DRIVE_ENCODER_DPP;
    }

    public void log() {
        while (!Thread.interrupted()) {
            if (DriverStation.getInstance().isEnabled()) {
                Log.d("Gyro: " + gyro.getFusedHeading());

                Log.d("Left front motor current: " + leftFrontMotor.getOutputCurrent());
                Log.d("Right front motor current: " + rightFrontMotor.getOutputCurrent());
                Log.d("Left back motor current: " + leftBackMotor.getOutputCurrent());
                Log.d("Right back motor current: " + rightBackMotor.getOutputCurrent());

                Log.d("Left front motor temperature: " + leftFrontMotor.getTemperature());
                Log.d("Right front motor temperature: " + rightFrontMotor.getTemperature());
                Log.d("Left back motor temperature: " + leftBackMotor.getTemperature());
                Log.d("Right back motor temperature: " + rightBackMotor.getTemperature());

                Log.d("Left front motor output percent: " + leftFrontMotor.getMotorOutputPercent());
                Log.d("Right front motor output percent: " + rightFrontMotor.getMotorOutputPercent());
                Log.d("Left back motor output percent: " + leftBackMotor.getMotorOutputPercent());
                Log.d("Right back motor output percent: " + rightBackMotor.getMotorOutputPercent());

                Log.d("Left front motor distance: " + leftFrontMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);
                Log.d("Right front motor distance: " + rightFrontMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);
                Log.d("Left back motor distance: " + leftBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);
                Log.d("Right back motor distance: " + rightBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);

                Log.d("Left front motor speed: " + leftFrontMotor.getSensorCollection().getQuadratureVelocity() / Config.DRIVE_ENCODER_DPP * 10);
                Log.d("Right front motor speed: " + rightFrontMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP * 10);
                Log.d("Left back motor speed: " + leftBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP * 10);
                Log.d("Right back motor speed: " + rightBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP * 10);
            }

            SmartDashboard.putNumber("Gyro", gyro.getFusedHeading());

            SmartDashboard.putNumber("Left front motor current", leftFrontMotor.getOutputCurrent());
            SmartDashboard.putNumber("Right front motor current", rightFrontMotor.getOutputCurrent());
            SmartDashboard.putNumber("Left back motor current", leftBackMotor.getOutputCurrent());
            SmartDashboard.putNumber("Right back motor current", rightBackMotor.getOutputCurrent());

            SmartDashboard.putNumber("Left front motor temp", leftFrontMotor.getTemperature());
            SmartDashboard.putNumber("Right front motor temp", rightFrontMotor.getTemperature());
            SmartDashboard.putNumber("Left back motor temp", leftBackMotor.getTemperature());
            SmartDashboard.putNumber("Right back motor temp", rightBackMotor.getTemperature());

            SmartDashboard.putNumber("Left front motor output", leftFrontMotor.getMotorOutputPercent());
            SmartDashboard.putNumber("Right front motor output", rightFrontMotor.getMotorOutputPercent());
            SmartDashboard.putNumber("Left back motor output", leftBackMotor.getMotorOutputPercent());
            SmartDashboard.putNumber("Right back motor output", rightBackMotor.getMotorOutputPercent());

            SmartDashboard.putNumber("Left front motor distance: ", leftFrontMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);
            SmartDashboard.putNumber("Right front motor distance: ", rightFrontMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);
            SmartDashboard.putNumber("Left back motor distance: ", leftBackMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);
            SmartDashboard.putNumber("Right back motor distance: ", rightBackMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);

            SmartDashboard.putNumber("Left front motor speed", leftFrontMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
            SmartDashboard.putNumber("Right front motor speed", rightFrontMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
            SmartDashboard.putNumber("Left back motor speed", leftBackMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
            SmartDashboard.putNumber("Right back motor speed", rightBackMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
        }

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * The drive mode of the robot
     */
    public enum DriveMode {
        /**
         * There is no control mode active
         */
        Disabled,

        /**
         * Standard open loop voltage control
         */
        OpenLoopVoltage,

        /**
         * Performs closed loop position control without heading support
         */
        PositionNoGyro,

        /**
         * Closed loop control with Auxiliary Pigeon Support
         */
        PositionGyro
    }
}