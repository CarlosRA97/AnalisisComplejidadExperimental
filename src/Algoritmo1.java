/**
 * Algoritmo1
 */
public class Algoritmo1 implements IAlgoritmo {
    long l = 0L;
    
    long value;
	
	@Override
	public void run() {
		f(value);
	}

    @Override
    public synchronized void f(long n) {
        l = n;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

	@Override
	public void setValue(long n) {
		value = n;
	}
    
}