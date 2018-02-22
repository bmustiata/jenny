#!/usr/bin/env bash

# this needs to run in the test container

set -x

export PATH="$PATH:/jenny"

cd /jenny/features/testset/parent
jenny

