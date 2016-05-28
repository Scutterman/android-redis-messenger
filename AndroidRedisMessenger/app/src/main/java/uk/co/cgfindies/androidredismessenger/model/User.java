package uk.co.cgfindies.androidredismessenger.model;

import org.droidparts.util.L;

import java.util.Map;

/**
 * Created by Scutterman on 28/05/2016.
 */
public class User extends Model
{
    public User(Map<String,String> values)
    {
        super(values);
        fields.add("username");
        fields.add("colour");
    }
}
