# DS-A3
Distributed Systems: Assignment 3 Paxos

# Overview
This README provides essential information about the Adelaide Suburbs Council Election system, focusing on the Paxos algorithm's implementation. The provided Makefile facilitates the compilation and execution of the application and integration tests.

# Contents
/docs: Contains detailed documentation, including testing documents and implementation details.
/src: Source code for the Paxos implementation and the Adelaide Suburbs Council Election system.
/test: Test suites for unit and integration testing.
/lib: Required libraries for logging and testing.
/bin: Compiled binaries for source code and tests.
Makefile: A make utility to compile and run the application and tests.


# Makefile Usage
The Makefile includes targets for compiling the source code, running unit tests for the Paxos components, and executing the Council Election application. It is specifically designed for integration tests and running the Council Election system through the run_council_election target.

## Key Targets
- compile: Compiles the entire project.
- test_paxos_integration: Runs the integration tests for the Paxos implementation.
- run_council_election: Executes the App.java for the Council Election system.

## Testing
- Integration tests are located in test/java/integration.
- To execute integration tests, use the test_paxos_integration target.
- Test logs are saved in the specified log files for each test scenario.

## Running the Application
- To run the Council Election system, use the run_council_election target.
- This will execute App.java, which kicks off the election process.

## Documentation
- The /docs directory contains comprehensive information about the system's implementation and testing.
- It includes a detailed explanation of the Paxos algorithm, integration testing strategies, and outcomes.
- Screenshots and log excerpts in the documentation illustrate the system's behavior under various test scenarios.

## Additional Information
- For more detailed information about specific components or features, refer to the source code documentation in the /src directory.
- The Makefile is configured to handle dependencies and paths required for successful compilation and execution of the tests and application.
