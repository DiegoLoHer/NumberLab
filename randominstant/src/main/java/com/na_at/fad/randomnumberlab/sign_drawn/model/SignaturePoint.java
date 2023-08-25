package com.na_at.fad.randomnumberlab.sign_drawn.model;

import java.io.Serializable;

public class SignaturePoint implements Serializable {
    float x;
    float y;
    long ts;

    public SignaturePoint(float x, float y, long ts) {
        this.x = x;
        this.y = y;
        this.ts = ts;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getTs() {
        return ts;
    }
}
