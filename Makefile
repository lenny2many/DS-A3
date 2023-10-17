SRCDIR = ./src/main/java
SRCJFLAGS = -cp $(SRCDIR):
TESTJFLAGS = -cp ./src/test/java/:

all: compile

compile: create_bin compile_paxos

create_bin:
	mkdir -p bin
	mkdir -p bin/src
	mkdir -p bin/test

compile_paxos: create_bin
	javac $(SRCJFLAGS) -d bin/src/ $(SRCDIR)/adelaidesuburbs/App.java

run_paxos: compile_paxos
	java $(SRCJFLAGS) adelaidesuburbs.council.App

run_paxos_test: compile_paxos
	java $(TESTJFLAGS) org.junit.runner.JUnitCore paxos.core.PaxosTest
	java $(TESTJFLAGS) org.junit.runner.JUnitCore paxos.core.MemberTest

clean:
	rm -rf ./bin