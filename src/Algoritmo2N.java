/**
 * Algoritmo2N
 */
public class Algoritmo2N implements IAlgoritmo {
    long l = 0L;

    @Override
    public synchronized void f(long n) {
        for (int i = 0; i <= java.lang.Math.pow(2, n); i++) {
            l += 1L;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
    
}