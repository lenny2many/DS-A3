package paxos.participants;

import paxos.messages.*;
import paxos.network.*;

import java.util.Collections;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.mockito.stubbing.Answer;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PaxosProposer module.
 */
public class PaxosProposerTest {
    private List<String> logMessages;
    private PaxosParticipant participant;
    private NetworkServer mockServer;
    private NetworkClient mockClient;
    private MessageQueue mockQueue;
    CountDownLatch latch;

    private List<PaxosParticipant.Node> nodes;
    private PaxosMessage receivedMessage;

    private static final Logger logger = Logger.getLogger(PaxosParticipant.class.getName());

    @Before
    public void setUp() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);
        logMessages = new ArrayList<>();

        mockServer = mock(NetworkServer.class);
        // thread safe
        mockQueue = mock(MessageQueue.class);
        nodes = Collections.singletonList(new PaxosParticipant.Node("localhost", 8001));

        latch = new CountDownLatch(1);
        participant = new PaxosParticipant(8000, 8001, nodes, mockServer, mockQueue) {
            public void receiveMessage(PaxosMessage message) {
                receivedMessage = message;
                latch.countDown();
                // this.stopMessageProcessingThread();
            }
        };

        try {
            doNothing().doThrow(new InterruptedException()).when(mockQueue).produceMessage(anyString());
            
            // Set up a blocking behavior for consumeMessage.
            Answer<String> blockingAnswer = invocation -> {
                // make thread wait indefinetely
                Thread.sleep(1000000000);
                return null;
            };

            // When consumeMessage is called:
            // - First, it returns a valid message.
            // - Then, it blocks indefinitely for subsequent calls.
            when(mockQueue.consumeMessage())
            .thenReturn("PREPARE;1;M1")  // first call returns the message
            .thenAnswer(blockingAnswer); // subsequent calls block
            
            doNothing().when(mockServer).startServer();
        } catch (Exception e) {
            logMessages.add("Exception thrown when mocking message queue");
        }
    }

    @After
    public void tearDown() {
        logMessages.forEach(System.out::println);

    }


    /**
     * Proposer initiates a proposal.
     *
     * Description: Test that the proposer correctly initiates a proposal.
     * Expectation: A 'prepare' message is sent to acceptors.
     */
    @Test
    public void testProposerInitiatesProposal() throws InterruptedException {
        logMessages.add("\n--- TEST: testProposerInitiatesProposal ---\n");

        // Start the proposer
        participant.start();

        // Wait for the message to be processed
        latch.await(1000, TimeUnit.MILLISECONDS);

        try {
            // Check that the message was processed
            assertNotNull("The message was not processed", receivedMessage);
            assertEquals("The message was not processed correctly", receivedMessage.toString(), new PaxosMessage("PREPARE", 1, "M1").toString());
            
            logMessages.add("The message was processed correctly");
        } catch (AssertionError e) {
            logMessages.add("Exception thrown when testing proposer initiates proposal");
            logMessages.add(e.getMessage());
            throw e;
        }

        // Stop the proposer
        participant.stopMessageProcessingThread();
    }

    /**
     * Proposer receives a promise from an acceptor.
     *
     * Description: Test that the proposer correctly handles a `promise` response from acceptors.
     * Expectation: The proposer should keep track of received promises and move to the next phase if a majority is reached.
     */
    @Test
    public void testProposerReceivesPromise() throws InterruptedException {
        logMessages.add("\n--- TEST: testProposerReceivesPromise ---\n");

        // Start the proposer
        participant.start();

        // Wait for the message to be processed
        latch.await(1000, TimeUnit.MILLISECONDS);

        try {
            // Check that the message was processed
            assertNotNull("The message was not processed", receivedMessage);
            assertEquals("The message was not processed correctly", receivedMessage.toString(), new PaxosMessage("PREPARE", 1, "M1").toString());
            
            logMessages.add("The message was processed correctly");
        } catch (AssertionError e) {
            logMessages.add("Exception thrown when testing proposer receives promise");
            logMessages.add(e.getMessage());
            throw e;
        }
    }

    /**
     * Proposer receives a nack from an acceptor.
     *
     * Description: Test handling of 'nack' responses to prepare requests. 
     * Expectation: The proposer may need to initiate a new proposal round with a higher proposal number.
     */
    @Test
    public void testProposerReceivesNack() throws InterruptedException {
        logMessages.add("\n--- TEST: testProposerReceivesNack ---\n");

        // Start the proposer
        participant.start();

        // Wait for the message to be processed
        latch.await(1000, TimeUnit.MILLISECONDS);

        try {
            // Check that the message was processed
            assertNotNull("The message was not processed", receivedMessage);
            assertEquals("The message was not processed correctly", receivedMessage.toString(), new PaxosMessage("PREPARE", 1, "M1").toString());
            
            logMessages.add("The message was processed correctly");
        } catch (AssertionError e) {
            logMessages.add("Exception thrown when testing proposer receives nack");
            logMessages.add(e.getMessage());
            throw e;
        }

    }
    
    /**
     * Proposer receives a majority of promises from acceptors.
     *
     * Description: Ensure the proposer sends 'accept' requests after Quorum of promises are received.
     * Expectation: 'accept' requests are sent out to acceptors.
     */
    @Test
    public void testProposerReceivesMajorityPromises() throws InterruptedException {
        logMessages.add("\n--- TEST: testProposerReceivesMajorityPromises ---\n");

        // Start the proposer
        participant.start();

        // Wait for the message to be processed
        latch.await(1000, TimeUnit.MILLISECONDS);

        try {
            // Check that the message was processed
            assertNotNull("The message was not processed", receivedMessage);
            assertEquals("The message was not processed correctly", receivedMessage.toString(), new PaxosMessage("PREPARE", 1, "M1").toString());
            
            logMessages.add("The message was processed correctly");
        } catch (AssertionError e) {
            logMessages.add("Exception thrown when testing proposer receives majority promises");
            logMessages.add(e.getMessage());
            throw e;
        }
    }

    /**
     * Proposer enters timeout state waiting for promises.
     *
     * Description: Test the proposer's behavior when it doesn't receive enough promise responses within a certain timeframe.
     * Expectation: The proposer may need to restart the proposal process with a higher proposal number.
     */
    @Test
    public void testProposerTimeout() throws InterruptedException {
        logMessages.add("\n--- TEST: testProposerTimeout ---\n");

    }


    /**
     * Proposer receives `accept` response from acceptor.
     *
     * Description: Test handling of 'accepted' messages from acceptors.
     * Expectation: The proposer should recognize when its proposal has been accepted by a majority.
     */
    @Test
    public void testProposerReceivesAccepted() throws InterruptedException {
        logMessages.add("\n--- TEST: testProposerReceivesAccepted ---\n");

    }

    // testProposerRecoveryAfterCrash

    // testProposerHandlesSplitVoteScenarios

    // testProposerDealsWithLateResponses

    // testMultipleProposersInteracting

}
