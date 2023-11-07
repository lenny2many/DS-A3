BINSRC = ./bin/src
LOGLIB = ./lib/logging/*
TESTLIB = ./lib/testing/*
SRCJFLAGS = -cp $(BINSRC):$(LOGLIB)

SRCDIR = ./src/main/java

UNITDIR = test/unit
INTEGRATIONDIR = test/integration

all: compile

compile: create_bin compile_paxos

create_bin:
	mkdir -p bin
	mkdir -p bin/src
	mkdir -p bin/test
	mkdir -p bin/test/unit/
	mkdir -p bin/test/integration/

compile_adelaidesuburbs: create_bin
	javac $(SRCJFLAGS) -d ./bin/src/ $(SRCDIR)/adelaidesuburbs/App.java

compile_paxos: create_bin compile_paxos_src compile_paxos_test

compile_paxos_src:
	javac -cp ./lib/logging/*:$(SRCDIR) -d ./bin/src/ $(SRCDIR)/paxos/participants/PaxosProposer.java

compile_paxos_test:
	javac -cp ./lib/logging/*:./lib/testing/*:./bin/src: -d ./bin/test/unit ./test/java/unit/paxos/participants/PaxosProposerTest.java
	# javac -cp ./lib/logging/*:./lib/testing/*:./bin/src: -d ./bin/test/unit ./test/java/unit/paxos/participants/PaxosAcceptorTest.java
	# javac -cp ./lib/logging/*:./lib/testing/*:./bin/src: -d ./bin/test/unit ./test/java/unit/paxos/participants/PaxosLearnerTest.java
	javac -cp ./lib/logging/*:./lib/testing/*:./bin/src: -d ./bin/test/unit ./test/java/unit/paxos/messages/PaxosMessageTest.java

run_council_election: compile_paxos
	java -cp ./bin/src/: adelaidesuburbs.App

test_paxos_unit: compile_paxos
	# java -cp ./lib/logging/*:./lib/testing/*:./bin/test/unit/:./bin/src/: org.junit.runner.JUnitCore paxos.participants.PaxosProposerTest
	java -cp ./lib/logging/*:./lib/testing/*:./bin/test/unit/:./bin/src/: org.junit.runner.JUnitCore paxos.messages.PaxosMessageTest
	# java -cp ./lib/logging/*:./lib/testing/*:./bin/test/unit/:./bin/src/: org.junit.runner.JUnitCore paxos.participants.PaxosAcceptorTest
	# java -cp ./lib/logging/*:./lib/testing/*:./bin/test/unit/:./bin/src/: org.junit.runner.JUnitCore paxos.participants.PaxosLearnerTest
	

test_paxos_integration: compile_paxos

clean:
	rm -rf ./bin