#!/usr/bin/env bash

task=$1
case $task in
  clean) ./gradlew clean ;;
  format) ./gradlew spotlessApply ;;
  *) ./gradlew build jpackage ;;
esac
