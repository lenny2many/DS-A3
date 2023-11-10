package adelaidesuburbs;

import adelaidesuburbs.council.CouncilElection;

public class App {

    
    public static void main(String[] args) {
        boolean useImmediateResponses = true;
        int proposerCount = 1;
        CouncilElection council = new CouncilElection(useImmediateResponses, proposerCount);
        council.kickoffElection();
        council.announceResult();
    }
}
