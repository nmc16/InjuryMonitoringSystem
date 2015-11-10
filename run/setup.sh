#!/bin/bash

BASEDIR=`pwd`

# Versions
DERBY_VERSION=10.12.1.1
JUNIT_VERSION=4.12
HAMCREST_VERSION=1.3

# Directories
ROOT=${BASEDIR}/../dependencies
DERBY_DIR=${ROOT}/db-derby-${DERBY_VERSION}-bin

# Jar Files
JUNIT_JAR=${ROOT}/junit-${JUNIT_VERSION}.jar
HAMCREST_JAR=${ROOT}/hamcrest-core-${HAMCREST_VERSION}.jar
JPA_JAR=${ROOT}/hibernate-jpa-2.1-api-1.0.0.Final.jar

echo "[INFO] Checking dependency directory..."
if [ ! -d "$ROOT" ]; then
    echo "[INFO] Created dependency directory."
    mkdir ${ROOT}
fi

echo "[INFO] Checking dependencies..."
# Download derby and unpack it if not already downloaded
if [ ! -d "$DERBY_DIR" ]; then
    echo "[INFO] Derby not installed yet! Downloading..."
    cd ${ROOT}
    wget apache.sunsite.ualberta.ca//db/derby/db-derby-10.12.1.1/db-derby-10.12.1.1-bin.tar.gz
    tar -zxf db-derby-${DERBY_VERSION}-bin.tar.gz
    rm db-derby-${DERBY_VERSION}-bin.tar.gz
fi

# Download junit jar if it does not already exist in the dependencies
if [ ! -f "$JUNIT_JAR" ]; then
    echo "[INFO] Junit not downloaded yet! Downloading..."
    cd ${ROOT}
    wget http://search.maven.org/remotecontent?filepath=junit/junit/4.12/junit-4.12.jar
fi

# Download hamcrest jar if it does not already exist in the dependencies
if [ ! -f "$HAMCREST_JAR" ]; then
    echo "[INFO] Hamcrest not downloaded yet! Downloading..."
    cd ${ROOT}
    wget http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
fi

# Download JPA jar if it does not already exist in the dependencies
if [ ! -f "$JPA_JAR" ]; then
    echo "[INFO] JPA not downloaded yet! Downloading..."
    cd ${ROOT}
    wget http://search.maven.org/remotecontent?filepath=org/hibernate/javax/persistence/hibernate-jpa-2.1-api/1.0.0.Final/hibernate-jpa-2.1-api-1.0.0.Final.jar
fi

echo "[INFO] Finished checking dependencies."

# Set the derby env vars 
export DERBY_HOME=${DERBY_DIR}

# Check that the java version is at least 5 or higher
echo "[INFO] Checking java installation..."
if ! type -p java > /dev/null; then
    echo "[ERROR] Java installation not on path!"
    exit 1
fi

version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ "$version" < "1.5" ]]; then
    echo "[ERROR] Java version not high enough! Requires at least 1.5!"
    exit 1
fi

# Add derby to the path if not already there
echo "[INFO] Setting path..."
export PATH="$PATH:$DERBY_HOME/bin"

# Setup the classpath using the tool from derby
echo "[INFO] Setting up classpath..."
. setEmbeddedCP
