package Esercizio1;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

class S11Es1Timer {
	private long start = -1, stop = -1;

	public void start() {
		this.start = System.currentTimeMillis();
	}

	public void stop() {
		this.stop = System.currentTimeMillis();
	}

	public long getElapsed() {
		if (start < 0 || stop < 0)
			return 0;
		return stop - start;
	}
}

class MergeSort extends RecursiveAction 
{
	public static final int SOGLIA = 10000;
	
	private int[] a;
	private int[] helper;
	private int lo;
	private int hi;
	
	public MergeSort(int[] a, int[] helper, int lo, int hi)
	{
		this.a=a;
		this.lo=lo;
		this.hi=hi;
		this.helper=helper;
	}
	
	public MergeSort(int[] a)
	{
		this.a=a;
		helper = new int [a.length];
		lo=0;
		hi=a.length-1;
	}
	
	/*public static void sort(final int[] a) 
	{
		final int[] helper = new int[a.length];
		mergesort(a, helper, 0, a.length - 1);
	}*/

	private static void mergesort(final int[] a, final int[] helper, final int lo, final int hi) 
	{
		if (lo >= hi)
			return;
		final int mid = lo + (hi - lo) / 2;
		mergesort(a, helper, lo, mid);
		mergesort(a, helper, mid + 1, hi);
		merge(a, helper, lo, mid, hi);
	}

	private static void merge(final int[] a, final int[] helper, final int lo, final int mid, final int hi) 
	{
		for (int i = lo; i <= hi; i++) {
			helper[i] = a[i];
		}
		int i = lo, j = mid + 1;
		for (int k = lo; k <= hi; k++) {
			if (i > mid)
				a[k] = helper[j++];
			else if (j > hi)
				a[k] = helper[i++];
			else if (helper[i] < helper[j])
				a[k] = helper[i++];
			else
				a[k] = helper[j++];
		}
	}

	@Override
	protected void compute() 
	{
		if (lo >= hi)
			return;
		final int mid = lo + (hi - lo) / 2;
		final int lenght = hi - lo;
		
		if(lenght>SOGLIA)
			invokeAll(new MergeSort(a, helper, lo, mid), new MergeSort(a, helper, mid+1, hi));
		else
		{
			mergesort(a, helper, lo, mid);
			mergesort(a, helper, mid + 1, hi);
		}
		
		merge(a, helper, lo, mid, hi);
	}
}

public class Esercizio1 
{
	static final int NUM_ELEMENTS = 134_217_728;

	public static void main(final String[] args) {

		// Generate random array
		final Random r = new Random();
		System.out.println("Generating random array");
		final int[] unsortedInts = new int[NUM_ELEMENTS];
		for (int i = 0; i < unsortedInts.length; i++)
			unsortedInts[i] = r.nextInt(10000);

		// Sort array
		System.out.println("Sorting");
		final S11Es1Timer t = new S11Es1Timer();
		ForkJoinPool pool = new ForkJoinPool(4);
		MergeSort merger = new MergeSort(unsortedInts);
		t.start();
		pool.invoke(merger);
		t.stop();

		// Check that values are sorted!
		System.out.print("Validating results :");
		if (validateResult(unsortedInts))
			System.out.println(" valid!");
		else
			System.out.println(" invalid!");
		System.out.println("Elapsed: " + t.getElapsed() + " ms");
	}

	private static boolean validateResult(final int[] sorted) 
	{
		for (int i = 1; i < sorted.length; i++)
			if (sorted[i - 1] > sorted[i])
				return false;
		return true;
	}
}