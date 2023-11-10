package adelaidesuburbs;

import adelaidesuburbs.council.CouncilElection;

public class App {

    
    public static void main(String[] args) {
        CouncilElection council = new CouncilElection();
        council.kickoffElection();
        // council.announceResult();
    }
}
