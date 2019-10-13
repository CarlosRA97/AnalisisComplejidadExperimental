/**
 * AlgoritmoN
 */
public class AlgoritmoN implements IAlgoritmo {
	
	long value;

	public void setValue(long value) {
		this.value = value;
	}
	
	@Override
	public void run() {
		f(value);
	}

    long l = 0L;

    @Override
    public synchronized void f(long n) {
        for (int i = 1; i <= n; i++)
        {
            l += 1L;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}