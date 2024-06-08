package com.example.insightanalytics;
import java.util.UUID;

public class UniqueTokenGenerator {


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }



}
