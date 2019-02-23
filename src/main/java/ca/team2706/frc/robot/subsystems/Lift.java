package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 * Subsystem that controls the elevator on the robot
 */

public class Lift extends PIDSubsystem {

    /**
     * The lift motor controller.
     */
    public WPI_TalonSRX liftMotor;
    /**
     * Limit switch used to zero the lift when in the furthest downwards position.
     */
    public DigitalInput liftLimitSwitch;

    private final double[] PORT_HEIGHTS = {
            27.5, // lowest in inches
            55.5, // med in inches
            83.5 // highest in inches
    };

    private final double[] HATCH_HEIGHTS = {
            19, // lowest in inches
            47, // med in inches
            75 // highest in inches
    };

    //taking the height presets above and putting them in order

    private final double[] LOWER_HEIGHTS = {
            HATCH_HEIGHTS[0] - 0.5,
            HATCH_HEIGHTS[1] - 0.5,
            HATCH_HEIGHTS[2] - 0.5
    };

    private final double MAX_HEIGHT = PORT_HEIGHTS[2];

    private double heightGoal = 1.0;

    private boolean loweringForHatch = false; //if the lift is lowering in order to deploy a hatch

    private static Lift currentInstance;

    /**
     * initialises a new Elevator object
     *
     * @return the new Elevator instance
     */

    public static Lift getInstance() {
        if (currentInstance == null) {
            init();
        }

        return currentInstance;
    }

    public static void init() {
        currentInstance = new Lift();
    }

    public void initDefaultCommand() {

    }

    @Override
    public double returnPIDInput() {
        return 0;
    }

    /**
     * Updates the setpoint for the PID controller
     *
     * @param setpoint the new setpoint
     */
    public void setDesiredPoint(double setpoint) {
        getPIDController().setSetpoint(setpoint);
    }

    @Override
    protected void usePIDOutput(double output) {
        liftMotor.set(output);
    }

    /**
     * Determines if the lift has reached the setpoint or not
     *
     * @return True if the lift has reached the setpoint, false otherwise.
     */
    public boolean reachedGoal() {
        final double tolerance = 5; //subject to change
        return tolerance < Math.abs(getSetpoint() - getPosition());
    }

    /**
     * Raises the lift up one preset
     * Height depends on whether the lift has a hatch or cargo
     */
    public void addToHeightGoal() {
        if (heightGoal < 2) {
            switch (Intake.getInstance().getMode()) {
                case HATCH:
                    heightGoal++;
                    setDesiredPoint(HATCH_HEIGHTS[(int) heightGoal]);
                    break;
                case CARGO:
                    heightGoal++;
                    setDesiredPoint(PORT_HEIGHTS[(int) heightGoal]);
                    break;
            }
        }
    }

    /**
     * Lowers the lift down 1 preset
     * Height depends on whether the lift has a hatch or cargo
     */
    public void subtractFromHeightGoal() {
        if (heightGoal > 0) {
            switch(Intake.getInstance().getMode()) {
                case HATCH:
                    heightGoal--;
                    setDesiredPoint(HATCH_HEIGHTS[(int) heightGoal]);
                    break;
                case CARGO:
                    heightGoal--;
                    setDesiredPoint(PORT_HEIGHTS[(int) heightGoal]);
                    break;
            }
        }
    }

    /**
     * Enables PID.
     */
    public void enablePID() {
        getPIDController().enable();
    }

    /**
     * Disables PID.
     */
    public void disablePID() {
        getPIDController().disable();
    }

    /**
     * Checks if the lift has reached either the top or bottom
     *
     * @return True if the lift has reached a limit, false otherwise.
     */
    public boolean reachedLimits() {
        return getPosition() >= MAX_HEIGHT || liftLimitSwitch.get();
    }

    public Lift() {
        super("lift", Config.ENCODER_LIFT_PID_UP[0], Config.ENCODER_LIFT_PID_UP[1], Config.ENCODER_LIFT_PID_UP[2]);
        setAbsoluteTolerance(0.05);
        liftMotor = new WPI_TalonSRX(5);
        liftLimitSwitch = new DigitalInput(1);
        getPIDController().setInputRange(-1, 1);
        liftMotor.setNeutralMode(NeutralMode.Brake);
    }

    /**
     * Lowering the lift to deploy a hatch
     */
    public void lowertoDeployHatch() {
        loweringForHatch = true;
        setDesiredPoint(LOWER_HEIGHTS[(int) heightGoal]);
    }

    /**
     * Stopping the lift
     */
    public void stop() {
        if (!loweringForHatch) {
            liftMotor.set(0);
        } else {
            setDesiredPoint(HATCH_HEIGHTS[(int) heightGoal]);
            Intake.getInstance().retractPlunger(); // Moving the plunger in.
            Intake.getInstance().raiseIntake();
            loweringForHatch = false;
        }
    }

    /**
     * Move the lift up manually
     *
     * @param speed the speed at which to move
     */
    public void moveUp(double speed) {
        if (getPosition() < MAX_HEIGHT) {
            liftMotor.set(speed);

        }
    }

    /**
     * Move the lift down manually
     *
     * @param speed the speed at which to move
     */
    public void moveDown(double speed) {
        if (!liftLimitSwitch.get() && getPosition() > 0) {
            liftMotor.set(-speed);
        }
    }

    /**
     * Movement in either direction regardless of PID value
     *
     * @param speed speed at which to move
     */
    public void moveOverride(double speed) {
        if (!liftLimitSwitch.get()) {
            liftMotor.set(speed);
        }
    }
}