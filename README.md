# Java-Calculator

A simple calculator in Java based on Google's browser calculator and Apple's iPhone calculator.

 
The calculator works by taking in a first number, an operator, and a second number, 
and the answer is stored and updated as the second number changes.
For example, if the first number is 10, the operator is add, and the second number is 123, the program 
calculates and stores the answer to "10 + 1", "10 + 12", and "10 + 123" until the equals button is called.
 
The possible operators are divide, multiply, add, subtract, square, and square root.
Square and square root don't use a second number and instead the answer is calculated based on the current first number.
The clear entry button deletes the character that was last entered, and the all clear button deletes everything.
 
When the equals button is called and the answer is calculated, the answer can be used as the first number
in the next equation by selecting an operator instead of inputting a new number.
 
When an answer is calculated, the label above the main display will show the equation that was just entered.
When a new equation is started, the label will show the answer that was just calculated.
Clicking on the answer will copy it to the user's clipboard.

There are some issues with the precision of the doubles where the answer will be 8.0000000002 instead of 8.
