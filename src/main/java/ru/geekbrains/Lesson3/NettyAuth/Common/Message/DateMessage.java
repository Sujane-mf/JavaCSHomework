package ru.geekbrains.Lesson3.NettyAuth.Common.Message;

import java.util.Date;

public class DateMessage extends Message {
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
