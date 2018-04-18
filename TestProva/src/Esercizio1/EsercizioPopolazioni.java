package Esercizio1;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Classe che simula periodi di carestia per i paesi
 */
class Carestia implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        // ad ogni giro introduce carestia per un determinato paese scelto a caso
        for (int i = 0; i < 50; i++) {
            final int popoloScelto = random.nextInt(EsercizioPopolazioni.popolazione.length());

            // 0.1 .. 0.7
            final double fattoreDecimazione = random.nextDouble() * 0.6 + 0.1;
            int popolazioneAggiornata;
            int oldPopolazione;
            
            do
            {
            	oldPopolazione=EsercizioPopolazioni.popolazione.get(popoloScelto);
            	popolazioneAggiornata=(int) (oldPopolazione*fattoreDecimazione);
            }
            while(!EsercizioPopolazioni.popolazione.compareAndSet(popoloScelto, oldPopolazione, popolazioneAggiornata));

            System.out.println("Carestia: Popolazione " + popoloScelto + " diminuita a " + popolazioneAggiornata
                    + " del fattore " + fattoreDecimazione);

            try {
                // pausa fra un periodo di carestia e l'altro
                Thread.sleep(100);
            } catch (final InterruptedException e) {
            }
        }
    }
}

/**
 * Classe che simula periodi di prosperita per i paesi
 */
class Prosperita implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        // ad ogni giro introduce prosperità per un determinato paese scelto a caso
        for (int i = 0; i < 100; i++) {
            final int popoloScelto = random.nextInt(EsercizioPopolazioni.popolazione.length());

            final double fattoreCrescita = random.nextDouble() * 0.55 + 1.05;

            // incrementa la popolazione
            int oldPopolazione;
            int newPopolazione;
            
            do
            {
            	oldPopolazione=EsercizioPopolazioni.popolazione.get(popoloScelto);
            	newPopolazione=(int) (oldPopolazione*fattoreCrescita);
            }
            while(!EsercizioPopolazioni.popolazione.compareAndSet(popoloScelto, oldPopolazione, newPopolazione));
            

            System.out.println("Prosperita: Popolazione " + popoloScelto + " cresciuta a " + newPopolazione
                    + " del fattore " + fattoreCrescita);

            try {
                // pausa fra un periodo di prosperità e l'altro
                Thread.sleep(50);
            } catch (final InterruptedException e) {
            }
        }
    }
}

/**
 * Programma che simula la variazione demografica di 5 paesi
 */
public class EsercizioPopolazioni {
  //  static volatile long popolazione[] = new long[5];
    static AtomicIntegerArray popolazione = new AtomicIntegerArray(5);
    static ReentrantLock lock = new ReentrantLock();

    public static void main(final String[] args) {
    	System.out.println("Atomic");
        // la popolazione iniziale è di 1000 abitanti per ogni paese
        for (int i = 0; i < 5; i++)
        	popolazione.set(i, 1000);

        final List<Thread> allThreads = new ArrayList<>();
        allThreads.add(new Thread(new Prosperita()));
        allThreads.add(new Thread(new Prosperita()));
        allThreads.add(new Thread(new Carestia()));

        System.out.println("Simulation started");
        for (final Thread t : allThreads)
            t.start();

        for (final Thread t : allThreads)
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        System.out.println("Simulation finished");
    }
}