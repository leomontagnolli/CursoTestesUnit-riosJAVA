package br.ce.wcaquino.runners;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;

public class ParalelRunner extends BlockJUnit4ClassRunner {

	public ParalelRunner(Class<?> klass) throws InitializationError {
		super(klass);
		setScheduler(new ThreadPool());

	}
	
	private static class ThreadPool implements RunnerScheduler{
		private ExecutorService executor;
		
		
		
		public ThreadPool() {
			executor = Executors.newFixedThreadPool(5);
		}

		@Override
		public void schedule(Runnable childStatement) {
			executor.submit(childStatement);
			
		}

		@Override
		public void finished() {
			executor.shutdown();
			try {
				executor.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			
			
		}
		
	}

}
