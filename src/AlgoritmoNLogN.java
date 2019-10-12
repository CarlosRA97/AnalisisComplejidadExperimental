/**
 * AlgoritmoNLogN
 */
public class AlgoritmoNLogN implements IAlgoritmo {
    long l = 0L;

    @Override
    public synchronized void f(long n) {
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j *= 2) {
                l += 1L;
            }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}