package ca.team2706.frc.robot.pneumatics;

/**
 * Enum for the desired new state of the plunger, either {@link #STOWED} or {@link #DEPLOYED}
 */
public enum PneumaticState {
    STOWED, DEPLOYED;

    /**
     * Gets the opposite state of whatever is provided.
     *
     * @param state The state to which the opposite is desired.
     * @return The opposite state. Deployed's opposite is stowed, stowed's opposite is deployed.
     */
    public static PneumaticState getOpposite(PneumaticState state) {
        final PneumaticState oppositeState;
        switch (state) {
            case STOWED:
                oppositeState = PneumaticState.DEPLOYED;
                break;
            case DEPLOYED:
                oppositeState = PneumaticState.STOWED;
                break;
            default:
                oppositeState = null;
                break;
        }

        return oppositeState;
    }
}
