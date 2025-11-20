#!/usr/bin/env bash



case $1 in
  "package")
    mvn clean package
    ;;
  "test")
    mvn clean test 
    ;;
  "run")
    mvn clean package
    java -jar target/kivarino-0.0.1-shaded.jar 
    ;;
  "compile")
    mvn clean package
    native-image -XX:-OmitStackTraceInFastThrow -Os -jar target/kivarino-0.0.1-shaded.jar 
    ;;
  *)
    printf "unsupported command; %s\n" $1
    ;;


  esac
