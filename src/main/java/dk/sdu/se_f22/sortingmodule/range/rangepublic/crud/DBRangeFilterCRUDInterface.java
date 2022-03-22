package dk.sdu.se_f22.sortingmodule.range.rangepublic.crud;

import dk.sdu.se_f22.sortingmodule.range.dbrangefilter.DBRangeFilter;
import dk.sdu.se_f22.sortingmodule.range.exceptions.InvalidFilterException;
import dk.sdu.se_f22.sortingmodule.range.exceptions.InvalidFilterIdException;
import dk.sdu.se_f22.sortingmodule.range.rangepublic.RangeFilter;

import java.util.List;

public interface DBRangeFilterCRUDInterface {
    DBRangeFilter create(String description, String name, String productAttribute, double min, double max) throws InvalidFilterException;
    RangeFilter read(int id) throws InvalidFilterIdException;
    DBRangeFilter update() throws InvalidFilterException;
    RangeFilter delete(int id) throws InvalidFilterIdException;

    List<RangeFilter> readAll();
}