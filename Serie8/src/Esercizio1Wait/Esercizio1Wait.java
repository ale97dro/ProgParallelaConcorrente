package Esercizio1Wait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Sommatore implements Runnable
{
	private final int id;
	private final Integer[] riga;
	private final Integer[] colonna;
	
	public Sommatore(final int id, final int[] riga, final Integer[] colonna2)
	{
		this.id=id;
		
		this.riga=new Integer[riga.length];
		this.colonna=new Integer[colonna2.length];
		
		for(int i=0;i<riga.length;i++)
			this.riga[i]=riga[i];
		
		for(int i=0;i<colonna2.length;i++)
			this.colonna[i]=colonna2[i];
	}
	
	@Override
	public void run() 
	{
		int somma=0;
		
		for(int i=0;i<riga.length;i++)
			somma+=riga[i];
		Esercizio1Wait.inserisciSommaRiga(somma, id);
		
		somma=0;
		
		for(int i=0;i<colonna.length;i++)
			somma+=colonna[i];
		Esercizio1Wait.inserisciSommaColonna(somma, id);
	}
	
}


public class Esercizio1Wait {
	final static int[][] matrix = new int[10][10];
	final static int[] rowSum = new int[matrix.length];
	final static int[] colSum = new int[matrix[0].length];
	static int counter=0;

	public static void inserisciSommaRiga(int somma, int index)
	{
		synchronized(rowSum)
		{
			rowSum[index]=somma;
			counter++;
			rowSum.notify();
		}
	}
	
	public static void inserisciSommaColonna(int somma, int index)
	{
		synchronized(colSum)
		{
			colSum[index]=somma;
			counter++;
			colSum.notify();
		}
	}
	
	
	public static void main(String[] args) 
	{
		System.out.println("Esercizio1Wait");
		List<Thread>threads=new ArrayList<>();
		int somma_righe=0, somma_colonne=0;
		//int c=0;
		
		initMatrix();
		
		for(int i=0;i<10;i++)
		{
			Integer[] colonna=new Integer[10];
			
			for(int k=0;k<10;k++)
			{
				colonna[k]=matrix[k][i];
			}
			threads.add(new Thread(new Sommatore(i, matrix[i], colonna)));
		}
		
		for(Thread t : threads)
			t.start();
		
		synchronized(rowSum)
		{
			while (counter != 10) {

				try {
					rowSum.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		for(int i=0;i<10;i++)
			somma_righe+=rowSum[i];
		counter=0;
		
		synchronized(colSum) 
		{
			while (counter != 10) {
				try {
					colSum.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		for(int i=0;i<10;i++)
			somma_colonne+=colSum[i];
		
		System.out.println("Somma righe: "+somma_righe);
		System.out.println("Somma colonne: "+somma_colonne);
		
		
		/*
		// Inizializza matrice con valori random
		initMatrix();

		// Stampa matrice
		System.out.println("Matrice:");
		printMatrix();

		// Calcola somma delle righe
		for (int row = 0; row < matrix.length; row++)
			rowSum[row] = sumRow(row);

		// Stampa somma delle righe
		System.out.println("Somme delle righe:");
		printArray(rowSum);

		// Calcola somma delle colonne
		for (int col = 0; col < matrix[0].length; col++)
			colSum[col] = sumColumn(col);

		// Stampa somma delle colonne
		System.out.println("Somme delle colonne:");
		printArray(colSum);*/
	}

	public static int sumRow(final int row) {
		int result = 0;
		for (int col = 0; col < matrix[row].length; col++)
			result += matrix[row][col];
		return result;
	}

	public static int sumColumn(final int row) {
		int temp = 0;
		for (int col = 0; col < matrix.length; col++)
			temp += matrix[col][row];
		return temp;
	}

	private static void initMatrix() {
		Random r = new Random();
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] = 1 + r.nextInt(100);
			}
		}
	}

	private static void printMatrix() {
		for (int i = 0; i < matrix.length; i++)
			printArray(matrix[i]);
	}

	private static void printArray(final int[] array) {
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + "\t");
		System.out.println();
	}
}
