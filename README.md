JFrog Intellij IDEA plugin
===================
JFrog Xray integration with Intellij IDEA to scan Maven project dependencies for vulnerabilities and licenses issues.

## Building and Testing the Sources
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
