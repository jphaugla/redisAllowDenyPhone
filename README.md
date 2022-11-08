# redisAllowDenyPhone
Caches allow/deny rules in a local java application server with a backend of the cache stored in redis.

## Outline
- [Overview](#overview)
- [Important Links](#important-linksnotes)
- [Instructions](#instructions)
    - [Multiple Deployment options](#multiple-options-for-creating-the-environment)
    - [Create Environment](#create-environment)
      - [Set the Environment](#set-the-environment)
      - [Start docker redis](#start-redis-with-docker)
    - [Use the Application](#using-the-application)
        - [Start Publisher](#start-publisher)
        - [Start Cache Instance](#start-cache-instance)
        - [Use the API](#api-calls)

## Overview
Manages a local to the application java cache using redis consumer part of redis streaming.  Also includes,
the producer as well as a bulk load from the objects stored in Redis. Assumes low write to read ratio as each 
application instance writes the data.

## Important Links/Notes
* [using pipelining with jedis](https://www.oreilly.com/library/view/redis-4x-cookbook/9781783988167/cd01c002-69f0-465a-af6c-1357558ffa71.xhtml)
* [pipelining with jedis 4 responses](https://www.baeldung.com/jedis-java-redis-client-library)
* [jedis scan](https://www.baeldung.com/redis-list-available-keys)
* [Spring Data Redis Reactive](https://www.baeldung.com/spring-data-redis-reactive)
* [Redis Stream with Spring Boot](https://www.vinsguru.com/redis-stream-with-spring-boot/)
* [github for blog](https://github.com/vinsguru/vinsguru-blog-code-samples/tree/master/redis/redis-stream)
* [java faker data](https://www.baeldung.com/java-faker)

## Instructions
### Create environment
Clone the github
```bash 
get clone https://github.com/jphaugla/redisAllowDenyPhone.git
```
### Set the environment
The docker compose file does not have an application server yet-this will be added later  The redis database must have redisearch installed.  In docker, the redis stack image contains all the modules.   In k8s, redisearch is added in the database yaml file.
Check the environment variables for appropriateness.   If outside of a container, there is a file created with
environment variable at scripts/setEnvironment.sh.

| variable               | Original Value     | Desccription                                                                                           |
|------------------------|--------------------|--------------------------------------------------------------------------------------------------------|
| REDIS_HOST             | redis              | The name of the redis docker container                                                                 |
| REDIS_PASSWORD         | <none>             | Redis Password  (not used yet)                                                                         |
| REDIS_PORT             | 6379               | redis port                                                                                             |     
| REDIS_URL              | redis://redis:6379 | redis URL                                                                                              |     
| ---------------------- | -----------------  | ------------------------------------------------------------------------------------------------------ |

### Multiple options for creating the environment:
* run with docker-compose using a maven container and redis container (maven container not completed yet)
* installing for mac os
* running on linux (probably in the cloud)
* running on kubernetes (not done yet)

### Start redis with docker
```bash
docker-compose up -d 
```
### Using the application
The application has a cache instance and a redis streams publisher.  

The cache instance has both a Rest Controller and a redis streams consumer.  
* On startup, the publisher will start randomly generating data using [java faker data](https://www.baeldung.com/java-faker)
* The consumer will write this data to redis and also update the in-memory cache.
* On consumer startup, the consumer will use scan to read all the redis hashes starting with "rle*" to refresh the cache
* On consumer startup, a consumer group is created to read messages
* While consumer is reading redis hashes, consumer group will already start reading also-assumes idempotency.
* API commands allow validating the cache and making addition calls

#### Start cache-instance
```bash
source scripts/setEnvironment.sh
cd cache-instance
java -jar target/cache-instance-0.0.1-SNAPSHOT.jar
```
#### Start publisher
Use a separate terminal window for this
```bash
source scripts/setEnvironment.sh
cd redis-publisher
java -jar target/redis-publisher-0.0.1-SNAPSHOT.jar
```
#### API calls
```bash
cd scripts 
# add some records
./postRule.sh
# get one key from database
./getKey.sh
# get one key from application cache
./getCache.sh
# reload all the data in cache from the redis and also truncate any existing data
./reloadData.sh
# dump cache - string print of cache
./dumpCache.sh
# check cache - get size of the client cache
./checkCache.sh
```
