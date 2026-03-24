package com.example.jeu2048.gameRender;

public class DrawableTile {
    private float x;
    private float y;

    private float animateX;
    private float animateY;

    private float width;
    private float height;

    private long value;

    public DrawableTile(float x, float y, float width, float height, long value) {
        this.x = x;
        this.y = y;
        this.animateX = x;
        this.animateY = y;
        this.width = width;
        this.height = height;
        this.value = value;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public long getValue() {
        return value;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public float getAnimateX() {
        return animateX;
    }

    public float getAnimateY() {
        return animateY;
    }

    public void setAnimateX(float animateX) {
        this.animateX = animateX;
    }

    public void setAnimateY(float animateY) {
        this.animateY = animateY;
    }
}
