#!/usr/bin/env python3

import subprocess
import os
import os.path
import unittest
import sys
import math

tc = unittest.TestCase()
tc.maxDiff = None
assertEquals = tc.assertEquals

PROJECT_FOLDER=os.path.dirname(os.path.dirname(os.path.realpath(__file__)))

def compare_lines(content1: str, content2: str) -> None:
    lines1 = content1.splitlines()
    lines2 = content2.splitlines()

    error_found = False

    if (len(lines1) != len(lines2)):
        error_found = True
        print("Expected %d lines, found %d lines." % 
                    (len(lines1), len(lines2)))

    for i in range(min(len(lines1), len(lines2))):
        if lines1[i] == lines2[i]:
            continue
        
        error_found = True
        print("%d: expected: %s" % (i, lines1[i]))
        print("%d: found   : %s" % (i, lines2[i]))

    if error_found:
        print("Comparison differences found")
        sys.exit(1)


def runSingleTest(folder_name: str, expected_file: str) -> None:
    print("Testing: {0}".format(folder_name))

    current_folder = os.curdir
    os.chdir("{0}/{1}".format(
        PROJECT_FOLDER,
        folder_name
    ))

    output = subprocess.check_output(
                    ["{0}/jenny".format(PROJECT_FOLDER)], 
                    stderr=subprocess.STDOUT) # type: bytes

    process_output = output.decode('UTF-8')
    
    expected_file_path = "{0}/{1}".format(PROJECT_FOLDER, expected_file)

    with open(expected_file_path) as f:
        expected_content = f.read()
        compare_lines(expected_content, process_output)

    os.chdir(current_folder)


runSingleTest("features/testset/parent",
              "features/testset/jenny-expected.txt")

runSingleTest("features/multiple-nodes",
              "features/multiple-nodes/jenny-expected.txt")

runSingleTest("features/child-section-skip/parent",
              "features/child-section-skip/jenny-expected.txt")

runSingleTest("features/dir-step",
              "features/dir-step/jenny-expected.txt")

runSingleTest("features/docker-support",
              "features/docker-support/jenny-expected.txt")
