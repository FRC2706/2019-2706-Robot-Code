package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.commands.drivebase.MotionMagic;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.sensors.LidarLitePWM;;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for 1-D driver assist using laser rangefinder 
 */
public class DriverAssistLaser extends Command {

    private LidarLitePWM lidarLitePWM;
    private double laserDistanceToTarget = 0.0;
    private boolean commandAborted = false;
    private MotionMagic motionMagic;

    private final double CENTIMETRES_TO_FEET = 1.0/(2.54*12);
    private final double LASER_DISTANCE_MIN = Config.TARGET_OFFSET_DISTANCE_LASER.value() + Config.ROBOT_HALF_LENGTH.value() - Config.ROBOTTOLASER_ROBOTY.value();

    /**
     * Creates driver assist laser command object
     */
    public DriverAssistLaser() {
        // Ensure that this command is the only one to run on the drive base
        requires(DriveBase.getInstance());
    }

    @Override
    public void initialize() {
        Log.d("DAL: initialize() called");

        // Get distance of robot from target using laster rangefinder
        laserDistanceToTarget = DriveBase.getInstance().getLidarLiteDistanceCm() * CENTIMETRES_TO_FEET;
        Log.d("laserDistanceToTarget:" + laserDistanceToTarget);
        
        if ((laserDistanceToTarget >= LASER_DISTANCE_MIN) && (laserDistanceToTarget <= Config.LASER_DISTANCE_MAX.value())) {
            double robotFrontBumperDistanceToTarget = laserDistanceToTarget - (Config.ROBOT_HALF_LENGTH.value() - Config.ROBOTTOLASER_ROBOTY.value());
            double robotDistanceToTravel = robotFrontBumperDistanceToTarget - Config.TARGET_OFFSET_DISTANCE_LASER.value();
            motionMagic = new MotionMagic(1.0, robotDistanceToTravel, 3, 0.0);
            motionMagic.start();
        } else if (laserDistanceToTarget < LASER_DISTANCE_MIN) {
            Log.d("DAL: Laser reading too low. Driver assist command rejected.");
 
        } else if (laserDistanceToTarget > Config.LASER_DISTANCE_MAX.value()) {
            Log.d("DAL: Laser reading too high. Driver assist command rejected.");
        }
    }

    @Override
    public boolean isFinished() {
        return (commandAborted || motionMagic.isCompleted());
    }

    @Override
    public void end() {
        if (motionMagic != null)
        motionMagic.close();
    }
}
