package Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import WebCrawler.SimpleWebCrawler;
import Worker.Worker;

public class Server {
	private Logger logger = Logger.getLogger(SimpleWebCrawler.class.getName());
	// CyclicBarrier to wait for workers to finish
	private static final int NUM_OF_WORKERS = 1;
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_OF_WORKERS);
	public static String keyword;

	// BlockingQueue for collecting available links
//	private static final int MAX_TASKS_WAITING = 256;
//	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(MAX_TASKS_WAITING);

	// Crawler
	private SimpleWebCrawler crawler = null;


	public Server(String URL, String keyword) {
		String[] keywords = keyword.toLowerCase().split(",");
		Server.keyword = String.join("|", keywords);
		executor.submit(new Worker(URL));
		crawler = new SimpleWebCrawler(URL, executor);
		crawler.getPageLinks();

		executor.shutdown();
		while (!executor.isTerminated()) {}
		System.out.println("Finished all threads");

	}


	public static void main(String[] args)    {
		Logger.getGlobal().setLevel(Level.WARNING);
		Server server = new Server(
				"https://www.artnet.com/artists/",
				"painting,sculpture,art"
				);
    }
}
