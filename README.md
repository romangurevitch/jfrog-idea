## Getting started
JFrog Intellij IDEA plugin supports Xray scanning of project dependencies.

### Prerequisites
Intellij IDEA version 2016.2 and above.

JFrog Xray version 1.7.2.4 and above.

### Installing from Intellij IDEA
Go to Settings (Preferences) -> Plugins -> Browse repositories -> Search for JFrog -> Install

### User guide

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

#### Filtering issues and licenses


## Contributing
The code is built using Gradle and includes two projects: xray-client-java, Idea plugin.

#### JFrog Xray Java Client
Build and test using Gradle: 
```
gradle clean build
```

#### JFrog Intellij Plugin
This project depends on JFrog Xray Java client project.

To build and run open the project with Intellij IDEA IDE.

Using gradle integration plugin run ```buildPlugin``` task in order to build the plugin.

Run ```runIdea``` in order to build and launch local Intellij IDEA instance with the plugin.
