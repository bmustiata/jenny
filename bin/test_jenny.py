#!/usr/bin/env python3

import subprocess
import os
import os.path
import unittest
import sys

tc = unittest.TestCase()
tc.maxDiff = None
assertEquals = tc.assertEquals

PROJECT_FOLDER = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))


def compare_lines(expected: str, actual: str) -> None:
    expected_lines = expected.splitlines()
    actual_lines = actual.splitlines()

    error_found = False

    if (len(expected_lines) != len(actual_lines)):
        error_found = True
        print("Expected %d lines, found %d lines." %
              (len(expected_lines), len(actual_lines)))

    for i in range(min(len(expected_lines), len(actual_lines))):
        if expected_lines[i] == actual_lines[i]:
            continue

        error_found = True
        print("%d: expected: %s" % (i, expected_lines[i]))
        print("%d: actual  : %s" % (i, actual_lines[i]))

    if error_found:
        with open("/tmp/expected.txt", mode='w', encoding='utf-8') as f:
            f.writelines(expected_lines)
        with open("/tmp/actual.txt", mode='w', encoding='utf-8') as f:
            f.writelines(actual_lines)
        print("Comparison differences found")
        sys.exit(1)


def run_single_test(folder_name: str) -> None:
    print("Testing: {0}".format(folder_name))

    expected_file = None  # type: str
    current_folder = os.curdir

    search_folder = "{0}/{1}".format(PROJECT_FOLDER, folder_name)

    os.chdir(search_folder)

    for folder, folders, files in os.walk(search_folder):
        if "jenny-expected.txt" in files:
            expected_file = "{0}/{1}".format(folder, "jenny-expected.txt")
            break

    p = subprocess.Popen(["%s/jenny" % PROJECT_FOLDER, "--keepLog"],
                         stdin=subprocess.PIPE,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE)
    stdout, stderr = p.communicate()  # type: bytes, bytes
    error_code = p.returncode

    process_output = stdout.decode('utf-8')

    if error_code != 0:
        print("Program failed with error code: %d" % error_code)
        print("Program STDOUT: %s" % process_output)
        print("Program STDERR: %s" % stderr.decode('utf-8'))

    with open(expected_file) as f:
        expected_content = f.read()
        compare_lines(expected_content, process_output)

    if error_code != 0:
        print("Even if the output was equal, the program exited with "
              "exit code: %d" % error_code)

    os.chdir(current_folder)


run_single_test("features/testset/parent")

run_single_test("features/multiple-nodes")

run_single_test("features/child-section-skip/parent")

run_single_test("features/dir-step")

run_single_test("features/docker-support")
