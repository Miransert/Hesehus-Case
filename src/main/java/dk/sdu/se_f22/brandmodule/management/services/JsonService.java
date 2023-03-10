package dk.sdu.se_f22.brandmodule.management.services;

import dk.sdu.se_f22.sharedlibrary.models.Brand;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonService implements IJsonService {

    @Override
    public List<Brand> deserializeBrand() {
        try {
            // Load .json file
            Object obj = new JSONParser().parse(new FileReader("src/main/java/dk/sdu/se_f22/brandmodule/management/json/brands.json"));

            // JSONObject will be worked with
            JSONObject jo = (JSONObject) obj;
            var keys = jo.keySet().iterator();

            // List of brands that will be returned
            ArrayList<Brand> brands = new ArrayList<>();

            while (keys.hasNext()) {
                var name = keys.next();
                JSONObject properties = (JSONObject) jo.get(name);

                // Build product array
                JSONArray objs = (JSONArray) properties.get("products");
                Iterator<String> iterator = objs.iterator();

                ArrayList<String> products = new ArrayList<>();
                iterator.forEachRemaining(p -> products.add(p));

                Brand brand = new Brand(
                        null,
                        name.toString(),
                        properties.get("description").toString(),
                        properties.get("founded").toString(),
                        properties.get("headquarters").toString(),
                        products
                );
                // Add brand to list of brands
                brands.add(brand);
            }

            return brands;

        } catch (IOException e) {
            System.out.println("IOException during JSON parsing.");
        } catch (ParseException e) {
            System.out.println("ParseException during JSON parsing.");
        }
        return null;
    }
}