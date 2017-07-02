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

Test connection using ```Test Connection``` button.

#### View
The JFrog Intellij plugin displays a window tool view which, by default, is at the lower section of the screen.

The window tool can be accessed at: View -> Tool windows -> JFrog 

#### Scanning and viewing the results
JFrog Xray automatically performs a scan  whenever there is a change in dependencies in the project.

For manually invoke a scan, click ```Refresh``` button in JFrog Plugin tool window.

#### Filtering Xray Scan Results
There are two ways to filter the scan results:
1. **Issue severity:** Only display issues with the specified severity.
2. **Component license:** Only display components with the specified licenses.

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
