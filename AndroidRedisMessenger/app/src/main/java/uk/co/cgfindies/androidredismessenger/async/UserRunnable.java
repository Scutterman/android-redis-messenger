package uk.co.cgfindies.androidredismessenger.async;

import org.droidparts.util.L;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a way to create random users, if they haven't been created yet.
 */
public class UserRunnable implements Runnable, JedisProvider.DoThisInterface
{
    @Override
    public void run() {
        JedisProvider.doThis(this);
    }

    @Override
    public void doThis(Jedis jedis) {
        long numberOfUsrs = jedis.llen("users");

        if (numberOfUsrs >= 10)
        {
            return;
        }

        int numberOfUsersRequired = 10 - (int)numberOfUsrs;
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < numberOfUsersRequired; i++)
        {
            int usernumber = random.nextInt(1000);

            String colour = "";
            if (usernumber >= 900)
            {
                colour = "#ff0000";
            }
            else if (usernumber >= 800)
            {
                colour = "#00ff00";
            }
            else if (usernumber >= 700)
            {
                colour = "#0000ff";
            }
            else if (usernumber >= 600)
            {
                colour = "#ffff00";
            }
            else if (usernumber >= 500)
            {
                colour = "#ff00ff";
            }
            else if (usernumber >= 400)
            {
                colour = "#00ffff";
            }
            else if (usernumber >= 300)
            {
                colour = "#ffffff";
            }
            else if (usernumber >= 200)
            {
                colour = "#000000";
            }
            else if (usernumber >= 100)
            {
                colour = "#808080";
            }
            else if (usernumber >= 800)
            {
                colour = "#ff0080";
            }

            Map<String, String> user = new HashMap<>();
            user.put("username", "user" + Integer.toString(usernumber));
            user.put("colour", colour);
            jedis.hmset("users:" + user.get("username"), user);
        }

    }
}