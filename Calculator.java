import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * 
 * @author Eric Chun
 * 
 * A simple calculator based off Google's browser calculator and Apple's iPhone calculator.
 * 
 * The calculator works by taking in a first number, then an operator, and then a second number, 
 * and the answer is stored and updated as the second number changes.
 * For example, if the first number is 10, the operator is add, and the second number is 123, the program 
 * calculates and stores the answer to "10 + 1", "10 + 12", and "10 + 123" until the equals button is called.
 * 
 * The possible operators are divide, multiply, add, subtract, square, and square root.
 * Square and square root don't use a second number, and instead calculate the answer based on the current first number.
 * The clear entry button deletes the character that was last entered, and the all clear button deletes everything.
 * 
 * When the equals button is called and the answer is calculated, the answer can be used as the first number
 * in the next equation by selecting an operator instead of inputting a new number.
 * 
 * When an answer is calculated, the label above the main display will show the equation that was just entered.
 * When a new equation is started, the label will show the answer that was just calculated.
 * Clicking on the answer will copy it to the user's clipboard.
 *
 */

public class Calculator {

	// fields
	private JFrame frame;
	private JPanel fullPanel, displayPanel, buttonPanel;
	private JButton square, sqrt, delete, clear, zero, decimal, one, two, three, four, five, six, seven, eight, nine, divide, multiply, add, subtract, equals;
	private JLabel previousLabel, mainLabel;

	ArrayList<JButton> buttons;
	ArrayList<JButton> printableButtons;
	ArrayList<JButton> operatorButtons;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();   // - All the JFrame components are scaled to the screensize.
	private int frameWidth = (int) screenSize.getWidth()/4;
	private int frameHeight = (int) screenSize.getHeight()*9/16;
	private int borderThickness = frameWidth/20;

	private boolean dividing;   // - True if their respective operators have been entered.
	private boolean multiplying;   // - "If multiplying, then multiply the first number by the second number".
	private boolean adding;
	private boolean subtracting;

	private String firstNumber;   // - First group of numbers that are entered in the equation.
	private double doubleFirstNumber;   // - firstNumber is parsed to a double to be used in calculations.
	private boolean addingToFirstNumber;   // - Stops or starts adding to firstNumber.
	private String secondNumber;
	private double doubleSecondNumber;
	private boolean addingToSecondNumber;

	private String answer;   // - A string to store the answer so it can be displayed.
	private double doubleAnswer;   // - The calculated answer that is parsed into a string so it can be displayed.
	private String answerText;   // - Text in the previousLabel to show the answer to the previous equation.
	private String equationText; // - Text in the previousLabel to show the equation that returned the current answer being displayed.

	private boolean calculated;   // - True if the equals button was just entered. Lets the user start a new equation right away.
	private boolean decimalEntered;   // - True if a decimal been entered, prevents multiple decimals in one number.
	private boolean operatorEntered;   // - True if an operator has just been entered.

	private StringSelection copiedAnswer;   // - StringSelection to store the answer so it can be copied to user's clipboard
	private Clipboard clipboard;   // - User's clipboard to copy the answer to;


	// create and show GUI
	private void createAndShowGUI() {
		frame = new JFrame("Calculator");   // - Create a JFrame object

		operatorEntered = false;   // - Initialize the variables.
		dividing = false;
		multiplying = false;
		adding = false;
		subtracting = false;
		firstNumber = "";
		doubleFirstNumber = 0;
		addingToFirstNumber = true;
		secondNumber = "";
		doubleSecondNumber = 0;
		addingToSecondNumber = false;
		answer = "";
		doubleAnswer = 0;
		equationText = "";
		answerText = "";
		calculated = false;
		decimalEntered = false;
		copiedAnswer = new StringSelection("");

		createPanels();   // - Create the GUI.
		createLabels();
		createButtons();

		frame.setBackground(Color.red);   // - Set up the frame.
		frame.setSize(frameWidth, frameHeight);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}


	// create and add panels
	private void createPanels() {
		fullPanel = new JPanel();   // - Create the fullPanel. It takes up the whole JFrame and holds displayPanel and buttonPanel.
		fullPanel.setBackground(Color.white);
		fullPanel.setLayout(new BoxLayout(fullPanel, BoxLayout.Y_AXIS));
		fullPanel.setBorder(BorderFactory.createEmptyBorder(borderThickness, borderThickness, borderThickness, borderThickness));

		displayPanel = new JPanel();   // - displayPanel is the screen at the top. It displays the equation and answer.
		displayPanel.setBackground(Color.white);
		displayPanel.setPreferredSize(new Dimension(frameWidth, frameHeight*1/5));
		displayPanel.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 2, true));

		buttonPanel = new JPanel();   // - buttonPanel holds the buttons.
		buttonPanel.setBackground(Color.white);
		buttonPanel.setPreferredSize(new Dimension(frameWidth, frameHeight*4/5));

		fullPanel.add(displayPanel);   // - Add displayPanel and buttonPanel to fullPanel. They're separated by a rigid area (the border).
		fullPanel.add(Box.createRigidArea(new Dimension(0, borderThickness)));
		fullPanel.add(buttonPanel);
		frame.add(fullPanel);
	}


	// create labels
	private void createLabels() {
		displayPanel.setLayout(new GridBagLayout());   // - displayPanel has a GridBagLayout.

		previousLabel = new JLabel("", SwingConstants.RIGHT);   // - previousLabel is the small part at the top that displays the previous answer and equation.
		previousLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		previousLabel.setForeground(new Color(120, 120, 120));
		previousLabel.setBorder(BorderFactory.createEmptyBorder(0, borderThickness, 0, borderThickness));
		GridBagConstraints previousLabelConstraints = new GridBagConstraints();
		previousLabelConstraints.fill = GridBagConstraints.HORIZONTAL;   // - Fill available horizontal space.
		previousLabelConstraints.gridx = 0;   // - Location of the top left corner.
		previousLabelConstraints.gridy = 0;
		previousLabelConstraints.weightx = 1.0;   // - Fill the empty horizontal space.
		previousLabelConstraints.ipady = frameHeight/24;   // - Add vertical space.

		mainLabel = new JLabel("0", SwingConstants.RIGHT);   // - mainLabel is the main part that displays the current equation and answer.
		mainLabel.setFont(new Font("Arial", Font.PLAIN, 26));
		mainLabel.setBorder(BorderFactory.createEmptyBorder(0, borderThickness, 0, borderThickness));
		GridBagConstraints mainLabelConstraints = new GridBagConstraints();
		mainLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
		mainLabelConstraints.gridx = 0;
		mainLabelConstraints.gridy = 1;   // - One row lower than previousLabel.
		mainLabelConstraints.weightx = 1.0;
		mainLabelConstraints.weighty = 1.0;   // - Fill the empty vertical space.
		mainLabelConstraints.ipady = frameHeight/12;   // - Add more vertical space than previousLabel.
		mainLabel.addMouseListener(new ButtonMouseListener());

		displayPanel.add(previousLabel, previousLabelConstraints);   // - Add labels to displayPanel under their respective constraints.
		displayPanel.add(mainLabel, mainLabelConstraints);

	}


	// create buttons
	@SuppressWarnings("unchecked")
	private void createButtons() {
		buttonPanel.setLayout(new GridLayout(5,4, borderThickness/2, borderThickness/2));   // - buttonPanel has a 6 x 4 GridLayout. The cells have a border.

		buttons = new ArrayList<JButton>();   // - Create all the buttons
		square = new JButton("x\u00B2");
		sqrt = new JButton("\u221A");
		delete = new JButton("CE");
		clear = new JButton("AC");
		seven = new JButton("7");
		eight = new JButton("8");
		nine = new JButton("9");
		divide = new JButton("÷");
		four = new JButton("4");
		five = new JButton("5");
		six = new JButton("6");
		multiply = new JButton("×");
		one = new JButton("1");
		two = new JButton("2");
		three = new JButton("3");
		subtract = new JButton("-");
		decimal = new JButton(".");
		zero = new JButton("0");
		equals = new JButton("=");
		add = new JButton("+");

		buttons.add(square);   // - Add all the buttons to ArrayList to use for-loops.
		buttons.add(sqrt);
		buttons.add(delete);
		buttons.add(clear);
		buttons.add(seven);
		buttons.add(eight);
		buttons.add(nine);
		buttons.add(divide);
		buttons.add(four);
		buttons.add(five);
		buttons.add(six);
		buttons.add(multiply);
		buttons.add(one);
		buttons.add(two);
		buttons.add(three);
		buttons.add(subtract);
		buttons.add(decimal);
		buttons.add(zero);
		buttons.add(equals);
		buttons.add(add);

		printableButtons = (ArrayList<JButton>) buttons.clone();   // - Printable means it can be displayed as part of the equation.
		printableButtons.remove(square);
		printableButtons.remove(sqrt);
		printableButtons.remove(delete);
		printableButtons.remove(clear);
		printableButtons.remove(equals);

		operatorButtons =  new ArrayList<JButton>();   // - Operators have their own ArrayList.
		operatorButtons.add(divide);
		operatorButtons.add(multiply);
		operatorButtons.add(add);
		operatorButtons.add(subtract);

		for (JButton button : buttons) {   // - Set up all the buttons.
			button.setBackground(new Color(240, 240, 240));
			button.setFont(new Font("Arial", Font.PLAIN, 17));
			button.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 2, true));
			button.addActionListener(new ButtonActionListener());
			button.addMouseListener(new ButtonMouseListener());
			buttonPanel.add(button);
		}
	}


	// button action listener
	private class ButtonActionListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			for (JButton button : buttons) {
				if (e.getSource().equals(button)) {   // - Identify what button has been clicked.
					frame.setTitle("Calculator");

					if (button.equals(square)) {   // - Square
						if (!mainLabel.getText().equals("")) {
							if (calculated || (addingToFirstNumber && firstNumber != "")) {
								doubleAnswer = Math.pow(doubleFirstNumber, 2);

								long longAnswer = (long) doubleAnswer;   // - Get rid of unnecessary decimals.
								if (longAnswer == doubleAnswer) {
									answer = longAnswer + "";
								}
								else {
									answer = doubleAnswer + "";
								}

								firstNumber = answer;   // - Update texts and numbers
								doubleFirstNumber = doubleAnswer;
								answerText = "Ans = " + answer;
								equationText = firstNumber + "\u00B2";
								mainLabel.setText(answer);
								calculated = true;
							}
						}
					}

					if (button.equals(sqrt)) {   // - Square root
						if (!mainLabel.getText().equals("")) {
							if (calculated || (addingToFirstNumber && firstNumber != "")) {
								doubleAnswer = Math.sqrt(doubleFirstNumber);

								long longAnswer = (long) doubleAnswer;   // - Get rid of unnecessary decimals.
								if (longAnswer == doubleAnswer) {
									answer = longAnswer + "";
								}
								else {
									answer = doubleAnswer + "";
								}

								firstNumber = answer;   // - Update texts and numbers
								doubleFirstNumber = doubleAnswer;
								answerText = "Ans = " + answer;
								equationText = "\u221A" + firstNumber;
								mainLabel.setText(answer);
								calculated = true;
							}
						}
					}

					if (button.equals(delete)) {   // - Delete
						if (!mainLabel.getText().equals("")) {
							if (addingToFirstNumber && firstNumber != "") {   // - Deleting from firstNumber.
								if (firstNumber.substring(0, firstNumber.length() - 1).equals("")) {   // - If deleting last entry leaves nothing, then firstNumber becomes 0.
									firstNumber = "0";
								}
								else {
									firstNumber = firstNumber.substring(0, firstNumber.length() - 1);   // - Delete last entry from firstNumber.
								}
								doubleFirstNumber = Double.parseDouble(firstNumber);   // - Update these variables with the new firstNumber.
								answer = firstNumber;
								doubleAnswer = doubleFirstNumber;
								equationText = firstNumber;
								mainLabel.setText(firstNumber);   // - Display the new firstNumber
							}

							if (addingToSecondNumber && secondNumber == "") {   // - Deleting the operator.
								answer = firstNumber;
								doubleAnswer = doubleFirstNumber;
								equationText = firstNumber;
								mainLabel.setText(firstNumber);
								addingToFirstNumber = true;   // - Start adding to firstNumber again.
								addingToSecondNumber = false;
								resetOperatorBooleans();   // - Reset operators.
							}

							if (addingToSecondNumber && secondNumber != "") {   // - Deleting from secondNumber.

								if (secondNumber.substring(0, secondNumber.length() - 1).equals("")) {   // - Deleting last entry leaves nothing in secondNumber.
									secondNumber = "";
									answer = firstNumber;   // - answer is firstNumber until a new secondNumber is entered.
									doubleAnswer = doubleFirstNumber;
									equationText = equationText.substring(0, equationText.length() - 1);   // - Delete last entry from equationText and mainLabel.
									mainLabel.setText(mainLabel.getText().substring(0, mainLabel.getText().length() - 1));
								}
								else {   // - Deleting lastEntry leaves a number in secondNumber.
									secondNumber = secondNumber.substring(0, secondNumber.length() - 1);
									doubleSecondNumber = Double.parseDouble(secondNumber);
									equationText = equationText.substring(0, equationText.length() - 1);   // - Delete last entry from equationText and mainLabel.
									mainLabel.setText(mainLabel.getText().substring(0, mainLabel.getText().length() - 1));
									calculation();
								}
							}
						}
					}

					if (button.equals(clear)) {   // - Clear

						mainLabel.setText("0");   // - Clear the mainLabel.
						previousLabel.setText(answerText);   // - previousLabel displays the previous answer
						resetOperatorBooleans();   // - Reset operators.						
						firstNumber = "";   // - Reset numbers
						doubleFirstNumber = 0;
						addingToFirstNumber = true;
						secondNumber = "";
						doubleSecondNumber = 0;
						addingToSecondNumber = false;
						answer = "0";   // - Set answer to 0 just to display something if equals is entered.
						doubleAnswer = 0;
						equationText = "";
						decimalEntered = false;
						operatorEntered = false;
					}

					if (button.equals(equals)) {   // - Equals
						mainLabel.setText(answer);   // - mainLabel displays the answer.
						previousLabel.setText(equationText + " = ");   // - previousLabel displays the previous equation.
						resetOperatorBooleans();   // - Reset operators.
						firstNumber = answer;   // - Store the answer as the first number so it can be used in the next equation.
						doubleFirstNumber = Double.parseDouble(firstNumber);
						addingToFirstNumber = true;   // - If a number is selected right after the answer is calculated, the answer is replaced by a new first number.
						secondNumber = "";
						doubleSecondNumber = 0;
						addingToSecondNumber = false;
						calculated = true;
						decimalEntered = false;
						operatorEntered = false;
					}

					if (printableButtons.contains(button)) {

						if (operatorButtons.contains(button)) {   // - Operators
							decimalEntered = false;   // - Reset decimalEntered because an operator means the number is complete.

							if (button.equals(divide)) {   // - Tell the calculator which operation to do.
								dividing = true;
							}
							if (button.equals(multiply)) {
								multiplying = true;
							}
							if (button.equals(add)) {
								adding = true;
							}
							if (button.equals(subtract)) {
								subtracting = true;
							}

							if (addingToSecondNumber == true && !secondNumber.equals("")) {   // - This means the second number is complete so calculate the first equation and start the next one.
								mainLabel.setText(answer);   // - Display the answer.
								firstNumber = answer;   // - Start the next equation with the answer as the firstNumber.
								doubleFirstNumber = Double.parseDouble(firstNumber);
								secondNumber = "";
								doubleSecondNumber = 0;
								operatorEntered = true;
							}

							if (operatorEntered == false || !mainLabel.getText().equals("")) {   // -These conditions prevent starting an equation with an operator and entering two operators in a row.
								mainLabel.setText(mainLabel.getText() + " " + button.getText() + " ");   // - Add operator normally.
								operatorEntered = true;
								addingToFirstNumber = false;   // - The firstNumber is complete so start adding to the secondNumber.
								addingToSecondNumber = true;
								equationText += " " + button.getText() + " ";
							}
						}

						else {   // - Numbers
							if (addingToFirstNumber) {

								if (calculated) {   // - Start a new equation. The user isn't using the previous answer so reset firstNumber and equationText.
									previousLabel.setText(answerText);   // - previousLabel displays previous answer.
									firstNumber = "";
									doubleFirstNumber = 0;
									mainLabel.setText("");   // - Clear the mainLabel.
									equationText = "";
									answer = "";
									calculated = false;
								}

								if (button.equals(decimal) && firstNumber.equals("") && !decimalEntered) {   // - If this is the start of a new equation, this displays "0." instead of "." 
									firstNumber = "0.";
									mainLabel.setText(firstNumber);
									answer = "0.";
									doubleAnswer = 0.;
									equationText += "0.";
									decimalEntered = true;
								}

								else if (!button.equals(decimal) || !decimalEntered){   // - These conditions prevent multiple decimals in a row.
									if (button.equals(decimal)) {
										decimalEntered = true;
									}

									if (mainLabel.getText().equals("0")) {   // - When a number is entered after every number has been deleted, the display will show "5" instead of "05"
										mainLabel.setText("");
										equationText = "";
										firstNumber = "";
									}
									mainLabel.setText(mainLabel.getText() + button.getText());   // - Add number/decimal normally.
									firstNumber += button.getText();
									doubleFirstNumber = Double.parseDouble(firstNumber);
									answer += button.getText();
									doubleAnswer = Double.parseDouble(answer);
									equationText += button.getText();
								}
							}

							if (addingToSecondNumber) {
								if (button.equals(decimal) && secondNumber.equals("") && !decimalEntered) {   // - If this is the start of a new secondNumber, this displays "0." instead of "."
									secondNumber = "0.";
									mainLabel.setText(mainLabel.getText() + secondNumber);
									answer = "0.";
									doubleAnswer = 0.;
									equationText += "0.";
									decimalEntered = true;
								}
								else if (!button.equals(decimal) || !decimalEntered){   // - These conditions prevent multiple decimals in a row.
									if (button.equals(decimal)) {
										decimalEntered = true;
									}
									mainLabel.setText(mainLabel.getText() + button.getText());   // - Add number/decimal normally.
									secondNumber += button.getText();
									doubleSecondNumber = Double.parseDouble(secondNumber);
									equationText += button.getText();
									calculation();
								}
							}
						}
					}	
				}
			}
		}
	}


	// button mouse listener
	private class ButtonMouseListener implements MouseListener{

		public void mouseClicked(MouseEvent e) {
			if (e.getSource() == mainLabel) {
				if (calculated) {
					copiedAnswer = new StringSelection(mainLabel.getText());
					clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(copiedAnswer, null);
					frame.setTitle("Calculator        *copied to clipboard*");
				}
			}
		}

		public void mouseEntered(MouseEvent e) {   // - Buttons change to darker color when the mouse enters their bounds.
			for (JButton button : buttons) {
				if (e.getSource() == button) {
					button.setBackground(new Color(220, 220, 220));
					button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true));
				}
			}
		}

		public void mouseExited(MouseEvent e) {   // - Buttons change back to original color when mouse exits their bounds.
			for (JButton button : buttons) {
				if (e.getSource() == button) {
					button.setBackground(new Color(240, 240, 240));
					button.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 2, true));
				}
			}		
		}
		public void mousePressed(MouseEvent e) {
		}
		public void mouseReleased(MouseEvent e) {
		}
	}


	// calculation method
	private void calculation() {

		if (dividing) {   // - Calculate and store the answer using the firstNumber, secondNumber, and operator boolean.
			doubleAnswer = doubleFirstNumber / doubleSecondNumber;
			long longAnswer = (long) doubleAnswer;
			if (longAnswer == doubleAnswer) {
				answer = longAnswer + "";
			}
			else {
				answer = doubleAnswer + "";
			}
			answerText = "Ans = " + answer;
			System.out.println("first number: " + firstNumber);
			System.out.println("second number: " + secondNumber);
			System.out.println("answer: " + answer);
		}
		if (multiplying) {
			doubleAnswer = doubleFirstNumber * doubleSecondNumber;
			long longAnswer = (long) doubleAnswer;
			if (longAnswer == doubleAnswer) {
				answer = longAnswer + "";
			}
			else {
				answer = doubleAnswer + "";
			}
			answerText = "Ans = " + answer;
			System.out.println("first number: " + firstNumber);
			System.out.println("second number: " + secondNumber);
			System.out.println("answer: " + answer);
		}
		if (adding) {
			doubleAnswer = doubleFirstNumber + doubleSecondNumber;
			long longAnswer = (long) doubleAnswer;
			if (longAnswer == doubleAnswer) {
				answer = longAnswer + "";
			}
			else {
				answer = doubleAnswer + "";
			}
			answerText = "Ans = " + answer;
			System.out.println("first number: " + firstNumber);
			System.out.println("second number: " + secondNumber);
			System.out.println("answer: " + answer);
		}
		if (subtracting) {
			doubleAnswer = doubleFirstNumber - doubleSecondNumber;
			long longAnswer = (long) doubleAnswer;
			if (longAnswer == doubleAnswer) {
				answer = longAnswer + "";
			}
			else {
				answer = doubleAnswer + "";
			}
			answerText = "Ans = " + answer;
			System.out.println("first number: " + firstNumber);
			System.out.println("second number: " + secondNumber);
			System.out.println("answer: " + answer);
		}
	}


	// reset operator booleans method
	private void resetOperatorBooleans() {
		dividing = false;
		multiplying = false;
		adding = false;
		subtracting = false;
	}

	// main method
	public static void main(String[] args) {
		Calculator myCalculator = new Calculator();
		javax.swing.SwingUtilities.invokeLater(()->myCalculator.createAndShowGUI());
	}

}
