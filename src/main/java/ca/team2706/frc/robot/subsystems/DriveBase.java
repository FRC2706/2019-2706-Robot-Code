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

public class DriveBase extends Subsystem {

    private final WPI_TalonSRX leftFrontMotor, leftBackMotor, rightFrontMotor, rightBackMotor;
    private final DifferentialDrive robotDriveBase;

    private final PigeonIMU gyro;
    private final TalonEncoder leftEncoder, rightEncoder;

    public DriveBase() {
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
    protected void initDefaultCommand() {
        // TODO: Initialize arcade drive command
    }

    public void setBrakeMode(boolean brake) {
        NeutralMode mode = brake ? NeutralMode.Brake : NeutralMode.Coast;

        leftFrontMotor.setNeutralMode(mode);
        leftBackMotor.setNeutralMode(mode);
        rightFrontMotor.setNeutralMode(mode);
        rightBackMotor.setNeutralMode(mode);
    }

    public void tankDrive(double leftSpeed, double rightSpeed, boolean squaredInputs) {
        robotDriveBase.tankDrive(leftSpeed, rightSpeed, squaredInputs);
    }

    public void arcadeDrive(double forwardSpeed, double rotateSpeed, boolean squaredInputs) {
        robotDriveBase.arcadeDrive(forwardSpeed, rotateSpeed, squaredInputs);
    }

    public void curvatureDrive(double forwardSpeed, double curveSpeed, boolean override) {
        robotDriveBase.curvatureDrive(forwardSpeed, curveSpeed, override);
    }

    public double getLeftDistance() {
        return leftEncoder.getDistance();
    }

    public double getRightDistance() {
        return rightEncoder.getDistance();
    }

    public double getLeftSpeed() {
        return leftEncoder.getRate();
    }

    public double getRightSpeed() {
        return rightEncoder.getRate();
    }

    public double getHeading() {
        return gyro.getFusedHeading();
    }

    public void resetEncoders() {
        leftEncoder.reset();
        rightEncoder.reset();
    }

    public void resetGyro() {
        gyro.setFusedHeading(0, Config.CAN_SHORT);
    }

    public void reset() {
        resetEncoders();
        resetGyro();
    }
}
