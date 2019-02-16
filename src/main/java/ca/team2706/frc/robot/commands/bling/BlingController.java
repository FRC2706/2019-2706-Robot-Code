package ca.team2706.frc.robot.commands.bling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import ca.team2706.frc.robot.commands.bling.patterns.Blank;
import ca.team2706.frc.robot.commands.bling.patterns.BlingPattern;
import ca.team2706.frc.robot.subsystems.Bling;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to control the bling on the robot.
 */
public class BlingController extends Command {


    private BlingPattern currentPattern = null;
    private double startTime = 0;

    /**
     * Possible periods of the robot.
     */
    public enum Period {
        AUTONOMOUS, TELEOP_WITHOUT_CLIMB, CLIMB
    }

    /**
     * How long (in seconds) that teleop lasts (including climb time)
     */
    private static final double TELEOP_TIME = 135;
    /**
     * How long (in seconds) the autonomous/sandstorm period lasts.
     */
    private static final double AUTONOMOUS_TIME = 15;

    /**
     * How long the climb period at the end of the match is (in seconds)
     */
    private static final double CLIMB_TIME = 30;

    /**
     * How long the entire match is (in seconds).
     */
    private static final double TOTAL_TIME = 150;

    private boolean useMatchTime;

    private final HashMap<Period, ArrayList<BlingPattern>> commands = new HashMap<>();

    public BlingController() {
        requires(Bling.getInstance());

        commands.put(Period.AUTONOMOUS, new ArrayList<>());
        commands.put(Period.CLIMB, new ArrayList<>());
        commands.put(Period.TELEOP_WITHOUT_CLIMB, new ArrayList<>());

        addPatterns();
    }

    /**
     * Adds the bling patterns to be used.
     */
    private void addPatterns() {
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
        /* If it's already teleop when we start, just subtract the autonomous time
        to make it seem as though we're in teleop. */
        if (DriverStation.getInstance().isOperatorControl()) startTime -= AUTONOMOUS_TIME;

        // If the FMS is attached, use the real match times.
        useMatchTime = DriverStation.getInstance().isFMSAttached();
    }

    /**
     * Adds the inputted command to the controller's queue or stuff to run.
     * This will be run whenever the execute method of this controller is
     * run, and if the conditions for the command to be run on the robot
     * are met, show this command on the robot.
     *
     * @param commandToAdd The command to add to the controller's queue.
     */
    private void add(BlingPattern commandToAdd) {
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

        // Filter and find the first pattern whose conditions have been met.
        Optional<BlingPattern> patternOptional = commands.get(currentPeriod).stream()
                .filter(BlingPattern::conditionsMet)
                .findFirst();

        // If there is a pattern, begin processing.
        if (patternOptional.isPresent()) {
            final BlingPattern pattern = patternOptional.get();
            /* Detect if the new pattern whose conditions are met was the last pattern to run.
             * If not, end the last pattern that ran, and start the new one.
             * Reset the pattern that we're no longer running
             */
            if (currentPattern == null || !currentPattern.equals(pattern)) {
                if (currentPattern != null) {
                    currentPattern.end();
                }
                pattern.initialize();
                currentPattern = pattern;

                // Now that we've selected the pattern, run it.
                runCurrentPattern();
            }
        }
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

    /**
     * Determines the robot's current period.
     *
     * @return The current robot period.
     */
    public Period getCurrentPeriod() {
        final Period currentPeriod;
        // If we're using match time, use match time. Otherwise, use the other time.
        final double timeIntoMatch = getTimeSinceStartOfMatch();

        if (DriverStation.getInstance().isAutonomous()) {
            currentPeriod = Period.AUTONOMOUS;
        } else if (timeIntoMatch <= TOTAL_TIME - CLIMB_TIME) {
            currentPeriod = Period.TELEOP_WITHOUT_CLIMB;
        } else {
            currentPeriod = Period.CLIMB;
        }

        return currentPeriod;
    }

    /**
     * Gets the time into the match, in seconds.
     *
     * @return How long it's been since the start of the match, in seconds.
     */
    public double getTimeSinceStartOfMatch() {
        return (useMatchTime) ? TELEOP_TIME - Timer.getMatchTime() + AUTONOMOUS_TIME : Timer.getFPGATimestamp() - startTime;
    }

    /**
     * Displays the current command in the bling subsystem
     */
    private void runCurrentPattern() {
        if (currentPattern != null) Bling.getInstance().display(currentPattern);
    }
}
