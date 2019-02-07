package ca.team2706.frc.robot.config;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    @Injectable
    BufferedReader bufferedReader;

    @Mocked
    Files files;

    @Before
    public void setUp() throws IOException, IllegalAccessException, NoSuchFieldException {
        Field pathField = Config.class.getDeclaredField("ROBOT_ID_LOC");
        pathField.setAccessible(true);
        Path path = (Path) pathField.get(null);

        new Expectations() {{
            Files.newBufferedReader(path);
            result = bufferedReader;
            minTimes = 0;
            bufferedReader.readLine();
            result = "0";
            minTimes = 0;
        }};
    }

    /**
     * Checks that the robot identifies itself correctly from a file
     *
     * @throws IOException For setting readLine() expectations
     * @throws NoSuchMethodException In case the method to get the robot id can't be found
     * @throws InvocationTargetException In case the method to get the robot id can't be invoked
     * @throws IllegalAccessException In case this test was to hacky for Java
     * @throws NoSuchFieldException In case the robotId field can't be found
     */
    @Test
    public void loadFileTest() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        new Expectations() {{
            bufferedReader.readLine();
            returns("-5", "0", "4", "two");
        }};

        Method robotIdMethod = Config.class.getDeclaredMethod("getRobotId");
        robotIdMethod.setAccessible(true);

        Field robotIdField = Config.class.getDeclaredField("robotId");
        robotIdField.setAccessible(true);

        robotIdField.set(null, -1);
        assertEquals(-5, robotIdMethod.invoke(null));

        robotIdField.set(null, -1);
        assertEquals(0, robotIdMethod.invoke(null));

        robotIdField.set(null, -1);
        assertEquals(4, robotIdMethod.invoke(null));

        robotIdField.set(null, -1);
        assertEquals(0, robotIdMethod.invoke(null));
    }

    /**
     * Checks that the correct item is returned for a {@code robotSpecific()} invocation
     *
     * @throws NoSuchMethodException In case robotSpecific() can't be found
     * @throws InvocationTargetException In case robotSpecific() can't be invoked
     * @throws IllegalAccessException In case this test was to hacky for Java (it is pretty close)
     * @throws NoSuchFieldException In case the robotId field couldn't be modified
     */
    @Test
    public void robotSpecificTests() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Field robotIdField = Config.class.getDeclaredField("robotId");
        robotIdField.setAccessible(true);

        Method robotSpecificMethod = Config.class.getDeclaredMethod("robotSpecific", Object.class, Object[].class);
        robotSpecificMethod.setAccessible(true);

        String a = "a", b = "b", c = "c", d = "d", e = "e";

        robotIdField.set(null, 0);
        assertEquals(a, robotSpecificMethod.invoke(null, a, a(b, c)));
        robotIdField.set(null, 3);
        assertEquals(a, robotSpecificMethod.invoke(null, a, a(b, c)));
        robotIdField.set(null, 2);
        assertEquals(c, robotSpecificMethod.invoke(null, a, a(b, c)));
        robotIdField.set(null, -1);
        assertEquals(a, robotSpecificMethod.invoke(null, a, a(b, c)));
        robotIdField.set(null, 3);
        assertEquals(d, robotSpecificMethod.invoke(null, a, a(b, c, d, e)));
        robotIdField.set(null, 4);
        assertEquals(e, robotSpecificMethod.invoke(null, a, a(b, c, d, e)));
        robotIdField.set(null, 0);
        assertEquals(a, robotSpecificMethod.invoke(null, a, a()));
    }

    private static Object[] a(Object... objects) {
        return objects;
    }
}