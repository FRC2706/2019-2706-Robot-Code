# 2019-2706-Robot-Code
The main robot code for the Merge Robotics (2706) robot for the 2019 FIRST Deep Space challenge. 

## Attribution and license

We release our software under the MIT license in the hopes that other teams use and/or modify our software.

Our one request is that if you do find our code helpful, please send us an email at frc2706@owcrobots.ca letting us know. We love to hear when we've helped somebody, and we'd like to be able to measure our impact on the community.

Thanks, and enjoy!

## Run Code
### From IntelliJ
1. Import the project as a Gradle Project.
2. Click the dropdown to the left of the run button in the top right corner.
3. Click edit configurations.
4. Click the plus button in the top left of the new window, and select gradle project.
5. Name the configuration, select this project for Gradle Project and set the task to "deploy"
6. If the code should be offline, add "--offline" to arguments
7. If the code should run in debug mode, add "-PdebugMode" to arguments
8. Click OK
9. Select the configuration from the dropdown menu
10. To run regular mode, click the green triangle
11. To run debug mode, click the bug to the right of the green triangle
## From Terminal
1. Open a terminal in the root of this project
2. Type the appropriate command to run the gradlew file (e.g. "./gradlew") and then add "deploy"
3. If the code should be offline, add "--offline"
4. If the code should run in debug mode, add "-PdebugMode"
5. Hit enter

## Want to help write robot code?

We have a lot of programmers on the team this year, so we've split the code out into chunks so each group can be in charge of a piece. Talk to your group's mentor or project leader to see which chunk you can work on.
