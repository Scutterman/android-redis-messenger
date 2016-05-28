package uk.co.cgfindies.androidredismessenger.model;

import org.droidparts.util.L;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;

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

    public static void addUser(Jedis jedis, int usernumber)
    {
        String colour = "";
        if (usernumber >= 900)
        {
            colour = "#FF0000";
        }
        else if (usernumber >= 800)
        {
            colour = "#00FF00";
        }
        else if (usernumber >= 700)
        {
            colour = "#0000FF";
        }
        else if (usernumber >= 600)
        {
            colour = "#FFFF00";
        }
        else if (usernumber >= 500)
        {
            colour = "#FF00FF";
        }
        else if (usernumber >= 400)
        {
            colour = "#00FFFF";
        }
        else if (usernumber >= 300)
        {
            colour = "#FFFFFF";
        }
        else if (usernumber >= 200)
        {
            colour = "#000000";
        }
        else if (usernumber >= 100)
        {
            colour = "#808080";
        }
        else if (usernumber >= 0)
        {
            colour = "#FF0080";
        }

        Map<String, String> user = new HashMap<>();
        user.put("username", "user" + Integer.toString(usernumber));
        user.put("colour", colour);
        jedis.hmset("users:" + user.get("username"), user);
        jedis.sadd("usernames", user.get("username"));
    }
}
