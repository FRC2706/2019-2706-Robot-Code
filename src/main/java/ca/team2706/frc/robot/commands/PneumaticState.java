package ca.team2706.frc.robot.commands;

/**
 * Enum for the desired new state of the plunger, either {@link #STOWED}, {@link #DEPLOYED}
 * or the opposite of whatever it currently is ({@link #TOGGLE}
 */
public enum PneumaticState {
    STOWED, DEPLOYED, TOGGLE;
}
