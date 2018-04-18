package Esercizio2;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Rectangle {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;

    static Rectangle factoryRectangle(final int newX1, final int newY1, final int newX2, final int newY2) //potevo usare anche dei lock
    {
    	return new Rectangle(newX1, newY1, newX2, newY2);
    }
    
    private Rectangle(final int newX1, final int newY1, final int newX2, final int newY2) 
    {
        this.x1 = newX1;
        this.y1 = newY1;
        this.x2 = newX2;
        this.y2 = newY2;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    @Override
    public String toString() {
        return "[" + x1 + ", " + y1 + ", " + x2 + ", " + y2 + "]";
    }
}

class Resizer implements Runnable {
    @Override
    public void run() {
        final Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            try {
                Thread.sleep(random.nextInt(3) + 2);
            } catch (final InterruptedException e) {
            }

            // genera variazione per punti tra -2 e 2
            final int deltaX2 = random.nextInt(5) - 2;
            final int deltaY2 = random.nextInt(5) - 2;

            // calcola nuove coordinate x2 e y2
            final int newX2, newY2;
            final boolean isLine, isPoint, isNegative;
            
            EsercizioRettangolo.readLock.lock();
            try
            {
	            newX2 = EsercizioRettangolo.rect.getX2() + deltaX2;
	            newY2 = EsercizioRettangolo.rect.getY2() + deltaY2;
	
	            isLine = (EsercizioRettangolo.rect.getX1() == newX2)
	                    || (EsercizioRettangolo.rect.getY1() == newY2);
	            isPoint = (EsercizioRettangolo.rect.getX1() == newX2)
	                    && (EsercizioRettangolo.rect.getY1() == newY2);
	            isNegative = (EsercizioRettangolo.rect.getX1() > newX2)
	                    || (EsercizioRettangolo.rect.getY1() > newY2);
            }
            finally { EsercizioRettangolo.readLock.unlock(); }

            // La trasformazione non deve rendere il rettangolo una linea, un
            // punto o avere le coordinate del secondo punto 'dietro' al punto
            // iniziale
            if (!isLine && !isPoint && !isNegative) {
            	final int x1;
            	final int y1;
            	EsercizioRettangolo.readLock.lock();;
            	try
            	{
            		x1=EsercizioRettangolo.rect.getX1();
                	y1=EsercizioRettangolo.rect.getY1();
            	}
            	finally { EsercizioRettangolo.readLock.unlock(); }
            	
            	Rectangle temp=Rectangle.factoryRectangle(x1, y1, newX2, newY2);
            	
            	EsercizioRettangolo.writeLock.lock();
            	try
            	{
	                EsercizioRettangolo.rect= temp;
	                System.out.println(EsercizioRettangolo.rect);
            	}
            	finally { EsercizioRettangolo.writeLock.unlock(); }
            }
        }
    }
}

/**
 * Programma che simula variazioni continue delle dimensioni di un rettangolo
 */
public class EsercizioRettangolo 
{
    static volatile Rectangle rect=Rectangle.factoryRectangle(10, 10, 20, 20);
    
    private static ReadWriteLock lock=new ReentrantReadWriteLock();
    public static Lock readLock=lock.readLock();
    public static Lock writeLock=lock.writeLock();
    
    public static void main(final String[] args) 
    {
    	System.out.println("Esercizio2Modificato");
        final List<Thread> allThreads = new ArrayList<Thread>();
        for (int i = 0; i < 5; i++)
            allThreads.add(new Thread(new Resizer()));

        System.out.println("Simulation started");
        for (final Thread t : allThreads) {
            t.start();
        }

        for (final Thread t : allThreads) {
            try {
                t.join();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Simulation finished");
    }
}