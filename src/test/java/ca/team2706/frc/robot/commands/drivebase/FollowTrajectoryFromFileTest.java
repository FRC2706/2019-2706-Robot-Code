package ca.team2706.frc.robot.commands.drivebase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FollowTrajectoryFromFileTest {
    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked
    private SmartDashboard smartDashboard;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Mocked
    private Notifier notifier;

    @Mocked
    private BufferedTrajectoryPointStream bufferedTrajectoryPointStream;

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Injectable
    private SensorCollection sensorCollection;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;
        }};

        new Expectations(ErrorCode.class) {{
            ErrorCode.worstOne((ErrorCode)any, (ErrorCode)any);
            result = ErrorCode.OK;
            minTimes = 0;
        }};

        Util.resetSubsystems();
    }

    /**
     * Tests trajectory from file
     */
    @Test
    public void testTrajectoryFromFile() throws IOException {
        Trajectory trajectory = new Trajectory(new Trajectory.Segment[]{
                new Trajectory.Segment(0.010, 0, 0, 0, 0, 2, 54, 0),
                new Trajectory.Segment(0.010, 0, 0, 0, 3, 2, 54, Pathfinder.d2r(345))
        });

        Trajectory trajectoryLeft = new Trajectory(new Trajectory.Segment[]{
                new Trajectory.Segment(0.010, 0, 0, 0, 0, 2, 54, 0),
                new Trajectory.Segment(0.010, 0, 0, 5, 3, 2, 54, Pathfinder.d2r(345))
        });

        Trajectory trajectoryRight = new Trajectory(new Trajectory.Segment[]{
                new Trajectory.Segment(0.010, 0, 0, 0, 0, 2, 54, 0),
                new Trajectory.Segment(0.010, 0, 0, 3, 3, 2, 54, Pathfinder.d2r(345))
        });

        new Expectations(Pathfinder.class) {{
            Pathfinder.readFromCSV((File) any);
            returns(trajectory, trajectoryLeft, trajectoryRight);
        }};

        FollowTrajectoryFromFile followTrajectoryFromFile = new FollowTrajectoryFromFile(0, 0, "ATest");

        followTrajectoryFromFile.initialize();

        new Verifications() {{
            List<TrajectoryPoint[]> trajectories = new ArrayList<>();
            bufferedTrajectoryPointStream.Write(withCapture(trajectories));

            assertEquals(15.0, trajectories.get(0)[1].headingDeg, 0.0);
            assertEquals(15.0, trajectories.get(1)[1].headingDeg, 0.0);

            assertTrue(trajectories.get(0)[1].position > trajectories.get(1)[1].position);
        }};
    }
}
