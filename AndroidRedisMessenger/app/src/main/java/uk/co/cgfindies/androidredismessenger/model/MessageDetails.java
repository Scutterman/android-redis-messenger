package uk.co.cgfindies.androidredismessenger.model;

import java.util.Map;

/**
 * Created by Scutterman on 28/05/2016.
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
}
