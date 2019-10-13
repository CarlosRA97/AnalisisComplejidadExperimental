/**
 * AlgoritmoN3
 */
public class AlgoritmoN3 implements IAlgoritmo {

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
        for (int i = 1; i <= n; i++)
        {
        for (int j = 1; j <= n; j++)
        {
        for (int k = 1; k <= n; k++) 
        {
            l += 1L;
        }
        }
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}