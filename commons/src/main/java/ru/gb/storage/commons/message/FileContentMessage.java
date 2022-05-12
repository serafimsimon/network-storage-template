package ru.gb.storage.commons.message;

public class FileContentMessage extends Message {

    private long startPosition;
    private byte[] content;
    public boolean Last;


    public boolean isLast() {
        return Last;
    }

    public void setLast(boolean last) {
        Last = last;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
