package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.SendablesTest;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import static org.junit.Assert.assertEquals;

public class DriverAssistVisionTest {

    @Tested
    private DriverAssistVision driverAssistVision;

    DriveBase driveBase = DriveBase.getInstance();

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Mocked
    private Notifier notifier;

    /**
     * Runs various test for generating a trajectory to targets on the cargo ship or 
     * the loading bay. For each test, the same vision input is used but the heading
     * of the robot in the field frame (referred to as "absolute heading" in the code)
     * is changed.
     */
    @Test
    public void testGenerateTrajectoryCargoShipAndLoading() {
        new Expectations(Config.class) {{
            Config.getTRAJ_DELTA_TIME(); result = 0.2;
            Config.getROBOTTOCAMERA_ROBOTX(); result = 1.0;
            Config.getROBOTTOCAMERA_ROBOTY(); result = 1.0;
            Config.getTARGET_OFFSET_DISTANCE(); result = 0.5;
        }};

        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(0.0),
                    SendablesTest.makePigeonExpectation(90.0),
                    SendablesTest.makePigeonExpectation(180.0));
        }};

        // Run the test
        double distanceCameraToTarget_Camera = 6.0*1.414213562373095;
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = true;
        boolean driverAssistRocket = false;

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double[] expectedAngRobotHeadingFinal_Field = {0.0, 90.0, 180.0, 270.0};
        for (int i = 0; i < 4; i++) {
            driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
                driverAssistCargoAndLoading, driverAssistRocket);
            Trajectory traj = driverAssistVision.getTraj();

            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
            assertEquals(traj.segments[traj.length()-1].x, -5.0, 0.3);
            assertEquals(traj.segments[traj.length()-1].y, 6.5, 0.3);
            assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
        }
    }

    /**
     * Runs various test for generating a trajectory to targets on the rocket. For each test, 
     * the same vision input is used but the heading of the robot in the field frame (referred 
     * to as "absolute heading" in the code) is changed.
     */
    @Test
    public void testGenerateTrajectoryRocket() {
        new Expectations(Config.class) {{
            Config.getTRAJ_DELTA_TIME(); result = 0.2;
            Config.getROBOTTOCAMERA_ROBOTX(); result = 1.0;
            Config.getROBOTTOCAMERA_ROBOTY(); result = 1.0;
            Config.getTARGET_OFFSET_DISTANCE(); result = 0.5;
        }};

        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);
            returns(SendablesTest.makePigeonExpectation(-30.0),
                    SendablesTest.makePigeonExpectation(-90.0),
                    SendablesTest.makePigeonExpectation(210.0),
                    SendablesTest.makePigeonExpectation(30.0),
                    SendablesTest.makePigeonExpectation(90.0),
                    SendablesTest.makePigeonExpectation(150.0));
        }};
        
        // Run the test
        double distanceCameraToTarget_Camera = 6.0*1.414213562373095;
        double yawAngleCameraToTarget_Camera = -45.0;
        boolean driverAssistCargoAndLoading = false;
        boolean driverAssistRocket = true;

        // Verify the values produced with expected values. Note that the generated trajectory
        // is compared against the expected trajectory by comparing the expected x, y, and heading
        // against those of the generated trajectory for the first and last segments.
        double[] expectedAngRobotHeadingFinal_Field = {60.0, 0.0, 300.0, 120.0, 180.0, 240.0};
        for (int i = 0; i < 6; i++) {
            driverAssistVision.generateTrajectoryRobotToTarget(distanceCameraToTarget_Camera, yawAngleCameraToTarget_Camera,
                driverAssistCargoAndLoading, driverAssistRocket);
            Trajectory traj = driverAssistVision.getTraj();

            assertEquals(driverAssistVision.getAngRobotHeadingFinal_Field(), expectedAngRobotHeadingFinal_Field[i], 0.1);
            assertEquals(traj.segments[0].y, 0.0, 0.3);
            assertEquals(traj.segments[0].heading, Pathfinder.d2r(90.0), 0.3);
            assertEquals(traj.segments[traj.length()-1].x, -5.0, 0.3);
            assertEquals(traj.segments[traj.length()-1].y, 6.5, 0.3);
            assertEquals(traj.segments[traj.length()-1].heading, Pathfinder.d2r(90.0), 0.3);
        }
    }
}