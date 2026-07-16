package com.spt.bas.client.vo;

/**
 * @Author: gaojy
 * @create 2022/3/18 18:00
 * @version: 1.0
 * @description:
 */
public class MessagePushVo{
    private String phone;
    private String email;
    private String title;
    private String message;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
