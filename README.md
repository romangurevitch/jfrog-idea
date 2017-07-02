# Getting started
JFrog Intellij IDEA plugin supports JFrog Xray scanning of Maven project dependencies.

### Prerequisites
Intellij IDEA version 2016.2 and above.

JFrog Xray version 1.7.2.4 and above.

### Installation
From Intellij IDEA:

Go to Settings (Preferences) -> Plugins -> Browse repositories -> Search for JFrog -> Install

### User Guide

#### Setting up JFrog Xray
Go to Settings (Preferences) -> Other Settings -> JFrog Xray Configuration

Configure JFrog Xray URL and credentials.

Test connection using ```Test Connection``` button.

#### View
JFrog plugin consist with a window tool view, by default at the lower section of the screen.

The window tool can be accessed at: View -> Tool windows -> JFrog 

#### Scanning and viewing the results
JFrog Xray scan performed on depenency changes in the project.

For manual scan click ```Refresh``` button in JFrog Plugin tool window.

#### Xray scan results filtering
There are two ways to filter the scan results:
1. Issue severity: selecting severity will effect the shown issues in the plugin view.
2. Component license: selecting licenses from current components licenses will update the view accordingly.

# Building and Testing the Sources
The plugin consists with two Gradle projects:
xray-client-java and Idea plugin.

#### JFrog Xray Java client
Build and test using Gradle: 
```
gradle clean build
```

#### JFrog Intellij plugin
This project depends on JFrog Xray Java client project.

To build and run the project import the project to Intellij IDEA IDE.

Using gradle integration plugin run ```buildPlugin``` task in order to build the plugin.

Run ```runIdea``` in order to build and launch local Intellij IDEA instance with the plugin.
