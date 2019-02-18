package ca.team2706.frc.robot.commands.bling;

import ca.team2706.frc.robot.commands.bling.patterns.BlingPattern;
import ca.team2706.frc.robot.subsystems.Bling;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BlingControllerTest {

    @Tested
    private BlingController blingController;

    /* Since DriverStation is accessed statically (it's a singleton), we have to mock all instances of
    it and stub out the static initializer */
    @Mocked(stubOutClassInitialization = true)
    private DriverStation station;

    @Before
    public void setUp() {
        Bling.init();
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
            returns(0D, 0D, 15D, 121D);
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

            station.isOperatorControl();
            result = true;
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

    /**
     * Tests to ensure that a matched pattern is only sent to the bling subsystem once.
     */
    @Test
    public void testSendingMatchedPatternOnce(@Mocked Bling bling) throws IllegalAccessException, NoSuchFieldException {
        final ArrayList<BlingController.Period> periods = new ArrayList<>();
        periods.add(BlingController.Period.CLIMB);
        periods.add(BlingController.Period.TELEOP_WITHOUT_CLIMB);
        periods.add(BlingController.Period.AUTONOMOUS);

        BlingPattern testPattern = new BlingPattern() {
            @Override
            public List<BlingController.Period> getPeriod() {
                return periods;
            }

            @Override
            public boolean conditionsMet() {
                return true;
            }
        };

        /*
        Java reflection to access the private commands field and set this command to be priority #1
        (probably not best practices, but I'd rather not change it).
        */
        Field field = blingController.getClass().getDeclaredField("commands");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") HashMap<BlingController.Period, ArrayList<BlingPattern>> commands = (HashMap<BlingController.Period, ArrayList<BlingPattern>>) field.get(blingController);
        commands.get(BlingController.Period.AUTONOMOUS).add(0, testPattern);
        commands.get(BlingController.Period.TELEOP_WITHOUT_CLIMB).add(0, testPattern);
        commands.get(BlingController.Period.CLIMB).add(0, testPattern);

        blingController.initialize(); // Initialize first, therefore simulating real behaviour.
        // Execute three times, should only call this method once.
        blingController.execute();
        blingController.execute();
        blingController.execute();

        new Verifications() {{
            bling.display(testPattern);
            times = 1;
        }};
    }

    /**
     * Tests to ensure that if multiple tests match their criteria, the one added first will be executed.
     */
    @Test
    public void testPatternsExecutedInOrderOfPriority(@Mocked Bling bling) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        new Expectations() {{
            station.isFMSAttached();
            result = true;

            station.isAutonomous();
            result = false;
        }};

        new Expectations(Timer.class) {{
            Timer.getMatchTime();
            result = 31D; // 31 seconds into the game, should be teleop
        }};

        final ArrayList<BlingController.Period> periods = new ArrayList<>();
        periods.add(BlingController.Period.TELEOP_WITHOUT_CLIMB);
        BlingPattern pattern1 = new BlingPattern() {
            @Override
            public ArrayList<BlingController.Period> getPeriod() {
                return periods;
            }

            @Override
            public boolean conditionsMet() {
                return false;
            }
        };
        BlingPattern pattern2 = new BlingPattern() {
            @Override
            public ArrayList<BlingController.Period> getPeriod() {
                return periods;
            }

            @Override
            public boolean conditionsMet() {
                return true;
            }
        };
        BlingPattern pattern3 = new BlingPattern() {
            @Override
            public ArrayList<BlingController.Period> getPeriod() {
                return periods;
            }

            @Override
            public boolean conditionsMet() {
                return true;
            }
        };

        // Now just need to access the private commands field and clear it.
        Field field = blingController.getClass().getDeclaredField("commands");
        field.setAccessible(true);
        @SuppressWarnings("unchecked") HashMap<BlingController.Period, ArrayList<BlingPattern>> commands = (HashMap<BlingController.Period, ArrayList<BlingPattern>>) field.get(blingController);

        commands.forEach((period, blingPatterns) -> blingPatterns.clear());

        Method method = blingController.getClass().getDeclaredMethod("add", BlingPattern.class);
        method.setAccessible(true);
        method.invoke(blingController, pattern1);
        method.invoke(blingController, pattern2);
        method.invoke(blingController, pattern3);

        blingController.initialize();
        blingController.execute();
        blingController.execute();
        blingController.execute();

        new Verifications() {{
            bling.display(pattern2);
            times = 1;
        }};
    }
}