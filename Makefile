SRCDIR = ./src/main/java
SRCJFLAGS = -cp $(SRCDIR):
TESTINGLIB = ./lib/testing/*
UNITDIR = ./src/test/unit
INTEGRATIONDIR = ./src/test/integration

all: compile

compile: create_bin compile_paxos

create_bin:
	mkdir -p bin
	mkdir -p bin/src
	mkdir -p bin/test
	mkdir -p bin/test/unit/
	mkdir -p bin/test/integration/

compile_paxos: create_bin
	javac $(SRCJFLAGS) -d ./bin/src/ $(SRCDIR)/adelaidesuburbs/App.java

run_council_election: compile_paxos
	java -cp ./bin/src/: adelaidesuburbs.App

test_paxos_unit: compile_paxos
	java -cp $(UNITDIR):$(TESTINGLIB) org.junit.runner.JUnitCore paxos.participant.PaxosAcceptorTest
	java -cp $(UNITDIR):$(TESTINGLIB) org.junit.runner.JUnitCore paxos.participant.PaxosLearnerTest
	java -cp $(UNITDIR):$(TESTINGLIB) org.junit.runner.JUnitCore paxos.participant.PaxosProposerTest
	java -cp $(UNITDIR):$(TESTINGLIB) org.junit.runner.JUnitCore paxos.messages.PaxosMessageTest

test_paxos_integration: compile_paxos

clean:
	rm -rf ./bin