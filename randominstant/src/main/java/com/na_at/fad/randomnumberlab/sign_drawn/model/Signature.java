package com.na_at.fad.randomnumberlab.sign_drawn.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Signature implements Serializable {
    public static final String UNIT_AVERAGE_SPEED = "px/s";
    public static final String UNIT_SIGN_LENGTH = "px";
    public static final String UNIT_SIGN_DURATION = "s";

    String averageSpeed;
    int spaces;
    int attackPoints;
    String signLenght;
    String signDuration;
    String unitSignLength; // px
    String unitSignDuration; // s
    List<SignaturePoint> signaturePoints;


    public Signature() {
        signaturePoints = new ArrayList<>();
        attackPoints = 0;
        spaces = 0;
    }

    public Signature(List<SignaturePoint> signaturePoints) {
        this.signaturePoints = signaturePoints;
    }

    public void addSignaturePoint(SignaturePoint signaturePoint) {
        signaturePoints.add(signaturePoint);
    }

    public void addAttackPoint() {
        attackPoints++;
        spaces = attackPoints - 1;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public int getSpaces() {
        return spaces;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public List<SignaturePoint> getSignaturePoints() {
        return signaturePoints;
    }

    public String getSignLenght() {
        return signLenght;
    }

    public String getSignDuration() {
        return signDuration;
    }

    public String getUnitSignLength() {
        return unitSignLength;
    }

    public String getUnitSignDuration() {
        return unitSignDuration;
    }

    public void calculateSpeed() {
        double total_pixels = getTotalLengthinPixels();
        double totalSecs = getTotalDurationinSeconds();
        double speed = total_pixels / totalSecs;
        averageSpeed = formatDecimal(speed, 2);
        signLenght = formatDecimal(total_pixels, 2);
        signDuration = totalSecs + "";
        unitSignLength = "px";
        unitSignDuration = "s";
    }

    private double getTotalLengthinPixels() {
        double total_pixels = 0;

        for (int i = 0; i < signaturePoints.size() - 1; i++) {
            total_pixels += distance_beetwen_points(signaturePoints.get(i), signaturePoints.get(i + 1));

        }
        return total_pixels;
    }

    private double getTotalDurationinSeconds() {
        long t1 = signaturePoints.get(0).getTs();
        long t2 = signaturePoints.get(signaturePoints.size() - 1).getTs();
        double totalmilis = t2 - t1;
        double totalSecs = totalmilis / 1000.0;

        return totalSecs;
    }

    private double distance_beetwen_points(SignaturePoint p1, SignaturePoint p2) {

        // c1 ^ 2
        double c1 = Math.pow(Math.abs(p1.getX() - p2.getX()), 2);

        // c2 ^ 2
        double c2 = Math.pow(Math.abs(p1.getY() - p2.getY()), 2);

        //teorema de pitagoras
        // h = raiz(c1^2 + c2^2)
        double distance = Math.sqrt(c1 + c2);

        return distance;

    }

    public String formatDecimal(double number, int numberdecimals) {
        return String.format("%." + numberdecimals + "f", number);
    }
}
