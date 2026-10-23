package com.taskmanager.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import com.taskmanager.exception.ValidationException;

public class IdGenerator {
    // T + 5 -> T1005
    public static String generatorId(String prefix, long number) {
        String cal = Long.toString(number + 1000);
        prefix += cal;
        return prefix;
    }

    public static long extranctNumber(String id) {
        Matcher m = Pattern.compile("\\d+").matcher(id);
        if (m.find()) {
            long number = Long.parseLong(m.group());
            return number;
        }
        throw new ValidationException("the id is not valid");
    }

    public static String getNextId(String prefix, List<String> existingIds) {
        long maxNnumber = 0;
        for (String id : existingIds) {
            long number = extranctNumber(id);
            if (number > maxNnumber) {
                maxNnumber = number;
            }
        }
        return generatorId(prefix, maxNnumber + 1);
    }
}
