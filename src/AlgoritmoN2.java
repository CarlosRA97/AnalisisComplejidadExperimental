/**
 * AlgoritmoN2
 */
public class AlgoritmoN2 implements IAlgoritmo {

    long l = 0L;

    @Override
    public synchronized void f(long n) {  
        for (int i = 0; i < n; i++)
        {
        for (int j = 0; j < n; j++)
        {
            l += 1L;
        }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}