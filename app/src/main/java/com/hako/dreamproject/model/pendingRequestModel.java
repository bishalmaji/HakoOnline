package com.hako.dreamproject.model;

public class pendingRequestModel {

    String documentID;
    String chatRoomId;
    String receiverId;
    String senderId;
    String senderImage;
    String senderName;

    public  pendingRequestModel(){
        //emppty constructor
    }

    public pendingRequestModel(String documentID, String chatRoomId, String receiverId, String senderId, String senderImage, String senderName) {
        this.documentID = documentID;
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.senderImage = senderImage;
        this.senderName = senderName;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
