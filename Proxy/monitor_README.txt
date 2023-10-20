To compile the monitor:
	g++ monitor_WINDOWS.cpp -o monitor_WINDOWS.exe
or	g++ monitor_UNIX.cpp -o monitor_UNIX.exe

Ensure that you have packaged the maven proxy project.
	Can be done with "mvn clean install"
	Eclipse: Right click project -> Run as Maven Clean, Run as Maven Install
	Not sure about Intelij, should be similar

A file titled "Proxy-0.0.1-SNAPSHOT.jar" should be created in target/
If the file is named something different for you, please adjust the code accordingly or rename your file.