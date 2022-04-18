package ru.geekbrains.Lesson3.NettyAuth.Common.Message;

public class TextMessage extends Message {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
