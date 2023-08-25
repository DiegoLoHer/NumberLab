package com.na_at.fad.randomnumberlab.sign_drawn;

import androidx.annotation.NonNull;

import java.util.List;

public class DrawUtils {

    private DrawUtils() {
        // singleton
    }

    /**
     * Check whether the list of points are just one point or not
     */
    static boolean isAPoint(@NonNull List<FPoint> points) {
        if (points.isEmpty())
            return false;

        if (points.size() == 1)
            return true;

        for (int i = 1; i < points.size(); i++) {
            if (points.get(i - 1).x != points.get(i).x || points.get(i - 1).y != points.get(i).y)
                return false;
        }

        return true;
    }

}
