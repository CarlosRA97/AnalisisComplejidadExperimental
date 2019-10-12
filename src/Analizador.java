import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.lang.model.util.ElementScanner6;

public class Analizador {
	
	/* 
	 * NOTA IMPORTANTE
	 * 
	 * Esta clase se proporciona solamente para ilustrar el formato de
	 * salida que deberia tener la solucion a este ejericio.
	 * Esta clase debe modificarse completamente para cumplir 
	 * mínimamente los requisitos de esta practica.
	 * Notese que ni siquiera esta completa......
	 */
	
	public static String masCercano(double ratio) {
			if (ratio < 1.5) {                      // aprox 1.0
				return "1";							
			} else if (1 <= ratio && ratio < 3.0) { // aprox 2.0
				return "N";
			} else if (3 <= ratio && ratio < 6.0) { // aprox 4.0
				return "N2";
			} else if (6 <= ratio && ratio < 10.0) { // aprox 8.0
				return "N3";
			} else { 								 // otras
				return "NF";
			}
	}

	// usar la desviacion o el ratio
	private static class Statistic {

		@SuppressWarnings("unchecked")
		private static <T extends Number> T sum(List<T> values) {
			Class<? extends Number> cls = values.get(0).getClass();
			if (cls == Integer.class) {
				T sum = (T) Integer.valueOf(0);

				for (T val : values) {
					sum = (T) Integer.valueOf(val.intValue() + (int) sum);
				}
				return sum;
			} else if (cls == Long.class) {
				T sum = (T) Long.valueOf(0);

				for (T val : values) {
					sum = (T) Long.valueOf(val.longValue() + (long) sum);
				}
				return sum;
			} else {
				T sum = (T) Double.valueOf(0);

				for (T val : values) {
					sum = (T) Double.valueOf(val.doubleValue() + (double) sum);
				}
				return sum;
			}
		}

		public static double mean (List<Long> testTimes) 
		{
			return sum(testTimes) / testTimes.size();

			// Unsupported Code only Java 8 and above

			// return testTimes.stream()
			// 	.mapToDouble(x -> (double) x)
			// 	.average()
			// 	.getAsDouble();
		}
	
		public static double standardDeviation (List<Long> testTimes) 
		{
			double mean = mean(testTimes);
			List<Double> differenceFromMeanList = new ArrayList<>();
			for (Long val : testTimes) {
				differenceFromMeanList.add(Math.pow(val - mean, 2));
			}
			double variance = sum(differenceFromMeanList) / (testTimes.size() - 1);
			return Math.sqrt(variance);

			// Unsupported Code only Java 8 and above
			
			// double variance = testTimes.stream()
			// 	.mapToDouble(x -> (double) x)
			// 	.map(x -> Math.pow( x - mean, 2 ))
			// 	.sum() / (testTimes.size() - 1);
		}
		
		public static double coefVariacion (List<Long> testTimes) 
		{
			return standardDeviation(testTimes) / mean(testTimes);
		}

		public static void getResults (List<Long> testTimesResult)
		{
			System.out.println(" / ********   STATS   ******* / ");
			System.out.println(" Tiempos: " + Arrays.toString(testTimesResult.stream().map( x -> (double) x / 1_000_000_000).toArray()));
			System.out.println(" Media: " + mean(testTimesResult));
			System.out.println(" Desviacion estandar: " + standardDeviation(testTimesResult));
			System.out.println(" Coeficiente de Variacion: " + coefVariacion(testTimesResult));
			System.out.println(" / ************************ / ");
		}
		
	}

	static class TestConfiguration {
		private int executionTimes;
		private Function executionFunction;
		private double aproximateCoeficientVariance;
		private String algorithmType;

		TestConfiguration(int times, Function function) {
			executionTimes = times;
			executionFunction = function;
		}

		TestConfiguration(int times, Function function, double aproximateCoeficientVariance, String algorithmType) {
			this(times, function);
			this.aproximateCoeficientVariance = aproximateCoeficientVariance;
			this.algorithmType = algorithmType;
		}

		public int getExecutionTimes() {
			return executionTimes;
		}

		public Function getExecutionFunction() {
			return executionFunction;
		}

		public double getAproximateCoeficientVariance() {
			return aproximateCoeficientVariance;
		}

		public String getAlgorithmType() {
			return algorithmType;
		}
	}

	static class AlgoritmoDesconocido implements IAlgoritmo {

		@Override
		public synchronized void f(long n) {
			Algoritmo.f(n);
		}
	
		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	static class MultiplyBy implements Function {

		int value;

		MultiplyBy(int value) {
			this.value = value;
		}

		@Override
		public long apply(int t) {
			return t * value;
		}		
	}

	// The order of this list is important to determine which type of algorithm is
	static List<TestConfiguration> configurations = Arrays.asList(
		// Algorithm NF
		new TestConfiguration(
			4, 
			new MultiplyBy(3), // on more than 4 executions and 3 times x exeeds test time
			1.95, // CoefVariance: 1.95 aprox on NF
			"NF"
		),  	

		// Algorithm 2N
		new TestConfiguration(
			5, 
			new MultiplyBy(5), // on more than 5 executions and 5 times x exeeds test time
			2.04, // CoefVariance: 2.04 aprox on 2N
			"2N"
		),  	

		// Algorithm N3
		new TestConfiguration(
			12, 
			new MultiplyBy(100), 
			1.22, // CoefVariance: 1.22 aprox on N3
			"N3"
		), 		

		// Algorithm N2
		new TestConfiguration(
			12, 
			new MultiplyBy(3_000), 
			1.04, // CoefVariance: 1.04 aprox on N2
			"N2"
		), 		

		// Algorithm NLogN
		new TestConfiguration(
			20, 
			new MultiplyBy(1_000_000), 
			0.82, // CoefVariance: 0.82 aprox on NLogN
			"NLOGN"
		), 	

		// Algorithm N
		new TestConfiguration(
			20, 
			x -> (long) x * (20_000_000), 
			0.75, // CoefVariance: 0.75 aprox on N
			"N"
		), 	

		// Algorithm LogN
		new TestConfiguration(
			20, 
			new MultiplyBy(53_000_000), 
			0.20, // CoefVariance: 0.20 aprox on LogN
			"LOGN"
		), 	

		// Algorithm 1
		new TestConfiguration(
			20, 
			new MultiplyBy(53_000_000), 
			0.09, // CoefVariance: 0.10 aprox on 1
			"1"
		) 	
	);

	public static void main(String arg[]) {
		
		

		Temporizador algoritmoTimer = new Temporizador(Temporizador.MILIS);

/**
 * 					INDIVIDUAL TESTS
 */

		Map<Double, TestConfiguration> cvDifferenceToAlgorithmRelation = new HashMap<>();

		IAlgoritmo algoritmoDesconocido = new AlgoritmoDesconocido();

		int currentConfig = 0;
		double timeExecuted = 0;
		double cv = 0;
		double cvDifference = 0;
		TestConfiguration config;
		do {
			config = configurations.get(currentConfig);

			algoritmoTimer.iniciar();
			List<Long> testTimesResult = tester(algoritmoDesconocido, config);
			algoritmoTimer.parar();
			
			timeExecuted = (algoritmoTimer.tiempoPasado());
			cv = Statistic.coefVariacion(testTimesResult);
			cvDifference = Math.abs(config.getAproximateCoeficientVariance() - cv);
			cvDifferenceToAlgorithmRelation.put(cvDifference, config);
			if (arg.length > 0 && arg[0].equals("debug")) {
				log(testTimesResult, algoritmoDesconocido, timeExecuted, config);
				System.out.println("Difference in CV: " + cvDifference);
			}

			algoritmoTimer.reiniciar();
			currentConfig++;
		} while (currentConfig < configurations.size() && timeExecuted < 600.0);

		if (currentConfig == configurations.size()) {
			config = cvDifferenceToAlgorithmRelation.get(Collections.min(cvDifferenceToAlgorithmRelation.keySet()));
		}

		System.out.println(config.getAlgorithmType());

		

/**
 * 					MULTIPLE TESTS
 */
		// System.out.println("\nMultiple Tests\n");

		// var algoritmos = getAlgoritmos();
		// long lastTime = 0L;

		// for (int i = 0; i < algoritmos.size(); i++) {

		// 	int timesExecuted = algoritmos.size(); // - i + 1;
		// 	Function functionExecuted = (x) ->(long) x*1000;

		// 	if (lastTime > 5000) {
		// 		functionExecuted = (x) -> (long) (100*Math.floor(Math.log(x)));
		// 	}
			
		// 	algoritmoTimer.iniciar();

		// 	List<Long> testTimesMultipleAlgoResult = tester(algoritmos.get(i), timesExecuted, functionExecuted);
			
		// 	algoritmoTimer.parar();
		// 	lastTime = algoritmoTimer.tiempoPasado();
		// 	algoritmoTimer.reiniciar();

		// 	System.out.println(" [" + algoritmos.get(i) + "] Tiempo de la prueba: " + lastTime + "ms");
		// 	printStatistics(testTimesMultipleAlgoResult);
		// 	System.out.println("\n");
		// }

		
		// Temporizador t = new Temporizador();
		// t.iniciar();
		// Algoritmo.f(n1);
		// t.parar();
		// long t1 = t.tiempoPasado();
		// t.reiniciar();
		// t.iniciar();
		// Algoritmo.f(n2);
		// t.parar();
		// long t2 = t.tiempoPasado();
		// double ratio = (double)t2/t1;
		// System.out.println(masCercano(ratio));
	}

	private static List<Long> tester(IAlgoritmo algoritmo, int nTimes, Function function) {
		Temporizador t = new Temporizador();
		List<Long> testTimes = new ArrayList<>();
		for (int i = 1; i < nTimes + 1; i++) {
			t.iniciar();

			algoritmo.f(function.apply(i));
			
			t.parar();
			testTimes.add(t.tiempoPasado());
		}
		return testTimes;
	}

	private static List<Long> tester(IAlgoritmo algoritmo, TestConfiguration configuration) {
		return tester(algoritmo, configuration.getExecutionTimes(), configuration.getExecutionFunction());
	}
	
	private static void log(List<Long> testTimesResult, IAlgoritmo algoritmoDesconocido, double timeExecuted, TestConfiguration config) {
		System.out.println();
		System.out.println("Expected CV: " + config.getAproximateCoeficientVariance());
		System.out.println(" [" + algoritmoDesconocido + "] Tiempo de la prueba: " + timeExecuted + "ms");
		Statistic.getResults(testTimesResult);
	}

	// private static List<IAlgoritmo> getAlgoritmos()
	// {
	// 	return Arrays.asList(
	// 		new Algoritmo1(), 
	// 		new AlgoritmoLogN(), 
	// 		new AlgoritmoN(), 
	// 		new AlgoritmoNLogN(), 
	// 		new AlgoritmoN2(),
	// 		new AlgoritmoN3(),
	// 		new Algoritmo2N(), 
	// 		new AlgoritmoNF()
	// 	);
	// }
	
}

