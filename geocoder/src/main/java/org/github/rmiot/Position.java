package org.github.rmiot;

public record Position(double latitude, double longitude) {

    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
