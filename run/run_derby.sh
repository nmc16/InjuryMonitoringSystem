#!/bin/bash

BASEDIR=`pwd`
DERBY_VERSION=10.12.1.1
DERBY_ROOT=$BASEDIR/..
DERBY_DIR=$DERBY_ROOT/db-derby-$DERBY_VERSION-bin

echo "[INFO] Checking derby installation..."
# Download derby and unpack it if not already downloaded
if [ ! -d "$DERBY_DIR" ]; then
    echo -e "[INFO] Derby not installed yet! Downloading...\n"
    cd $DERBY_ROOT 
    wget apache.sunsite.ualberta.ca//db/derby/db-derby-10.12.1.1/db-derby-10.12.1.1-bin.tar.gz
    tar -zxf db-derby-$DERBY_VERSION-bin.tar.g
    rm db-derby-$DERBY_VERSION-bin.tar.gz
fi

# Set the derby env vars 
export DERBY_HOME=$DERBY_DIR

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
