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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PaxosProposer module.
 */
public class PaxosProposerTest {

    PaxosMessageQueue mockMessageQueue;

    private static final Logger logger = Logger.getLogger(PaxosProposerTest.class.getName());

    @Before
    public void setUp() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);

        mockMessageQueue = mock(PaxosMessageQueue.class);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test to ensure that a proposal is initialized correctly.
     */
    @Test
    public void proposerInitialisesProposalTest() {
        int proposerId = 1;
        PaxosProposer proposer = new PaxosProposer(proposerId, mockMessageQueue);
        proposer.initialiseProposal();

        List<String> logMessages = new ArrayList<>();
        logMessages.add("\n--- TEST: proposerInitialisesProposalTest ---\n");
        
        try {
            verify(mockMessageQueue, times(1)).addMessage(any(PaxosMessage.class));
            logMessages.add("Test passed: Message was added to message queue successfully.\n");
        } catch (AssertionError e) {
            logMessages.add("Test failed: Message was not added to message queue successfully.\n");
            throw e;
        } finally {
            logMessages.forEach(System.out::println);
        }
    }

    /**
     * Test to ensure that promises are handled correctly.
     */
    @Test
    public void proposerHandlesPromisesTest() {
        int proposerId = 1;
        PaxosProposer proposer = new PaxosProposer(proposerId, mockMessageQueue);
        PaxosMessage mockMessage = mock(PaxosMessage.class);
        when(mockMessage.getType()).thenReturn("PROMISE");
        proposer.onReceive(mockMessage);
        
        List<String> logMessages = new ArrayList<>();
        logMessages.add("\n--- TEST: proposerHandlesPromisesTest ---\n");

        try {
            verify(mockMessageQueue, times(1)).addMessage(any(PaxosMessage.class));
            logMessages.add("Test passed: Message was added to message queue successfully.\n");
        } catch (AssertionError e) {
            logMessages.add("Test failed: Message was not added to message queue successfully.\n");
            throw e;
        } finally {
            logMessages.forEach(System.out::println);
        }
    }

    /**
     * Test to ensure that accepted messages are handled correctly.
     */
    @Test
    public void proposerHandlesAcceptedMessagesTest() {
        int proposerId = 1;
        PaxosProposer proposer = new PaxosProposer(proposerId, mockMessageQueue);
        PaxosMessage mockMessage = mock(PaxosMessage.class);
        when(mockMessage.getType()).thenReturn("ACCEPTED");
        proposer.onReceive(mockMessage);
        
        List<String> logMessages = new ArrayList<>();
        logMessages.add("\n--- TEST: proposerHandlesAcceptedMessagesTest ---\n");

        try {
            verify(mockMessageQueue, times(1)).addMessage(any(PaxosMessage.class));
            logMessages.add("Test passed: Message was added to message queue successfully.\n");
        } catch (AssertionError e) {
            logMessages.add("Test failed: Message was not added to message queue successfully.\n");
            throw e;
        } finally {
            logMessages.forEach(System.out::println);
        }
    }
}
