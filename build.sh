#!/usr/bin/env bash
set -e

BUILD_BRANCH=${BUILD_BRANCH:-sa/creating-packages}
CHIP=$(echo "${1:-CPU}" | awk '{ print toupper($0)}')
PLATFORM=$(echo "${2:-linux}" | awk '{ print tolower($0)}')
DISTRO_TYPE=$(echo "${3:-tar}" | awk '{ print tolower($0)}')

USAGE_STRING="Usage: bash build.sh [CPU|GPU] [linux|windows|macosx] [tar|zip|exe|rpm|deb]"
EXAMPLE_STRING="Example: bash build.sh GPU linux tar,deb"
PROJECT_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)

if [[ $* == *--help* ]]
then
    echo "A command line utility for building konduit-serving distro packages."
    echo $USAGE_STRING
    echo $EXAMPLE_STRING
    exit 0
fi

if [[ "$CHIP" != "CPU" && "$CHIP" != "GPU" ]]
then
    echo "Selected CHIP $CHIP should be one of [CPU, GPU]"
    echo $USAGE_STRING
    echo $EXAMPLE_STRING
    exit 1
fi

if [[ "$PLATFORM" != "linux" && "$PLATFORM" != "windows" && "$PLATFORM" != "macosx" ]]
then
    echo "Selected PLATFORM $PLATFORM should be one of [linux, windows, macosx]"
    echo $USAGE_STRING
    echo $EXAMPLE_STRING
    exit 1
fi

if [[ "$DISTRO_TYPE" == *tar* || "$DISTRO_TYPE" == *zip* || "$DISTRO_TYPE" == *exe* || "$DISTRO_TYPE" == *rpm* || "$DISTRO_TYPE" == *deb* ]]
then
    echo ""
else
    echo "DISTRO TYPE $DISTRO_TYPE should be one of [tar, zip, exe, rpm, deb]"
    echo $USAGE_STRING
    echo $EXAMPLE_STRING
    exit 1
fi

if [[ "$PLATFORM" == macosx && "$CHIP" == "GPU" ]]
then
    echo "GPU chip type is not available for macosx platform"
    exit 1
fi

echo "Building project version: ${PROJECT_VERSION}"

echo "Building a konduit-serving distributable JAR file..."

echo "Selecting CHIP=$CHIP"

echo "Refreshing codebase"
git fetch 
git checkout "${BUILD_BRANCH}"
git pull

echo "Building $CHIP version of konduit-serving for ${PLATFORM} with distro types: (${DISTRO_TYPE}) ..."

if [[ "$CHIP" == "CPU" ]]
then
    BUILD_PROFILES=-Ppython,uberjar,tar
else
    BUILD_PROFILES=-Ppython,uberjar,tar,gpu,intel,cuda-redist
fi 

BUILD_PROFILES=${BUILD_PROFILES},${DISTRO_TYPE}

RUN_COMMAND="mvn clean install -Dmaven.test.skip=true -Denforcer.skip=true -Djavacpp.platform=${PLATFORM}-x86_64 ${BUILD_PROFILES} -Ddevice=${CHIP}"
echo Running command: ${RUN_COMMAND}

${RUN_COMMAND}

if [[ "$DISTRO_TYPE" == *tar* || "$DISTRO_TYPE" == *zip* ]]
then
    echo "----------------------------------------"
    echo "TAR and ZIP distros are available at: "
    echo "konduit-serving-tar/target/konduit-serving-tar-${PROJECT_VERSION}-dist.tar.gz"
    echo "konduit-serving-tar/target/konduit-serving-tar-${PROJECT_VERSION}-dist.zip"
    exit 1
fi

if [[ "$DISTRO_TYPE" == *rpm*]]
then
    echo "----------------------------------------"
    echo "RPM distro is available at: "
    echo $(ls "konduit-serving-rpm/target/rpm/konduit-serving-custom-${PLATFORM}-x86_64-${CHIP}/RPMS/x86_64/konduit-serving-custom-${PLATFORM}-x86_64-${CHIP}-${PROJECT_VERSION}*.x86_64.rpm")
    exit 1
fi

if [[ "$DISTRO_TYPE" == *deb* ]]
then
    echo "----------------------------------------"
    echo "DEB distro is available at: "
    echo "konduit-serving-deb/target/konduit-serving-custom-${CPU}_${PROJECT_VERSION}.deb"
    exit 1
fi

if [[ "$DISTRO_TYPE" == *exe* ]]
then
    echo "----------------------------------------"
    echo "EXE distro is available at: "
    echo "konduit-serving-exe/target/konduit.exe"
    exit 1
fi