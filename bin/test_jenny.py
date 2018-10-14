#!/usr/bin/env python3

import subprocess
import os
import os.path
import unittest
import sys
from typing import List, Optional
import re

tc = unittest.TestCase()
tc.maxDiff = None
assertEquals = tc.assertEquals

PROJECT_FOLDER = os.path.dirname(os.path.dirname(os.path.realpath(__file__)))

GUID = re.compile(
    r'.*[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}.*')
JAVA_VERSION = re.compile(r'.*Java version:.*vendor: Oracle Corporation.*')


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

        if GUID.match(expected_lines[i]) and GUID.match(actual_lines[i]):
            continue

        if JAVA_VERSION.match(expected_lines[i]) and JAVA_VERSION.match(actual_lines[i]):
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
    run_external_process_test(folder_name, "jenny-expected.txt")


def run_single_info_test(folder_name: str) -> None:
    run_external_process_test(folder_name,
                              "jenny-expected-info.txt",
                              ["--info"])


def run_external_process_test(folder_name: str,
                              expected_output_file_name: str,
                              extra_parameters: List[str] = None) -> None:
    if extra_parameters is None:
        extra_parameters = []

    print("Testing: {0} ({1})".format(folder_name, " ".join(extra_parameters)))

    expected_file = None  # type: Optional[str]
    current_folder = os.curdir

    search_folder = "{0}/{1}".format(PROJECT_FOLDER, folder_name)

    os.chdir(search_folder)

    for folder, folders, files in os.walk(search_folder):
        if expected_output_file_name in files:
            expected_file = "{0}/{1}".format(folder, expected_output_file_name)
            break

    popen_parameters = ["%s/jenny" % PROJECT_FOLDER, "--keepLog"]
    popen_parameters.extend(extra_parameters)

    p = subprocess.Popen(popen_parameters,
                         stdin=subprocess.PIPE,
                         stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE)

    stdout, stderr = p.communicate()  # type: bytes, bytes
    error_code = p.returncode

    process_output = stdout.decode('utf-8')

    if error_code != 0 and 'fail' not in folder_name:
        print("Program failed with error code: %d" % error_code)
        # print("Program STDOUT: %s" % process_output)
        # print("Program STDERR: %s" % stderr.decode('utf-8'))

    assert expected_file

    with open(expected_file) as f:
        expected_content = f.read()
        compare_lines(expected_content, process_output)

    if error_code != 0 and 'fail' not in folder_name:
        print("Even if the output was equal, the program exited with "
              "exit code: %d" % error_code)
        sys.exit(1)

    os.chdir(current_folder)


tests_to_run = [
    "features/ansiColor-support",
    "features/archiveArtifacts",
    "features/build_id-environment-variable",
    "features/checkout-preserves-permissions",
    "features/child-section-skip/parent",
    "features/credentials-in-container",
    "features/current-build",
    "features/different-work-folder/parent",
    "features/dir-step",
    "features/external-classes",
    "features/failing-project",
    "features/failing-sh-inside-docker",
    "features/mock-missing-functions",
    "features/multiple-nodes",
    "features/parameters-boolean-value",
    "features/stash-unstash",
    "features/testset/parent",
    "features/upstream-pipeline-trigger",

    # these tests need docker
    "features/write-file",
    "features/sh-support",

    "features/docker-user-id-inside",
    "features/docker-support",
    "features/junit-support"
]

if len(sys.argv) > 1:
    tests_to_run = sys.argv[1:]

for test in tests_to_run:
    run_single_test(test)
    run_single_info_test(test)
