package com.na_at.fad.randomnumberlab.sign_drawn;

import com.na_at.fad.randomnumberlab.sign_drawn.model.Signature;

public interface DrawingCanvasListener {

    void onDrawDown(Signature signature);

    void onDrawUp(Signature signature);
}
