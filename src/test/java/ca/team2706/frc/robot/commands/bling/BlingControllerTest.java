package ca.team2706.frc.robot.commands.bling;

import ca.team2706.frc.robot.subsystems.Bling;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlingControllerTest {

    @Tested
    private BlingController blingController;

    /* Since DriverStation is accessed statically (it's a singleton), we have to mock all instances of
    it and stub out the static initializer */
    @Mocked(stubOutClassInitialization=true)
    private DriverStation station;

    @Before
    public void setUp() {
        blingController = new BlingController();
    }

    /**
     * Tests getting the robot's operation period while not in a real match.
     */
    @Test
    public void testGetOperationPeriodDuringRealMatch() {
        new Expectations() {{
            station.isFMSAttached();
            result = true;

            station.isAutonomous();
            returns(true, false);
        }};

        new Expectations(Timer.class) {{
            Timer.getMatchTime();
            returns(15D, 135D, 30D, 29D, 0D);
        }};

        // Need to initialize the controller.
        blingController.initialize();


        assertEquals(BlingController.Period.AUTONOMOUS, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.TELEOP_WITHOUT_CLIMB, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.TELEOP_WITHOUT_CLIMB, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.CLIMB, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.CLIMB, blingController.getCurrentPeriod());
    }

    /**
     * Tests getting the robot's operation period while not in a real match.
     */
    @Test
    public void testGetOperationPeriodDuringPractice() {
        new Expectations() {{
            station.isFMSAttached();
            result = false;

            station.isAutonomous();
            returns(true, false);
        }};

        new Expectations(Timer.class) {{
           Timer.getFPGATimestamp();
           // First one is to get the start time.
           returns(0D, 0D, 15D, 120D);
        }};

        blingController.initialize();

        assertEquals(BlingController.Period.AUTONOMOUS, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.TELEOP_WITHOUT_CLIMB, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.CLIMB, blingController.getCurrentPeriod());
    }

    /**
     * Tests starting the robot in teleop and having the bling detect the period properly.
     */
    @Test
    public void testGetOperationPeriodStartingInTeleop() {
        new Expectations() {{
            station.isFMSAttached();
            result = false;

            station.isAutonomous();
            result = false;
        }};

        new Expectations(Timer.class) {{
            Timer.getFPGATimestamp();
            // First one is start time, next one is teleop time, next one is climb.
            returns(0D, 0D, 106D);
        }};

        blingController.initialize();

        assertEquals(BlingController.Period.TELEOP_WITHOUT_CLIMB, blingController.getCurrentPeriod());
        assertEquals(BlingController.Period.CLIMB, blingController.getCurrentPeriod());
    }
}