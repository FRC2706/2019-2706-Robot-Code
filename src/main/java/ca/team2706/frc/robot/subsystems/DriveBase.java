package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Sendables;
import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.sensors.AnalogSelector;
import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Arrays;

/**
 * Subsystem that controls the driving of the robot as well as certain sensors that are used for driving
 */
public class DriveBase extends Subsystem {

    private static DriveBase currentInstance;

    public static DriveBase getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new drive base object.
     */
    public static SubsystemStatus init() {
        if (currentInstance == null) {
            currentInstance = new DriveBase();
        }

        return currentInstance.getStatus();
    }

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
     * Indicates whether the robot is in brake mode
     */
    private boolean brakeMode;

    /**
     * Saves the absolute heading when the gyro is reset so that it can be calculated from the relative angle
     */
    private double savedAngle;

    /**
     * Logs data to SmartDashboard and files periodically
     */
    private Notifier loggingNotifier;

    /**
     * Holds the status for the motion profile
     */
    private MotionProfileStatus motionProfileStatus;

    /**
     * Holds the points to stream for the right motion profile
     */
    private BufferedTrajectoryPointStream motionProfilePointStreamRight;

    /**
     * Holds the points to stream for the left motion profile
     */
    private BufferedTrajectoryPointStream motionProfilePointStreamLeft;

    private final SubsystemStatus status;

    /**
     * Creates a drive base, and initializes all required sensors and motors
     */
    private DriveBase() {
        leftFrontMotor = new WPI_TalonSRX(Config.LEFT_FRONT_DRIVE_MOTOR_ID);
        leftBackMotor = new WPI_TalonSRX(Config.LEFT_BACK_DRIVE_MOTOR_ID);
        rightFrontMotor = new WPI_TalonSRX(Config.RIGHT_FRONT_DRIVE_MOTOR_ID);
        rightBackMotor = new WPI_TalonSRX(Config.RIGHT_BACK_DRIVE_MOTOR_ID);

        gyro = new PigeonIMU(new TalonSRX(Config.GYRO_TALON_ID));

        status = resetTalonConfiguration();

        robotDriveBase = new DifferentialDrive(leftFrontMotor, rightFrontMotor);
        robotDriveBase.setRightSideInverted(false);
        robotDriveBase.setSafetyEnabled(false);

        selector = new AnalogSelector(Config.SELECTOR_ID);

        light = new PWM(Config.PURPLE_LIGHT);

        light.setRaw(4095);

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

        setBrakeMode(false);

        loggingNotifier = new Notifier(this::log);
        loggingNotifier.startPeriodic(Config.LOG_PERIOD);

        motionProfileStatus = new MotionProfileStatus();

        motionProfilePointStreamLeft = new BufferedTrajectoryPointStream();
        motionProfilePointStreamRight = new BufferedTrajectoryPointStream();

        resetAbsoluteGyro();
    }

    /**
     * Gets the subsystem's initialization status (status of sensors and systems).
     *
     * @return The subsystem's status
     */
    private SubsystemStatus getStatus() {
        return status;
    }

    /**
     * Whether there are errors in initialization that should disable autonomous
     *
     * @return Whether autonomous can run
     */
    private boolean canRunAuto() {
        return status == SubsystemStatus.OK || status == SubsystemStatus.WORKABLE;
    }

    /**
     * Resets the talon configuration back to the initial config.
     */
    private SubsystemStatus resetTalonConfiguration() {
        SubsystemStatus status = SubsystemStatus.OK;

        if (SubsystemStatus.checkError(leftFrontMotor.configFactoryDefault(Config.CAN_LONG))) {
            Log.e("Can't reset left front motor to factory default");
            status = SubsystemStatus.maxError(status, SubsystemStatus.ERROR);
        }

        if (SubsystemStatus.checkError(leftBackMotor.configFactoryDefault(Config.CAN_LONG))) {
            Log.e("Can't reset left back motor to factory default");
            status = SubsystemStatus.maxError(status, SubsystemStatus.ERROR);
        }

        if (SubsystemStatus.checkError(rightFrontMotor.configFactoryDefault(Config.CAN_LONG))) {
            Log.e("Can't reset right front motor to factory default");
            status = SubsystemStatus.maxError(status, SubsystemStatus.ERROR);
        }

        if (SubsystemStatus.checkError(rightBackMotor.configFactoryDefault(Config.CAN_LONG))) {
            Log.e("Can't reset right back motor to factory default");
            status = SubsystemStatus.maxError(status, SubsystemStatus.ERROR);
        }

        status = SubsystemStatus.maxError(status, configTalon(leftFrontMotor));
        status = SubsystemStatus.maxError(status, configTalon(rightFrontMotor));
        status = SubsystemStatus.maxError(status, configTalon(leftBackMotor));
        status = SubsystemStatus.maxError(status, configTalon(rightBackMotor));

        status = SubsystemStatus.maxError(status, setupFrontLeftMotor());
        status = SubsystemStatus.maxError(status, setupFrontRightMotor());
        status = SubsystemStatus.maxError(status, setupBackLeftMotor());
        status = SubsystemStatus.maxError(status, setupBackRightMotor());

        return status;
    }

    private SubsystemStatus configTalon(WPI_TalonSRX talon) {
        talon.configNeutralDeadband(Config.DRIVE_CLOSED_LOOP_DEADBAND.value(), Config.CAN_LONG);

        talon.configMotionSCurveStrength(Config.DRIVEBASE_MOTION_MAGIC_SMOOTHING.value(), Config.CAN_LONG);
        talon.configMotionCruiseVelocity((int) (Config.DRIVEBASE_MOTION_MAGIC_CRUISE_VELOCITY.value() / Config.DRIVE_ENCODER_DPP / 10), Config.CAN_LONG);
        talon.configMotionAcceleration((int) (Config.DRIVEBASE_MOTION_MAGIC_ACCELERATION.value() / Config.DRIVE_ENCODER_DPP / 10), Config.CAN_LONG);

        /* set the base trajectory period to zero, use the individual trajectory period */
        talon.configMotionProfileTrajectoryPeriod(0, Config.CAN_LONG);

        return SubsystemStatus.OK;
    }


    private SubsystemStatus setupFrontLeftMotor() {
        leftFrontMotor.setInverted(Config.INVERT_FRONT_LEFT_DRIVE);

        leftFrontMotor.configRemoteFeedbackFilter(gyro.getDeviceID(), RemoteSensorSource.GadgeteerPigeon_Yaw, 0, Config.CAN_LONG);

        leftFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor0, 0, Config.CAN_LONG);

        leftFrontMotor.configSelectedFeedbackCoefficient(1.0, 0, Config.CAN_LONG);

        leftFrontMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_LEFT.value());

        // TODO: Config timings

        leftFrontMotor.config_kP(0, Config.TURN_P.value(), Config.CAN_LONG);
        leftFrontMotor.config_kI(0, Config.TURN_I.value(), Config.CAN_LONG);
        leftFrontMotor.config_kD(0, Config.TURN_D.value(), Config.CAN_LONG);
        leftFrontMotor.config_kF(0, Config.TURN_F.value(), Config.CAN_LONG);

        leftFrontMotor.config_kP(1, Config.TURN_MM_P.value(), Config.CAN_LONG);
        leftFrontMotor.config_kI(1, Config.TURN_MM_I.value(), Config.CAN_LONG);
        leftFrontMotor.config_kD(1, Config.TURN_MM_D.value(), Config.CAN_LONG);
        leftFrontMotor.config_kF(1, Config.TURN_MM_F.value(), Config.CAN_LONG);

        leftFrontMotor.config_kP(3, Config.TURN_MM_P.value(), Config.CAN_LONG);
        leftFrontMotor.config_kI(3, Config.TURN_MM_I.value(), Config.CAN_LONG);
        leftFrontMotor.config_kD(3, Config.TURN_MM_D.value(), Config.CAN_LONG);
        leftFrontMotor.config_kF(3, Config.TURN_MM_F.value(), Config.CAN_LONG);

        leftFrontMotor.configClosedLoopPeriod(0, 1, Config.CAN_LONG);
        leftFrontMotor.configClosedLoopPeriod(1, 1, Config.CAN_LONG);
        leftFrontMotor.configClosedLoopPeriod(3, 1, Config.CAN_LONG);

        leftFrontMotor.configAuxPIDPolarity(true, Config.CAN_LONG);

        return SubsystemStatus.OK;
    }

    private SubsystemStatus setupFrontRightMotor() {
        leftFrontMotor.setInverted(Config.INVERT_FRONT_RIGHT_DRIVE);

        rightFrontMotor.configRemoteFeedbackFilter(leftFrontMotor.getDeviceID(), RemoteSensorSource.TalonSRX_SelectedSensor, 0, Config.CAN_LONG);
        rightFrontMotor.configRemoteFeedbackFilter(gyro.getDeviceID(), RemoteSensorSource.GadgeteerPigeon_Yaw, 1, Config.CAN_LONG);

        rightFrontMotor.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0, Config.CAN_LONG);
        rightFrontMotor.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative, Config.CAN_LONG);

        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, 0, Config.CAN_LONG);
        rightFrontMotor.configSelectedFeedbackCoefficient(0.5, 0, Config.CAN_LONG);

        rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, 1, Config.CAN_LONG);

        rightFrontMotor.configSelectedFeedbackCoefficient(1, 1, Config.CAN_LONG);

        // TODO: Config timings

        rightFrontMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_RIGHT.value());

        rightFrontMotor.config_kP(0, Config.DRIVE_CLOSED_LOOP_P.value(), Config.CAN_LONG);
        rightFrontMotor.config_kI(0, Config.DRIVE_CLOSED_LOOP_I.value(), Config.CAN_LONG);
        rightFrontMotor.config_kD(0, Config.DRIVE_CLOSED_LOOP_D.value(), Config.CAN_LONG);
        rightFrontMotor.config_kF(0, Config.DRIVE_CLOSED_LOOP_F.value(), Config.CAN_LONG);

        rightFrontMotor.config_kP(1, Config.DRIVE_MOTION_MAGIC_P.value(), Config.CAN_LONG);
        rightFrontMotor.config_kI(1, Config.DRIVE_MOTION_MAGIC_I.value(), Config.CAN_LONG);
        rightFrontMotor.config_kD(1, Config.DRIVE_MOTION_MAGIC_D.value(), Config.CAN_LONG);
        rightFrontMotor.config_kF(1, Config.DRIVE_MOTION_MAGIC_F.value(), Config.CAN_LONG);

        rightFrontMotor.config_kP(2, Config.PIGEON_KP.value(), Config.CAN_LONG);
        rightFrontMotor.config_kI(2, Config.PIGEON_KI.value(), Config.CAN_LONG);
        rightFrontMotor.config_kD(2, Config.PIGEON_KD.value(), Config.CAN_LONG);
        rightFrontMotor.config_kF(2, Config.PIGEON_KF.value(), Config.CAN_LONG);

        rightFrontMotor.configClosedLoopPeriod(0, 1, Config.CAN_LONG);
        rightFrontMotor.configClosedLoopPeriod(1, 1, Config.CAN_LONG);
        rightFrontMotor.configClosedLoopPeriod(2, 1, Config.CAN_LONG);
        rightFrontMotor.configAuxPIDPolarity(false, Config.CAN_LONG);

        return SubsystemStatus.OK;
    }

    private SubsystemStatus setupBackLeftMotor() {
        leftFrontMotor.setInverted(Config.INVERT_BACK_LEFT_DRIVE);

        leftBackMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_LONG);

        leftBackMotor.configRemoteFeedbackFilter(gyro.getDeviceID(), RemoteSensorSource.GadgeteerPigeon_Yaw, 1, Config.CAN_LONG);

        leftBackMotor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, 1, Config.CAN_LONG);

        leftBackMotor.configSelectedFeedbackCoefficient(1, 1, Config.CAN_LONG);
        leftBackMotor.configSelectedFeedbackCoefficient(1, 0, Config.CAN_LONG);

        leftBackMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_LEFT.value());
        // TODO: Config timings

        leftBackMotor.config_kP(0, Config.DRIVE_CLOSED_LOOP_P.value());
        leftBackMotor.config_kI(0, Config.DRIVE_CLOSED_LOOP_I.value());
        leftBackMotor.config_kD(0, Config.DRIVE_CLOSED_LOOP_D.value());
        leftBackMotor.config_kF(1, Config.DRIVE_MOTION_MAGIC_F.value());

        leftBackMotor.config_kP(1, Config.DRIVE_MOTION_MAGIC_P.value());
        leftBackMotor.config_kI(1, Config.DRIVE_MOTION_MAGIC_I.value());
        leftBackMotor.config_kD(1, Config.DRIVE_MOTION_MAGIC_D.value());
        leftBackMotor.config_kF(1, Config.DRIVE_MOTION_MAGIC_F.value());

        leftBackMotor.config_kP(2, Config.PIGEON_KP.value());
        leftBackMotor.config_kI(2, Config.PIGEON_KI.value());
        leftBackMotor.config_kD(2, Config.PIGEON_KD.value());
        leftBackMotor.config_kF(2, Config.PIGEON_KF.value());

        leftBackMotor.configClosedLoopPeriod(0, 1, Config.CAN_LONG);
        leftBackMotor.configClosedLoopPeriod(1, 1, Config.CAN_LONG);
        leftBackMotor.configClosedLoopPeriod(2, 1, Config.CAN_LONG);
        leftBackMotor.configAuxPIDPolarity(true, Config.CAN_LONG);

        return SubsystemStatus.OK;
    }

    private SubsystemStatus setupBackRightMotor() {
        leftFrontMotor.setInverted(Config.INVERT_BACK_RIGHT_DRIVE);

        rightBackMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Config.CAN_LONG);

        rightBackMotor.configRemoteFeedbackFilter(gyro.getDeviceID(), RemoteSensorSource.GadgeteerPigeon_Yaw, 1, Config.CAN_LONG);

        rightBackMotor.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, 1, Config.CAN_LONG);

        rightBackMotor.configSelectedFeedbackCoefficient(1, 1, Config.CAN_LONG);
        rightBackMotor.configSelectedFeedbackCoefficient(1, 0, Config.CAN_LONG);

        rightBackMotor.setSensorPhase(Config.DRIVE_SUM_PHASE_RIGHT.value());

        // TODO: Config timings

        rightBackMotor.config_kP(0, Config.DRIVE_CLOSED_LOOP_P.value());
        rightBackMotor.config_kI(0, Config.DRIVE_CLOSED_LOOP_I.value());
        rightBackMotor.config_kD(0, Config.DRIVE_CLOSED_LOOP_D.value());
        rightBackMotor.config_kF(0, Config.DRIVE_CLOSED_LOOP_F.value());

        rightBackMotor.config_kP(1, Config.DRIVE_MOTION_MAGIC_P.value());
        rightBackMotor.config_kI(1, Config.DRIVE_MOTION_MAGIC_I.value());
        rightBackMotor.config_kD(1, Config.DRIVE_MOTION_MAGIC_D.value());
        rightBackMotor.config_kF(1, Config.DRIVE_MOTION_MAGIC_F.value());

        rightBackMotor.config_kP(2, Config.PIGEON_KP.value());
        rightBackMotor.config_kI(2, Config.PIGEON_KI.value());
        rightBackMotor.config_kD(2, Config.PIGEON_KD.value());
        rightBackMotor.config_kF(2, Config.PIGEON_KF.value());

        rightBackMotor.configClosedLoopPeriod(0, 1, Config.CAN_LONG);
        rightBackMotor.configClosedLoopPeriod(1, 1, Config.CAN_LONG);
        rightBackMotor.configClosedLoopPeriod(2, 1, Config.CAN_LONG);
        rightBackMotor.configAuxPIDPolarity(false, Config.CAN_LONG);

        return SubsystemStatus.OK;
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
     * Stops the robot
     */
    public void stop() {
        leftFrontMotor.stopMotor();
        rightFrontMotor.stopMotor();
        leftBackMotor.stopMotor();
        rightFrontMotor.stopMotor();
    }

    @Override
    protected void initDefaultCommand() {
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
        robotDriveBase.tankDrive(leftSpeed, rightSpeed, squaredInputs);

        leftBackMotor.follow(leftFrontMotor);
        rightBackMotor.follow(rightFrontMotor);
    }

    /**
     * Drives the robot by controlling the forward and rotation values
     *
     * @param forwardSpeed  The amount to drive forward
     * @param rotateSpeed   The amount to rotate
     * @param squaredInputs Whether to square each of the values
     */
    public void arcadeDrive(double forwardSpeed, double rotateSpeed, boolean squaredInputs) {
        robotDriveBase.arcadeDrive(forwardSpeed, rotateSpeed, squaredInputs);

        leftBackMotor.follow(leftFrontMotor, FollowerType.PercentOutput);
        rightBackMotor.follow(rightFrontMotor);
    }

    /**
     * Drives the robot by controlling the forward and amount of curve values
     *
     * @param forwardSpeed The amount to drive forward
     * @param curveSpeed   The amount that the robot should curve while driving
     * @param override     When true will only use rotation values
     */
    public void curvatureDrive(double forwardSpeed, double curveSpeed, boolean override) {
        robotDriveBase.curvatureDrive(forwardSpeed, curveSpeed, override);

        leftBackMotor.follow(leftFrontMotor);
        rightBackMotor.follow(rightFrontMotor);
    }

    /**
     * Goes to a position with the closed loop Talon PIDs using only encoder information
     *
     * @param speed    The speed from 0 to 1
     * @param setpoint The setpoint to go to in feet
     */
    public void setPositionNoGyro(double speed, double setpoint) {
        if (canRunAuto()) {
            rightFrontMotor.selectProfileSlot(0, 0);
            rightFrontMotor.set(ControlMode.Position, setpoint / Config.DRIVE_ENCODER_DPP);

            leftFrontMotor.follow(rightFrontMotor);
            leftBackMotor.follow(rightFrontMotor);
            rightBackMotor.follow(rightFrontMotor);
        } else {
            stop();
        }
    }

    /**
     * Follows motion magic profile
     *
     * @param speed          The speed from 0 to 1
     * @param setpoint       The setpoint in feet
     * @param targetRotation The target to rotate to
     */
    public void setMotionMagicPositionGyro(double speed, double setpoint, double targetRotation) {
        if (canRunAuto()) {
            rightFrontMotor.selectProfileSlot(1, 0);
            rightFrontMotor.selectProfileSlot(2, 1);
            rightFrontMotor.set(ControlMode.MotionMagic, setpoint / Config.DRIVE_ENCODER_DPP, DemandType.AuxPID, targetRotation / Config.PIGEON_DPP);

            leftFrontMotor.follow(rightFrontMotor, FollowerType.AuxOutput1);
            leftBackMotor.follow(leftFrontMotor);
            rightBackMotor.follow(rightFrontMotor);
        } else {
            stop();
        }
    }

    /**
     * Runs the motion profile
     *
     * @param speed The speed from 0 to 1
     */
    public void runMotionProfile(final double speed) {
        if (canRunAuto()) {
            rightFrontMotor.selectProfileSlot(1, 0);
            rightFrontMotor.selectProfileSlot(2, 1);

            rightFrontMotor.feed();

            leftFrontMotor.follow(rightFrontMotor, FollowerType.AuxOutput1);
            leftBackMotor.follow(leftFrontMotor);
            rightBackMotor.follow(rightFrontMotor);
        } else {
            stop();
        }
    }

    /**
     * Runs the motion profile for each wheel
     *
     * @param speed The speed from 0 to 1
     */
    public void runMotionProfile2Wheel(final double speed) {
        if (canRunAuto()) {
            leftBackMotor.selectProfileSlot(1, 0);
            leftBackMotor.selectProfileSlot(2, 1);

            rightBackMotor.selectProfileSlot(1, 0);
            rightBackMotor.selectProfileSlot(2, 1);

            leftBackMotor.feed();
            rightBackMotor.feed();

            leftFrontMotor.follow(leftBackMotor);
            rightFrontMotor.follow(rightBackMotor);
        } else {
            stop();
        }
    }

    /**
     * Applies the motion profile
     *
     * @param pos         The position of the robot at a trajectory point
     * @param vel         The velocity of the robot at a trajectory point
     * @param heading     The heading of the robot at a trajectory point
     * @param time        The time for each trajectory point
     * @param size        How many trajectories there are
     * @param talon       The talon
     * @param pointStream The point stream
     */
    private void pushMotionProfile(double[] pos, double[] vel, double[] heading, int[] time, int size, WPI_TalonSRX talon, BufferedTrajectoryPointStream pointStream) {
        /* create an empty point */
        TrajectoryPoint[] points = new TrajectoryPoint[size];

        /*
         * just in case we are interrupting another MP and there is still buffer
         * points in memory, clear it.
         */
        talon.clearMotionProfileTrajectories();
        pointStream.Clear();

        /* This is fast since it's just into our TOP buffer */
        for (int i = 0; i < size; ++i) {
            points[i] = new TrajectoryPoint();
            /* for each point, fill our structure and pass it to API */
            points[i].position = pos[i] / Config.DRIVE_ENCODER_DPP;
            points[i].velocity = vel[i] / Config.DRIVE_ENCODER_DPP / 10;
            points[i].auxiliaryPos = heading[i] / Config.PIGEON_DPP; /* scaled such that 3600 => 360 deg */
            points[i].headingDeg = heading[i];
            points[i].profileSlotSelect0 = 0;
            points[i].profileSlotSelect1 = 1;
            points[i].timeDur = time[i];
            points[i].zeroPos = i == 0;
            points[i].useAuxPID = true;
            points[i].auxiliaryArbFeedFwd = Config.CURVE_ADJUSTMENT.value() * vel[i] / Config.DRIVEBASE_MOTION_MAGIC_CRUISE_VELOCITY.value();

            points[i].isLastPoint = (i + 1) == size;
        }

        pointStream.Write(points);
    }

    /**
     * Applies the motion profile for 1 wheel
     *
     * @param forwards Whether the robot is going forwards or not
     * @param pos      The position of the robot at a trajectory point
     * @param vel      The velocity of the robot at a trajectory point
     * @param heading  The heading of the robot at a trajectory point
     * @param time     The time for each trajectory point
     * @param size     How many trajectory points there are
     */
    public void pushMotionProfile1Wheel(boolean forwards, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        pushMotionProfile(forwards ? pos : negateDoubleArray(pos), forwards ? vel : negateDoubleArray(vel), heading, time, size, rightFrontMotor, motionProfilePointStreamRight);

        rightFrontMotor.startMotionProfile(motionProfilePointStreamRight, 20, ControlMode.MotionProfileArc);
    }

    /**
     * Applies the motion profile for 2 wheels
     *
     * @param forwards Whether the robot is going forwards or not
     * @param posLeft  The position of the robot at a trajectory point for the left wheel
     * @param velLeft  The velocity of the robot at a trajectory point for the left wheel
     * @param heading  The heading of the robot at a trajectory point
     * @param time     The time for each trajectory point
     * @param size     How many trajectory points there are
     * @param posRight The position of the robot at a trajectory point for the right wheel
     * @param velRight The velocity of the robot at a trajectory point for the right wheel
     */
    public void pushMotionProfile2Wheel(boolean forwards, double[] posLeft, double[] velLeft, double[] heading, int[] time, int size, double[] posRight, double[] velRight) {
        pushMotionProfile(forwards ? posLeft : negateDoubleArray(posLeft), forwards ? velLeft : negateDoubleArray(velLeft), heading, time, size, leftBackMotor, motionProfilePointStreamLeft);
        pushMotionProfile(forwards ? posRight : negateDoubleArray(posRight), forwards ? velRight : negateDoubleArray(velRight), heading, time, size, rightBackMotor, motionProfilePointStreamRight);

        leftBackMotor.startMotionProfile(motionProfilePointStreamRight, 20, ControlMode.MotionProfileArc);
        rightBackMotor.startMotionProfile(motionProfilePointStreamRight, 20, ControlMode.MotionProfileArc);
    }

    /**
     * Makes all the elements in the double array negative
     *
     * @param array The array that is negated
     * @return The array
     */
    public static double[] negateDoubleArray(final double[] array) {
        return Arrays.stream(array)
                .map(operand -> -operand)
                .toArray();
    }

    /*
     * Sets the amount that the robot has to rotate.
     *
     * @param speed    The speed of the rotation.
     * @param setpoint The setpoint (angle) to which the robot should rotate, in degrees.
     */
    public void setRotation(double speed, double setpoint) {
        if (canRunAuto()) {
            leftFrontMotor.selectProfileSlot(3, 0);
            leftFrontMotor.selectProfileSlot(0, 1);

            leftFrontMotor.set(ControlMode.Position, 0.0, DemandType.AuxPID, setpoint / Config.PIGEON_DPP);
            rightFrontMotor.follow(leftFrontMotor, FollowerType.AuxOutput1);

            leftBackMotor.follow(leftFrontMotor);
            rightBackMotor.follow(rightFrontMotor);
        } else {
            stop();
        }
    }

    /**
     * Goes to a position with the closed loop Talon PIDs using only encoder and gyro
     *
     * @param speed          The speed from 0 to 1
     * @param setpoint       The setpoint to go to in feet
     * @param targetRotation The desired rotation
     */
    public void setPositionGyro(double speed, double setpoint, double targetRotation) {
        if (canRunAuto()) {
            rightFrontMotor.selectProfileSlot(0, 0);
            rightFrontMotor.selectProfileSlot(2, 1);

            rightFrontMotor.set(ControlMode.Position, setpoint / Config.DRIVE_ENCODER_DPP, DemandType.AuxPID, targetRotation);

            leftFrontMotor.follow(rightFrontMotor, FollowerType.AuxOutput1);
            leftBackMotor.follow(leftFrontMotor);
            rightBackMotor.follow(rightFrontMotor);
        } else {
            stop();
        }
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
     * Gets the angle that the robot is facing relative to when it was first powered on
     *
     * @return The absolute rotation of the robot in degrees
     */
    public double getAbsoluteHeading() {
        return savedAngle + getHeading();
    }

    /**
     * Resets the encoder values to 0 ticks
     */
    public void resetEncoders() {
        leftFrontMotor.getSensorCollection().setQuadraturePosition(0, Config.CAN_SHORT);
        rightFrontMotor.getSensorCollection().setQuadraturePosition(0, Config.CAN_SHORT);
    }

    /**
     * Perform initializations so that the absolute gyro positon returned by
     * the getAbsoluteHeading() method is equal to the angle specified by
     * Config.ROBOT_START_ANGLE. The gyro measurement device will be reset to
     * 0 degrees.
     */
    public void resetAbsoluteGyro() {
        resetAbsoluteGyro(Config.ROBOT_START_ANGLE.value());
    }

    /**
     * Resets the absolute gyro to a certain angle
     *
     * @param savedAngle The angle from 0 to 360
     */
    public void resetAbsoluteGyro(double savedAngle) {
        this.savedAngle = savedAngle;
        gyro.setYaw(0, Config.CAN_SHORT);
    }

    /**
     * Resets the gyro to 0 degrees
     */
    public void resetGyro() {
        savedAngle = getAbsoluteHeading();
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

    /**
     * Gets the current error on the pigeon (how far off target it is).
     *
     * @return Pigeon error in degrees.
     */
    public double getPigeonError() {
        return rightFrontMotor.getClosedLoopError(0) * Config.PIGEON_DPP;
    }


    /**
     * Returns if the motion profile for 1 wheel is finished
     */
    public boolean isFinishedMotionProfile() {
        return rightFrontMotor.isMotionProfileFinished();
    }

    /**
     * Returns if the motion profile for 2 wheels is finished
     *
     * @return If its finished or not
     */
    public boolean isFinishedMotionProfile2Wheel() {
        return (rightFrontMotor.isMotionProfileFinished() || leftFrontMotor.isMotionProfileFinished());
    }

    /**
     * Logs
     */
    public void log() {
        if (DriverStation.getInstance().isEnabled()) {
            Log.d("Relative Gyro: " + getHeading());
            Log.d("Absolute Gyro: " + getAbsoluteHeading());

            Log.d("Left front motor temperature: " + leftFrontMotor.getTemperature());
            Log.d("Right front motor temperature: " + rightFrontMotor.getTemperature());
            Log.d("Left back motor temperature: " + leftBackMotor.getTemperature());
            Log.d("Right back motor temperature: " + rightBackMotor.getTemperature());

            Log.d("Left front motor distance: " + leftFrontMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);
            Log.d("Right front motor distance: " + rightFrontMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);
            Log.d("Left back motor distance: " + leftBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);
            Log.d("Right back motor distance: " + rightBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP);

            Log.d("Left front motor speed: " + leftFrontMotor.getSensorCollection().getQuadratureVelocity() / Config.DRIVE_ENCODER_DPP * 10);
            Log.d("Right front motor speed: " + rightFrontMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP * 10);
            Log.d("Left back motor speed: " + leftBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP * 10);
            Log.d("Right back motor speed: " + rightBackMotor.getSensorCollection().getQuadraturePosition() / Config.DRIVE_ENCODER_DPP * 10);
        }

        SmartDashboard.putNumber("Relative Gyro", getHeading());
        SmartDashboard.putNumber("Absolute Gyro", getAbsoluteHeading());

        SmartDashboard.putNumber("Left front motor temp", leftFrontMotor.getTemperature());
        SmartDashboard.putNumber("Right front motor temp", rightFrontMotor.getTemperature());
        SmartDashboard.putNumber("Left back motor temp", leftBackMotor.getTemperature());
        SmartDashboard.putNumber("Right back motor temp", rightBackMotor.getTemperature());

        SmartDashboard.putNumber("Left front motor distance: ", leftFrontMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);
        SmartDashboard.putNumber("Right front motor distance: ", rightFrontMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);
        SmartDashboard.putNumber("Left back motor distance: ", leftBackMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);
        SmartDashboard.putNumber("Right back motor distance: ", rightBackMotor.getSensorCollection().getQuadraturePosition() * Config.DRIVE_ENCODER_DPP);

        SmartDashboard.putNumber("Left front motor speed", leftFrontMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
        SmartDashboard.putNumber("Right front motor speed", rightFrontMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
        SmartDashboard.putNumber("Left back motor speed", leftBackMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
        SmartDashboard.putNumber("Right back motor speed", rightBackMotor.getSensorCollection().getQuadratureVelocity() * Config.DRIVE_ENCODER_DPP * 10);
    }
}

