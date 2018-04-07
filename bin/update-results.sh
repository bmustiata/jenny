#!/usr/bin/env bash

# this needs to run in the test container

set -e

PROJECT_FOLDER=$(readlink -f "$(dirname "$0")/..")
cd $PROJECT_FOLDER

export PATH="$PATH:/jenny"

update_single_test() {
    CURRENT_FOLDER=$(pwd)
    PROJECT_TO_TEST=$(readlink -f $1)
    EXPECTED_OUTPUT_FILE=$(readlink -f $2)

    echo "Updating: $PROJECT_TO_TEST"

    cd $PROJECT_TO_TEST
    jenny > $EXPECTED_OUTPUT_FILE
    cd $CURRENT_FOLDER
}

update_single_test features/testset/parent \
                features/testset/jenny-expected.txt

update_single_test features/multiple-nodes \
                features/multiple-nodes/jenny-expected.txt

update_single_test features/child-section-skip/parent \
                features/child-section-skip/jenny-expected.txt

update_single_test features/dir-step \
                features/dir-step/jenny-expected.txt

update_single_test features/docker-support\
                features/docker-support/jenny-expected.txt

