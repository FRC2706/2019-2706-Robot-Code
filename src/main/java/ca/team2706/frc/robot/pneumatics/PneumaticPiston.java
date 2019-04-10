package ca.team2706.frc.robot.pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;

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
     * @param forwardChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param reverseChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param startPosition The default starting position for the pneumatic piston, either in or out.
     */
    public PneumaticPiston(int forwardChannel, int reverseChannel, PneumaticState startPosition) {
        this(forwardChannel, reverseChannel, startPosition, false);
    }

    /**
     * Constructs a new PneumaticPiston class with given forward channel, reverse channel and start position.
     * @param forwardChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param reverseChannel {@link DoubleSolenoid#DoubleSolenoid(int, int)}
     * @param startPosition The default starting position for the pneumatic piston, either in or out.
     *                      Must be one of {@link PneumaticState#DEPLOYED} or {@link PneumaticState#STOWED}
     * @param checkState True to not call the double solenoid if the pneumatic piston is already
     *                   in the desired position, false otherwise.
     */
    public PneumaticPiston(int forwardChannel, int reverseChannel, PneumaticState startPosition, boolean checkState) {
        super(forwardChannel, reverseChannel);

        if (startPosition != PneumaticState.STOWED && startPosition != PneumaticState.DEPLOYED) {
            throw new IllegalArgumentException("Start position of a pneumatic piston must either be stowed or deployed");
        }
        this.currentState = startPosition;
        this.checkState = checkState;
    }


    @Override
    public void set(final Value value) {
        if (checkState && !getState().equals(value)) {
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
     * Performs the necessary action on the pneumatic piston to move it to the correct position.
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
     * @return The Value state of the pneumatic piston.
     */
    public PneumaticState getState() {
        return currentState;
    }
}
