#!/bin/bash

if [ -e "env.sh" ]; then
	source env.sh
elif [ -e "../env.sh" ]; then
    source ../env.sh
fi

# mvn avro:schema --help
$MVN -e avro:schema
