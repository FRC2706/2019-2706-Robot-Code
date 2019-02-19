package ca.team2706.frc.robot;

import ca.team2706.frc.robot.commands.drivebase.MotionMagic;
import ca.team2706.frc.robot.commands.drivebase.MotionProfile;
import ca.team2706.frc.robot.commands.drivebase.StraightDrive;
import ca.team2706.frc.robot.commands.drivebase.StraightDriveGyro;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.Bling;
import ca.team2706.frc.robot.subsystems.DriveBase;
import ca.team2706.frc.robot.subsystems.SensorExtras;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Main Robot class
 */
public class Robot extends TimedRobot {
    private boolean isInitialized;

    private Command[] commands;

    private static Robot latestInstance;

    private final List<Consumer<RobotState>> stateListeners = new ArrayList<>();

    public Robot() {
        latestInstance = this;
    }

    /**
     * Method run on robot initialization.
     */
    @Override
    public void robotInit() {
        onStateChange(RobotState.ROBOT_INIT);
        isInitialized = true;

        Config.init();

        // Initialize subsystems
        Bling.init();
        DriveBase.init();

        // Make sure that this is last initialized subsystem
        SensorExtras.init();

        // OI depends on subsystems, so initialize it after
        OI.init();

        // The USB camera used on the Robot, not enabled during simulation mode
        if (Config.ENABLE_CAMERA) {
            UsbCamera camera0 = CameraServer.getInstance().startAutomaticCapture();
            UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture();

            // Prevents crashing of simulation robot
            if (isReal()) {
                camera0.setConnectVerbose(0);
                camera1.setConnectVerbose(0);
            }
        }

        commands = new Command[]{
                OI.getInstance().driveCommand,                               // 0
                OI.getInstance().driveCommand,                               // 1
                new StraightDrive(0.2, 2.0, 100),  // 2
                new MotionMagic(0.2, 15.54, 100),  //3
                new StraightDriveGyro(0.2, 2.0, 100),  // 4
                new MotionProfile(0.3, new double []{0.0, 3.85E-4, 0.00154, 0.0034649999999999998, 0.00616, 0.009625000000000002, 0.013859999999999999, 0.018865000000000003, 0.02464, 0.031184999999999997, 0.038500000000000006, 0.046585, 0.055439999999999996, 0.06506500000000001, 0.07546000000000001, 0.086625, 0.09856, 0.11126500000000002, 0.12473999999999999, 0.138985, 0.15400000000000003, 0.169785, 0.18634, 0.203665, 0.22175999999999998, 0.240625, 0.26026000000000005, 0.28066500000000005, 0.30184000000000005, 0.323785, 0.3465, 0.369985, 0.39424, 0.41926500000000005, 0.44506000000000007, 0.47162499999999996, 0.49895999999999996, 0.527065, 0.55594, 0.585585, 0.6160000000000001, 0.647185, 0.67914, 0.711865, 0.74536, 0.7796250000000001, 0.81466, 0.8504649999999999, 0.8870399999999999, 0.924385, 0.9625, 1.001385, 1.0410400000000002, 1.0814650000000001, 1.1226600000000002, 1.1646250000000002, 1.2073600000000002, 1.2508649999999997, 1.29514, 1.340185, 1.386, 1.432585, 1.47994, 1.528065, 1.57696, 1.626625, 1.6770600000000002, 1.7282650000000004, 1.7802400000000003, 1.8329849999999999, 1.8864999999999998, 1.9407849999999998, 1.9958399999999998, 2.051665, 2.10826, 2.1656250000000004, 2.22376, 2.282665, 2.34234, 2.402785, 2.4640000000000004, 2.5259850000000004, 2.58874, 2.652265, 2.71656, 2.781625, 2.84746, 2.914065, 2.98144, 3.0495850000000004, 3.1185000000000005, 3.1881850000000003, 3.25864, 3.3298650000000003, 3.4018599999999997, 3.4746249999999996, 3.5481599999999998, 3.622465, 3.69754, 3.773385, 3.85, 3.927, 4.0040000000000004, 4.081, 4.158, 4.235, 4.312, 4.389, 4.466000000000001, 4.543000000000001, 4.620000000000001, 4.697000000000001, 4.774000000000001, 4.850999999999999, 4.927999999999999, 5.004999999999999, 5.081999999999999, 5.159, 5.236, 5.313, 5.39, 5.467, 5.544, 5.621, 5.698, 5.775, 5.852, 5.929, 6.006, 6.083, 6.16, 6.237, 6.314, 6.391000000000001, 6.468000000000001, 6.545000000000001, 6.622000000000001, 6.699000000000002, 6.776, 6.853, 6.93, 7.007, 7.084, 7.161, 7.2379999999999995, 7.3149999999999995, 7.3919999999999995, 7.468999999999999, 7.545999999999999, 7.623, 7.7, 7.777, 7.854000000000001, 7.931000000000001, 8.008000000000001, 8.085, 8.162, 8.239, 8.316, 8.393, 8.47, 8.547, 8.624, 8.700999999999999, 8.777999999999999, 8.854999999999999, 8.932, 9.009, 9.086, 9.163, 9.24, 9.317, 9.394, 9.471, 9.548, 9.625, 9.702, 9.779, 9.856, 9.933, 10.01, 10.087000000000002, 10.164000000000001, 10.241000000000001, 10.318000000000001, 10.395000000000001, 10.472000000000001, 10.549000000000001, 10.626, 10.703, 10.78, 10.857, 10.934, 11.011, 11.088, 11.165, 11.241999999999999, 11.319, 11.396, 11.473, 11.55, 11.5885, 11.627, 11.6655, 11.704, 11.7425, 11.781, 11.8195, 11.858, 11.896500000000001, 11.935000000000002, 11.973500000000001, 12.012000000000002, 12.050500000000001, 12.089000000000002, 12.127500000000001, 12.166, 12.2045, 12.243, 12.2815, 12.32, 12.3585, 12.397, 12.435500000000001, 12.474000000000002, 12.512500000000001, 12.551, 12.589500000000001, 12.628, 12.666500000000001, 12.705, 12.743500000000001, 12.782, 12.820500000000001, 12.859, 12.8975, 12.936, 12.9745, 13.013, 13.0515, 13.09, 13.128500000000003, 13.167000000000002, 13.205500000000002, 13.244000000000002, 13.282500000000002, 13.321000000000002, 13.3595, 13.398, 13.4365, 13.475, 13.513499999999999, 13.552000000000001, 13.5905, 13.629000000000001, 13.6675, 13.706000000000001, 13.7445, 13.783000000000001, 13.8215, 13.860000000000001, 13.8985, 13.937000000000001, 13.9755, 14.014000000000001, 14.0525, 14.091000000000001, 14.1295, 14.168000000000001, 14.2065, 14.245000000000001, 14.2835, 14.322000000000001, 14.3605, 14.399000000000004, 14.437500000000004, 14.476000000000003, 14.514499999999998, 14.552999999999997, 14.591500000000003, 14.630000000000003, 14.668500000000002, 14.707, 14.745500000000002, 14.784, 14.822500000000002, 14.861, 14.899500000000002, 14.938, 14.976500000000001, 15.015, 15.053500000000001, 15.092, 15.130500000000001, 15.169, 15.207500000000001, 15.246, 15.284500000000001, 15.323, 15.361500000000001, 15.54,
                }, new double []{0.0, 0.077, 0.154, 0.23099999999999998, 0.308, 0.385, 0.46199999999999997, 0.539, 0.616, 0.693, 0.77, 0.847, 0.9239999999999999, 1.0010000000000001, 1.078, 1.155, 1.232, 1.3090000000000002, 1.386, 1.463, 1.54, 1.617, 1.694, 1.7710000000000001, 1.8479999999999999, 1.925, 2.0020000000000002, 2.079, 2.156, 2.233, 2.31, 2.387, 2.464, 2.5410000000000004, 2.6180000000000003, 2.695, 2.772, 2.849, 2.926, 3.003, 3.08, 3.157, 3.234, 3.311, 3.388, 3.4650000000000003, 3.5420000000000003, 3.6189999999999998, 3.6959999999999997, 3.773, 3.85, 3.927, 4.0040000000000004, 4.081, 4.158, 4.235, 4.312, 4.388999999999999, 4.466, 4.543, 4.62, 4.697, 4.774, 4.851, 4.928, 5.005, 5.082000000000001, 5.159000000000001, 5.236000000000001, 5.313, 5.39, 5.467, 5.544, 5.6209999999999996, 5.698, 5.775, 5.852, 5.929, 6.006, 6.083, 6.16, 6.237000000000001, 6.314, 6.391, 6.468, 6.545, 6.622, 6.699, 6.776, 6.853000000000001, 6.930000000000001, 7.007000000000001, 7.0840000000000005, 7.1610000000000005, 7.2379999999999995, 7.3149999999999995, 7.3919999999999995, 7.469, 7.546, 7.623, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.7, 7.623000000000002, 7.546, 7.469000000000002, 7.3919999999999995, 7.315000000000001, 7.2379999999999995, 7.161000000000001, 7.084, 7.0070000000000014, 6.93, 6.8530000000000015, 6.776, 6.699000000000001, 6.621999999999999, 6.545000000000001, 6.467999999999999, 6.391000000000001, 6.313999999999999, 6.237, 6.159999999999998, 6.083, 6.0059999999999985, 5.929, 5.8519999999999985, 5.775, 5.698000000000002, 5.621, 5.544000000000002, 5.467, 5.3900000000000015, 5.313, 5.2360000000000015, 5.158999999999999, 5.082000000000001, 5.004999999999999, 4.928000000000001, 4.850999999999999, 4.774000000000001, 4.696999999999999, 4.620000000000001, 4.542999999999999, 4.466000000000001, 4.388999999999999, 4.312000000000001, 4.2349999999999985, 4.158, 4.080999999999999, 4.0040000000000004, 3.9269999999999983, 3.85, 3.773000000000002, 3.6959999999999997, 3.6190000000000015, 3.542, 3.4650000000000016, 3.388, 3.3110000000000017, 3.233999999999999, 3.157000000000001, 3.079999999999999, 3.003000000000001, 2.9259999999999993, 2.849000000000001, 2.7719999999999994, 2.695000000000001, 2.6179999999999986, 2.5410000000000004, 2.4639999999999986, 2.3870000000000005, 2.3099999999999987, 2.2330000000000005, 2.155999999999999, 2.0790000000000006, 2.001999999999998, 1.9249999999999998, 1.8480000000000016, 1.771, 1.6940000000000017, 1.617, 1.5400000000000018, 1.4629999999999992, 1.386000000000001, 1.3089999999999993, 1.232000000000001, 1.1549999999999994, 1.0780000000000012, 1.0009999999999994, 0.9240000000000013, 0.8469999999999986, 0.7700000000000005, 0.6929999999999987, 0.6160000000000005, 0.5389999999999988, 0.46200000000000063, 0.3849999999999989, 0.3080000000000007, 0.2309999999999981, 0.15399999999999991, 0.07699999999999818, 0,
                },new double []{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                }, new int []{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10,
                }, 301)//5
        };
    }

    /**
     * Called periodically (every cycle) while the robot is on.
     */
    @Override
    public void robotPeriodic() {

    }

    /**
     * Called when the robot enters the disabled state.
     */
    @Override
    public void disabledInit() {
        // If test mode was run, disable live window, and start scheduler
        if (LiveWindow.isEnabled()) {
            LiveWindow.setEnabled(false);
        }

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.DISABLED);
    }

    /**
     * Called periodically (every cycle) while the robot is disabled.
     */
    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * Called at the beginning of autonomous.
     */
    @Override
    public void autonomousInit() {
        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.AUTONOMOUS);

        selectorInit();
    }

    /**
     * Checks to see if the desired command is assigned and runs 0 or does nothing if not
     */
    private void selectorInit() {
        // The index based the voltage of the selector
        final int index = DriveBase.getInstance().getAnalogSelectorIndex();

        // Check to see if the command exists in the desired index
        if (DriveBase.getInstance().getAnalogSelectorIndex() < commands.length && commands[index] != null) {
            commands[DriveBase.getInstance().getAnalogSelectorIndex()].start();
        } else if (commands.length > 0 && commands[0] != null) {
            commands[0].start();
        }
    }

    /**
     * Called periodically (every cycle) while the robot is in autonomous mode.
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * Called when the robot enters teleop mode.
     */
    @Override
    public void teleopInit() {
        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.TELEOP);
    }

    /**
     * Called periodically (every cycle) while the robot is in teleop mode.
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }

    /**
     * Called when the robot enters test mode.
     */
    @Override
    public void testInit() {
        // Disable scheduler and run live window
        LiveWindow.setEnabled(true);

        // Iterate through each of the state-change listeners and call them.
        onStateChange(RobotState.TEST);
    }

    /**
     * Called periodically during test mode.
     */
    @Override
    public void testPeriodic() {

    }

    /**
     * Main method, called when the robot code is run like a desktop application.
     *
     * @param args Arguments passed on startup
     */
    public static void main(String[] args) {
        Log.init();

        Runtime.getRuntime().addShutdownHook(new Thread(Robot::shutdown));

        RobotBase.startRobot(Robot::new);
    }

    /**
     * Adds a method to be called when the robot's state is changed.
     *
     * @param listener The listener to be added.
     */
    public void addStateListener(Consumer<RobotState> listener) {
        stateListeners.add(listener);
    }

    /**
     * Sets the given listener to be called when the robot is disabled.
     *
     * @param listener The listener to be invoked when the robot is disabled.
     */
    public static void setOnStateChange(Consumer<RobotState> listener) {
        if (latestInstance != null) {
            latestInstance.addStateListener(listener);
        }
    }

    /**
     * Removes a state listener.
     *
     * @param listener The listener to be removed.
     */
    public void removeListener(Consumer<RobotState> listener) {
        stateListeners.remove(listener);
    }

    /**
     * Removes a state listener so that it is no longer subscribed to robot state change events.
     *
     * @param listener The listener to be removed.
     */
    public static void removeStateListener(Consumer<RobotState> listener) {
        if (latestInstance != null) {
            latestInstance.removeListener(listener);
        }
    }

    /**
     * Determines if the current instance of the robot has been initialized.
     *
     * @return True if the robot has been initialized, false otherwise.
     */
    public static boolean isRobotInitialized() {
        return latestInstance != null && latestInstance.isInitialized();
    }

    /**
     * Determines if this robot has been initialized properly.
     *
     * @return True if initialized, false otherwise.
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Calls the state change event, executing the listeners.
     *
     * @param newState The robot's current (new) state.
     */
    private void onStateChange(RobotState newState) {
        // Make shallow copy of this.
        ArrayList<Consumer<RobotState>> listeners = new ArrayList<>(stateListeners);
        listeners.forEach(action -> action.accept(newState));
    }

    /**
     * Called when the robot is shutting down.
     */
    private static void shutdown() {
        // Iterate through each of the state-change listeners and call them.
        latestInstance.onStateChange(RobotState.SHUTDOWN);
    }
}
