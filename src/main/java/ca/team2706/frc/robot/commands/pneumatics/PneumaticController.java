package ca.team2706.frc.robot.commands.pneumatics;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.command.TimedCommand;

import java.util.Objects;
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
     * Set to true to always move the piston, even if it is already in the desired position.
     */
    private boolean alwaysMovePiston;

    /**
     * Constructs a generic pneumatic controller command for controlling a pneumatic piston.
     *
     * @param moveFunction         The function for moving the piston, which accepts an argument of the piston's
     *                             desired state.
     *                             Cannot be null.
     * @param desiredStateProvider The supplier of the desired state of the piston.
     *                             The provided argument is the current state of the piston.
     *                             The expected return value is the desired state of the piston.
     *                             Cannot be null or return null.
     * @param currentStateProvider The supplier for the piston's current state. Can be null, but must not return null.
     * @param moveTime             The time that should be allotted for moving the piston, if it is necessary to move it.
     * @param stopFunction         The function for turning off the pneumatics. Cannot be null.
     * @param alwaysMovePiston     True to continue with the command and move the piston even if it is already
     *                             in the desired position.
     */
    public PneumaticController(final Consumer<PneumaticState> moveFunction,
                               final Function<PneumaticState, PneumaticState> desiredStateProvider,
                               final Supplier<PneumaticState> currentStateProvider,
                               final double moveTime,
                               final Runnable stopFunction,
                               final boolean alwaysMovePiston) {
        super(moveTime);

        this.moveFunction = Objects.requireNonNull(moveFunction, "Move Function cannot be null.");
        this.desiredStateProvider =
                Objects.requireNonNull(desiredStateProvider, "Desired state supplier cannot be null.");
        this.currentStateSupplier = currentStateProvider;
        this.stopFunction = Objects.requireNonNull(stopFunction, "Stop function cannot be null.");
        setAlwaysMovePiston(alwaysMovePiston);
    }

    /**
     * Constructs a generic pneumatic controller command for controlling a pneumatic piston.
     *
     * @param moveFunction         The function for moving the piston, which accepts an argument of the piston's
     *                             desired state.
     *                             Cannot be null.
     * @param desiredStateProvider The supplier of the desired state of the piston.
     *                             The provided argument is the current state of the piston.
     *                             The expected return value is the desired state of the piston.
     *                             Cannot be null or return null.
     * @param currentStateProvider The supplier for the piston's current state. Can be null, but must not return null.
     * @param moveTime             The time that should be allotted for moving the piston, if it is necessary to move it.
     * @param stopFunction         The function for turning off the pneumatics. Cannot be null.
     */
    public PneumaticController(final Consumer<PneumaticState> moveFunction,
                               final Function<PneumaticState, PneumaticState> desiredStateProvider,
                               final Supplier<PneumaticState> currentStateProvider,
                               final double moveTime,
                               final Runnable stopFunction) {
        this(moveFunction, desiredStateProvider, currentStateProvider, moveTime, stopFunction, false);
    }

    /**
     * Constructs a generic pneumatic controller command for controlling a pneumatic piston.
     *
     * @param deployFunction       The function for deploying (extending) the pneumatic piston.
     *                             Can be null if deploying isn't desired behaviour.
     * @param stowFunction         The function for retracting (stowing) the pneumatic piston.
     *                             Can be null if stowing isn't desired behaviour.
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
                    if (deployFunction != null) {
                        deployFunction.run();
                    }
                    break;
                case STOWED:
                    if (stowFunction != null) {
                        stowFunction.run();
                    }
                    break;
            }
        }, desiredStateProvider, currentStateProvider, moveTime, stopFunction);
    }

    /**
     * Constructs a generic pneumatic controller command for controlling a pneumatic piston.
     *
     * @param deployFunction       The function for deploying (extending) the pneumatic piston.
     *                             Can be null if deploying isn't desired behaviour.
     * @param stowFunction         The function for retracting (stowing) the pneumatic piston.
     *                             Can be null if stowing isn't desired behaviour.
     * @param desiredStateProvider The supplier of the desired state of the piston.
     *                             The provided argument is the current state of the piston.
     *                             The expected return value is the desired state of the piston.
     * @param currentStateProvider The supplier for the piston's current state.
     * @param moveTime             The time that should be allotted for moving the piston, if it is necessary to move it.
     * @param stopFunction         The function for turning off the pneumatics.
     * @param alwaysMovePiston     True to continue with the command and move the piston even if it is already
     *                             in the desired position.
     */
    public PneumaticController(final Runnable deployFunction,
                               final Runnable stowFunction,
                               final Function<PneumaticState, PneumaticState> desiredStateProvider,
                               final Supplier<PneumaticState> currentStateProvider,
                               final double moveTime,
                               final Runnable stopFunction,
                               final boolean alwaysMovePiston) {
        this(pneumaticState -> {
            switch (pneumaticState) {
                case DEPLOYED:
                    if (deployFunction != null) {
                        deployFunction.run();
                    }
                    break;
                case STOWED:
                    if (stowFunction != null) {
                        stowFunction.run();
                    }
                    break;
            }
        }, desiredStateProvider, currentStateProvider, moveTime, stopFunction, alwaysMovePiston);
    }

    /**
     * Constructs a pneumatic controller command for moving a pneumatic piston.
     *
     * @param moveFunction         The function for moving the piston, which accepts an argument of the piston's
     *                             desired state.
     *                             Cannot be null.
     * @param desiredStateSupplier The supplier of the desired state of the piston.
     *                             The provided argument is the current state of the piston.
     *                             The expected return value is the desired state of the piston.
     * @param stopFunction         The function for turning off the pneumatics.
     * @param moveTime             The time that should be allotted for moving the piston, if it is necessary to move it.
     */
    public PneumaticController(final Consumer<PneumaticState> moveFunction,
                               final Function<PneumaticState, PneumaticState> desiredStateSupplier,
                               final Runnable stopFunction,
                               final double moveTime) {
        this(moveFunction, desiredStateSupplier, null, moveTime, stopFunction);
    }

    /**
     * Determines if this command is set to always move the piston, even if
     * it is in the correct position.
     *
     * @return True if the command will always move the piston, false otherwise.
     */
    public boolean willAlwaysMovePiston() {
        return alwaysMovePiston;
    }

    /**
     * Sets whether or not the command will always move the piston.
     *
     * @param alwaysMovePiston True to move the piston even if it is in position already (and thereby
     *                         making the command delay if the piston is in its correct position), false otherwise.
     */
    public void setAlwaysMovePiston(boolean alwaysMovePiston) {
        this.alwaysMovePiston = alwaysMovePiston;
    }


    @Override
    protected void initialize() {
        super.initialize();

        final PneumaticState previousState = (currentStateSupplier != null) ? currentStateSupplier.get() : null;
        final PneumaticState desiredState = desiredStateProvider.apply(previousState);

        isAlreadyInPosition = previousState == desiredState;

        if (willAlwaysMovePiston() || !isAlreadyInPosition) {
            moveFunction.accept(desiredState);
        }
    }

    @Override
    protected boolean isFinished() {
        return super.isFinished() || (!willAlwaysMovePiston() && isAlreadyInPosition);
    }

    @Override
    protected void end() {
        super.end();
        stopFunction.run();
        this.isAlreadyInPosition = false;
    }
}
