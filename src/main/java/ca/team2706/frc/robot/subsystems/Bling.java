package ca.team2706.frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem for controlling bling operations.
 * @author Kyle Anderson
 */
public class Bling extends Subsystem {

    private static Bling currentInstance;
    public static Bling getInstance() {
        if (currentInstance == null) {
            init();
        }

        return currentInstance;
    }

    /**
     * Initializes a new bling object.
     */
    public static void init() {
        currentInstance = new Bling();
    }

    @Override
    protected void initDefaultCommand() {

    }


}
