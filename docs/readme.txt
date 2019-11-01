#######################################################################
to assemble jar (use profile depending on the environment)
#######################################################################
mvn -DskipTests=true -Penv assembly:assembly

#before run, define maven profile with the following properties:
-set up log directory
-set up input directory(file system) that would be scanned for jpeg images
-set up output directory(file system) where bmp images would be placed

#######################################################################
connection(address might be changed)
#######################################################################
ssh pi@92.115.183.17 -p 51313

after connection established create output directory(file system):
/media/pi/seagate/repo-min

/media/pi/seagate/repo-min would be used to keep meta/system data (logs)
/media/pi/seagate/repo-min/photo-min root of the output directory

mkdir repo-min
cd repo-min
mkdir photo-min

output path:
/media/pi/seagate/repo-min/photo-min

input path to:
/media/pi/seagate/repo/photo

#######################################################################
deployment
#######################################################################
change folder owner to let pi user copy .jar
sudo chown -R pi:1000 /usr/jpeg-decoder

Copy 
.jar to /usr/jpeg-decoder

scp -P 51313 jpeg-decoder.jar pi@92.115.183.17:/usr/jpeg-decoder
#if need to copy from remote to local
scp -P 51313 pi@92.115.183.17:/media/pi/seagate/repo-min/.logs/app.log /c/endava/workspace/jpeg-decoder/target

Run 
nohup java -jar -Dname=jpeg-decoder jpeg-decoder.jar >/dev/null &

#######################################################################
monitoring
#######################################################################
(1) to list processes  
jps -v
(2) add dummy param when starting application with java -jar and then 
inspect with (1) 
-Dname=jpeg-decoder

(3)
------------------------ Java agent -----------------------------------
Java agent has to packed as -jar file
It's structure is provided below:

under debug package place debug.InstrumentationAgent.java
***********************************************************************
package debug;

import java.lang.instrument.Instrumentation;

public class InstrumentationAgent {

	private static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    public static long getObjectSize(Object o) {
        return instrumentation.getObjectSize(o);
    }
	
}
***********************************************************************

pom.file
***********************************************************************
<project xmlns="http://maven.apache.org/POM/4.0.0" 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
		  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>java-agent</groupId>
  <artifactId>java-agent</artifactId>
  <version>1</version>
  <build>
    <sourceDirectory>src</sourceDirectory>
    <plugins>
      <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.0</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
        </configuration>
      </plugin>
      <plugin>
      	<!-- Build an executable JAR -->
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-jar-plugin</artifactId>
        <version>3.1.0</version>
        <configuration>
        	<archive>
            	<manifestEntries>
                	<premain-class>debug.InstrumentationAgent</premain-class>
                </manifestEntries>
            </archive>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
***********************************************************************
-----------------------------------------------------------------------

Include the following dependency in the jpeg-decoder pom:
<dependency>
	<groupId>java-agent</groupId>
	<artifactId>java-agent</artifactId>
	<version>1</version>
</dependency>

Use InstrumentationAgent.getObjectSize(obj) to get its size in bytes

(4)Use
	Runtime.getRuntime().totalMemory()
	Runtime.getRuntime().freeMemory()
	Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
to get total available, free and used memory 

(5) To run use param -javaagent:target\java-agent-1.jar when launch with java -jar