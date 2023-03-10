package dk.sdu.se_f22.sortingmodule.range.validators;

import dk.sdu.se_f22.sortingmodule.range.exceptions.InvalidFilterException;

import java.time.Instant;

public class Validator {
    public static void NoNegativeValue(double v) throws InvalidFilterException {
        if (v < 0){
            throw new InvalidFilterException("Input is negative.");
        }
    }
    public static void NoNegativeValue(long v) throws InvalidFilterException {
        if (v < 0){
            throw new InvalidFilterException("Input is negative.");
        }
    }


    public static void NoSpecialCharacters(String s) throws InvalidFilterException{
        String[] specialCharacters = {"%", "/", "!", "#", "", "(", ")", "=", "{", "}", " ", "", ".", "-", "*"};
        if(s == null || s.isEmpty()) {
            throw new InvalidFilterException("String was empty.");
        }

        String sub = s.substring(0,1);
        for (String c : specialCharacters){
            if (sub.equals(c)) {
                throw new InvalidFilterException("String started with a special character.");
            }
        }
    }

    public static void MaxLessThanMin(double min, double max) throws InvalidFilterException{
        if (min > max) {
            throw new InvalidFilterException("min cannot be greater than max");
        }
    }
    public static void MaxLessThanMin(long min, long max) throws InvalidFilterException{
        if (min > max) {
            throw new InvalidFilterException("min cannot be greater than max");
        }
    }
    public static void MaxLessThanMin(Instant min, Instant max) throws InvalidFilterException{
        if (min.isAfter(max)) {
            throw new InvalidFilterException("min cannot be greater than max");
        }
    }
}
