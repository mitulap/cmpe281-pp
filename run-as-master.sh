#!/bin/bash
sudo rm -r build/*
javac -cp lib/\* -d build src/com/cmpe281/restpackage/*.java
javac -cp lib/\* -d build src/com/cmpe281/paulpackage/*.java
java -cp build:lib/\* com/cmpe281/restpackage/SimpleRestServiceApplication master