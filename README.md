### Sample Java Project
This sample project is designed to get you up and running within few simple steps.

Begin with installing the dependencies below, and continue with the Getting Started procedure below.

### Dependencies
There are several prerequisite dependencies you should install on your machine prior to starting to work with this project:

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

* An IDE to write your tests on - [Eclipse](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/marsr) or [IntelliJ](https://www.jetbrains.com/idea/download/#)

* [Maven](https://maven.apache.org/)

* Android studio

* Local Appium Server running at http://127.0.0.1 ,  port: 4723

* Download the OS specific chromedriver into libs folder of the project and update the  webdriver.chrome.driver as applicable

Eclipse users should also install:

1. [Maven Plugin](http://marketplace.eclipse.org/content/m2e-connector-maven-dependency-plugin)

2. [TestNG Plugin](http://testng.org/doc/download.html)

IntelliJ IDEA users should also install:

1. [Maven Plugin for IDEA](https://plugins.jetbrains.com/plugin/1166)

TestNG Plugin is built-in in the IntelliJ IDEA, from version 7 onwards.
 
#### Optional Installations
* For source control management, you can install [git](https://git-scm.com/downloads).
* To be able to interact with a real device from Perfecto cloud directly from your IDE, and use Perfecto Reporting, install [Perfecto CQ Lab Plugin](https://www.perfectomobile.com/ni/resources/downloads/add-ins-plugins-and-extensions) for your IDE.

## Downloading the Sample Project

* [Clone](https://github.com/PerfectoMobileSA/PerfectoSampleProject.git) the repository.

* After downloading and unzipping the project to your computer, open it from your IDE by choosing the folder containing the pom.xml 

**********************
# Getting Started

* Download the OS specific chromedriver into libs folder of the project and update the webdriver.chrome.driver system path where ever applicable

* Local Appium Server should be running at http://127.0.0.1 ,  port: 4723


## Running sample as is


* Configure the name of your Perfecto cloud as a system variable by passing your cloud name as a -DcloudName=<<cloud name>> from CI / Maven system property while running the install goal of Maven or simply hardcode your cloud name in the script.

* Configure your security token from Maven/ any CI tools like Jenkins by passing the -DsecurityToken=<<token>> system property while running the install goal of Maven or simply hardcode it.

* Run pom.xml with the below maven goals & properties

		clean
		install
		-DcloudName=${cloudName}
		-DsecurityToken=${securityToken}

* The Maven will automatically kick start the parallel execution of different examples inside perfecto packaage in parallel.

* CI dashboard integration can be performed by supplying the below properties to top-level Maven Targets:

		clean
		install
		-DcloudName=${cloudName}
		-DsecurityToken=${securityToken}
		-Dreportium-job-name=${JOB_NAME} 
		-Dreportium-job-number=${BUILD_NUMBER} 
		-Dreportium-job-branch=${GIT_BRANCH} 
		-Dreportium-tags=${myTag}