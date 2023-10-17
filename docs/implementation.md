# Paxos Election System Implementation Notes
## Introduction
This document provides an overview of the implementation details and challenges faced during the development of the Paxos Election System.

## Implementation Overview
The system is developed in Java, making use of object-oriented design patterns to represent Paxos participants and messages.

### Paxos Participants
Each participant extends the PaxosParticipant abstract class, ensuring a common interface for sending and receiving messages.

`PaxosProposer`<br>
- Initiates the proposal process and handles promises.
- If a majority is achieved, sends out accept requests.

`PaxosAcceptor`<br>
- Maintains the highest proposal number seen.
- Sends promises if the received proposal number is higher than any seen before.
- Sends accepted messages once an accept request is received and validated.

`PaxosLearner`<br>
- Collects accepted messages and determines when a consensus is achieved.

### Paxos Messages
Messages are implemented as objects with types (`PREPARE`, `PROMISE`, `ACCEPT`, `ACCEPTED`).

`PaxosMessageQueue`<br>
- Implemented using a concurrent message queue.
- Ensures thread-safe exchange of messages between participants.

## Challenges & Solutions
1. Handling Simultaneous Proposals:
    - Used proposal numbers combined with participant IDs to ensure uniqueness and to resolve conflicts.

2. Handling Member Behavior:
    - Introduced artificial delays and drop rates to simulate behaviors of M1, M2, and M3.

3. Achieving Consensus:
    - Implemented retry mechanisms for proposers if a consensus isn't reached within a given time frame.

4. Network Failures:
    - Implemented message acknowledgments and retries to handle potential message losses.

## Testing
- Unit tests were created for each participant type and message exchange logic.
- Integration tests simulated the entire election process, incorporating the different member behaviors.