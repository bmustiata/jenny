#!/usr/bin/env python3

import os
import os.path
import subprocess

PROJECT_FOLDER=os.path.dirname(os.path.dirname(os.path.realpath(__file__)))

def update_single_test(folder: str, expected_file: str) -> None:
    current_folder = os.curdir # type: str
    os.chdir("{0}/{1}".format(PROJECT_FOLDER, folder))

    with open("{0}/{1}".format(PROJECT_FOLDER, expected_file), 
              mode="w") as output_file:
        output = subprocess.check_output("{0}/jenny".format(PROJECT_FOLDER)) # type: bytes
        output_file.write(output.decode("utf-8"))

    os.chdir(current_folder)

update_single_test("features/docker-support",
                   "features/docker-support/jenny-expected.txt")
