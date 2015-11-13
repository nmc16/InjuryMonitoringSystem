#!/usr/bin/env bash

BASEDIR=`pwd`
ROOT=${BASEDIR}/../
TARGET=${ROOT}/target
DEPENDENCIES=${ROOT}/dependencies
CP="${ROOT}/src:${DEPENDENCIES}/eclipselink-2.6.1.jar:${DEPENDENCIES}/javax.persistence_2.1.0.jar:${DEPENDENCIES}/hamcrest-core-1.3.jar:${DEPENDENCIES}/junit-4.12.jar:${DEPENDENCIES}/db-derby-10.12.1.1-bin/lib/derby.jar:${ROOT}/src/META-INF"

compile() {
    # Check that the target directory has been made
    if [ ! -d "$TARGET" ]; then
        mkdir ${TARGET}
    fi

    echo "[COMPILE] Compiling java code..."

    # Compile the code to the output directory target
    javac -d ${TARGET} -classpath ${CP} ${ROOT}/src/**/*.java
}

run() {
    # Get the main class to run
    class="$1"

    echo "[RUN] Running java code with main class ${class}..."

    # Check that there is compiled code
    if [ ! -d "$TARGET" ]; then
        echo "[ERROR] Could not find target directory! Has the code been compiled?"
        exit 1
    fi

    # Run the code
    cd ${TARGET}
    java -classpath ${CP}:${TARGET} ${class}
}

case "$1" in
    -c)
        compile
    ;;

    -r)
        run "$2"
    ;;

    -a)
        compile
        run "$2"
    ;;

    *)
        echo "Script has three options, one must be used:"
        echo "      -c                  Compile the code"
        echo "      -r [Main Class]     Run the code with the main"
        echo "                            class given"
        echo "      -a [Main Class]     Compiles and runs with the"
        echo "                            main class given"
esac



