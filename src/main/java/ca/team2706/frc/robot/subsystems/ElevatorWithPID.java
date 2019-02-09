package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.*;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;


public class ElevatorWithPID extends PIDSubsystem {
  
  private WPI_TalonSRX m_liftMotor;
  //private TalonSRX m_liftMotor;
  public DigitalInput m_limitSwitchDown;

  public final double SPEED = 1.0;

  private final double[] PORT_HEIGHTS = {
    27.5, //lowest in inches
    55.5, //med in inches
    83.5 //highest in inches
  };

  private final double[] HATCH_HEIGHTS = {
    19, //lowest in inches
    47, //med in inches
    75 //highest in inches
  };

  //taking the height presets above and putting them in order

  private final double[] LOWER_HEIGHTS = {
    HATCH_HEIGHTS[0] - 0.5,
    HATCH_HEIGHTS[1] - 0.5,
    HATCH_HEIGHTS[2] - 0.5
  };

  private final double MAX_HEIGHT = PORT_HEIGHTS[2];

  private double heightGoal = 1.0;

  private boolean loweringForHatch = false;
  
  private static ElevatorWithPID currentInstance;

  public static ElevatorWithPID getInstance(){
    if (currentInstance == null){
      init();
    }

    return currentInstance;
  }

  public static void init(){
    currentInstance = new ElevatorWithPID();
  }

  public void initDefaultCommand(){

  }
  @Override
  public double returnPIDInput(){
    return 0;
  }

  public void setTheSetPoint(double setpoint){
    getPIDController().setSetpoint(setpoint);
  }

  @Override
  protected void usePIDOutput(double output){
    m_liftMotor.set(output);
  }

  public boolean reachedGoal(){
    final double tolerance = 5; //subject to change
    return tolerance > Math.abs(tolerance - getPosition());
  }

  public void addToHeightGoal(){
    Intake intake = Intake.getInstance();
    if (heightGoal < 2 && intake.hatchMode){
      heightGoal ++;
      setTheSetPoint(HATCH_HEIGHTS[(int) heightGoal]);
    } else if (heightGoal < 2 && !intake.hatchMode){
      heightGoal ++;
      setTheSetPoint(PORT_HEIGHTS[(int) heightGoal]);
    }
  }

  public void subtractFromHeightGoal(){
    Intake intake = Intake.getInstance();
    if (heightGoal > 0 && intake.hatchMode){
      heightGoal --;
      setTheSetPoint(HATCH_HEIGHTS[(int) heightGoal]);
    } else if (heightGoal > 0 && !intake.hatchMode){
      heightGoal --;
      setTheSetPoint(PORT_HEIGHTS[(int) heightGoal]);
    }
  }

  public void enablePID(){
    getPIDController().enable();
  }

  public void disablePID(){
    getPIDController().disable();
  }

  public boolean reachedLimits(){
    return getPosition() >= MAX_HEIGHT || m_limitSwitchDown.get();
  }
  
  public ElevatorWithPID() {
    super("lift", Config.ENCODER_LIFT_PID_UP[0], Config.ENCODER_LIFT_PID_UP[1], Config.ENCODER_LIFT_PID_UP[2]);
    setAbsoluteTolerance(0.05);
    m_liftMotor = new WPI_TalonSRX(1);
    m_limitSwitchDown = new DigitalInput(1);
    getPIDController().setInputRange(-1, 1);
  }

  public void lowertoDeployHatch(){
    loweringForHatch = true;
    setTheSetPoint(LOWER_HEIGHTS[(int) heightGoal]);
  }

  public void stop(){
    if (!loweringForHatch){
      m_liftMotor.set(0);
    } else {
      setTheSetPoint(HATCH_HEIGHTS[(int) heightGoal]);
      Intake.getInstance().retractHatchMech();
      Intake.getInstance().raiseIntake();
      loweringForHatch = false;
    }
  }

  public void moveUp(double speed){
    if (getPosition() < MAX_HEIGHT) {
      m_liftMotor.set(speed);

    }
  }

  public void moveDown(double speed){
    if (!m_limitSwitchDown.get() && getPosition() > 0){
        m_liftMotor.set(-speed);
    }
  }

  public void moveOverride(double speed){
    if (!m_limitSwitchDown.get()){
      m_liftMotor.set(speed);
    }
  }
}