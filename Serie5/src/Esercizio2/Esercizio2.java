package Esercizio2;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Coordinate 
{
	private final double lat;
	private final double lon;
	
	public static Coordinate CreaCoordinate(final double lat, final double lon) //factory method: usa costruttore privato
	{
		return new Coordinate(lat, lon);
	}
	
	private Coordinate(final double lat, final double lon) //costruttore privato evita che la classe possa essere ereditata
	{
		this.lat=lat;
		this.lon=lon;
	}
	
	/**
	 * Returns the distance (expressed in km) between two coordinates
	 */
	public double distance(final Coordinate from) 
	{
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
		
		while (!Esercizio2.completed)
		{
			Coordinate temp = Coordinate.CreaCoordinate(ThreadLocalRandom.current().nextDouble(-90.0, +90.0), ThreadLocalRandom.current().nextDouble(-180.0, +180.0)); //oggetto temporaneo per alleggerire il tempo di lock
			
			Esercizio2.lock.lock();
			try {
				Esercizio2.curLocation = temp;
			} finally {
				Esercizio2.lock.unlock();
			}

			// Wait before updating position
			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(1, 5));
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}

public class Esercizio2 
{
	static boolean completed = false;
	static Lock lock = new ReentrantLock();
	static Coordinate curLocation = null;
	
	public static void main(final String[] args)
	{
		System.out.println("Esercizio 2");
		
		// Create and start GPS thread
		final Thread gpsThread = new Thread(new GPS());
		gpsThread.start();

		System.out.println("Simulation started");
		Coordinate prevLocation = null;
		// Wait until location changes
		do {
			lock.lock();
			try {
				prevLocation = curLocation;
			} finally {
				lock.unlock();
			}
		}
		while (prevLocation == null);
		
		System.out.println("Initial position received");

		// Request 10 position updates
		for (int i = 0; i < 100; i++) {
			Coordinate lastLocation;
			do {
				lock.lock();
				try {
					lastLocation = curLocation;
				} finally {
					lock.unlock();
				}
			} while (lastLocation == prevLocation);

			// Write distance between firstLocation and secondLocation position
			System.out.println("Distance from " + prevLocation + " to "
					+ lastLocation + " is "
					+ prevLocation.distance(lastLocation));
			
			prevLocation = lastLocation;
		}
		
		completed = true;
		
		// Stop GPS thread and wait until it finishes
		try {
			gpsThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Simulation completed");
	}
}
