#! /bin/bash

PID=$(ps aux | grep -E '[g]radlew -classpath .*portfolio' | tr -s ' ' | head -n 1 | cut -d ' ' -f2)
IID=$(ps aux | grep -E '[g]radlew -classpath .*identityprovider' | tr -s ' ' | head -n 1 | cut -d ' ' -f2)
if [ -n $PID ]; then kill $PID; fi
if [ -n $IID ]; then kill $IID; fi

echo "Starting IDP"
pushd .
cd ./identityprovider
./gradlew bootrun > /dev/null 2>&1 &
popd

echo "Starting Portfolio"
pushd .
cd ./portfolio
./gradlew bootrun > /dev/null 2>&1 &
popd

echo "Wait for servers to start"
sleep 20
echo "Run integration tests"
cd ./portfolio; ./gradlew integrationtest || true

echo "Kill servers again"
PID=$(ps aux | grep -E '[g]radlew -classpath .*portfolio' | tr -s ' ' | head -n 1 | cut -d ' ' -f2)
IID=$(ps aux | grep -E '[g]radlew -classpath .*identityprovider' | tr -s ' ' | head -n 1 | cut -d ' ' -f2)
if [ -n $PID ]; then kill $PID; fi
if [ -n $IID ]; then kill $IID; fi