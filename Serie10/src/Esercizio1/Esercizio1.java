package Esercizio1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Moltiplicatore implements Callable<int[][]>
{
	private final int[][] matrice1;
	private final int[][] matrice2;
	
	public Moltiplicatore(int[][] m0, int[][] m1)
	{
		this.matrice1=m0;
		this.matrice2=m1;
	}
	
	@Override
	public int[][] call() throws Exception 
	{
		int[][] risultato = new int[matrice1.length][matrice1.length];
		
		for (int i = 0; i < matrice1[0].length; i++)
			for (int j = 0; j < matrice2.length; j++)
				for (int k = 0; k < matrice1.length; k++)
					risultato[i][j] += matrice1[i][k] * matrice2[k][j];
		
		return risultato;
	}
	
}

public class Esercizio1 {
	public static final int NUM_OPERATIONS = 100_000;
	public static final int MATRIX_SIZE = 64;

	public static void main(final String[] args) {
		final Random rand = new Random();
		System.out.println("Simulazione iniziata");
		
		ExecutorService executorService = Executors.newFixedThreadPool(15);
		List<Future<int[][]>> futures = new ArrayList<>();
		
		for (int operation = 0; operation < NUM_OPERATIONS; operation++) {
			// Crea matrici
			final int[][] m0 = new int[MATRIX_SIZE][MATRIX_SIZE];
			final int[][] m1 = new int[MATRIX_SIZE][MATRIX_SIZE];
			// Inizializza gli array con numeri random
			for (int i = 0; i < MATRIX_SIZE; i++)
				for (int j = 0; j < MATRIX_SIZE; j++) 
				{
					m0[i][j] = rand.nextInt(10);
					m1[i][j] = rand.nextInt(10);
				}
			
			//eseguo moltiplicazioni
			futures.add(executorService.submit(new Moltiplicatore(m0, m1)));
		}
		
		executorService.shutdown();
		System.out.println("Simulazione terminata");
		
	}
}