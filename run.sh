#!/bin/bash

JAR_FILE="textFinder.jar"

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=127.0.0.1:5005 -jar "$JAR_FILE" "$@"
