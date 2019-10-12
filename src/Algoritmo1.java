/**
 * Algoritmo1
 */
public class Algoritmo1 implements IAlgoritmo {
    long l = 0L;

    @Override
    public synchronized void f(long n) {
        l = n;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}