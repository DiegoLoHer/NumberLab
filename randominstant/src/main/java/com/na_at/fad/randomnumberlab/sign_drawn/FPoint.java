package com.na_at.fad.randomnumberlab.sign_drawn;

import java.io.Serializable;

class FPoint implements Serializable {

    float x;
    float y;

    FPoint() {
        x = y = -1;
    }

    @Override
    public String toString() {
        return "x: " + x + ", y: " + y;
    }

}
