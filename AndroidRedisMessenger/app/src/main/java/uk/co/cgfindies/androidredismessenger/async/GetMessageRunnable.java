package uk.co.cgfindies.androidredismessenger.async;

import org.droidparts.util.L;

import redis.clients.jedis.Jedis;
import uk.co.cgfindies.androidredismessenger.storage.JedisProvider;

/**
 * Provides a way to get messages.
 */
public class GetMessageRunnable extends RepeatRunnable
{
    private static GetMessageRunnable instance;

    private String lastMessage = "";

    private NewMessageFoundInListInterface messageInterface = null;

    public interface NewMessageFoundInListInterface
    {
        void newMessageFound(String message);
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
            String message = jedis.lpop("messages");
            if (message != null && !message.equals(lastMessage) && messageInterface != null)
            {
                lastMessage = message;
                messageInterface.newMessageFound(message);
            }
        }
        catch (Exception e)
        {
            L.w(e.getMessage());
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
