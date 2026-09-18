package com.catcalculator.test;

import com.catcalculator.model.CalculatorEngine;

/**
 * Headless test runner to automatically verify CalculatorEngine arithmetic and behavior.
 */
public class EngineTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("Running CAT Calculator Engine Tests...\n");

        testBasicArithmetic();
        testOperatorPrecedence();
        testUnaryOperations();
        testMemoryOperations();
        testBackspaceAndClear();
        testDivisionByZero();

        System.out.println("\n-------------------------------------------");
        System.out.println("Test Results: " + passed + " Passed, " + failed + " Failed.");
        System.out.println("-------------------------------------------");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertEquals(String testName, String expected, String actual) {
        if (expected.equals(actual)) {
            System.out.println("[PASS] " + testName + " => " + actual);
            passed++;
        } else {
            System.err.println("[FAIL] " + testName + " => Expected: [" + expected + "], Got: [" + actual + "]");
            failed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            passed++;
        } else {
            System.err.println("[FAIL] " + testName + " => Condition was false");
            failed++;
        }
    }

    private static void testBasicArithmetic() {
        CalculatorEngine e = new CalculatorEngine();

        // 12 + 8 = 20
        e.inputDigit("1");
        e.inputDigit("2");
        e.inputBinaryOp("+");
        e.inputDigit("8");
        e.calculateEquals();
        assertEquals("Addition (12 + 8)", "20", e.getCurrentInput());

        // 45 - 15 = 30
        e.allClear();
        e.inputDigit("4");
        e.inputDigit("5");
        e.inputBinaryOp("-");
        e.inputDigit("1");
        e.inputDigit("5");
        e.calculateEquals();
        assertEquals("Subtraction (45 - 15)", "30", e.getCurrentInput());

        // 7 * 6 = 42
        e.allClear();
        e.inputDigit("7");
        e.inputBinaryOp("*");
        e.inputDigit("6");
        e.calculateEquals();
        assertEquals("Multiplication (7 * 6)", "42", e.getCurrentInput());

        // 100 / 4 = 25
        e.allClear();
        e.inputDigit("1");
        e.inputDigit("0");
        e.inputDigit("0");
        e.inputBinaryOp("/");
        e.inputDigit("4");
        e.calculateEquals();
        assertEquals("Division (100 / 4)", "25", e.getCurrentInput());
    }

    private static void testOperatorPrecedence() {
        CalculatorEngine e = new CalculatorEngine();

        // 2 + 3 * 4 = 14
        e.inputDigit("2");
        e.inputBinaryOp("+");
        e.inputDigit("3");
        e.inputBinaryOp("*");
        e.inputDigit("4");
        e.calculateEquals();
        assertEquals("Precedence (2 + 3 * 4)", "14", e.getCurrentInput());

        // 20 - 6 / 2 = 17
        e.allClear();
        e.inputDigit("2");
        e.inputDigit("0");
        e.inputBinaryOp("-");
        e.inputDigit("6");
        e.inputBinaryOp("/");
        e.inputDigit("2");
        e.calculateEquals();
        assertEquals("Precedence (20 - 6 / 2)", "17", e.getCurrentInput());
    }

    private static void testUnaryOperations() {
        CalculatorEngine e = new CalculatorEngine();

        // sqrt(25) = 5
        e.inputDigit("2");
        e.inputDigit("5");
        e.inputUnaryOp("sqrt");
        assertEquals("Square Root (sqrt(25))", "5", e.getCurrentInput());

        // 1/x (4) = 0.25
        e.allClear();
        e.inputDigit("4");
        e.inputUnaryOp("1/x");
        assertEquals("Reciprocal (1/4)", "0.25", e.getCurrentInput());

        // +/- (9) = -9
        e.allClear();
        e.inputDigit("9");
        e.inputUnaryOp("+/-");
        assertEquals("Negate (+/- 9)", "-9", e.getCurrentInput());
    }

    private static void testMemoryOperations() {
        CalculatorEngine e = new CalculatorEngine();

        // MS (15)
        e.inputDigit("1");
        e.inputDigit("5");
        e.inputMemory("MS");
        assertTrue("Memory Flag Set after MS", e.hasMemory());

        // Clear and add MR: 10 + MR = 25
        e.allClear();
        assertTrue("Memory persists across AllClear", e.hasMemory());
        e.inputDigit("1");
        e.inputDigit("0");
        e.inputBinaryOp("+");
        e.inputMemory("MR");
        e.calculateEquals();
        assertEquals("Memory Recall (10 + MR)", "25", e.getCurrentInput());

        // M+ (5): memory becomes 20
        e.allClear();
        e.inputDigit("5");
        e.inputMemory("M+");
        e.allClear();
        e.inputMemory("MR");
        assertEquals("Memory Add (M+)", "20", e.getCurrentInput());

        // MC: memory cleared
        e.inputMemory("MC");
        assertTrue("Memory Flag Cleared after MC", !e.hasMemory());
    }

    private static void testBackspaceAndClear() {
        CalculatorEngine e = new CalculatorEngine();

        // 1234 <- -> 123
        e.inputDigit("1");
        e.inputDigit("2");
        e.inputDigit("3");
        e.inputDigit("4");
        e.backspace();
        assertEquals("Backspace (1234 <-)", "123", e.getCurrentInput());

        // 5 <- -> 0
        e.allClear();
        e.inputDigit("5");
        e.backspace();
        assertEquals("Backspace single digit (5 <-)", "0", e.getCurrentInput());
    }

    private static void testDivisionByZero() {
        CalculatorEngine e = new CalculatorEngine();

        // 10 / 0 = Math Error
        e.inputDigit("1");
        e.inputDigit("0");
        e.inputBinaryOp("/");
        e.inputDigit("0");
        e.calculateEquals();
        assertEquals("Division by zero", "Math Error", e.getCurrentInput());
    }
}
