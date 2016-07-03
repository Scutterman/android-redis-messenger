package uk.co.cgfindies.androidredismessenger.async;

import java.util.Random;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.model.User;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a way to create random users, if they haven't been created yet.
 */
public class UserRunnable implements Runnable, JedisProvider.DoThisInterface
{
    private JedisProvider.HandleNoConnectionInterface hncInterface;
    public UserRunnable(JedisProvider.HandleNoConnectionInterface hncInterface) {
        this.hncInterface = hncInterface;
    }

    @Override
    public void run() {
        JedisProvider.doThis(this, hncInterface);
    }

    @Override
    public void doThis(Jedis jedis)
    {
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
            User.addUser(jedis, usernumber);
        }

    }
}
