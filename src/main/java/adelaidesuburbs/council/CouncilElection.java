package adelaidesuburbs.council;

import paxos.participants.PaxosParticipant.DelayProfile;
import paxos.participants.PaxosParticipant.Node;
import paxos.participants.PaxosProposer;
import paxos.participants.PaxosAcceptor;

import java.util.List;
import java.util.ArrayList;

import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * This year, Adelaide Suburbs Council is holding elections for council president.
 * Any member of its nine person council is eligible to become council president.
 * 
 * Member M1:
 * M1 has wanted to be council president for a very long time. M1 is very chatty
 * over social media and responds to emails/texts/calls almost instantly. It is 
 * as if M1 has an in-brain connection with their mobile phone!
 * 
 * Member M2:
 * M2 has also wanted to be council president for a very long time, except their 
 * very long time is longer than everybody else's. M2 lives in the Adelaide Hills 
 * and thus their internet connection is really poor, almost non-existent. Responses 
 * to emails come in very late, and sometimes only to one of the emails in the email 
 * thread, so it is unclear whether M2 has read/understood them all. However, M2 
 * sometimes likes to work at Sheoak Café. When that happens, their responses are 
 * instant and M2 replies to all emails.
 * 
 * Member M3:
 * M3 has also wanted to be council president. M3 is not as responsive as M1, nor as 
 * late as M2, however sometimes emails completely do not get to M3. The other 
 * councillors suspect that it’s because sometimes M3 goes camping in the Coorong, 
 * completely disconnected from the world.
 * 
 * Members M4-M9:
 * have no particular ambitions about council presidency and no particular preferences 
 * or animosities, so they will try to vote fairly. Their jobs keep them fairly busy 
 * and as such their response times  will vary.
 * 
 * How does voting happen: 
 * On the day of the vote, one of the councillors will send out an email/message to 
 * all councillors with a proposal for a president. A majority (half+1) is required 
 * for somebody to be elected president.
 */

public class CouncilElection {
    private List<CouncilMember> members;
    private List<Node> nodes;
    private static final int acceptorPortBase = 8000;
    private static final int proposerPortBase = 9000;

    private static final Logger logger = Logger.getLogger(CouncilElection.class.getName());

    public CouncilElection(boolean useImmediateResponses) {
        this.nodes = new ArrayList<>();
        this.members = new ArrayList<>();
        initialiseNodes();
        initialiseMembers(useImmediateResponses);
    }

    private void initialiseNodes() {
        // Create nodes for each council member
        for (int memberId = 1; memberId <= 9; memberId++) {
            nodes.add(new Node("M" + memberId, "localhost", acceptorPortBase + memberId, proposerPortBase + memberId));
        }
    }

    private void initialiseMembers(boolean useImmediateResponses) {
        logger.info("INITIALISING COUNCIL");

        // Profiles for demonstration
        DelayProfile[] profiles = DelayProfile.values();

        // Create council members
        for (int memberId = 1; memberId <= 9; memberId++) {
            boolean isProposer = determineIfMemberIsProposer(memberId);
            Node serverNode = new Node("M" + memberId, "localhost", acceptorPortBase + memberId, proposerPortBase + memberId);

            DelayProfile profile;
            if (useImmediateResponses) {
                // Set all profiles to IMMEDIATE_RESPONSE
                profile = DelayProfile.IMMEDIATE_RESPONSE;
            } else {
                // Assign random profiles to members
                profile = profiles[memberId % profiles.length]; // Assign profiles in a round-robin fashion
            }

            CouncilMember member = new CouncilMember(memberId, serverNode, nodes, isProposer, profile);
            members.add(member);
            member.startParticipant();  // Start the Paxos roles for the member
        }
        printCouncilDetailsHelper();
    }

    private boolean determineIfMemberIsProposer(int memberId) {
        // Logic to determine the role of each member (e.g., M1, M2, M3 can be proposers)
        // return memberId == 1; // For simplicity, only M1 is proposer
        return memberId <= 3; // M1, M2, M3 are all proposers
    }

    public void kickoffElection() {
        logger.info("STARTING ELECTION");
        CouncilMember proposer = members.stream()
                                        .filter(member -> member.isProposer())
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("No proposer found."));
        proposer.startProposal(proposer.selfNode.getNodeName());
    }

    public void announceResult() {
        logger.info("ANNOUNCING RESULT");
        // Announce the result of the election
        // The result is determined once a majority consensus is reached
    }

    private void printCouncilDetailsHelper() {
    System.out.println();
    System.out.println("Details about the council:");
    for (CouncilMember member : members) {
        System.out.println(String.format("    Name: M%s", member.memberId));
        System.out.println(String.format("    Port: %s,%s", member.selfNode.getAcceptorPort(), member.selfNode.getProposerPort()));
        System.out.println(String.format("    ConnectedTo: [%s]", member.getConnectedNodes().stream()
            .map(node -> String.valueOf(node.getNodeName()))
            .collect(Collectors.joining(", "))));
        System.out.println(String.format("    DelayProfile: %s", member.profile));

        // Constructing the roles string
        List<String> roles = new ArrayList<>();
        if (member.proposerRole != null) {
            roles.add("Proposer");
        }
        if (member.acceptorRole != null) {
            roles.add("Acceptor");
        }
        String rolesString = String.join(", ", roles);
        System.out.println("    Role: " + rolesString);

        System.out.println();
    }
}


    
    public static class CouncilMember {
        private int memberId;
        private Node selfNode;
        private PaxosAcceptor acceptorRole;
        private PaxosProposer proposerRole;
        private List<Node> connectedNodes;
        private DelayProfile profile;
    
        public CouncilMember(int memberId, Node selfNode, List<Node> connectedNodes, boolean isProposer, DelayProfile profile) {
            this.memberId = memberId;
            this.selfNode = selfNode;
            this.profile = profile;
            this.connectedNodes = new ArrayList<>(connectedNodes);

            // Initialise the Paxos roles for the member
            this.acceptorRole = new PaxosAcceptor(selfNode, connectedNodes, profile);
            if (isProposer) {
                this.proposerRole = new PaxosProposer(selfNode, connectedNodes, profile);
            }
        }

        public void startProposal(String proposedValue) {
            if (this.proposerRole != null) {
                this.proposerRole.startProposal(proposedValue);
            }
        }

        public void startParticipant() {
            this.acceptorRole.start();
            if (this.proposerRole != null) {
                this.proposerRole.start();
            }
        }

        public boolean isProposer() {
            return proposerRole != null;
        }

        @Override
        public String toString() {
            return "CouncilMember{" +
                    "memberId=" + memberId +
                    ", roles=" + getRolesString() +
                    ", connectedNodes=" + connectedNodes +
                    '}';
        }

        private String getRolesString() {
            List<String> roles = new ArrayList<>();
            roles.add("Acceptor");
            if (isProposer()) {
                roles.add("Proposer");
            }
            return String.join(", ", roles);
        }

        public List<Node> getConnectedNodes() {
            return connectedNodes;
        }
    }

       
}
