package Entity;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    private String messageId;
    private String action;
    private String clientId;
    private String friendClientId;
    private String messageContent;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFriendClientId() {
        return friendClientId;
    }

    public void setFriendClientId(String friendClientId) {
        this.friendClientId = friendClientId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
