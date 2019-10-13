/**
 * Algoritmo2N
 */
public class Algoritmo2N implements IAlgoritmo {
    long l = 0L;
    
    long value;

	public void setValue(long value) {
		this.value = value;
	}

	@Override
	public void run() {
		f(value);
	}

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