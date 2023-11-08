package paxos.participants;

import paxos.messages.*;
import paxos.network.*;

import java.util.Collections;
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
 * Unit tests for the PaxosParticipant module.
 */
public class PaxosParticipantTest {
    private List<String> logMessages;
    private PaxosParticipant participant;
    private NetworkServer mockServer;
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
            .thenThrow(new InterruptedException());  // subsequent calls block
            
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
     * Test if receiveMessage is called when a valid message is processed.
     */
    @Test
    public void testReceiveMessageIsCalled() throws InterruptedException {
        logMessages.add("\n--- TEST: testReceiveMessageIsCalled ---\n");

        // Arrange
        PaxosMessage expectedMessage = new PaxosMessage("PREPARE", 1, "M1");
        
        try {
            // Act
            participant.startMessageProcessingThread();
            participant.messageQueue.produceMessage(expectedMessage.toString());

            boolean awaitSuccess = latch.await(2, TimeUnit.SECONDS);
            
            // Assert
            assertTrue("The message was not processed in time.", awaitSuccess);

            participant.stopMessageProcessingThread();


            verify(mockQueue, times(2)).consumeMessage();
            assertEquals(expectedMessage.toString(), receivedMessage.toString());
            logMessages.add("Test passed: receiveMessage was called and message was consumed once only");
        } catch (AssertionError e) {
            logMessages.add("Test failed: receiveMessage was not called");
            logMessages.add(e.getMessage());
            throw e;
        } finally {
            participant.stopMessageProcessingThread(); // Make sure to stop the thread after the test
        }
    }
}
