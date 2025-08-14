package com.gradlemedium100.common.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BaseValidatorTest {

    @Mock
    private Validator javaValidator;

    private BaseValidator baseValidator;
    private Set<ConstraintViolation<Object>> violations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        baseValidator = new BaseValidator(javaValidator);
        violations = new HashSet<>();
    }

    @Test
    public void testValidate_WithValidObject() {
        // Setup
        Object testObject = new Object();
        when(javaValidator.validate(testObject)).thenReturn(violations);

        // Execute
        boolean result = baseValidator.validate(testObject);

        // Verify
        assertTrue(result);
        verify(javaValidator).validate(testObject);
    }

    @Test
    public void testValidate_WithNullObject() {
        // Execute
        boolean result = baseValidator.validate(null);

        // Verify
        assertFalse(result);
        verify(javaValidator, never()).validate(any());
    }

    @Test
    public void testValidateWithErrors_NoErrors() {
        // Setup
        Object testObject = new Object();
        when(javaValidator.validate(testObject)).thenReturn(violations);

        // Execute
        List<String> errors = baseValidator.validateWithErrors(testObject);

        // Verify
        assertTrue(errors.isEmpty());
        verify(javaValidator).validate(testObject);
    }

    @Test
    public void testIsValid_ValidEmail() {
        // Execute & Verify
        assertTrue(baseValidator.isValid("user@example.com", "email"));
    }

    @Test
    public void testIsValid_InvalidEmail() {
        // Execute & Verify
        assertFalse(baseValidator.isValid("invalid-email", "email"));
    }

    @Test
    public void testRegisterValidationType() {
        // Setup
        String typeName = "custom";
        Pattern pattern = Pattern.compile("^[A-Z]{3}$");

        // Execute
        baseValidator.registerValidationType(typeName, pattern);

        // Verify
        assertTrue(baseValidator.hasValidationType(typeName));
        assertTrue(baseValidator.isValid("ABC", typeName));
        assertFalse(baseValidator.isValid("abc", typeName));
    }

    @Test
    public void testIsValid_UnknownType() {
        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> {
            baseValidator.isValid("test", "unknown-type");
        });
    }

    @Test
    public void testRemoveValidationType() {
        // Setup
        String typeName = "temp";
        baseValidator.registerValidationType(typeName, Pattern.compile(".*"));

        // Execute
        boolean removed = baseValidator.removeValidationType(typeName);

        // Verify
        assertTrue(removed);
        assertFalse(baseValidator.hasValidationType(typeName));
    }

    @Test
    public void testGetValidationTypeNames() {
        // Execute
        Set<String> typeNames = baseValidator.getValidationTypeNames();

        // Verify
        assertTrue(typeNames.contains("email"));
        assertTrue(typeNames.contains("phone"));
        assertTrue(typeNames.contains("zipcode"));
        assertTrue(typeNames.contains("url"));
    }
}