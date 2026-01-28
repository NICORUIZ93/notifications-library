#!/bin/sh
# Maven Wrapper script

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support
case "$(uname -s)" in
    CYGWIN*|MINGW*|MSYS*) cygwin=true ;;
    *) cygwin=false ;;
esac

# Determine JAVA_HOME
if [ -z "${JAVA_HOME-}" ]; then
    JAVACMD="java"
else
    JAVACMD="$JAVA_HOME/bin/java"
fi

# Resolve project base directory
MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
MAVEN_OPTS="${MAVEN_OPTS-}"

# Download maven-wrapper.jar if needed
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    if command -v curl > /dev/null 2>&1; then
        curl -fsSL -o "$WRAPPER_JAR" "$WRAPPER_URL"
    elif command -v wget > /dev/null 2>&1; then
        wget -q -O "$WRAPPER_JAR" "$WRAPPER_URL"
    else
        echo "Error: curl or wget required to download maven-wrapper.jar"
        exit 1
    fi
fi

exec "$JAVACMD" \
    $MAVEN_OPTS \
    -classpath "$WRAPPER_JAR" \
    org.apache.maven.wrapper.MavenWrapperMain "$@"
