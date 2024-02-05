# A Python test system is used rather than Java because it is much faster to
# set up a Python environment than a Java environment using GitHub Actions.
import re
import os
import sys

LEADING_WHITESPACE = "^[^\S\t\n]"
TRAILING_WHITESPACE = "[^\S\n]$"

PADDED_BRACKETS = "(\( |\[ | \)| \])"
UNPADDED_OPERATORS_AFTER = "(\+[^\s=;+]|-[^\s=;-]|\*[^\s=]|\/[^\s=]|%[^\s=]|&[^\s=&]|\^[^\s=]|\|[^\s=|]|![^\s=]|=[^\s=]|>[^\s=>(,;]|<[^\s=<>ABCDEFGHIJKLMNOPQRSTUVWXYZ]|\?[^\s]|~[^\s]|:[^\s])"
UNPADDED_OPERATORS_BEFORE = "([^\s+]\+|[^\s-]-|[^\s]\*|[^\s]\/|[^\s]%|[^\s&]&]|[^\s=]\^|[^\s|]\||[^\s]!|[^\s=]=|(\s|^|<)[a-z][A-z]*>|(\s|^|<)[a-z][A-z]*<|[^\s]\?|[^\s]~|[^\s]:)"

LINE_ENDING = "\n"
TAB_SIZE = 4

fileData = ""
errorCount = 0
fname = ""

def loadFile(path):
    global fileData
    f = open(path, "r")
    fileData = f.read()

def error(msg, lnum = 0):
    global errorCount
    if lnum > 0:
        print(f"Error on line {lnum} of {fname}: {msg}.")
    else:
        print(f"Error in {fname}: {msg}.")
    errorCount += 1

def doLineSpecificChecks():
    global fileData
    lnum = 0
    lines = fileData.split(LINE_ENDING)
    for ln in lines:
        lnum += 1
        x = re.search(LEADING_WHITESPACE, ln)
        if x:
            error("Indentation with spaces", lnum)
        x = re.search(TRAILING_WHITESPACE, ln)
        if x:
            error("Trailing whitespace", lnum)
        if ln.count("\t") * (TAB_SIZE - 1) + len(ln) > 120:
            error("Line longer than 120 chars", lnum)

def doGeneralChecks():
    global fileData
    if "\n" != fileData[-1]:
        error("Missing final newline")

def doCodeBodyChecks():
    global fileData
    lnum = 0
    inString = False
    inComment = False
    charEscaped = False
    strippedData = ""
    lastChar = ""
    for char in fileData:
        if charEscaped:
            charEscaped = False
        elif inComment == True:
            if char == LINE_ENDING:
                inComment = False
                strippedData += char
        elif char == "\"":
            inString = not inString
            strippedData += char
        elif char == "/" and not inString and lastChar == "/":
            strippedData = strippedData[:-1]
            inComment = True
        elif char == "\\":
            charEscaped = True
        elif not (inString or inComment):
            strippedData += char
        lastChar = char
    lines = strippedData.split(LINE_ENDING)
    for ln in lines:
        lnum += 1
        x = re.search(PADDED_BRACKETS, ln)
        if x:
            error("Brackets padded with spaces", lnum)
        #x = re.search(UNPADDED_OPERATORS_AFTER, ln) # Currently commented out because they don't work
        #if x:
        #    error("Missing space after operator", lnum)
        #x = re.search(UNPADDED_OPERATORS_BEFORE, ln)
        #if x:
        #    error("Missing space before operator", lnum)

# Perform the stylecheck, using the first argument passed to the linter as the
# directory to perform a style check on (other than the path to this script).
dir = sys.argv[1]
for f in os.listdir(os.fsencode(dir)):
    fname = os.fsdecode(f)
    if fname.endswith(".java"):
        loadFile(dir + fname)
        print(f"Checking file {fname}...")
        doLineSpecificChecks()
        doGeneralChecks()
        doCodeBodyChecks()

if errorCount > 0:
    print(f"Style check failed with {errorCount} errors.")
    sys.exit(errorCount)
else:
    print("All files passed style check!")
