
CURR_DIR = $(shell pwd)
CLASS_DIR = $(CURR_DIR)/classFiles
JUNIT = .:/usr/local/JUNIT/junit-4.13-beta-2.jar:/usr/local/JUNIT/hamcrest-core-2.1.jar

all:
	javac -d $(CLASS_DIR) $(CURR_DIR)/HandshakeMessage.java
	javac -d $(CLASS_DIR) $(CURR_DIR)/Message.java
	javac -d $(CLASS_DIR) $(CURR_DIR)/Type1Message.java
	javac -d $(CLASS_DIR) $(CURR_DIR)/Type2Message.java	
	javac -d $(CLASS_DIR) $(CURR_DIR)/PeerProcess.java
	javac -d $(CLASS_DIR) $(CURR_DIR)/Log.java
	
clean:
	rm -f *.log
	rm -f $(CLASS_DIR)/*.class
