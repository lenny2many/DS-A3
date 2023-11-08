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

public class PaxosProposerTest {
    private List<String> logMessages;

    private static final Logger logger = Logger.getLogger(PaxosProposer.class.getName());

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
     * Test if the proposer can initiate the prepare phase.
     * This test checks if a proposer sends a prepare request correctly when starting the Paxos round.
     */
    @Test
    public void testInitiatePreparePhase() {
        // Test implementation
    }

    /**
     * Test if the proposer correctly handles rejection from an acceptor.
     * This test ensures that a proposer responds appropriately to a rejection message from an acceptor,
     * possibly by incrementing the proposal number and retrying.
     */
    @Test
    public void testHandleRejectionFromAcceptor() {
        // Test implementation
    }

    /**
     * Test if the proposer sends an accept request after receiving enough promises.
     * This test verifies that the proposer moves to the accept phase after a quorum of acceptors send promise messages.
     */
    @Test
    public void testSendingAcceptOnQuorumPromises() {
        // Test implementation
    }

    /**
     * Test the proposer's behavior when it receives conflicting promises.
     * This test checks how the proposer handles the situation where it receives promises that include values from other proposals.
     */
    @Test
    public void testHandlingConflictingPromises() {
        // Test implementation
    }

    /**
     * Test the proposer's behavior when a majority is not reached.
     * This test ensures that the proposer retries the prepare phase with a higher proposal number if it cannot get a majority of promises.
     */
    @Test
    public void testRetryingOnMajorityNotReached() {
        // Test implementation
    }
}
