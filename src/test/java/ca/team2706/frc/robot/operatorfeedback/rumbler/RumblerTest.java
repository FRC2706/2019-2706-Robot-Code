package ca.team2706.frc.robot.operatorfeedback.rumbler;

import ca.team2706.frc.robot.OI;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class RumblerTest {
    @Injectable
    private Joystick driverStick;

    @Injectable
    private Joystick operatorStick;

    @Mocked
    private OI oi;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Before
    public void setUp() {
        new Expectations() {{
            oi.getDriverStick();
            result = driverStick;
            minTimes = 0;

            oi.getControlStick();
            result = operatorStick;
            minTimes = 0;
        }};
    }

    /**
     * Tests to ensure that the rumbler rumbles when the pattern it has wants it to.
     */
    @Test
    public void testRumbles() {
        RumblePattern pattern = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        rumbler.execute();

        assertTrue(rumbler.isRumbling());

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0));
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0));

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0));
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0));
        }};
    }

    /**
     * Tests to ensure that the rumbler turns off when asked to do so by the pattern.
     */
    @Test
    public void testStopsRumble() {
        RumblePattern pattern = new RumblePattern() {

            int times = 0;

            @Override
            boolean shouldRumble(long millisecondsOn) {
                times++;
                return times <= 1; // First time it should rumble, second time it should stop.
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        runRumbler(rumbler, 2);

        assertFalse(rumbler.isRumbling());

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
        }};
    }

    /**
     * Ensures that when the rumbler is ended the controller is told to stop rumbling.
     */
    @Test
    public void testRumbleStopsOnEnd() {
        RumblePattern pattern = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        rumbler.execute();
        assertTrue(rumbler.isRumbling());
        rumbler.end();
        assertFalse(rumbler.isRumbling());

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
        }};
    }

    @Test
    public void testPatternEnd() {
        RumblePattern pattern = new RumblePattern() {
            int times = 0;

            @Override
            boolean shouldRumble(long millisecondsOn) {
                times++;
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return times >= 2; // Will say the pattern is over after 4 seconds.
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        rumbler.execute();
        assertTrue(rumbler.isRumbling());
        rumbler.execute();
        assertTrue(rumbler.isRumbling());
        assertTrue(rumbler.isFinished());

        rumbler.end();

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
        }};
    }

    @Test
    public void testRightControllerIsRumbled() {
        RumblePattern pattern1 = new RumblePattern() {
            int times = 0;

            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.DRIVER_JOYSTICK;
            }
        };
        RumblePattern pattern2 = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.OPERATOR_JOYSTICK;
            }
        };

        Rumbler rumbler1 = new Rumbler(pattern1);
        rumbler1.initialize();
        rumbler1.execute();
        assertTrue(rumbler1.isRumbling());

        Rumbler rumbler2 = new Rumbler(pattern2);
        rumbler2.initialize();
        rumbler2.execute();
        assertTrue(rumbler2.isRumbling());

        new VerificationsInOrder() {{
            driverStick.setRumble((GenericHID.RumbleType) any, withNotEqual(0));
            times = 2;

            operatorStick.setRumble((GenericHID.RumbleType) any, withNotEqual(0));
            times = 2;
        }};
    }

    @Test
    public void testRealisticPattern() {
        new MockUp<System>() {
            @Mock
            public long currentTimeMillis() {
                return 0;
            }
        };

//        new Expectations() {{
//            System.currentTimeMillis();
//            returns(0, 50, 100, 150, 200, 300, 500, 550, 590, 700, 800, 2000);
//            times = 12;
//        }};

        RumblePattern pattern = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return millisecondsOn % 200 < 150;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return millisecondsOn >= 2000;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.start();
        rumbler.initialize();

        runRumbler(rumbler, 2);
        assertTrue(rumbler.isRumbling());
        runRumbler(rumbler, 1);
        assertFalse(rumbler.isRumbling());
        runRumbler(rumbler, 3);
        assertTrue(rumbler.isRumbling());
        runRumbler(rumbler, 2);
        assertFalse(rumbler.isRumbling());
        runRumbler(rumbler, 3);
        assertTrue(rumbler.isRumbling());

        assertTrue(rumbler.isFinished());

        rumbler.end();

        new Verifications() {{
            // Verifications for turning on.
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0));
            times = 3;
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0));
            times = 3;

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0));
            times = 3;
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0));
            times = 3;


            // Verifications for turning off.
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            times = 3;
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
            times = 3;

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            times = 3;
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
            times = 3;
        }};
    }

    /**
     * Runs the {@link Rumbler#execute()} method <code>numTimes</code> number of times.
     *
     * @param rumbler  The rumbler class to be run.
     * @param numTimes The number of times to call the execute method.
     */
    private static void runRumbler(Rumbler rumbler, int numTimes) {
        for (int i = 0; i < numTimes; i++) {
            rumbler.execute();
        }
    }

}