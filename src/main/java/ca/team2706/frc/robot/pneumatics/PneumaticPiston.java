package ca.team2706.frc.robot.pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Class for representing a pneumatic piston, a device which goes in or out
 * using pneumatics.
 */
public class PneumaticPiston extends DoubleSolenoid {
    /**
     * Current state position of the pneumatics, either kForward or kReverse.
     */
    private PneumaticState currentState;

    private final boolean checkState;

    /**
     * Constructs a new PneumaticPiston class with given forward channel, reverse channel and start position.
     *
     * @param forwardChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param reverseChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param startPosition  The default starting position for the pneumatic piston, either in or out.
     */
    public PneumaticPiston(final int forwardChannel, final int reverseChannel, final PneumaticState startPosition) {
        this(forwardChannel, reverseChannel, startPosition, false);
    }

    /**
     * Constructs a new PneumaticPiston class with given forward channel, reverse channel and start position.
     *
     * @param forwardChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param reverseChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param startPosition  The default starting position for the pneumatic piston, either in or out.
     *                       Must be one of {@link PneumaticState#DEPLOYED} or {@link PneumaticState#STOWED},
     *                       or null if the state is unknown, in which case the state can be set with
     *                       {@link #forceSetState(PneumaticState)} or will be set when the piston is moved.
     * @param checkState     True to not call the double solenoid if the pneumatic piston is already
     *                       in the desired position, false otherwise.
     */
    public PneumaticPiston(final int forwardChannel, final int reverseChannel,
                           final PneumaticState startPosition, final boolean checkState) {
        super(forwardChannel, reverseChannel);
        this.checkState = checkState;

        forceSetState(startPosition);
    }


    @Override
    public void set(final Value value) {
        if (!checkState || !isSameState(value, getState())) {
            super.set(value);
            switch (value) {
                case kForward:
                    currentState = PneumaticState.DEPLOYED;
                    break;
                case kReverse:
                    currentState = PneumaticState.STOWED;
                    break;
            }
        }
    }

    /**
     * Determines if the given Value is equivalent to the given state.
     * kForward is equivalent to DEPLOYED, kReverse is equivalent to STOWED
     *
     * @param value The value to be checked.
     * @param state The PneumaticState to be checked.
     * @return True if the two states are equivalent, false otherwise.
     */
    private static boolean isSameState(Value value, PneumaticState state) {
        return (value == Value.kForward && state == PneumaticState.DEPLOYED) ||
                (value == Value.kReverse && state == PneumaticState.STOWED);
    }

    /**
     * Performs the necessary action on the pneumatic piston to move it to the correct position.
     *
     * @param desiredState The desired pneumatic piston's position.
     */
    public void set(PneumaticState desiredState) {
        switch (desiredState) {
            case DEPLOYED:
                set(Value.kForward);
                break;
            case STOWED:
                set(Value.kReverse);
                break;
        }
    }

    /**
     * Gets the current state of the pneumatic piston, either deployed or retracted.
     *
     * @return The Value state of the pneumatic piston.
     */
    public PneumaticState getState() {
        return currentState;
    }

    /**
     * Sets the state of the pneumatic piston without moving it.
     * @param state The state of the pneumatic piston.
     *              Must be one of {@link PneumaticState#STOWED} or {@link PneumaticState#DEPLOYED} or null for unknown.
     */
    public void forceSetState(final PneumaticState state) {
        if (state != PneumaticState.STOWED && state != PneumaticState.DEPLOYED && state != null) {
            throw new IllegalArgumentException("Start position of a pneumatic piston must either be stowed or " +
                    "deployed or null for unknown.");
        } else {
            currentState = state;
        }
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addStringProperty("State", () -> {
            PneumaticState state = getState();
            String stringState;
            if (state == null) {
                stringState = "NULL";
            } else {
                stringState = state.toString();
            }

            return stringState;
        }, s -> set(PneumaticState.valueOf(s)));
        super.initSendable(builder);
    }
}
