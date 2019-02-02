package ca.team2706.frc.robot.logging;

import ca.team2706.frc.robot.Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {

    private static final Logger LOGGER = LogManager.getLogger(Robot.class.getName());

    public static void init ()
    {
            LOGGER.debug("Debug Message Logged !!!");
            LOGGER.warn("Warn Message Logged !!!");
            LOGGER.info("Info Message Logged !!!");
            LOGGER.error("Error Message Logged !!!", new NullPointerException("NullError"));
            LOGGER.fatal("Fatal Message Logged !!!");
    }

}



