#!/bin/bash


javac -cp .:io/github/alirostom1/payflow/drivers/mysql-connector-j-9.4.0.jar -d out io/github/alirostom1/payflow/Main.java

java -cp out:io/github/alirostom1/payflow/drivers/mysql-connector-j-9.4.0.jar io.github.alirostom1.payflow.Main