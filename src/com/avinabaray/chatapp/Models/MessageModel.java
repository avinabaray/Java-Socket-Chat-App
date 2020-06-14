package com.avinabaray.chatapp.Models;

import com.avinabaray.chatapp.Server.ClientHandler;

import java.io.Serializable;
import java.util.Vector;

public class MessageModel implements Serializable {

    private MessageType messageType;
    private String sender;
    private String receiver;
    private String message;
    private String timestamp;
    private Vector<ClientHandler> activeUsers;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Vector<ClientHandler> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(Vector<ClientHandler> activeUsers) {
        this.activeUsers = activeUsers;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
