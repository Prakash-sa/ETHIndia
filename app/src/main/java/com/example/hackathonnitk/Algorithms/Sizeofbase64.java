package com.example.hackathonnitk.Algorithms;
public class Sizeofbase64 {
    public Double calcBase64SizeInKBytes(String base64String) {
        Double result = -1.0;
        if(!base64String.isEmpty()) {
            Double padding ;
            if(base64String.endsWith("==")) padding = 2.0;
            else if (base64String.endsWith("=")) padding = 1.0;
            else padding=0.0;
            result = ((base64String.length() / 4) * 3 ) - padding;
        }
        return result / 1000;
    }
}
