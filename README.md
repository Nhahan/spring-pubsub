# Spring Pub/Sub Testing Project

This project aims to validate the functionality and compare the performance of Server-Sent Events (SSE) and WebSocket-based APIs using both a traditional Spring MVC approach and a reactive WebFlux-based approach. Redis Pub/Sub is used as a message broker to distribute messages between these components.

## Prerequisites

- ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white) : Required to build and run the project modules using Docker Compose.

## Project Modules

1. `mvc-module`
   - Purpose: Provides SSE and WebSocket-based API endpoints using Spring MVC.
   - API Endpoints:
     - SSE Subscription: Subscribes clients to a topic via SSE.
       - Endpoint: `GET /topics/{topic}/subscribe`
     - WebSocket Subscription: Allows clients to connect and subscribe to a topic via WebSocket.
       - Endpoint: `ws://{host}:{port}/topics/{topic}/subscribe`
2. `webflux-module`
   - Purpose: Provides an SSE API using a reactive approach (WebFlux) for topic-based subscriptions.
   - API Endpoints:
     - SSE Subscription: Subscribes clients to a topic via a reactive SSE stream.
       - Endpoint: `GET /topics/{topic}/subscribe`
3. `test-module`
   - Purpose: Facilitates functional and performance testing of the `mvc-module` and `webflux-module` SSE APIs.
   - API Endpoints:
     - Functional Testing Endpoint:
       - Endpoint: `POST /topics/{topic}/publish`
       - Parameters:
         - `topic`: The topic to which a single UUID message is sent.
       - Function: Publishes a single UUID message to the topic to verify that SSE (in both `mvc-module` and `webflux-module`) and WebSocket (in `mvc-module`) APIs function as expected.
   - Performance Testing Endpoint:
     - Endpoint: `GET /topics/{topic}/test`
       - Parameters:
         - topic: The topic to which messages are sent.
         - messageCount: Number of messages to publish for load testing.
       - Function: Simulates high-traffic load by publishing the specified number of messages to the topic. This endpoint is used to compare the performance of SSE in `mvc-module` vs. `webflux-module`.

## Setup and Usage

1. Run Project Modules:
    - Execute the `./run.sh` script in the root directory to build and launch the project modules via Docker Compose. This script handles Docker builds and container orchestration automatically.
2. Functional Test:
    - Use the `POST /topics/{topic}/publish` endpoint in `test-module` to publish a single UUID message to a topic. This verifies the basic functionality of each module’s SSE and WebSocket endpoints, ensuring that Redis Pub/Sub, SSE, and WebSocket components are correctly integrated.
3. Performance Test:
    - Use the `GET /topics/{topic}/test` endpoint in `test-module` to simulate high traffic by sending multiple messages to a specified topic. This facilitates a comparative performance test of the SSE endpoints in `mvc-module` and `webflux-module`.

## Expected Outcomes

- Functional Test: All subscriptions (SSE and WebSocket) across modules should successfully receive the UUID message, confirming the correct operation of Redis Pub/Sub with the SSE and WebSocket functionalities.
- Performance Test: This evaluates response times and the reliability of both `mvc-module` and `webflux-module` SSE implementations under high traffic conditions, helping to understand their relative performance.
