package integration;

import adelaidesuburbs.council.CouncilElection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class PaxosIntegrationTests {

    private static Logger rootLogger = Logger.getLogger("");

    @Before
    public void setup() {
    }

    @Test
    public void testImmediateResponseOneProposer() {
        // set file name for each test
        String logFileName = "logs/testImmediateResponseOneProposer.log";
        setupLogging(logFileName);

        boolean useImmediateResponses = true;
        int proposerCount = 1;

        CouncilElection council = new CouncilElection(useImmediateResponses, proposerCount);
        council.kickoffElection();
        council.announceResult();
    }

    @Test
    public void testVariousDelaysOneProposer() {
        // set file name for each test
        String logFileName = "logs/testVariousDelaysOneProposer.log";
        setupLogging(logFileName);

        boolean useImmediateResponses = false;
        int proposerCount = 1;

        CouncilElection council = new CouncilElection(useImmediateResponses, proposerCount);
        council.kickoffElection();
        council.announceResult();
    }

    @Test
    public void testTwoProposersConcurrent() throws InterruptedException {
        // set file name for each test
        String logFileName = "logs/testTwoProposersConcurrent.log";
        setupLogging(logFileName);

        boolean useImmediateResponses = true;
        int proposerCount = 2;
        CouncilElection council = new CouncilElection(useImmediateResponses, proposerCount);
        council.kickoffElection();
        council.announceResult();
    }

    @Test
    public void testTwoProposersConcurrentVariousDelays() throws InterruptedException {
        // set file name for each test
        String logFileName = "logs/testTwoProposersConcurrentVariousDelays.log";
        setupLogging(logFileName);

        boolean useImmediateResponses = false;
        int proposerCount = 2;
        CouncilElection council = new CouncilElection(useImmediateResponses, proposerCount);
        council.kickoffElection();
        council.announceResult();
    }

    private static void setupLogging(String logFileName) {
        try {
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO); // Set the desired level
        } catch (IOException e) {
            rootLogger.log(Level.SEVERE, "Error setting up logger", e);
        }
    }
    
    @After
    public void tearDown() {
        // Close all handlers (important to flush logs to file)
        for (Handler handler : rootLogger.getHandlers()) {
            handler.close();
        }
    }
}
