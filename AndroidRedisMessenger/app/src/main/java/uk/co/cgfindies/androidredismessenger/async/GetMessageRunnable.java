package uk.co.cgfindies.androidredismessenger.async;

import org.droidparts.util.L;

import java.util.Map;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.model.MessageDetails;
import uk.co.cgfindies.androidredismessenger.model.User;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a way to get messages.
 */
public class GetMessageRunnable extends RepeatRunnable
{
    private static GetMessageRunnable instance;

    private String lastMessageKey = "";

    private NewMessageFoundInListInterface messageInterface = null;

    public interface NewMessageFoundInListInterface
    {
        void newMessageFound(MessageDetails messageDetails);
    }

    /**
     * Set up the runner to get new messages.
     */
    private GetMessageRunnable(NewMessageFoundInListInterface iface) {
        super();
        messageInterface = iface;
    }

    /**
     * Don't want more than one of these running at once.
     *
     * @param iface Used to report messages back. Can be null.
     * @return static
     */
    public static GetMessageRunnable getInstance(NewMessageFoundInListInterface iface)
    {
        if (instance == null)
        {
            instance = new GetMessageRunnable(iface);
        }

        return instance;
    }

    @Override
    public void run() {
        Jedis jedis = null;

        try
        {
            jedis = JedisProvider.getInstance().getJedisInstance();
            String messageKey = jedis.lindex("messageKeys", -1);

            if (messageKey != null && !messageKey.equals(lastMessageKey) && messageInterface != null)
            {
                Map<String, String> messageDetails = jedis.hgetAll("messages:" + messageKey);
                User user = new User(jedis.hgetAll("users:" + messageDetails.get("username")));
                MessageDetails details = new MessageDetails(messageDetails, user);

                lastMessageKey = messageKey;
                messageInterface.newMessageFound(details);
            }
        }
        catch (Exception e)
        {
            L.w(e.getStackTrace());
        }
        finally
        {
            if (jedis != null)
            {
                jedis.close();
            }
        }

        super.run();
    }

}
