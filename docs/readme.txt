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