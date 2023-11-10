package adelaidesuburbs;

import adelaidesuburbs.council.CouncilElection;

public class App {

    
    public static void main(String[] args) {
        boolean useImmediateResponses = true;
        CouncilElection council = new CouncilElection(useImmediateResponses);
        council.kickoffElection();
        // council.announceResult();
    }
}
