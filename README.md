# 2019-2706-Robot-Code
The main robot code for the Merge Robotics (2706) robot for the 2019 FIRST Deep Space challenge.

[![Build Status](https://dev.azure.com/FRC2706/2019-2706%20Robot%20Code/_apis/build/status/FRC2706.2019-2706-Robot-Code?branchName=master)](https://dev.azure.com/FRC2706/2019-2706%20Robot%20Code/_build/latest?definitionId=1&branchName=master)

## Attribution and license

Robot Overlord's (Shep) license applies.

We release our software under the MIT license in the hopes that other teams use and/or modify our software.

Our one request is that if you do find our code helpful, please send us an email at frc2706@owcrobots.ca letting us know. We love to hear when we've helped somebody, and we'd like to be able to measure our impact on the community.

Thanks, and enjoy!

## Run Code
Ensure that JDK 11 is installed on the computer before proceeding.

### From VS Code
1. Make sure that the Java and WPILib plugin are both installed.
1. Open the project in VS Code.
2. Open the command pallette with Ctrl + Shift + P.
3. Search for the desired run configuration.
4. Select the command to run.
### From Eclipse
1. Import the project as a Gradle Project.
1. Select the desired run configuration from the dropdown menu beside the green "run" triangle.
### From IntelliJ
1. Import the project as a Gradle Project, ensuring that the project format is directory based.
1. Premade run configurations can be found in the runConfigurations folder. Simply copy the folder to inside the .idea folder.
1. After copying, head to File--> Close Project and then reopen the project right after to get IntelliJ to detect the new run configurations.
1. Select the desired run configuration from the dropdown menu beside the green "run" triangle.
1. To run regular mode, click the green "run" triangle.
1. To run debug mode, click the bug to the right of the green "run" triangle.
## From Terminal
1. Open a terminal in the root of this project
1. Type the appropriate command to run the gradlew file (e.g. "./gradlew") and then add "deploy"
1. If the code should be offline, add "--offline"
1. If the code should run in debug mode, add "-PdebugMode"
1. Hit enter

## Want to help write robot code?

We have a lot of programmers on the team this year, so we've split the code out into chunks so each group can be in charge of a piece. Talk to your group's mentor or project leader to see which chunk you can work on.
