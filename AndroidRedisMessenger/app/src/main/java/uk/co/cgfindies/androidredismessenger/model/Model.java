package uk.co.cgfindies.androidredismessenger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides a model for interacting with Redis Hashmaps
 */
public class Model
{
    protected List<String> fields = new ArrayList<>();
    protected Map<String, String> values;

    public Model(Map<String, String> values)
    {
        this.values = values;
    }

    public String get(String fieldName)
    {
        String value = null;
        if (fields.contains(fieldName))
        {
            value = values.get(fieldName);
        }

        return value;
    }

    public void set(String fieldName, String value)
    {
        if (fields.contains(fieldName))
        {
            values.put(fieldName, value);
        }
    }
}
