# Getting started
JFrog Intellij IDEA plugin adds JFrog Xray scanning of Maven project dependencies to your Intellij IDE.

### Prerequisites
Intellij IDEA version 2016.2 and above.

JFrog Xray version 1.7.2.4 and above.

### Installation
From Intellij IDEA:

Go to Settings (Preferences) -> Plugins -> Browse repositories -> Search for JFrog -> Install
![Alt text](docs/install.png?raw=true "Installing JFrog plugin")

### User Guide

#### Setting up JFrog Xray
Go to Settings (Preferences) -> Other Settings -> JFrog Xray Configuration

Configure JFrog Xray URL and credentials.

Test your connection to Xray using the ```Test Connection``` button.

![Alt text](docs/credentials.png?raw=true "Setting up credentials")

#### View
The JFrog Intellij plugin displays a window tool view which, by default, is at the bottom of the screen.

The window tool can be accessed at: View -> Tool windows -> JFrog 

![Alt text](docs/enable_tool_window.png?raw=true "Enable tool window")

#### Scanning and viewing the results
JFrog Xray automatically performs a scan  whenever there is a change in dependencies in the project.

To manually invoke a scan, click ```Refresh``` button in JFrog Plugin tool window.

![Alt text](docs/tool_window.png?raw=true "Scan results window")

#### Filtering Xray Scan Results
There are two ways to filter the scan results:
1. **Issue severity:** Only display issues with the specified severity.
2. **Component license:** Only display components with the specified licenses.


![Alt text](docs/filter_issues.png?raw=true "Issues filter")
![Alt text](docs/filter_licenses.png?raw=true "Licenses filter")
# Building and Testing the Sources
The plugin consists of two Gradle projects:
xray-client-java and IDEA plugin.

#### JFrog Xray Java Client
Build and test using Gradle: 
```
gradle clean build
```
#### JFrog Intellij Plugin
This project depends on the JFrog Xray Java client project.

To build and run the project, import the project to the Intellij IDEA IDE.

Using the gradle integration plugin, run the ```buildPlugin``` task in order to build the plugin.

Run ```runIdea``` in order to build and launch local Intellij IDEA instance with the plugin.
