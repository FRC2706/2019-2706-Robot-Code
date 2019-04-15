package ca.team2706.frc.robot.commands.pneumatics;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.command.TimedCommand;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generic class for controlling a pneumatic piston (something controlled with pneumatics
 * which has two states: deployed and stowed).
 */
public class PneumaticController extends TimedCommand {


    private final Consumer<PneumaticState> moveFunction;
    private final Function<PneumaticState, PneumaticState> desiredStateProvider;
    private final Supplier<PneumaticState> currentStateSupplier;
    private final Runnable stopFunction;
    private boolean isAlreadyInPosition;

    /**
     * Constructs a generic pneumatic controller command for controlling a pneumatic piston.
     *
     * @param moveFunction         The function for moving the piston, which accepts an argument of the piston's
     *                             desired state.
     * @param desiredStateProvider The supplier of the desired state of the piston.
     *                             The provided argument is the current state of the piston.
     *                             The expected return value is the desired state of the piston.
     * @param currentStateProvider The supplier for the piston's current state.
     * @param moveTime             The time that should be allotted for moving the piston, if it is necessary to move it.
     * @param stopFunction         The function for turning off the pneumatics.
     */
    public PneumaticController(final Consumer<PneumaticState> moveFunction,
                               final Function<PneumaticState, PneumaticState> desiredStateProvider,
                               final Supplier<PneumaticState> currentStateProvider,
                               final double moveTime,
                               final Runnable stopFunction) {
        super(moveTime);

        this.moveFunction = moveFunction;
        this.desiredStateProvider = desiredStateProvider;
        this.currentStateSupplier = currentStateProvider;
        this.stopFunction = stopFunction;
    }

    /**
     * Constructs a generic pneumatic controller command for controlling a pneumatic piston.
     *
     * @param deployFunction       The function for deploying (extending) the pneumatic piston.
     * @param stowFunction         The function for retracting (stowing) the pneumatic piston.
     * @param desiredStateProvider The supplier of the desired state of the piston.
     *                             The provided argument is the current state of the piston.
     *                             The expected return value is the desired state of the piston.
     * @param currentStateProvider The supplier for the piston's current state.
     * @param moveTime             The time that should be allotted for moving the piston, if it is necessary to move it.
     * @param stopFunction         The function for turning off the pneumatics.
     */
    public PneumaticController(final Runnable deployFunction,
                               final Runnable stowFunction,
                               final Function<PneumaticState, PneumaticState> desiredStateProvider,
                               final Supplier<PneumaticState> currentStateProvider,
                               final double moveTime,
                               final Runnable stopFunction) {
        this(pneumaticState -> {
            switch (pneumaticState) {
                case DEPLOYED:
                    deployFunction.run();
                    break;
                case STOWED:
                    stowFunction.run();
                    break;
            }
        }, desiredStateProvider, currentStateProvider, moveTime, stopFunction);
    }


    @Override
    protected void initialize() {
        super.initialize();

        final PneumaticState previousState = currentStateSupplier.get();
        final PneumaticState desiredState = desiredStateProvider.apply(previousState);

        isAlreadyInPosition = previousState == desiredState;
        moveFunction.accept(desiredState);
    }

    @Override
    protected boolean isFinished() {
        /*
        TODO if the piston is already in position, it will be turned on and then off instantaneously.
        Need to determine what the desired behaviour for this is.
        */
        return super.isFinished() || isAlreadyInPosition;
    }

    @Override
    protected void end() {
        super.end();
        stopFunction.run();
        this.isAlreadyInPosition = false;
    }
}
