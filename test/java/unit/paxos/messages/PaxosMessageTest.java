package paxos.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    /**
     * Test to ensure the proposal number is set correctly for a PaxosMessage.
     */
    @Test
    public void testMessageProposalNumber() {
        logMessages.add("\n--- TEST: testMessageProposalNumber ---\n");

        int expectedProposalNumber = 1;

        try {
            PaxosMessage message = new PaxosMessage("PREPARE", expectedProposalNumber, "SomeValue");
            assertEquals("Checking proposal number", expectedProposalNumber, message.getProposalNumber());

            logMessages.add("Test passed: Proposal number {" + expectedProposalNumber + "} was set successfully.\n");
        } catch (AssertionError e) {
            logMessages.add("Test failed: Proposal number was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            fail(e.getMessage());
        } catch (Exception e) {
            logMessages.add("Test failed: Proposal number was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            fail(e.getMessage());
        }
    }

    /**
     * Test to ensure the value is set correctly for a PaxosMessage.
     */
    @Test
    public void testMessageValue() {
        logMessages.add("\n--- TEST: testMessageValue ---\n");

        String expectedValue = "SomeValue";

        try {
            PaxosMessage message = new PaxosMessage("PREPARE", 1, expectedValue);

            assertEquals("Checking message value", expectedValue, message.getValue());

            logMessages.add("Test passed: Message value {" + expectedValue + "} was set successfully.\n");
        } catch (AssertionError e) {
            logMessages.add("Test failed: Message value was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            fail(e.getMessage());
        } catch (Exception e) {
            logMessages.add("Test failed: Message value was not set successfully.");
            logMessages.add(e.getMessage() + "\n");
            fail(e.getMessage());
        }
    }

    /**
     * Test to ensure the type is set correctly for a PaxosMessage.
     */
    @Test
    public void testAllValidMessageTypes() {
        logMessages.add("\n--- TEST: testAllValidMessageTypes ---\n");

        boolean passed = true;

        String[] expectedTypes = {"PREPARE", "PROMISE", "ACCEPT_REQUEST", "ACCEPTED"};

        for (String expectedType : expectedTypes) {

            try {
                PaxosMessage message = new PaxosMessage(expectedType, 1, "SomeValue");

                assertEquals("Checking message type", expectedType, message.getType());

                logMessages.add("Test passed: Message type {" + expectedType + "} was set successfully.\n");
            } catch (AssertionError e) {
                logMessages.add("Test failed: Message type {" + expectedType + "} was not set successfully.");
                logMessages.add(e.getMessage() + "\n");
                passed = false;
            } catch (Exception e) {
                logMessages.add("Test failed: Message type {" + expectedType + "} was not set successfully.");
                logMessages.add(e.getMessage() + "\n");
                passed = false;
            }
        }
        if (!passed) {
            fail("One or more message types were not set successfully.");
        }
    }

    /**
     * Test to ensure an IllegalArgumentException is thrown for an invalid message type when new PaxosMessage is created.
     */
    @Test
    public void testInvalidMessageType() {
        logMessages.add("\n--- TEST: testInvalidMessageType ---\n");

        String expectedType = "INVALID_TYPE";
        
        try {
            // The following line should throw an IllegalArgumentException.
            PaxosMessage message = new PaxosMessage(expectedType, 1, "SomeValue");

            logMessages.add("Test failed: IllegalArgumentException was not thrown for invalid message type {" + expectedType + "}.\n");
            fail("IllegalArgumentException was not thrown for invalid message type {" + expectedType + "}.");
        } catch (IllegalArgumentException e) {
            logMessages.add("Test passed: IllegalArgumentException was thrown for invalid message type {" + expectedType + "}.");
            logMessages.add(e.getMessage() + "\n");
        }
    }

    
    /**
     * Test to ensure that parseMessageFromString correctly parses a valid message string.
     */
    @Test
    public void testParseValidMessageFromString() {
        logMessages.add("\n--- TEST: testParseValidMessageFromString ---\n");

        String validMessageString = "PREPARE;42;SomeValue";

        try {
            Optional<PaxosMessage> messageOpt = PaxosMessage.parseMessageFromString(validMessageString);

            boolean passed = true;

            // If messageOpt is present, check that the message was parsed correctly.
            if (messageOpt.isPresent()) {
                PaxosMessage message = messageOpt.get();

                // Ensure that each field was parsed correctly.
                if (message.getProposalNumber() != 42) {
                    logMessages.add("Test failed: Expected proposal number {42}, found {" + message.getProposalNumber() + "}.");
                    passed = false;
                }
                if (!message.getValue().equals("SomeValue")) {
                    logMessages.add("Test failed: Expected value {SomeValue}, found {" + message.getValue() + "}.");
                    passed = false;
                }
                if (!message.getType().equals("PREPARE")) {
                    logMessages.add("Test failed: Expected type {PREPARE}, found {" + message.getType() + "}.");
                    passed = false;
                }

                if (passed) {
                    logMessages.add("Test passed: Message string {" + validMessageString + "} was parsed successfully.\n");
                } else {
                    logMessages.add("Test failed: Message string {" + validMessageString + "} was not parsed successfully.\n");
                    fail(logMessages.get(logMessages.size() - 1));
                }
            
            // If messageOpt is empty, the test failed.
            } else {
                logMessages.add("Test failed: Message string {" + validMessageString + "} was not parsed successfully.\n");
                fail(logMessages.get(logMessages.size() - 1));
            }
        } catch (Exception e) {
            logMessages.add("Test failed: IllegalArgumentException was thrown for valid message type {" + validMessageString + "}.");
            logMessages.add("PaxosMessage constructor threw exception: " + e.getMessage() + "\n");
            fail(e.getMessage());
        }

        
    }

    /**
     * Test to ensure that parseMessageFromString returns an empty Optional for an invalid message format.
     */
    @Test
    public void testParseInvalidMessageFromString() {
        logMessages.add("\n--- TEST: testParseInvalidMessageFromString ---\n");

        String invalidMessageString = "INVALID_FORMAT";
        Optional<PaxosMessage> messageOpt = PaxosMessage.parseMessageFromString(invalidMessageString);

        if (!messageOpt.isPresent()) {
            logMessages.add("Test passed: Empty Optional was returned for invalid message string {" + invalidMessageString + "}.\n");
        } else {
            logMessages.add("Test failed: Empty Optional was not returned for invalid message string {" + invalidMessageString + "}.\n");
        }
    }
}
