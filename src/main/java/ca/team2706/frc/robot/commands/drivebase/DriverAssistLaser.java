package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.commands.drivebase.MotionMagic;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.sensors.LidarLitePWM;;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalSource;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

/**
 * Command for 1-D driver assist using laser rangefinder
 * 
 */
public class DriverAssistLaser extends Command {

    private LidarLitePWM lidarLitePWM;
    private double laserDistanceToTarget = 0.0;
    private boolean commandAborted = false;
    private Trajectory traj;
    private MotionMagic motionMagic;

    private final double CENTIMETRES_TO_FEET = 1.0/(2.54*12);
    private final double LASER_DISTANCE_MIN = Config.TARGET_OFFSET_DISTANCE_LASER.value() + Config.ROBOT_LENGTH.value()/2.0 - Config.ROBOTTOLASER_ROBOTY.value();
    /**
     * Creates driver assist laser command object
     */
    public DriverAssistLaser() {
        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());

        DigitalSource ds = new DigitalInput(0);
        lidarLitePWM = new LidarLitePWM(ds);

        laserDistanceToTarget = lidarLitePWM.getDistancePWMCm() * CENTIMETRES_TO_FEET;
        System.out.println("laserDistanceToTarget:" + laserDistanceToTarget);
    }

    /**
     * initialize() method does all the work of the command up to and including generating the trajectory
     */
    @Override
    public void initialize() {

        Log.d("DAL: initialize() called");

        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        // Get distance of robot from target using laster rangefinder
        laserDistanceToTarget = lidarLitePWM.getDistancePWMCm() * CENTIMETRES_TO_FEET;
        System.out.println("laserDistanceToTarget:" + laserDistanceToTarget);
        
        if (laserDistanceToTarget < LASER_DISTANCE_MIN) {
            Log.d("DAL: Laser reading too low. Driver assist command rejected.");
            commandAborted = true;
            return;
        } else if (laserDistanceToTarget > Config.LASER_DISTANCE_MAX.value()) {
            Log.d("DAL: Laser reading too high. Driver assist command rejected.");
            commandAborted = true;
            return;
        }

        double robotFrontBumperDistanceToTarget = laserDistanceToTarget - (Config.ROBOT_LENGTH.value()/2.0 - Config.ROBOTTOLASER_ROBOTY.value());

        double robotDistanceToTravel = robotFrontBumperDistanceToTarget - Config.TARGET_OFFSET_DISTANCE_LASER.value();

        commandMotion();

        motionMagic = new MotionMagic(1.0, robotDistanceToTravel, 3);
        motionMagic.start();
    }

    @Override
    public boolean isFinished() {
        return (commandAborted || motionMagic.isCompleted());
    }

    @Override
    public void end() {
        motionMagic.close();

        // Go back to disabled mode
        DriveBase.getInstance().setDisabledMode();
    }

    public void commandMotion() {

    }
}
