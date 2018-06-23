package Esercizio6;

import java.util.concurrent.ThreadLocalRandom;

final class Coordinate {
    private final double lat;
    private final double lon;

    public Coordinate(double lat, double lon)
    {
    	this.lat=lat;
    	this.lon=lon;
    }
 
    /**
     * Returns the distance (expressed in km) between two coordinates
     */
    public double distance(final Coordinate from) {
        final double dLat = Math.toRadians(from.lat - this.lat);
        final double dLng = Math.toRadians(from.lon - this.lon);
        final double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(from.lat))
                * Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        return (6371.000 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
    }

    @Override
    public String toString() {
        return "[" + lat + ", " + lon + "]";
    }
}

class GPS implements Runnable {
    @Override
    public void run() {
        // Update curLocation with first coordinate
    	
    	Coordinate temp = new Coordinate(ThreadLocalRandom.current().nextDouble(-90.0, +90.0), ThreadLocalRandom.current().nextDouble(-180.0, +180.0));
    	
        Esercizio6.curLocation = temp;

        // Wait before updating position
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(10, 20));
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Update curLocation with second coordinate
        temp = new Coordinate(ThreadLocalRandom.current().nextDouble(-90.0, +90.0), ThreadLocalRandom.current().nextDouble(-180.0, +180.0));
        Esercizio6.curLocation = temp;
    }
}

public class Esercizio6 {
    static volatile Coordinate curLocation = null;

    public static void main(final String[] args) {
        // Create and start GPS thread
        final Thread gpsThread = new Thread(new GPS());
        gpsThread.start();

        System.out.println("Simulation started");
        while (curLocation == null) {
            // Wait until location changes
        }
        final Coordinate firstLocation = curLocation;

        while (curLocation == firstLocation) {
            // Wait until location changes
        }
        
        final Coordinate secondLocation = curLocation;

        // Write distance between firstLocation and secondLocation position
        System.out.println("Distance from " + firstLocation + " to "
                + secondLocation + " is "
                + firstLocation.distance(secondLocation));

        // Wait until GPS thread finishes
        try {
            gpsThread.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Simulation completed");
    }
}