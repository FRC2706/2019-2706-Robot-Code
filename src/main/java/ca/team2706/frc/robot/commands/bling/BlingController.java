package ca.team2706.frc.robot.commands.bling;

import ca.team2706.frc.robot.commands.bling.patterns.Blank;
import ca.team2706.frc.robot.commands.bling.patterns.BlingPattern;
import ca.team2706.frc.robot.commands.bling.patterns.TestPattern;
import ca.team2706.frc.robot.subsystems.Bling;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Command to control the bling on the robot.
 */
public class BlingController extends Command {

    private BlingPattern currentPattern = null;
    private double startTime = 0;

    public enum Period {
        AUTONOMOUS, TELEOP_WITHOUT_CLIMB, CLIMB
    }

    boolean useMatchTime = false;

    HashMap<Period, ArrayList<BlingPattern>> commands;

    public BlingController() {
        requires(Bling.getInstance());

        commands = new HashMap<>() {
            private static final long serialVersionUID = 1L;

            {
                put(Period.AUTONOMOUS, new ArrayList<BlingPattern>());
                put(Period.CLIMB, new ArrayList<BlingPattern>());
                put(Period.TELEOP_WITHOUT_CLIMB, new ArrayList<BlingPattern>());
            }
        };

        /* Make and add the bling patterns.
         * They need to be created in order of highest to lowest priority.
         *
         * Since patterns from different periods won't run at the same time, you only have to
         * make sure you put patterns from the same period in proper order.
         */
        // Do blank as a last priority
        add(new Blank());
    }

    @Override
    public void initialize() {
        startTime = Timer.getFPGATimestamp();
        // If it's already teleop when we start, just subtract 15 seconds to make it seem as though we're in teleop.
        if (!DriverStation.getInstance().isOperatorControl()) startTime -= 15;

        // If the match time provided is not below 0, it's valid and we're in a game and use it.
        useMatchTime = Timer.getMatchTime() >= 0;
    }

    /**
     * Adds the inputted command to the controller's queue or stuff to run.
     * This will be run whenever the execute method of this controller is
     * run, and if the conditions for the command to be run on the robot
     * are met, show this command on the robot.
     *
     * @param commandToAdd The command to add to the controller's queue.
     */
    public void add(BlingPattern commandToAdd) {
        // add it to its proper place.
        // Loop around all of the periods it can be in.
        for (Period period : commandToAdd.getPeriod()) {
            // add it to the periods it can be in
            commands.get(period).add(commandToAdd);
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    public void execute() {
        // Get the current period
        final Period currentPeriod = getCurrentPeriod();

        // Loop around all the patterns for the current period, and evaluate the conditions
        for (BlingPattern pattern : commands.get(currentPeriod)) {
            // Break at the first positive return. 
            if (pattern.conditionsMet()) {

                /* Detect if the new pattern whose conditions are met was the last pattern to run.
                 * If not, end the last pattern that ran, and start the new one.
                 * Reset the pattern that we're no longer running
                 */
                if (currentPattern != null && !currentPattern.equals(pattern)) {
                    currentPattern.end();
                    pattern.initialize();
                }
                currentPattern = pattern;
                break;
            }
        }

        // Now that we've selected the pattern, run it.
        runCurrentPattern();
    }

    /**
     * Called when the command is ended
     */
    @Override
    public void end() {
        // Just clear the strip at the end.
        Bling.getInstance().clearStrip();
        if (currentPattern != null) currentPattern.end();
        currentPattern = null;
    }

    private Period getCurrentPeriod() {
        final Period currentPeriod;
        // If we're using match time, use match time. Otherwise, use the other time.
        final double timeSinceStart = (useMatchTime) ? Timer.getMatchTime() : Timer.getFPGATimestamp() - startTime;

        if (DriverStation.getInstance().isAutonomous()) {
            currentPeriod = Period.AUTONOMOUS;
        } else if (timeSinceStart <= 105) {
            currentPeriod = Period.TELEOP_WITHOUT_CLIMB;
        } else {
            currentPeriod = Period.CLIMB;
        }

        return currentPeriod;
    }

    /**
     * Displays the current command in the bling subsystem
     */
    private void runCurrentPattern() {
        if (currentPattern != null) Bling.getInstance().display(currentPattern);
    }
}
