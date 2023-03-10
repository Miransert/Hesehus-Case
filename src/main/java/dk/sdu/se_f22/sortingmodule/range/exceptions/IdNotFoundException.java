package dk.sdu.se_f22.sortingmodule.range.exceptions;

/**
 * IdNotFoundException is thrown when a filter is not found from the database with the given id.
 */
public class IdNotFoundException extends RangeFilterException {
    public IdNotFoundException(String message) { super(message); }
}
