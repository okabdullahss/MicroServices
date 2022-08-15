# MicroServices
A simple application demonstrating how basic microservices mechanism work and microservices communicate each other

1- Eureka Discover Server - reponsible for tie up all components of the project, microservices, configs, gateway

2- API Gateway - intercepts the request from client, and hence the request doesnt reach the microservices directly.

3- Reservation Service, Car Service - two microservices in this project, impoting the "core" project as a dependency, that includes all the common necessities of 2 microservices

