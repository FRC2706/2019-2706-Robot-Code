package util;

import ca.team2706.frc.robot.OI;
import ca.team2706.frc.robot.subsystems.Bling;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.subsystems.SensorExtras;

import java.lang.reflect.Field;

public class Util {
    /**
     * Sets the currentInstance variable of all subsystems to null in order to reset them during testes.
     *
     * @throws NoSuchFieldException   If the field doesn't exist.
     * @throws IllegalAccessException If the field for currentInstance is inaccessible.
     */
    public static void resetSubsystems() throws NoSuchFieldException, IllegalAccessException {
        setCurrentInstanceFieldNull(Bling.class);
        setCurrentInstanceFieldNull(OI.class);
        setCurrentInstanceFieldNull(SensorExtras.class);
        setCurrentInstanceFieldNull(DriveBase.class);
    }

    /**
     * Sets the static private field to the desired value.
     *
     * @param classObject The class to which the field belongs.
     * @throws NoSuchFieldException   When the field doesn't exist.
     * @throws IllegalAccessException When the field is inaccessible.
     */
    private static void setCurrentInstanceFieldNull(final Class classObject) throws NoSuchFieldException, IllegalAccessException {
        Field field = classObject.getDeclaredField("currentInstance");
        field.setAccessible(true);
        field.set(null, null);
    }

}
