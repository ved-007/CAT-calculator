package com.catcalculator.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Calculation engine faithful to the official TCS iON CAT on-screen calculator.
 */
public class CalculatorEngine {
    private static final int MAX_LENGTH = 8;
    private static final String STR_MATH_ERROR = "Math Error";
    private static final String STR_INFINITY = "Infinity";

    private String currentInput = "0";
    private String expressionString = "";
    private double memory = 0.0;
    private boolean hasMemory = false;

    // Calculation stacks and op codes matching CAT calciLogic:
    // 1: +, 2: -, 3: *, 4: /, 11: %
    private int opCode = 0;
    private int newOpCode = 0;
    private double stackVal = 0.0;
    private final List<Double> stackArray = new ArrayList<>();
    private final List<Integer> opCodeArray = new ArrayList<>();

    // State flags
    private boolean boolClear = true;
    private int stackVal1 = 1; // 0: after op, 1: typing, 2: after enter, 3: after unary
    private int stackVal2 = 0;
    private String trigDisplay = "";

    public CalculatorEngine() {
        allClear();
    }

    public String getCurrentInput() {
        return currentInput;
    }

    public String getExpressionString() {
        return expressionString;
    }

    public boolean hasMemory() {
        return hasMemory;
    }

    public double getMemory() {
        return memory;
    }

    /**
     * Handles numeric button press: '0' - '9' or '.'
     */
    public void inputDigit(String digit) {
        if (isErrorState()) return;

        if (boolClear) {
            currentInput = "0";
            boolClear = false;
        }

        if (".".equals(digit)) {
            if (currentInput.contains(".")) {
                if (stackVal1 == 2) { // after enter
                    currentInput = "0.";
                    expressionString = "";
                }
                return;
            }
        }

        if (currentInput.replace("-", "").replace(".", "").length() >= MAX_LENGTH) {
            return;
        }

        displayCheck();

        if (!"0".equals(currentInput) || currentInput.length() > 1 || ".".equals(digit)) {
            currentInput += digit;
        } else {
            currentInput = digit;
        }
        stackVal1 = 1;
    }

    /**
     * Handles binary operators: +, -, *, /, %
     */
    public void inputBinaryOp(String op) {
        if (isErrorState()) return;

        switch (op) {
            case "+":
                stackCheck("+");
                newOpCode = 1;
                if (opCode == 10 && !stackArray.isEmpty() && stackArray.get(stackArray.size() - 1) == Double.MIN_VALUE) {
                    opcodeChange();
                }
                operation();
                stackVal1 = 0;
                break;
            case "-":
                stackCheck("-");
                newOpCode = 2;
                if (opCode == 10 && !stackArray.isEmpty() && stackArray.get(stackArray.size() - 1) == Double.MIN_VALUE) {
                    opcodeChange();
                }
                operation();
                stackVal1 = 0;
                break;
            case "*":
                stackCheck("*");
                newOpCode = 3;
                if (opCode == 1 || opCode == 2) {
                    opcodeChange();
                }
                if (opCode == 10) {
                    if (!opCodeArray.isEmpty() && opCodeArray.get(opCodeArray.size() - 1) < 3) {
                        opcodeChange();
                    } else {
                        operation();
                    }
                }
                stackVal1 = 0;
                break;
            case "/":
                stackCheck("/");
                newOpCode = 4;
                if (opCode > 0 && opCode < 4) {
                    opcodeChange();
                }
                if (opCode == 10) {
                    if (!opCodeArray.isEmpty() && opCodeArray.get(opCodeArray.size() - 1) < 4) {
                        opcodeChange();
                    } else {
                        operation();
                    }
                }
                stackVal1 = 0;
                break;
            case "%":
                stackCheck("%");
                newOpCode = 11;
                if (opCode > 0 && opCode < 6) {
                    opcodeChange();
                }
                if (opCode == 10) {
                    if (!opCodeArray.isEmpty() && opCodeArray.get(opCodeArray.size() - 1) < 6) {
                        opcodeChange();
                    } else {
                        operation();
                    }
                }
                stackVal1 = 0;
                break;
            default:
                return;
        }

        if (opCode != 0) {
            oscBinaryOperation();
        } else {
            stackVal = parseCurrentInput();
            boolClear = true;
        }

        opCode = newOpCode;
    }

    /**
     * Handles Unary operations: +/-, 1/x, sqrt
     */
    public void inputUnaryOp(String op) {
        if (isErrorState()) return;

        double x = parseCurrentInput();
        double retVal;

        switch (op) {
            case "+/-":
                retVal = -x;
                stackVal2 = 3;
                break;
            case "1/x":
                if (x == 0) {
                    currentInput = STR_MATH_ERROR;
                    boolClear = true;
                    return;
                }
                retVal = 1.0 / x;
                displayUnaryHistory("reciproc", x);
                break;
            case "sqrt":
            case "√":
                if (x < 0) {
                    currentInput = STR_MATH_ERROR;
                    boolClear = true;
                    return;
                }
                retVal = Math.sqrt(x);
                displayUnaryHistory("sqrt", x);
                break;
            default:
                return;
        }

        if (stackVal2 == 1) stackVal = retVal;
        if (stackVal2 != 3) stackVal2 = 2;
        stackVal1 = 3;
        boolClear = true;

        currentInput = formatNumber(retVal);
    }

    /**
     * Handles Equals (=)
     */
    public void calculateEquals() {
        if (isErrorState()) return;

        String strInput = currentInput;
        while (opCode != 0 || !opCodeArray.isEmpty()) {
            oscBinaryOperation();
            if (!stackArray.isEmpty()) {
                stackVal = stackArray.remove(stackArray.size() - 1);
            }
            if (!opCodeArray.isEmpty()) {
                opCode = opCodeArray.remove(opCodeArray.size() - 1);
            } else {
                opCode = 0;
            }
        }

        opCode = 0;
        trigDisplay = "";
        stackVal = 0;

        if (stackVal1 != 2) {
            if (stackVal1 == 3 || stackVal2 == 1) {
                if (stackVal2 != 3) strInput = "";
            }
            expressionString = expressionString + strInput;
        }

        stackVal1 = 2;
        newOpCode = 0;
        stackVal2 = 0;
        stackArray.clear();
        opCodeArray.clear();
    }

    /**
     * Handles Backspace (<-)
     */
    public void backspace() {
        if (isErrorState()) return;

        if (stackVal1 == 1 || stackVal2 == 3) {
            if (currentInput.length() > 1) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                if ("-".equals(currentInput)) {
                    currentInput = "0";
                }
            } else {
                currentInput = "0";
            }
        }
    }

    /**
     * Handles Clear (C / All Clear)
     */
    public void allClear() {
        currentInput = "0";
        expressionString = "";
        trigDisplay = "";
        stackArray.clear();
        opCodeArray.clear();
        stackVal = 0.0;
        stackVal1 = 1;
        stackVal2 = 0;
        newOpCode = 0;
        opCode = 0;
        boolClear = true;
    }

    /**
     * Memory Operations: MC, MR, MS, M+, M-
     */
    public void inputMemory(String memOp) {
        if (isErrorState()) return;

        double x = parseCurrentInput();
        switch (memOp) {
            case "MS":
                memory = x;
                hasMemory = (memory != 0.0);
                break;
            case "M+":
                memory += x;
                hasMemory = (memory != 0.0);
                break;
            case "M-":
                memory -= x;
                hasMemory = (memory != 0.0);
                break;
            case "MR":
                currentInput = formatNumber(memory);
                stackVal1 = 1;
                break;
            case "MC":
                memory = 0.0;
                hasMemory = false;
                break;
            default:
                break;
        }
        boolClear = true;
    }

    // --- Internal Helpers faithful to calciLogic.js ---

    private boolean isErrorState() {
        return currentInput.contains(STR_INFINITY) || currentInput.contains(STR_MATH_ERROR);
    }

    private double parseCurrentInput() {
        try {
            return Double.parseDouble(currentInput);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void stackCheck(String text) {
        if (stackVal1 == 2) {
            expressionString = "";
        }
        if (stackVal1 == 0) {
            opCode = 0;
            if (!expressionString.isEmpty()) {
                expressionString = expressionString.substring(0, expressionString.length() - 1);
            }
            stackVal2 = 2;
        }

        if (stackVal1 == 5 || stackVal2 == 2) {
            stackVal2 = 0;
            expressionString = expressionString + text;
        } else {
            expressionString = expressionString + currentInput + text;
        }
    }

    private void operation() {
        while (!opCodeArray.isEmpty() && opCode != 0) {
            if (opCode == 10) {
                opCode = opCodeArray.get(opCodeArray.size() - 1);
                stackVal = stackArray.get(stackArray.size() - 1);
                if (newOpCode == 1 || newOpCode == 2 || newOpCode <= opCode) {
                    opCodeArray.remove(opCodeArray.size() - 1);
                    stackArray.remove(stackArray.size() - 1);
                } else {
                    opCode = 0;
                    break;
                }
            } else {
                oscBinaryOperation();
                stackVal = stackArray.get(stackArray.size() - 1);
                opCode = opCodeArray.get(opCodeArray.size() - 1);
                if (newOpCode == 1 || newOpCode == 2 || newOpCode <= opCode) {
                    opCodeArray.remove(opCodeArray.size() - 1);
                    stackArray.remove(stackArray.size() - 1);
                } else {
                    opCode = 0;
                    break;
                }
                if (opCodeArray.isEmpty() && !stackArray.isEmpty()) {
                    stackVal = stackArray.get(stackArray.size() - 1);
                }
            }
        }
    }

    private void opcodeChange() {
        if (opCode != 10 && opCode != 0) {
            opCodeArray.add(opCode);
            stackArray.add(stackVal);
        }
        if (opCode == 0) {
            stackArray.add(stackVal);
        }
        opCode = 0;
    }

    private void displayCheck() {
        switch (stackVal1) {
            case 2:
                expressionString = "";
                break;
            case 3:
                if (expressionString.endsWith(trigDisplay)) {
                    expressionString = expressionString.substring(0, expressionString.length() - trigDisplay.length());
                }
                stackVal2 = 4;
                break;
            default:
                break;
        }
    }

    private void oscBinaryOperation() {
        double x2 = parseCurrentInput();
        switch (opCode) {
            case 1: // +
                stackVal += x2;
                break;
            case 2: // -
                stackVal -= x2;
                break;
            case 3: // *
                stackVal *= x2;
                break;
            case 4: // /
                if (x2 == 0) {
                    currentInput = STR_MATH_ERROR;
                    boolClear = true;
                    return;
                }
                stackVal /= x2;
                break;
            case 11: // %
                stackVal = (stackVal / 100.0) * x2;
                break;
            case 0:
                stackVal = x2;
                break;
            default:
                break;
        }

        currentInput = formatNumber(stackVal);
        boolClear = true;
    }

    private void displayUnaryHistory(String func, double val) {
        String formattedVal = formatNumber(val);
        if (stackVal2 == 2 || stackVal1 == 3) {
            if (stackVal2 == 3) {
                trigDisplay = func + "(" + formattedVal + ")";
                stackVal2 = 2;
            } else {
                if (expressionString.endsWith(trigDisplay)) {
                    expressionString = expressionString.substring(0, expressionString.length() - trigDisplay.length());
                }
                trigDisplay = func + "(" + trigDisplay + ")";
            }
        } else {
            if (stackVal2 == 4) {
                expressionString = "";
            }
            trigDisplay = func + "(" + formattedVal + ")";
        }
        expressionString = expressionString + trigDisplay;
    }

    /**
     * Formats number to CAT exam specifications (8 significant digits max, clean decimals, no trailing zero noise).
     */
    public static String formatNumber(double num) {
        if (Double.isNaN(num)) return STR_MATH_ERROR;
        if (Double.isInfinite(num)) return STR_INFINITY;
        if (num == 0.0 || num == -0.0) return "0";

        // Check if integer
        if (num == Math.floor(num) && !Double.isInfinite(num) && Math.abs(num) < 1e12) {
            return String.valueOf((long) num);
        }

        // Round to 8 decimal places or up to 8 significant digits
        BigDecimal bd = BigDecimal.valueOf(num);
        bd = bd.setScale(8, RoundingMode.HALF_UP).stripTrailingZeros();
        String str = bd.toPlainString();

        // If length exceeds 10 chars (including minus and dot), format cleanly
        if (str.length() > 10) {
            if (str.contains(".")) {
                int dotIdx = str.indexOf('.');
                int allowedDecimals = Math.max(1, 8 - dotIdx);
                bd = bd.setScale(allowedDecimals, RoundingMode.HALF_UP).stripTrailingZeros();
                str = bd.toPlainString();
            }
        }
        return str;
    }
}
