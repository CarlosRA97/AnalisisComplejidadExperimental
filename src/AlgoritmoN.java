/**
 * AlgoritmoN
 */
public class AlgoritmoN implements IAlgoritmo {

    long l = 0L;

    @Override
    public synchronized void f(long n) {
        for (int i = 0; i < n; i++)
        {
            l += 1L;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}