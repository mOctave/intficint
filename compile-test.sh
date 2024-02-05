# !/bin/bash

cd ..

# Compile all java files
javac intficint/src/*.java
javac intficint/tests/*.java

# Run tests
java intficint.src.Test

# Do syntax checking
cd intficint/tests
java Stylecheck
cd ../..

# Turn things into a JAR for easy use
jar -v -f intficint/intficint.jar -e intficint.src.Main -c intficint/src/*.class
