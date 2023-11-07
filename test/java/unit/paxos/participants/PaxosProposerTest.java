package paxos.participants;

import paxos.messages.PaxosMessage;
import paxos.messages.PaxosMessageQueue;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.logging.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for the PaxosProposer module.
 */
public class PaxosProposerTest {
    List<String> logMessages;
    PaxosMessageQueue mockMessageQueue;

    private static final Logger logger = Logger.getLogger(PaxosProposer.class.getName());

    @Before
    public void setUp() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);
        logMessages = new ArrayList<>();

        mockMessageQueue = mock(PaxosMessageQueue.class);
    }

    @After
    public void tearDown() {
        logMessages.forEach(System.out::println);
    }

    /**
     * Test to ensure the proposer id is set correctly.
     */
    @Test
    public void testProposerId() {
        logMessages.add("\n--- TEST: testProposerId ---\n");

        int expectedProposerId = 1;
        PaxosProposer proposer = new PaxosProposer(expectedProposerId, mockMessageQueue);

        try {
            assertEquals("Checking proposer id", expectedProposerId, proposer.id);

            logMessages.add("Test passed: Proposer id {" + expectedProposerId + "} was set successfully.\n");
        } catch (Error e) {
            logMessages.add("Test failed: Proposer id was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
        } catch (Exception e) {
            logMessages.add("Test failed: Proposer id was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            // throw e;
        }
    }

    /**
     * Test to ensure the proposer sends proposal messages to acceptors.
     */
    @Test
    public void testProposerSendsProposalMessages() {
        logMessages.add("\n--- TEST: testProposerSendsProposalMessages ---\n");

        int proposerId = 1;
        PaxosProposer proposer = new PaxosProposer(proposerId, mockMessageQueue);

        try {
            Object value = "SomeValue";
            proposer.propose(value);

            ArgumentCaptor<PaxosMessage> argument = ArgumentCaptor.forClass(PaxosMessage.class);
            verify(mockMessageQueue, times(1)).addMessage(argument.capture());

            PaxosMessage message = argument.getValue();
            assertEquals("Checking message type", "PREPARE", message.getType());
            assertEquals("Checking proposal number", 0, message.getProposalNumber());
            assertEquals("Checking value", value, message.getValue());

            logMessages.add("Test passed: Proposer sent proposal message successfully.\n");
        } catch (Error e) {
            logMessages.add("Test failed: Proposer did not send proposal message successfully.");
            logMessages.add(e.getMessage() + "\n");
        } catch (Exception e) {
            logMessages.add("Test failed: Proposer did not send proposal message successfully.");
            logMessages.add(e.getMessage() + "\n");
            // throw e;
        }

}
