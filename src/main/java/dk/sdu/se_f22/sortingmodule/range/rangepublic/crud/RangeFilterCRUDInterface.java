package dk.sdu.se_f22.sortingmodule.range.rangepublic.crud;

import dk.sdu.se_f22.sortingmodule.range.exceptions.InvalidFilterException;
import dk.sdu.se_f22.sortingmodule.range.exceptions.InvalidFilterIdException;
import dk.sdu.se_f22.sortingmodule.range.rangepublic.RangeFilter;

import java.time.Instant;
import java.util.List;

public interface RangeFilterCRUDInterface {
    RangeFilter create(String description, String name, String productAttribute, double dbMinToSave, double dbMaxToSave) throws InvalidFilterException;
    RangeFilter create(String description, String name, String productAttribute, long dbMinToSave, long dbMaxToSave) throws InvalidFilterException;
    RangeFilter create(String description, String name, String productAttribute, Instant dbMinToSave, Instant dbMaxToSave) throws InvalidFilterException;

    RangeFilter read(int id) throws InvalidFilterIdException;
    RangeFilter delete(int id) throws InvalidFilterIdException;

    RangeFilter update(RangeFilter filter, String newName) throws InvalidFilterException;
    RangeFilter update(RangeFilter filter, String newName, String newDescription) throws InvalidFilterException;
    RangeFilter update(RangeFilter filter, double dbMinToSave, double dbMaxToSave) throws InvalidFilterException;
    RangeFilter update(RangeFilter filter, long dbMinToSave, long dbMaxToSave) throws InvalidFilterException;
    RangeFilter update(RangeFilter filter, Instant dbMinToSave, Instant dbMaxToSave) throws InvalidFilterException;

    List<RangeFilter> readAll();
}