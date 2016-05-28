package uk.co.cgfindies.androidredismessenger.model;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;

/**
 * Provides a container for messages
 */
public class MessageDetails extends Model
{
    private User user;

    public MessageDetails(Map<String, String> values, User user) {
        super(values);
        fields.add("message");
        fields.add("username");
        fields.add("timestamp");

        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public static void addMessage(Jedis jedis, String messageContent, String username, long timestamp)
    {
        if (timestamp == -1)
        {
            timestamp = System.currentTimeMillis();
        }

        String timestampString = Long.toString(timestamp);
        Map<String, String> message = new HashMap<>();
        message.put("message", messageContent);
        message.put("timestamp", timestampString);
        message.put("username", username);

        jedis.hmset("messages:" + timestampString, message);
        jedis.rpush("messageKeys", timestampString);

    }
}
