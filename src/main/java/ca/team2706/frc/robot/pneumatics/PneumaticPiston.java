package ca.team2706.frc.robot.pneumatics;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public class PneumaticPiston extends DoubleSolenoid {
    private PneumaticState currentState = PneumaticState.STOWED;

    public PneumaticPiston(int forwardChannel, int reverseChannel) {
        super(forwardChannel, reverseChannel);
    }


    @Override
    public void set(Value value) {
        super.set(value);
        if (value == Value.kForward) {
            currentState = PneumaticState.DEPLOYED;
        } else if (value == Value.kReverse) {
            currentState = PneumaticState.STOWED;
        }
    }

    public void set(PneumaticState desiredState) {
        if (desiredState == PneumaticState.TOGGLE) {
            switch (getState()) {
                case DEPLOYED:
                    desiredState = PneumaticState.STOWED;
                    break;
                case STOWED:
                    desiredState = PneumaticState.DEPLOYED;
                    break;
            }
        }

        switch (desiredState) {
            case DEPLOYED:
                set(Value.kForward);
                break;
            case STOWED:
                set(Value.kReverse);
                break;
        }
    }

    public PneumaticState getState() {
        return  currentState;
    }
}
