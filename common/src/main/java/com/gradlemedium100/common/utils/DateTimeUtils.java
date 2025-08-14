package com.gradlemedium100.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date and time operations, formatting, parsing and conversion between different formats.
 * This class provides common operations for working with the Java 8 Date and Time API.
 */
public final class DateTimeUtils {

    /**
     * Default date format string (yyyy-MM-dd)
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Default date-time format string (yyyy-MM-dd HH:mm:ss)
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Zone ID for UTC timezone
     */
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private DateTimeUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Formats a LocalDate using the provided pattern
     *
     * @param date    the LocalDate to format, not null
     * @param pattern the pattern to use for formatting, not null
     * @return the formatted date string
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws NullPointerException     if date or pattern is null
     */
    public static String formatDate(LocalDate date, String pattern) {
        if (date == null) {
            throw new NullPointerException("Date must not be null");
        }
        if (pattern == null) {
            throw new NullPointerException("Pattern must not be null");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return date.format(formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date pattern: " + pattern, e);
        }
    }

    /**
     * Formats a LocalDateTime using the provided pattern
     *
     * @param dateTime the LocalDateTime to format, not null
     * @param pattern  the pattern to use for formatting, not null
     * @return the formatted date-time string
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws NullPointerException     if dateTime or pattern is null
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            throw new NullPointerException("DateTime must not be null");
        }
        if (pattern == null) {
            throw new NullPointerException("Pattern must not be null");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date-time pattern: " + pattern, e);
        }
    }

    /**
     * Parses a string into a LocalDate using the provided pattern
     *
     * @param dateStr the date string to parse, not null
     * @param pattern the pattern to use for parsing, not null
     * @return the parsed LocalDate
     * @throws DateTimeParseException   if the text cannot be parsed
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws NullPointerException     if dateStr or pattern is null
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null) {
            throw new NullPointerException("Date string must not be null");
        }
        if (pattern == null) {
            throw new NullPointerException("Pattern must not be null");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Failed to parse date: " + dateStr, e.getParsedString(), e.getErrorIndex(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date pattern: " + pattern, e);
        }
    }

    /**
     * Parses a string into a LocalDateTime using the provided pattern
     *
     * @param dateTimeStr the date-time string to parse, not null
     * @param pattern     the pattern to use for parsing, not null
     * @return the parsed LocalDateTime
     * @throws DateTimeParseException   if the text cannot be parsed
     * @throws IllegalArgumentException if the pattern is invalid
     * @throws NullPointerException     if dateTimeStr or pattern is null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null) {
            throw new NullPointerException("DateTime string must not be null");
        }
        if (pattern == null) {
            throw new NullPointerException("Pattern must not be null");
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Failed to parse date-time: " + dateTimeStr, e.getParsedString(), e.getErrorIndex(), e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid date-time pattern: " + pattern, e);
        }
    }

    /**
     * Converts a LocalDateTime from a specific timezone to UTC
     *
     * @param localDateTime the LocalDateTime to convert, not null
     * @param zoneId        the ZoneId of the provided LocalDateTime, not null
     * @return the converted ZonedDateTime in UTC
     * @throws NullPointerException if localDateTime or zoneId is null
     */
    public static ZonedDateTime convertToUtc(LocalDateTime localDateTime, ZoneId zoneId) {
        if (localDateTime == null) {
            throw new NullPointerException("LocalDateTime must not be null");
        }
        if (zoneId == null) {
            throw new NullPointerException("ZoneId must not be null");
        }
        
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        return zonedDateTime.withZoneSameInstant(UTC_ZONE_ID);
    }

    /**
     * Gets the current timestamp in milliseconds
     *
     * @return the current timestamp in milliseconds since epoch
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * Convenience method to format a LocalDate using the default date format
     * 
     * @param date the LocalDate to format, not null
     * @return the formatted date string
     * @throws NullPointerException if date is null
     */
    public static String formatDate(LocalDate date) {
        return formatDate(date, DEFAULT_DATE_FORMAT);
    }
    
    /**
     * Convenience method to format a LocalDateTime using the default date-time format
     * 
     * @param dateTime the LocalDateTime to format, not null
     * @return the formatted date-time string
     * @throws NullPointerException if dateTime is null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * Convenience method to parse a string into a LocalDate using the default date format
     * 
     * @param dateStr the date string to parse, not null
     * @return the parsed LocalDate
     * @throws DateTimeParseException if the text cannot be parsed
     * @throws NullPointerException if dateStr is null
     */
    public static LocalDate parseDate(String dateStr) {
        return parseDate(dateStr, DEFAULT_DATE_FORMAT);
    }
    
    /**
     * Convenience method to parse a string into a LocalDateTime using the default date-time format
     * 
     * @param dateTimeStr the date-time string to parse, not null
     * @return the parsed LocalDateTime
     * @throws DateTimeParseException if the text cannot be parsed
     * @throws NullPointerException if dateTimeStr is null
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return parseDateTime(dateTimeStr, DEFAULT_DATETIME_FORMAT);
    }
    
    /**
     * Converts a millisecond timestamp to LocalDateTime
     * 
     * @param timestamp the timestamp in milliseconds
     * @return the LocalDateTime representing the timestamp
     */
    public static LocalDateTime timestampToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), UTC_ZONE_ID);
    }
    
    /**
     * Converts a LocalDateTime to millisecond timestamp
     * 
     * @param localDateTime the LocalDateTime to convert, not null
     * @return the timestamp in milliseconds
     * @throws NullPointerException if localDateTime is null
     */
    public static long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new NullPointerException("LocalDateTime must not be null");
        }
        
        return localDateTime.atZone(UTC_ZONE_ID).toInstant().toEpochMilli();
    }

    // TODO: Add methods for working with durations and periods
    
    // FIXME: The current implementation doesn't handle time zones correctly in all cases
}