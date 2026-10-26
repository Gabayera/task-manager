package com.taskmanager.util;

import java.util.List;
import com.taskmanager.exception.ValidationException;

public class IdGenerator {

    public static String generateId(String prefix, long number) {
        return prefix + (1000 + number);
    }

    public static long extractNumber(String id) {
        if (id == null || id.length() < 2) {
            throw new ValidationException("Invalid ID format");
        }
        try {
            // Example: T2001 -> extracts 2001
            return Long.parseLong(id.substring(1));
        } catch (NumberFormatException e) {
            throw new ValidationException("ID contains non-numeric characters");
        }
    }

    public static String getNextId(String prefix, List<String> existingIds) {
        if (existingIds == null || existingIds.isEmpty()) {
            return generateId(prefix, 1);
        }

        long maxAbsoluteNumber = 0;
        for (String id : existingIds) {
            long number = extractNumber(id);
            if (number > maxAbsoluteNumber) {
                maxAbsoluteNumber = number;
            }
        }

        // Next absolute number, e.g., if max is 1005, next is 1006
        long nextAbsoluteNumber = maxAbsoluteNumber + 1;

        // Convert back to the offset format required by generateId
        return prefix + nextAbsoluteNumber;
    }
}