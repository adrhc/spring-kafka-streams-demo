#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e "../env.sh" ]; then
    source ../env.sh
fi

# mvn avro:schema --help
#$MVN -e avro:protocol
$MVN -Dmaven.javadoc.skip=true -Dmaven.test.skip=true compile "$@"
