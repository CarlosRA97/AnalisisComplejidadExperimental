import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Analizador {

	/*
	 * NOTA IMPORTANTE
	 * 
	 * Esta clase se proporciona solamente para ilustrar el formato de salida que
	 * deberia tener la solucion a este ejericio. Esta clase debe modificarse
	 * completamente para cumplir mínimamente los requisitos de esta practica.
	 * Notese que ni siquiera esta completa......
	 */

	public static String masCercano(double ratio) {
		if (ratio < 1.5) { // aprox 1.0
			return "1";
		} else if (1 <= ratio && ratio < 3.0) { // aprox 2.0
			return "N";
		} else if (3 <= ratio && ratio < 6.0) { // aprox 4.0
			return "N2";
		} else if (6 <= ratio && ratio < 10.0) { // aprox 8.0
			return "N3";
		} else { // otras
			return "NF";
		}
	}

	// usar la desviacion o el ratio
	private static class Statistic {

		public static double mean(List<Long> testTimes) {
			Long sum = 0L;
			for (Long val : testTimes) {
				sum += val;
			}
			return sum / testTimes.size();

			// Unsupported Code only Java 8 and above

			// return testTimes.stream()
			// .mapToDouble(x -> (double) x)
			// .average()
			// .getAsDouble();
		}

		public static double standardDeviation(List<Long> testTimes) {
			double mean = mean(testTimes);
			List<Double> differenceFromMeanList = new ArrayList<>();
			for (Long val : testTimes) {
				differenceFromMeanList.add(Math.pow(val - mean, 2));
			}
			Double sum = 0.0;
			for (Double val : differenceFromMeanList) {
				sum += val;
			}
			double variance = sum / (testTimes.size() - 1);
			return Math.sqrt(variance);

			// Unsupported Code only Java 8 and above

			// double variance = testTimes.stream()
			// .mapToDouble(x -> (double) x)
			// .map(x -> Math.pow( x - mean, 2 ))
			// .sum() / (testTimes.size() - 1);
		}

		public static double coefVariacion(List<Long> testTimes) {
			return standardDeviation(testTimes) / mean(testTimes);
		}

		public static String getResults(List<Long> testTimesResult) {
			StringBuilder sb = new StringBuilder();
			sb.append(" / ********   STATS   ******* / ");
			sb.append(" Tiempos: "
					+ Arrays.toString(testTimesResult.stream().map(x -> (double) x / 1_000_000_000).toArray()));
			sb.append(" Media: " + mean(testTimesResult));
			sb.append(" Desviacion estandar: " + standardDeviation(testTimesResult));
			sb.append(" Coeficiente de Variacion: " + coefVariacion(testTimesResult));
			sb.append(" / ************************ / ");
			return sb.toString();
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

		public double getDifferenceCV(double cv) {
			return Math.abs(getAproximateCoeficientVariance() - cv);
		}

		public String getAlgorithmType() {
			return algorithmType;
		}
	}

	static class Tester implements Callable<List<Long>> {
		
		IAlgoritmo algoritmo; TestConfiguration configuration;
		
		private List<Long> tester(IAlgoritmo algoritmo, int nTimes, Function function) {
			Temporizador t = new Temporizador();
			List<Long> testTimes = new ArrayList<>();
			
			int i = 1;
			while (i < nTimes + 1 && !Thread.currentThread().isInterrupted()) {
				t.iniciar();

				algoritmo.f(function.apply(i));

				t.parar();
				testTimes.add(t.tiempoPasado());
				
				i++;
			}
			
			return testTimes;
		}

		Tester(IAlgoritmo algoritmo, TestConfiguration configuration) {
			this.algoritmo = algoritmo;
			this.configuration = configuration;
		}

		@Override
		public List<Long> call() throws Exception {
			return tester(algoritmo, configuration.getExecutionTimes(), configuration.getExecutionFunction());
		}
		
	}
	
	static class AlgoritmoDesconocido implements IAlgoritmo {

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
			new TestConfiguration(3, new MultiplyBy(3), // on more than 4 executions and 3 times x exeeds test time
					1.95, // CoefVariance: 1.95 aprox on NF
					"NF"),

			// Algorithm 2N
			new TestConfiguration(5, new MultiplyBy(5), // on more than 5 executions and 5 times x exeeds test time
					2.04, // CoefVariance: 2.04 aprox on 2N
					"2N"),

			// Algorithm N3
			new TestConfiguration(12, new MultiplyBy(100), 1.22, // CoefVariance: 1.22 aprox on N3
					"N3"),

			// Algorithm N2
			new TestConfiguration(12, new MultiplyBy(3_000), 1.04, // CoefVariance: 1.04 aprox on N2
					"N2"),

			// Algorithm NLogN
			new TestConfiguration(20, new MultiplyBy(1_000_000), 0.82, // CoefVariance: 0.82 aprox on NLogN
					"NLOGN"),

			// Algorithm N
			new TestConfiguration(20, new MultiplyBy(20_000_000), 0.75, // CoefVariance: 0.75 aprox on N
					"N"),

			// Algorithm LogN
			new TestConfiguration(20, new MultiplyBy(53_000_000), 0.20, // CoefVariance: 0.20 aprox on LogN
					"LOGN"),

			// Algorithm 1
			new TestConfiguration(30, new MultiplyBy(60_000_000), 0.30, // CoefVariance: 0.10 aprox on 1
					"1"));

	public static void main(String arg[]) throws FileNotFoundException {
		
		boolean isDebug		= arg.length > 0 && arg[0].equals("debug");
		boolean isOutfile 	= arg.length > 1 && isDebug && arg[1].equals("file");

		Temporizador algoritmoTimer = new Temporizador(Temporizador.MILIS);

		PrintWriter out = new PrintWriter(System.out);
		if (isOutfile) {
			out = new PrintWriter("stats.txt");
		}

		if (isDebug) {
//			List<IAlgoritmo> algoritmos = getAlgoritmos();
//			Collections.shuffle(algoritmos);
//			for (IAlgoritmo algoritmo : algoritmos) {
//				individualTest(algoritmo, isDebug, algoritmoTimer, out);
//			}
		} else {
			IAlgoritmo algoritmo = new AlgoritmoDesconocido();
			individualTest(algoritmo, isDebug, algoritmoTimer, out);
		}
		out.close();
		System.exit(0);
	}

	private static void individualTest(IAlgoritmo algoritmo, boolean isDebug, Temporizador algoritmoTimer,
			PrintWriter out) {
		
		Map<Double, TestConfiguration> cvDifferenceToAlgorithmRelation = new HashMap<>();

		int currentConfig = 0;
		double timeExecuted = 0;
		double cv = 0;
		double cvDifference = 0;
		boolean end = false;
		TestConfiguration config;
		
		do {
			config = configurations.get(currentConfig);

			algoritmoTimer.iniciar();
			List<Long> testTimesResult = null;
			ExecutorService executor = Executors.newFixedThreadPool(1);
			try {
				testTimesResult = executor.submit(new Tester(algoritmo, config)).get(10, TimeUnit.SECONDS);
				algoritmoTimer.parar();

				timeExecuted = algoritmoTimer.tiempoPasado();
				
				cv = Statistic.coefVariacion(testTimesResult);
				cvDifferenceToAlgorithmRelation.put(config.getDifferenceCV(cv), config);

				if (isDebug) {
//					try {
//						sendLog(testTimesResult, algoritmo, timeExecuted, config);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					printLog(testTimesResult, algoritmo, timeExecuted, config, out);
				}

				currentConfig++;
				algoritmoTimer.reiniciar();
			} catch (InterruptedException e) {
				e.printStackTrace(out);
			} catch (ExecutionException e) {
				e.printStackTrace(out);
			} catch (TimeoutException e) {
				if (isDebug) {
					e.printStackTrace(out);
				}
				if (cvDifference < 1.0) {
					currentConfig--;
				} else {
					currentConfig++;
				}
				config = configurations.get(currentConfig);
				end = true;
			} finally {
				executor.shutdownNow();
			}
		} while (!end && currentConfig < configurations.size() && timeExecuted < 300.0);
		
		if (isDebug) {
			out.println(algoritmo + " is " + config.getAlgorithmType());
		} else {
			out.println(config.getAlgorithmType());
		}
	}

	private static String log(List<Long> testTimesResult, IAlgoritmo algoritmo, double timeExecuted,
			TestConfiguration config) {
		StringJoiner sj = new StringJoiner("\n");
		
		sj.add("Expected CV: " + config.getAproximateCoeficientVariance());
		sj.add(" [" + algoritmo + "] Tiempo de la prueba: " + timeExecuted + "ms");
		sj.add(Statistic.getResults(testTimesResult));
		sj.add("Difference in CV: " + config.getDifferenceCV(Statistic.coefVariacion(testTimesResult)));
		
		return sj.toString();
	}
	
	private static void printLog(List<Long> testTimesResult, IAlgoritmo algoritmo, double timeExecuted,
			TestConfiguration config, PrintWriter out) {
		out.println(log(testTimesResult, algoritmo, timeExecuted, config));
	}

//	private static List<IAlgoritmo> getAlgoritmos() {
//		return Arrays.asList(new Algoritmo1(), new AlgoritmoLogN(), new AlgoritmoN(), new AlgoritmoNLogN(),
//				new AlgoritmoN2(), new AlgoritmoN3(), new Algoritmo2N(), new AlgoritmoNF());
//	}
	
//	private static void sendLog(List<Long> testTimesResult, IAlgoritmo algoritmo, double timeExecuted,
//			TestConfiguration config) throws Exception {
//
//        String url = "https://bridgeappdockerized.azurewebsites.net/api/ifttt";
//
//
//        String json = "{\"topic\":\"/pract/stats\",\"message\":\"" + log(testTimesResult, algoritmo, timeExecuted, config) + "\",\"key\":\"jGSp1qHmEQPQMT-6WY-OXTomqPw78JPZGihWQjI3XZH\"}";
//        		
//		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//		con.setRequestMethod("POST");
//		
//		con.setRequestProperty("Content-Type", "application/json; utf-8");
//		con.setRequestProperty("Accept", "application/json");
//		
//		con.setDoOutput(true);
//		
//		//JSON String need to be constructed for the specific resource. 
//		//We may construct complex JSON using any third-party JSON libraries such as jackson or org.json
//		
//		try(OutputStream os = con.getOutputStream()){
//			byte[] input = json.getBytes("utf-8");
//			os.write(input, 0, input.length);			
//		}
//
//		int code = con.getResponseCode();
//		System.out.println(code);
//		
//		try(BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))){
//			StringBuilder response = new StringBuilder();
//			String responseLine = null;
//			while ((responseLine = br.readLine()) != null) {
//				response.append(responseLine.trim());
//			}
//			System.out.println(response.toString());
//		}
//
//    }
	
	// curl --header "Content-Type: application/json" --request POST --data "topic":"/home/light/2","message":"0","key":"jGSp1qHmEQPQMT-6WY-OXTomqPw78JPZGihWQjI3XZH"}' https://bridgeappdockerized.azurewebsites.net/api/ifttt    
}
