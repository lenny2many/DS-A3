package paxos.messages;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

/**
 * Unit tests for the PaxosMessage module.
 */
public class PaxosMessageTest {
    List<String> logMessages;

    private static final Logger logger = Logger.getLogger(PaxosMessage.class.getName());

    @Before
    public void setUp() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.OFF);
        logMessages = new ArrayList<>();
    }

    @After
    public void tearDown() {
        logMessages.forEach(System.out::println);
    }

    @Test
    public void testMessageProposalNumber() {
        logMessages.add("\n--- TEST: testMessageProposalNumber ---\n");

        int expectedProposalNumber = 1;
        PaxosMessage message = new PaxosMessage("PREPARE", expectedProposalNumber, "SomeValue");

        try {
            assertEquals("Checking proposal number", expectedProposalNumber, message.getProposalNumber());

            logMessages.add("Test passed: Proposal number {" + expectedProposalNumber + "} was set successfully.\n");
        } catch (Error e) {
            logMessages.add("Test failed: Proposal number was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
        } catch (Exception e) {
            logMessages.add("Test failed: Proposal number was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            // throw e;
        }
    }

    @Test
    public void testMessageValue() {
        logMessages.add("\n--- TEST: testMessageValue ---\n");

        String expectedValue = "SomeValue";
        PaxosMessage message = new PaxosMessage("PREPARE", 1, expectedValue);

        try {
            assertEquals("Checking message value", expectedValue, message.getValue());

            logMessages.add("Test passed: Message value {" + expectedValue + "} was set successfully.\n");
        } catch (Error e) {
            logMessages.add("Test failed: Message value was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
        } catch (Exception e) {
            logMessages.add("Test failed: Message value was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            // throw e;
        }
    }


    @Test
    public void testAllMessageTypes() {
        logMessages.add("\n--- TEST: testAllMessageTypes ---\n");

        String[] expectedTypes = {"PREPARE", "PROMISE", "ACCEPT_REQUEST", "ACCEPTED"};

        for (String expectedType : expectedTypes) {
            PaxosMessage message = new PaxosMessage(expectedType, 1, "SomeValue");

            try {
                assertEquals("Checking message type", expectedType, message.getType());

                logMessages.add("Test passed: Message type {" + expectedType + "} was set successfully.\n");
            } catch (Error e) {
                logMessages.add("Test failed: Message type {" + expectedType + "} was not set successfully.");
                logMessages.add(e.getMessage() + "\n");
            } catch (Exception e) {
                logMessages.add("Test failed: Message type {" + expectedType + "} was not set successfully.");
                logMessages.add(e.getMessage() + "\n");
                throw e;
            }
        }
    }

    @Test
    public void testInvalidMessageType() {
        logMessages.add("\n--- TEST: testInvalidMessageType ---\n");

        String expectedType = "INVALID_TYPE";
        

        try {
            // The following line should throw an IllegalArgumentException.
            PaxosMessage message = new PaxosMessage(expectedType, 1, "SomeValue");

            logMessages.add("Test failed: IllegalArgumentException was not thrown for invalid message type {" + expectedType + "}.\n");
        } catch (IllegalArgumentException e) {
            logMessages.add("Test passed: IllegalArgumentException was thrown for invalid message type {" + expectedType + "}.");
            logMessages.add(e.getMessage() + "\n");
        }
    }

}
