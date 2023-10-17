# Paxos Election System Testing Guide
## Introduction
This document outlines the testing strategy, scenarios, and results for the Paxos Election System used in the Adelaide Suburbs Council Election.

## Testing Strategy
Our testing strategy ensures that the Paxos algorithm, as adapted for our election scenario, functions reliably and handles edge cases, member behaviors, and potential communication interruptions.

### Testing Tools & Frameworks:
- **JUnit:** For unit and integration testing of the Java codebase.
- **Mockito:** For mocking objects and testing in isolation.
- **Log4j:** To generate logs that can be analyzed post-test for any unexpected behavior.

## Test Scenarios
### Unit Tests:
`PaxosProposer`<br>
- Test the initialization of proposals.
- Test response handling when receiving promises.
- Test response handling when receiving accepted messages.

`PaxosAcceptor`<br>
- Test promise generation upon receiving a proposal.
- Test the handling of multiple simultaneous proposals.
- Test accept message generation.

`PaxosLearner`<br>
- Test the tallying of accepted messages.
- Test consensus achievement notification.

`PaxosMessage & PaxosMessageQueue`<br>
- Test message creation and types.
- Test thread-safe exchange of messages between participants.

### Integration Tests:
`Basic Consensus Achievement`<br>
- Simulate a straightforward election where all members respond promptly.

`Simultaneous Proposals`<br>
- Simulate scenarios where two councillors send voting proposals simultaneously.

`Varying Response Times`<br>
- Introduce artificial delays to mimic behaviors of M1, M2, and M3.

`Offline Proposers`<br>
- Test scenarios where proposers like M2 or M3 go offline post-proposal.

`Network Interruptions`<br>
- Simulate message drops and network delays to ensure the robustness of the algorithm.

## Testing Results
After running the aforementioned scenarios: