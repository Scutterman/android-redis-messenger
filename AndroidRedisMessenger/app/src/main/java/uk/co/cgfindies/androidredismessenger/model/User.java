package uk.co.cgfindies.androidredismessenger.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;

/**
 * Provides a container for User data
 */
public class User extends Model
{
    public User(Map<String,String> values)
    {
        super(values);
        fields.add("username");
        fields.add("colour");
    }

    public static User createFromUsername(Jedis jedis, String username)
    {
        User user = null;

        Map<String, String> userMap = jedis.hgetAll("users:" + username);
        if (userMap != null)
        {
            user = new User(userMap);
        }

        return user;
    }

    public static String addUser(Jedis jedis, int usernumber)
    {
        if (usernumber == -1)
        {
            usernumber = (new Random(System.currentTimeMillis()).nextInt(1000));
        }

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

        String username = "user" + Integer.toString(usernumber);
        Map<String, String> user = new HashMap<>();
        user.put("username", username);
        user.put("colour", colour);
        jedis.hmset("users:" + username, user);
        jedis.sadd("usernames", username);

        return username;
    }
}
