#!/usr/bin/env python3

import subprocess
import os
import unittest

tc = unittest.TestCase()
assertEquals = tc.assertEquals

def runSingleTest(folder_name: str, expected_file: str) -> None:
    current_folder = os.curdir
    os.chdir(folder_name)

    output = subprocess.check_output(["jenny"], stderr=subprocess.STDOUT)
    
    with open(expected_file) as f:
        expected_content = f.read()
        assertEquals(expected_content, output)
    
    os.chdir(current_folder)

runSingleTest("features/testset/parent",
              "features/testset/jenny-expected.txt")    