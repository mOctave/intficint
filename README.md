# mOctave's INTeractive FICtion INTerpreter
For some reason, I like to make things hard for myself. So, I figured I'd write an interactive fiction interpreter, which I'm calling Sumatra, since it's not Java but Java's what makes it work.

The interpreter is painfully hard to use and throws a lot of errors, but it's technically functional. If you would like to use part or all of the code for your own projects, or extend it and patch it up to make it actually run smoothly, you are free to do so without limitation (except for any applicable laws), although I disclaim any liability for the consequences, which may include your computer eating your kitten and then only outputting unintelligible symbols from then on. If you do use this, please give credit including my GitHub username and a link to this repository.

# Building
Although a JAR file is provided, you may want to build the program yourself. To do this, run the commands in `compile-test.sh`, or run it from the command line with the current directory set here and the command `./compile-test.sh`. This will create the JAR file.

# Using
In order to make sure that all the Sumatra library files are available, ensure that the `sumatra` directory is a subdirectory of the directory that holds `intficint.jar`. Then, simply run `intficint.jar` and everything should run smoothly.
