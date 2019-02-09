package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class ConfigTest {
    private Robot robot;

    @Injectable
    private BufferedReader reader;

    @Injectable
    private BufferedWriter writer;

    @Mocked(stubOutClassInitialization = true)
    private Files files;

    @Before
    public void setUp() throws Exception {
        Field robotIdLocField = Config.class.getDeclaredField("ROBOT_ID_LOC");
        robotIdLocField.setAccessible(true);
        final Path robotIdLoc = (Path) robotIdLocField.get(null);

        Field configSaveLocField = Config.class.getDeclaredField("SAVE_FILE");
        configSaveLocField.setAccessible(true);
        final Path saveFileLoc = (Path) configSaveLocField.get(null);

        new Expectations() {{
            Files.newBufferedReader(robotIdLoc);
            result = reader;
            minTimes = 0;

            Files.newBufferedWriter(saveFileLoc);
            result = writer;
            minTimes = 0;

            reader.readLine();
            result = "0";
            minTimes = 0;
        }};

        robot = new Robot();
    }

    /**
     * Tests to ensure that fluid constants are saved to file when disabling the robot.
     */
    @Test
    public void testSaveConstants() throws IOException {
        robot.disabledInit();

        new Verifications() {{
            writer.write(withSubstring("Deployed: "));
            times = 1;
        }};
    }

    /**
     * Tests to ensure that the robot's id is retrieved properly.
     *
     * @throws Exception Java reflection exception.
     */
    @Test
    public void testGetRobotID() throws Exception {
        Method getIdMethod = Config.class.getDeclaredMethod("getRobotId");
        getIdMethod.setAccessible(true);

        new Expectations() {{
            reader.readLine();
            returns("1", "2", "3", "4");
        }};

        resetIdField();
        assertEquals(1, (int) getIdMethod.invoke(null));

        resetIdField();
        assertEquals(2, (int) getIdMethod.invoke(null));

        resetIdField();
        assertEquals(3, (int) getIdMethod.invoke(null));

        resetIdField();
        assertEquals(4, (int) getIdMethod.invoke(null));
    }

    /**
     * Tests to ensure that if the robot ID file cannot be retrieved that the robot's id is zero.
     */
    @Test
    public void testRobotIDIsZeroWhenNotSet() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method getIdMethod = Config.class.getDeclaredMethod("getRobotId");
        getIdMethod.setAccessible(true);

        assertEquals("Wrong robot ID found", 0, (int) getIdMethod.invoke(null));
    }

    /**
     * Checks that the correct item is returned for a {@code robotSpecific()} invocation
     *
     * @throws NoSuchMethodException     In case robotSpecific() can't be found
     * @throws InvocationTargetException In case robotSpecific() can't be invoked
     * @throws IllegalAccessException    In case this test was to hacky for Java (it is pretty close)
     * @throws NoSuchFieldException      In case the robotId field couldn't be modified
     */
    @Test
    public void robotSpecificTests() throws Exception {
        new Expectations() {{
            reader.readLine();
            returns("0", "3", "2", "-1", "3", "4", "0");
        }};

        Method robotSpecificMethod = Config.class.getDeclaredMethod("robotSpecific", Object.class, Object[].class);
        robotSpecificMethod.setAccessible(true);

        final String a = "a", b = "b", c = "c", d = "d", e = "e";

        resetIdField();
        assertEquals(a, robotSpecificMethod.invoke(null, a, makeArray(b, c)));
        resetIdField();
        assertEquals(a, robotSpecificMethod.invoke(null, a, makeArray(b, c)));
        resetIdField();
        assertEquals(c, robotSpecificMethod.invoke(null, a, makeArray(b, c)));
        resetIdField();
        assertEquals(a, robotSpecificMethod.invoke(null, a, makeArray(b, c)));
        resetIdField();
        assertEquals(d, robotSpecificMethod.invoke(null, a, makeArray(b, c, d, e)));
        resetIdField();
        assertEquals(e, robotSpecificMethod.invoke(null, a, makeArray(b, c, d, e)));
        resetIdField();
        assertEquals(a, robotSpecificMethod.invoke(null, a, makeArray()));
    }

    /**
     * Makes an array out of the given objects.
     *
     * @param objects The objects.
     * @return An array of the given objects.
     */
    private static Object[] makeArray(Object... objects) {
        return objects;
    }

    /**
     * Resets the robot id field back to something which will casue the robot to
     * check the file again.
     *
     * @throws Exception When bad stuff happens.
     */
    private void resetIdField() throws Exception {
        Field idField = Config.class.getDeclaredField("robotId");
        idField.setAccessible(true);
        idField.set(null, -1);
    }
}