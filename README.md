# Spring Pub/Sub Testing Project

This project validates functionality and compares performance of Server-Sent Events (SSE) and WebSocket APIs using Spring MVC and reactive WebFlux, with Redis Pub/Sub as the message broker.

## Prerequisites

![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) : Required to build and run the project modules using Docker Compose.

## Setup and Usage

1. **Run Project Modules**:
    - Execute the `./run.sh` script in the root directory to build and launch the project modules via Docker Compose. This script handles Docker builds and container orchestration automatically.
2. **Functional Test**:
    - Use the `POST /topics/{topic}/publish` endpoint in `test-module` to publish a single UUID message to a topic. This verifies the basic functionality of each module’s SSE and WebSocket endpoints, ensuring that Redis Pub/Sub, SSE, and WebSocket components are correctly integrated.
3. **Performance Test**:
    - Use the `GET /topics/{topic}/test` endpoint in `test-module` to simulate high traffic by sending multiple messages to a specified topic. This facilitates a comparative performance test of the SSE endpoints in `mvc-module` and `webflux-module`.

## Project Modules

1. `mvc-module`
   - **Purpose**: Provides SSE and WebSocket-based API endpoints using Spring MVC.
   - **API Endpoints**:
     - **SSE Subscription**: Subscribes clients to a topic via SSE.
       - **Endpoint**: `GET /topics/{topic}/subscribe`
     - **WebSocket Subscription**: Allows clients to connect and subscribe to a topic via WebSocket.
       - **Connection Endpoint**: `ws://localhost:8081/ws`
       - **Topic Subscription**: After establishing a WebSocket connection, send a message in the format `subscribe:{topic}` to subscribe to a specific topic.
       - **Message Delivery**: After subscribing to a topic, messages are sent to the client via the WebSocket connection.
2. `webflux-module`
   - **Purpose**: Provides an SSE API using a reactive approach (WebFlux) for topic-based subscriptions.
   - **API Endpoints**:
     - **SSE Subscription**: Subscribes clients to a topic via a reactive SSE stream.
       - **Endpoint**: `GET /topics/{topic}/subscribe`
3. `test-module`
   - **Purpose**: Facilitates functional and performance testing of the `mvc-module` and `webflux-module` SSE APIs.
   - **API Endpoints**:
     - **Functional Testing Endpoint**:
       - **Endpoint**: `POST /topics/{topic}/publish`
       - **Parameters**:
         - `topic`: The topic to which a single UUID message is sent.
       - **Function**: Publishes a single UUID message to the topic to verify that SSE (in both `mvc-module` and `webflux-module`) and WebSocket (in `mvc-module`) APIs function as expected.
   - **Performance Testing Endpoint**:
     - **Endpoint**: `GET /topics/{topic}/test`
       - **Parameters**:
         - **topic**: The topic to which messages are sent.
         - **messageCount**: Number of messages to publish for load testing.
       - **Function**: Simulates high-traffic load by publishing the specified number of messages to the topic. This endpoint is used to compare the performance of SSE in `mvc-module` vs. `webflux-module`.

## Expected Outcomes

- **Functional Test**: All subscriptions (SSE and WebSocket) across modules should successfully receive the UUID message, confirming the correct operation of Redis Pub/Sub with the SSE and WebSocket functionalities.
- **Performance Test**: This evaluates response times and the reliability of both `mvc-module` and `webflux-module` SSE implementations under high traffic conditions, helping to understand their relative performance.

## Performance Comparison: MVC vs. WebFlux
This compares `mvc-module` and `webflux-module` on **total message consumption time** across different message counts via $\textbf{\normalsize{\color{#D32F2F}SSE}}$.

### Test Stages and Expected Metrics

Each stage involves publishing a set number of messages to the topic and recording the total time taken for message consumption.

| Stage | Number of Messages | `mvc-module` Total Consumption Time (s) | `webflux-module` Total Consumption Time (s) |
|-------|--------------------|-----------------------------------------|---------------------------------------------|
| 1     | 1,000              | 0.481                                   | 0.295                                       |
| 2     | 5,000              | 0.830                                   | 0.421                                       |
| 3     | 10,000             | 2.127                                   | 0.934                                       |
| 4     | 50,000             | 7.130                                   | 2.372                                       |
| 5     | 100,000            | 12.847                                  | 4.137                                       |
| 6     | 500,000            | 69.808                                  | 24.844                                      |
| 7     | 1,000,000          | `java.io.IOException: Broken pipe`      | 42.323                                      |
| 8     | 5,000,000          | `java.io.IOException: Broken pipe`      | 197.264                                     |
| 9     | 10,000,000         | `java.io.IOException: Broken pipe`      | 446.924                                     |

> **Note**: Measured on an M1 Pro base model.

This table serves as a benchmark to compare the relative efficiency of Spring MVC and WebFlux implementations under increasing load, helping to identify which approach scales better for real-time streaming with Redis Pub/Sub.
