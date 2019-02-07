package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConfigTest {
    private Robot robot;

    // We want to use the temporary directory for storing test files. We could mock the BufferedReader but that's not as good as the actual thing.
    private static final Path SAVE_FILE = Paths.get(System.getProperty("java.io.tmpdir"), "FluidConstants.txt.tmp");
    private static final Path ID_FILE = Paths.get(System.getProperty("java.io.tmpdir"), "RobotID.txt.conf");

    @Before
    public void setUp() throws Exception {
        // Need to set the save file constant to a temporary file location.
        Field fileField = Config.class.getDeclaredField("SAVE_FILE");
        setFinalStatic(fileField, SAVE_FILE);

        Field idField = Config.class.getDeclaredField("ROBOT_ID_LOC");
        setFinalStatic(idField, ID_FILE);

        robot = new Robot();
    }

    /**
     * Tests to ensure that fluid constants are saved to file when disabling the robot.
     */
    @Test
    public void testSaveConstants() {

        robot.disabledInit();

        assertTrue("Config file wasn't written.", new File(SAVE_FILE.toString()).exists());
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

        writeToRobotIdFile(1);
        assertEquals(1, (int) getIdMethod.invoke(null));

        writeToRobotIdFile(2);
        assertEquals(2, (int) getIdMethod.invoke(null));
    }

    /**
     * Tests to ensure that if the robot ID file cannot be retrieved that the robot's id is zero.
     */
    @Test
    public void testRobotIDIsZeroWhenNotSet() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Files.delete(ID_FILE);

        Method getIdMethod = Config.class.getDeclaredMethod("getRobotId");
        getIdMethod.setAccessible(true);

        assertEquals("Wrong robot ID found", 0, (int) getIdMethod.invoke(null));
    }

    /**
     * Writes the given integer robot ID to the robot ID file.
     *
     * @param robotID The robot ID to be written.
     */
    private void writeToRobotIdFile(final int robotID) {
        try (BufferedWriter writer = Files.newBufferedWriter(ID_FILE)) {
            writer.write(String.valueOf(robotID));
        } catch (IOException ignored) {
        }
    }

    /**
     * Sets the given static final variable to a value.
     *
     * @param field    The field to be set.
     * @param newValue The new value for the field.
     * @throws Exception Thrown when something inevitably goes wrong with reflection.
     */
    private static void setFinalStatic(final Field field, final Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}