### Sample Java Project

![CircleCI status](https://circleci.com/gh/PerfectoMobileSA/PerfectoJavaSample.svg?style=shield "CircleCI status")

This sample project is designed to get you up and running within few simple steps.

Begin with installing the dependencies below, and continue with the Getting Started procedure below.

### Dependencies
There are several prerequisite de	pendencies you should install on your machine prior to starting to work with this project:

* Java 11

* An IDE to write your tests on - [Eclipse](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/marsr) or [IntelliJ](https://www.jetbrains.com/idea/download/#)

* [Maven](https://maven.apache.org/)

* Android studio

* Local Appium Server running at http://127.0.0.1 ,  port: 4723

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

* [Clone](https://github.com/PerfectoMobileSA/PerfectoJavaSample.git) the repository.

* After downloading and unzipping the project to your computer, open it from your IDE by choosing the folder containing the pom.xml 

**********************
# Getting Started

* Local Appium prerequisite: Local Appium Server should be running at http://127.0.0.1 ,  port: 4723


## Running sample as is


* Options to provide Perfecto cloud details:

<p>&nbsp;&nbsp;&nbsp;&nbsp; A. Open the application.properties, uncomment the properties and provide value.</p>
<p>&nbsp;&nbsp;&nbsp;&nbsp; B. Pass following parameters as maven properties:</p>
		<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a. `-Dperfecto.cloud.name=<<cloud name>>` (Required if not provided in option A)
		<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b. `-Dperfecto.security.token=<<security token>>` (Required if not provided in option A)
		<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;c. `-Dreportium.job.name=<<job name>>` (Optional)
		<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;d. `-Dreportium.job.number=<<job number>>` (Optional)

Note: Refer to official documentation on how to execute from eclipse / IntelliJ. </br>
* Run pom.xml with the below maven goals & properties when: </p>
   a. If credentials are provided using application.properties:
		
		clean
		install
		
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b. If credentials are passed as parameters:
		
		clean
		install
		-Dperfecto.cloud.name=${cloudName}
		-Dperfecto.security.token=${securityToken}
		-DtestngXmlFile=testng_perfecto.xml
</p>

* Maven will automatically kick start the parallel execution of different examples inside perfecto package in parallel if `-DtestngXmlFile=testng.xml` is passed as maven properties, if you want to run only perfecto scripts just pass this: `-DtestngXmlFile=testng_perfecto.xml` as maven properties. (this is the default behaviour) </p>

* CI dashboard integration can be performed by supplying the below properties to top-level Maven Targets:

		clean
		install
		-Dperfecto.cloud.name=${cloudName}
		-DtestngXmlFile=testng_perfecto.xml
		-Dperfecto.security.token=${securityToken}
		-Dreportium.job.name=${JOB_NAME} 
		-Dreportium.job.number=${BUILD_NUMBER} 
		-Dreportium-job-branch=${GIT_BRANCH} 
		-Dreportium.tags=${myTag}
