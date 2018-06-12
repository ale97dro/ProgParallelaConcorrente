package Esercizio2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

final class Coordinate {
	private final double lat;
	private final double lon;

	public Coordinate(final double lat, final double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	/**
	 * Returns the distance (expressed in km) between two coordinates
	 *
	 * @param from
	 * @return Returns the distance expressed in km
	 */
	public double distance(final Coordinate from) {
		final double earthRadius = 6371.000; // km
		final double dLat = Math.toRadians(from.lat - this.lat);
		final double dLng = Math.toRadians(from.lon - this.lon);
		final double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(from.lat))
				* Math.cos(Math.toRadians(this.lat)) * Math.sin(dLng / 2.0) * Math.sin(dLng / 2.0);
		final double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (earthRadius * c);
	}

	@Override
	public String toString() {
		return String.format("[%.5f, %.5f]", lat, lon);
	}
}

class Earthquake {
	private final static String CSV_REGEX = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

	private final Date time;
	private final Coordinate position;
	private final double depth;
	private final double magnitude;
	private final String place;

	public Earthquake(final Date time, final Coordinate pos, final double depth, final double mag, final String place) {
		this.time = time;
		this.position = pos;
		this.depth = depth;
		this.magnitude = mag;
		this.place = place;
	}

	public Coordinate getPosition() {
		return position;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public double getDepth() {
		return depth;
	}

	public static Earthquake parse(final String csvLine) {
		final String[] splits = csvLine.split(CSV_REGEX);
		if (splits.length != 15) {
			System.out.println("Failed to parse: " + csvLine);
			return null;
		}

		final Date time;

		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			time = sdf.parse(splits[0]);
		} catch (final ParseException e) {
			return null;
		}

		final double lat = tryParseDouble(splits[1]);
		final double lon = tryParseDouble(splits[2]);
		final double depth = tryParseDouble(splits[3]);
		final double mag = tryParseDouble(splits[4]);
		final String place = splits[13];

		return new Earthquake(time, new Coordinate(lat, lon), depth, mag, place);
	}

	@SuppressWarnings("deprecation")
	private static Double tryParseDouble(final String str) {
		try {
			return Double.parseDouble(str);
		} catch (final NumberFormatException e) {
			return new Double(0);
		}
	}

	@Override
	public String toString() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(time) + " mag: " + magnitude
				+ " depth: " + depth + "km @ " + position + " " + place;
	}
}

public class Esercizio2 {

	private static List<Earthquake> loadEarthquakeDB(final String address, final boolean isLocalFile) {
		final List<Earthquake> quakes = new ArrayList<Earthquake>();

		final Reader reader;
		if (isLocalFile) {
			try {
				final File file = new File(address);
				reader = new FileReader(file);
			} catch (final FileNotFoundException e2) {
				System.out.println("Failed to open file: " + address);
				return Collections.emptyList();
			}
		} else {
			final URL url;
			try {
				url = new URL(address);
			} catch (final MalformedURLException e) {
				System.out.println("Failed to create URL for address: " + address);
				return Collections.emptyList();
			}
			final InputStream is;
			try {
				is = url.openStream();
			} catch (final IOException e) {
				System.out.println("Failed to open stream for: " + address);
				return Collections.emptyList();
			}
			reader = new InputStreamReader(is);
		}

		System.out.println("Requesting earthquake data from: " + address + " ...");

		String line;
		try {
			final BufferedReader br = new BufferedReader(reader);
			line = br.readLine();
			while ((line = br.readLine()) != null) {
				final Earthquake quake = Earthquake.parse(line);
				if (quake != null)
					quakes.add(quake);
				else
					System.out.println("Failed to parse: " + line);

			}

			br.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return quakes;
	}

	public static void main(final String[] args) throws InterruptedException, ExecutionException {
		
		Scanner wait = new Scanner(System.in);
		
		String x = wait.next();
		
		System.out.println(x);
		
		
		
		//final String URI = "Esercizi/serie12/2014-2015.csv";
		final String URI ="2014-2015.csv";
		final long startTime = System.currentTimeMillis();
		
		//final List<Earthquake> quakes = loadEarthquakeDB(URI, true);
		final long computeTime = System.currentTimeMillis();
		
		
		//carico con thread a parte il db
		CompletableFuture<List<Earthquake>> quakes = CompletableFuture.supplyAsync(() -> {
			return loadEarthquakeDB(URI, true);
		});
		
		
		if (quakes.get().isEmpty()) {
			System.out.println("No earthquakes found!");
			return;
		}
		System.out.println("Loaded " + quakes.get().size() + " earthquakes");

		final Coordinate supsi = new Coordinate(46.0234, 8.9172);

		System.out.println("Searching for nearest earthquake ...");
		Earthquake curNearestQuake = null;
		double curNearestDistance = Double.MAX_VALUE;
		for (final Earthquake quake : quakes.get()) {
			final double distance = quake.getPosition().distance(supsi);
			if (curNearestDistance > distance) {
				curNearestDistance = distance;
				curNearestQuake = quake;
			}
		}
		
		
		// Results
		System.out.println("Nearest  : " + curNearestQuake + " distance: " + curNearestDistance);
		
		//NUOVE COSE
		CompletableFuture<Earthquake> piuLontano = quakes.thenApplyAsync((q) -> piuLontano(q, supsi));
		CompletableFuture<Earthquake> piuForte = quakes.thenApplyAsync((q) -> piuForte(q, supsi));
		CompletableFuture<List<Earthquake>> magnitudoVicini = quakes.thenApplyAsync((q) -> magnitudoVicini(q, supsi));
		CompletableFuture<Long> latitudine46 = quakes.thenApplyAsync((q) -> latitudine46(q));
		CompletableFuture<Long> longitudine8 = quakes.thenApplyAsync((q) -> longitudine8(q));
		CompletableFuture<Map<Integer, Long>> profondita = quakes.thenApplyAsync((q)->fasceProfondita(q));
		CompletableFuture<Map<Integer, Long>> intensita = quakes.thenApplyAsync((q)->fasceIntensita(q));
		
		
		//Risultati
		
		System.out.println("Terremoto più lontano: " + piuLontano.get().getPosition().distance(supsi));
		System.out.println("Magnituto più forte: " + piuForte.get().getMagnitude());
		
		for(Earthquake e : magnitudoVicini.get())
			System.out.println(e.getPosition().distance(supsi));
		
		System.out.println("Latitudine 46: "+latitudine46);
		System.out.println("Longitudine 8: "+longitudine8);
		
		for(Integer f : profondita.get().keySet())
			System.out.println(f+" - "+(f+1)+": "+profondita.get().get(f));
		
		for(Integer f : intensita.get().keySet())
			System.out.println(f+" - "+(f+1)+": "+intensita.get().get(f));
		
		
		final long endTime = System.currentTimeMillis();
		System.out.println("Completed in " + ((endTime - startTime)) + " ms" + " (computation time=" + (endTime - computeTime) + " ms)");
		
		
		x = wait.next();
	}
	
	public static Earthquake piuLontano(List<Earthquake> quakes, Coordinate supsi)
	{
		return quakes.parallelStream()
				.max(new Comparator<Earthquake>() 
				{
					@Override
					public int compare(Earthquake arg0, Earthquake arg1) 
					{
						return (int) (arg0.getPosition().distance(supsi) - arg1.getPosition().distance(supsi));
					}
				}).get();
	}
	
	public static Earthquake piuForte(List<Earthquake> quakes, Coordinate supsi) 
	{
		return quakes.parallelStream()
				.max(new Comparator<Earthquake>() 
				{
					@Override
					public int compare(Earthquake arg0, Earthquake arg1) 
					{
						return (int) (arg0.getMagnitude() - arg1.getMagnitude());
					}
				}).get();
	}

	public static List<Earthquake> magnitudoVicini(List<Earthquake> quakes, Coordinate supsi)
	{
		return quakes.parallelStream()
				.filter(e -> e.getMagnitude() > 4 && e.getMagnitude() < 6)
				.filter(e -> e.getPosition().distance(supsi) > 2000).collect(Collectors.toList());
	}

	public static long latitudine46(List<Earthquake> quakes)
	{
		return quakes.parallelStream()
				.filter(e -> e.getPosition().getLat() >= 46 && e.getPosition().getLat() < 47)
				.count();
	}

	public static long longitudine8(List<Earthquake> quakes)
	{
		return quakes.parallelStream()
				.filter(e -> e.getPosition().getLon() >= 8 && e.getPosition().getLon() < 9)
				.count();
	}

	public static Map<Integer, Long> fasceProfondita(List<Earthquake> quakes)
	{
		return quakes.parallelStream()
				.collect(Collectors.groupingBy(((e) -> (int)e.getDepth()/100), Collectors.counting()));
	}

	public static Map<Integer, Long> fasceIntensita(List<Earthquake> quakes)
	{
		return quakes.parallelStream()
				.collect(Collectors.groupingBy(((e) -> (int)e.getMagnitude()), Collectors.counting()));
	}
}
