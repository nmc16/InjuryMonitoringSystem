#!/usr/bin/env bash

BASEDIR=`pwd`
ROOT=${BASEDIR}/../
TARGET=${ROOT}/target
DEPENDENCIES=${ROOT}/dependencies

if [ ! -d "$TARGET" ]; then
    mkdir ${TARGET}
fi

javac -d ${TARGET} -classpath ${ROOT}/src:${DEPENDENCIES}/eclipselink-2.6.1.jar:${DEPENDENCIES}/javax.persistence_2.1.0.jar:${DEPENDENCIES}/hamcrest-core-1.3.jar:${DEPENDENCIES}/junit-4.12.jar:${DEPENDENCIES}/db-derby-10.12.1.1-bin/lib/derby.jar:${ROOT}/src/META-INF  ${ROOT}/src/**/*.java

cd ${TARGET}
java -classpath ${ROOT}/src:${TARGET}:${DEPENDENCIES}/eclipselink-2.6.1.jar:${DEPENDENCIES}/javax.persistence_2.1.0.jar:${DEPENDENCIES}/hamcrest-core-1.3.jar:${DEPENDENCIES}/junit-4.12.jar:${DEPENDENCIES}/db-derby-10.12.1.1-bin/lib/derby.jar:${ROOT}/src/META-INF/ database.Database
