# Paxos Election System Design Document

## Introduction
This document describes the design considerations and architecture of the Paxos Election System for the Adelaide Suburbs Council Election.

## System Overview
The Paxos Election System is built on the Paxos consensus algorithm, tailored for the unique requirements of the Adelaide Suburbs Council.

## Components
### 1. Paxos Participants
`PaxosProposer`<br>
Handles initiating the proposal process for council president elections.

`PaxosAcceptor`<br>
Processes proposals from PaxosProposer and sends promises and accepted messages accordingly.

`PaxosLearner`<br>
Collects accepted messages from PaxosAcceptor to determine and notify of a consensus achievement.

### 2. Paxos Messages
Messages used for communication among participants.

`PaxosMessageQueue`

Manages sending and receiving of Paxos messages between participants.

## Behavior Profiles
- **Member M1:** Instant response.
- **Member M2:** Varying response times based on location.
- **Member M3:** Occasional no-response scenarios when camping in the Coorong.
- **Members M4-M9:** Randomized response times based on workload.

## Communication Mechanism
Participants communicate via sockets, ensuring real-time exchange of messages.