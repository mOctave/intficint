# Sumatra Methodcode
The core elements of Sumatra that allow anything to be done are _methods_. These can take zero or more inputs, and produce zero or more outputs while executing a block of code. To implement these, it is necessary to use Sumatra Methodcode, a BASIC-esque and relatively simple programming language.  

## Keywords
### Data Manipulation
**LET** Sets a global variable to the result of the rest of the expression on the line. Complete syntax is `LET <variable> <expression>`.  
**LOCAL** Sets a local variable (only for that method) to the result of the rest of the expression on the line. Complete syntax is `LOCAL <variable> <expression>`.  
**SET** Sets a property of a given object. The property will be presumed to be a custom property if it is not hardcoded. Complete syntax is `SET <object> <property> <expression>`. Each element of this call should either be a single word or enclosed by string grouping operators.  
**GET** Gets a given property from an object. The property will be a hardcoded one if available, otherwise it will be a custom one. Complete syntax is `GET <object> <property>`.  
**Getting the values of variables:** To get the value of a variable, enter its name preceded by any typecasting and the symbol `@`—or `$` if it happens to be a local variable.  
**ATTACH** Sets the current object attached to methods. This is usually used with the `FORMAT` keyword. Syntax is `ATTACH <object>`.  
**DETACH** Clears the current object attached to methods. To do this, the syntax is simply `DETACH`.  

### Typecasting
In Sumatra Methodcode, all variables are stored as strings, and are converted into a double float for arithmetic operations or a boolean value for logical operations. There is currently no way to typecast values, although any double value can be converted to an integer to be represented. When you attempt to store a value to a variable, it will be converted back into a string. This allows for a simpler user experience at the cost of memory and power, negligible for most applications.  

**INT** Take the integer representation of the following expression. Complete syntax is `INT <expression>`. Integers cannot be stored as variables: they will always be converted back to strings.  

### Flow Control
**LABEL** Labels allow for more complicated flow. To define a label, simply write `LABEL <label name>` on an otherwise empty line. Everything after the keyword LABEL will be interpreted as the case-insensitive label name.  
**GOTO** Push the current line number to the top of the GOTO stack, then go to the specified label name. Complete syntax is `GOTO <label name>`. Like a LABEL statement, a GOTO statement takes up the entire line.  
**GOBACK** Pop the top line off the GOTO stack, and "go back" to the line after it.  
**SKIPTO** SKIPTO is identical to GOTO, but it doesn't push anything to the GOTO stack. Complete syntax is `SKIPTO <label name>`.  
**IF THEN ELSE** If statements take up an entire line, and are formatted as follows: `IF <boolean expression> THEN <label>[ ELSE <label>]`. The ELSE statement is completely optional. If it is not included, then the code will simply go on to the next line. Both THEN and ELSE statements function like GOTO. IF statements can also be used to set up loops.  
**PUSH** Push the current line number to the top of the GOTO stack.  
**DROP** Pop the top item of the GOTO stack, but don't do anything with it.  
**METHOD** Used to define a method, similarly to a label. However, METHOD statements also set the number of parameters and returns they have, following the syntax `METHOD <name> <number of parameters> <number of returns>`, where the name is one word and both numbers are non-negative integers. This is used to prevent the accidental passing of too many or few values going unnoticed until they cause major damage (a "fail early" approach). If the interpreter runs across a METHOD statement while executing another method it will cause a fatal error: all methods should end with a RETURN statement even if there are no values to return. Parameters passed to methods become the local variables 1, 2, etc. The local variable 0 is the method name.  
**RETURN** Returns one or more (usually local) variables to whatever method called it, and goes back to the line after it. Full syntax is `RETURN <variable>[ <variable> [<variable> ...]]`. Variables returned should not be typecasted, and the number of variables returned must match the number set by the method. Single-word values such as integers may also be returned, although they will be passed as strings.  
**EXECUTE** Execute another labelled method with variables passed to it as input parameters. Full syntax is `EXECUTE <method> <variable>[ <variable> [<variable> ...]]`. No variable passed as a parameter may be typecasted, and the number of variables passed as parameters must match the number set by the method. Single-word values such as integers may also be passed, although they will be passed as strings.  
**EXIT** Exits the interpreter. Syntax is simply `EXIT`.  

### Input and Output
Sumatra Methodcode has five I/O keywords, one for input, two for output, one for formatting strings, and one for setting the command prompt.  

**INPUT** Asks the user for a value that is then set to a variable, using the syntax `INPUT <variable> [<prompt>]`. The prompt may be any valid expression or string. If no prompt is provided, the default command prompt is used.  
**PRINT** Prints the (unformatted) result of the expression or string following it, using the syntax `PRINT <expression>`.  
**SAY** Prints the result of the expression following it after passing it through the Sumatra string formatter. This makes it much easier to print user output that includes (for example) the name or description of a thing. The syntax for this is `SAY <expression>`.  
**FORMAT** Formats the result of the expression following it. This is essentially the same as SAY but is not followed by PRINT as SAY is, and allows for formatted strings to be used in other places, such as command prompts. Complete syntax is `FORMAT <expression>`.  
**CPRMPT** Sets the command prompt to the (unformatted) result of the following expression, following the syntax `CPRMPT <expression>`. A formatted command prompt can be achieved with `CPRMPT FORMAT <expression>`.  
While not technically I/O, Sumatra Methodcode also allows for user-created errors and warnings to be thrown. All warning and error descriptions thrown by the code are prefixed with the string `Thrown by methodcode - ` to distinguish them from interpreter warnings and errors.  
**WARN** Throws a methodcode warning with the (unformatted) description equal to the remainder of the expression. Full syntax is `WARN <expression>`.  
**ERROR** Throws a fatal methodcode error with the (unformatted) description equal to the remainder of the expression. Full syntax is `ERROR <expression>`.  
**CATCH** Catches a fatal methodcode error and redirects the program to the chosen label, using the syntax `CATCH <label>`.  
**ENDCATCH** Ends a catch block.  

## Operators
Expressions in Sumatra Methodcode are made up of values and operators. Operators allow for operations to take place, and are broadly classified into three families: comparative and logical operators, arithmetic operators, and the precedence operators. **All operators must be surrounded by whitespace on either side;** `@A==@B` will be treated as a call to the variable `A==@B`. In general, keywords have the highest precedence, second only to the precedence operators.

### Comparative and Logical Operators
Comparative and logical operators in Sumatra have the lowest precedence of any operator. This allows for the expressions they compare to be evaluated without the programmer becoming overwhelmed by parentheses.  

**==** The operator `==` returns `true` if the expressions on its right and left sides are equal, and `false` if they are not.  
**!=** The operator `!==` returns `false` if the expressions on its right and left sides are equal, and `true` if they are not.  
**==** The operator `=` returns `true` if the expressions on its right and left sides result in the same number, and `false` if they do not. Note that this does not require that they be the same string: `1.0 = 1` is true even though `1.0 == 1` is not.  
**!=** The operator `!=` returns `false` if the expressions on its right and left sides result in the same number, and `true` if they are not.  
*Be careful not to confuse the string equation operators `==` and `!==` with the number equation operators `=` and `!=`!*  

**>** The operator `>` returns `true` if the expression on its left side is greater than that on its right, and `false` if it is not.  
**<** The operator `<` returns `true` if the expression on its right side is greater than that on its left, and `false` if it is not.  
**>=** The operator `>=` returns `true` if the expression on its left side is greater than or equal to that on its right, and `false` if it is not.  
**<=** The operator `<=` returns `true` if the expression on its right side is greater than or equal to that on its left, and `false` if it is not.  
**&&** The operator `&&` returns `true` if both the expression on its right side and that on its left are true, and `false` if one or both are false.  
**||** The operator `||` returns `true` if both the expression on its right side or that on its left are true, and `false` if both are false.  
**!** The operator `!` returns `true` if both the expression following it is false, and `false` if it is true.  

### Arithmetic Operators
Sumatra Methodcode supports six arithmetic operators.  

**+** The operator `+` adds the expressions to its right and left and returns the result.  
**-** The operator `-` subtracts the expression on its left from that on its right and returns the result.  
**\*** The operator `*` multiplies the expressions on its right and left and returns the result.  
**/** The operator `/` divides the expression on its left by that on its right and returns the result.  
**^** The operator `^` raises the expression on its left to the power of that on its right and returns the result.  
**%** The operator `%` takes the remainder of the expression on its left when divided by the expression on its right.  

### Precedence of Operators
Nearly every operator in Sumatra Methocode has a different level of precedence. These levels are as follows, with the highest number corresponding to the highest level of precedence:

| Precedence |     Operator Type     |        Symbols        |
| ---------- | --------------------- | --------------------- |
| 13         | String Grouping       | "                     |
| 12         | Variable Substitution | @ $                   |
| 11         | User Input Call       | INPUT                 |
| 10         | Precedence Indicators | ( )                   |
| 9          | Object Attribute Call | :                     |
| 8          | String Methods        | ~ ¢ ¬                 |
| 7          | Exponent and Modulus  | ^ %                   |
| 6          | Product and Quotient  | * /                   |
| 5          | Sum and Difference    | + -                   |
| 4          | Comparative Operators | == !== = != > < >= <= |
| 3          | Logical Operators     | && \|\| !             |
| 2          | Integer Conversion    | INT                   |
| 1          | String Formatting     | FORMAT                |
| 0          | Processing Nullspace  | § ¶ ‡                 |

## Comments
Sumatra Methodcode does not support comments natively, but if it is being parsed by the Sumatra interpreter it can be commented upon in the same way as any other form of Sumatra data.  

# Error Guide
Sumatra Methodcode throws both errors and warnings during execution. Here is a guide to all of them.

## Fatal Errors
Fatal errors stop execution of the code and print a red message to the screen. These include:

**Missing arguments in SET statement:** You called SET on less than three arguments.  
**Invalid GOTO location:** You tried to GOTO a label that doesn't exist anywhere. This also apply to the similar errors `Invalid GOTO location on THEN statement` and `Invalid GOTO location on ELSE statement`.  
**Invalid SKIPTO location:** You tried to SKIPTO a label that doesn't exist anywhere.  
**GOTO stack is empty:** You tried to GOBACK, but there was nowhere to GOBACK to.  
**Inconclusive IF statement:** Your if statement doesn't end with a valid THEN or ELSE clause. If you added one, check to make sure that it has a properly formatted label after it.  
**Method not found:** You tried to EXECUTE a method that doesn't exist anywhere.  
**Expected X inputs to method, got Y:** You either gave the method too many input arguments or too few. Check your string grouping operators.  
**Expected X returns from method, got Y:** You either returned too many or too few input arguments from a method. Check your string grouping operators.  
**Thrown by methodcode:** This error is thrown by an ERROR statement in the methodcode.  
**Unknown global variable:** You tried to call the value of a global variable that doesn't exist. Check to make sure it's not a local variable.  
**Unknown local variable:** You tried to call the value of a local variable that doesn't exist. Check to make sure it's not a global variable and that it exists in the current context.  
**Object not found:** No object was found to match the key you provided in your object attribute call.  

## Warnings
Warnings do not halt execution of the program, but they are usually an indication that something has gone wrong behind the scenes. These include:

**End of code reached:** The methodcode interpreter has ran straight to the end of its code without hitting a RETURN statement.  
**Extra arguments in SET statement:** You called SET on more than three arguments. This probably occurred because you forgot to add string grouping operators. Extra arguments will be ignored.  
**GOTO stack is empty:** You tried to DROP, but there was nothing on the GOTO stack to drop.  
**Thrown by methodcode:** This error is thrown by an WARN statement in the methodcode.  
**Invalid operation:** This line of methodcode did not begin with any of the recognized keywords.  
**No value input:** The user did not input anything when INPUT was called.  
**Object not found:** No object was found to match the key you provided in your SET statement. No attribute was set.  
**Invalid CATCH label:** You set a CATCH to GOTO a label that doesn't exist. It won't catch any errors.  