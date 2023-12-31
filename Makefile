BINSRC = ./bin/src
LOGLIB = ./lib/logging/*
TESTLIB = ./lib/testing/*
SRCJFLAGS = -cp $(BINSRC):$(LOGLIB)
TESTJFLAGS = -cp $(TESTLIB):$(BINSRC)

SRCDIR = ./src/main/java

UNITDIR = test/java/unit
INTEGRATIONDIR = test/java/integration

all: compile

compile: create_bin compile_adelaidesuburbs compile_paxos

create_bin:
	mkdir -p bin
	mkdir -p bin/src
	mkdir -p bin/test
	mkdir -p bin/test/unit/
	mkdir -p bin/test/integration/

compile_adelaidesuburbs: create_bin
	javac $(SRCJFLAGS):$(SRCDIR) -d ./bin/src/ $(SRCDIR)/adelaidesuburbs/App.java

compile_paxos: compile_paxos_src compile_paxos_test


# Source targets
compile_paxos_src: compile_paxos_participant compile_paxos_proposer compile_paxos_acceptor compile_paxos_message

compile_paxos_participant: create_bin
	javac $(SRCJFLAGS) -d ./bin/src/ $(SRCDIR)/paxos/participants/PaxosParticipant.java

compile_paxos_proposer: create_bin
	javac $(SRCJFLAGS) -d ./bin/src/ $(SRCDIR)/paxos/participants/PaxosProposer.java

compile_paxos_acceptor: create_bin
	javac $(SRCJFLAGS) -d ./bin/src/ $(SRCDIR)/paxos/participants/PaxosAcceptor.java

compile_paxos_message: create_bin
	javac $(SRCJFLAGS) -d ./bin/src/ $(SRCDIR)/paxos/messages/PaxosMessage.java


# Test targets
compile_paxos_test: compile_paxos_test_integration # compile_paxos_test_participant compile_paxos_test_proposer compile_paxos_test_acceptor compile_paxos_test_message

# compile_paxos_test_participant: create_bin compile_paxos_participant
# 	javac $(TESTJFLAGS):$(SRCDIR) -d ./bin/test/unit $(UNITDIR)/paxos/participants/PaxosParticipantTest.java

# compile_paxos_test_proposer: create_bin compile_paxos_proposer
# 	javac $(TESTJFLAGS):$(SRCDIR) -d ./bin/test/unit $(UNITDIR)/paxos/participants/PaxosProposerTest.java

# compile_paxos_test_acceptor: create_bin compile_paxos_acceptor
# 	javac $(TESTJFLAGS):$(SRCDIR) -d ./bin/test/unit $(UNITDIR)/paxos/participants/PaxosAcceptorTest.java

# compile_paxos_test_message: create_bin compile_paxos_message
# 	javac $(TESTJFLAGS):$(SRCDIR) -d ./bin/test/unit $(UNITDIR)/paxos/messages/PaxosMessageTest.java

compile_paxos_test_integration: compile_adelaidesuburbs
	javac $(TESTJFLAGS):$(SRCDIR) -d ./bin/test/integration $(INTEGRATIONDIR)/PaxosIntegrationTests.java

# Test run targets
# test_paxos_unit_participant: compile_paxos_test_participant
# 	java -cp $(TESTLIB):./bin/test/unit/:$(BINSRC) org.junit.runner.JUnitCore paxos.participants.PaxosParticipantTest

# test_paxos_unit_proposer: compile_paxos_test_proposer
# 	java -cp $(TESTLIB):./bin/test/unit/:$(BINSRC) org.junit.runner.JUnitCore paxos.participants.PaxosProposerTest

# test_paxos_unit_acceptor: compile_paxos_test_acceptor
# 	java -cp $(TESTLIB):./bin/test/unit/:$(BINSRC) org.junit.runner.JUnitCore paxos.participants.PaxosAcceptorTest

# test_paxos_unit_learner: compile_paxos_test_learner
# 	java -cp $(TESTLIB):./bin/test/unit/:$(BINSRC) org.junit.runner.JUnitCore paxos.participants.PaxosLearnerTest

# test_paxos_unit_message: compile_paxos_test_message
# 	java -cp $(TESTLIB):./bin/test/unit/:$(BINSRC) org.junit.runner.JUnitCore paxos.messages.PaxosMessageTest



test_paxos_integration: compile_adelaidesuburbs compile_paxos_test_integration
	java -cp $(TESTLIB):./bin/test/integration/:$(BINSRC) org.junit.runner.JUnitCore integration.PaxosIntegrationTests

# Run targets
run_council_election: compile_adelaidesuburbs
	java $(SRCJFLAGS) adelaidesuburbs.App

# Clean targets
clean:
	rm -rf ./bin