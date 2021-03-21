#!/bin/bash

jdk_path="/usr/lib/jvm/jdk-11.0.10/bin/java"
jar_name="acitvemq-0.0.1-SNAPSHOT.jar"

while :
do
run=$(ps -ef |grep $jar_name |grep -v "grep")
if [ "$run" ] ; then
echo "The service is alive!"
else
echo "The service was shutdown! Try to start the service"
$jdk_path -jar $jar_name
fi
sleep 1
done

