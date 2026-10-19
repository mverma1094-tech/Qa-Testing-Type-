package com.manish.qa.unit;

import com.manish.shop.security.InputValidator;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * UNIT TESTING
 * Basic valid/invalid checks for each validator method.
 */
@Test(groups = "unit")
public class InputValidatorTest {

    public void acceptsNormalUsername() {
        assertTrue(InputValidator.isValidUsername("standard_user"));
    }

    public void rejectsNullOrBlankUsername() {
        assertFalse(InputValidator.isValidUsername(null));
        assertFalse(InputValidator.isValidUsername(""));
        assertFalse(InputValidator.isValidUsername("   "));
    }

    public void acceptsAndRejectsEmails() {
        assertTrue(InputValidator.isValidEmail("manish@example.com"));
        assertFalse(InputValidator.isValidEmail("manish@"));
        assertFalse(InputValidator.isValidEmail("no-at-sign.com"));
    }

    public void acceptsFiveAndNineDigitZips() {
        assertTrue(InputValidator.isValidZip("20151"));
        assertTrue(InputValidator.isValidZip("20151-1234"));
        assertFalse(InputValidator.isValidZip("2015"));
        assertFalse(InputValidator.isValidZip("ABCDE"));
    }

    public void escapeHtmlLeavesPlainTextAlone() {
        assertEquals(InputValidator.escapeHtml("Manish"), "Manish");
        assertEquals(InputValidator.escapeHtml(null), "");
    }
}
