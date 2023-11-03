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
	private static final int NUM_OF_WORKERS = 4;
	private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(NUM_OF_WORKERS);
	public static String keyword;


	// Crawler
	private SimpleWebCrawler crawler = null;
	private int maxDepth = 5;
	private String URL;


	public Server(String URL, String keyword) {
		this.URL = URL;
		String[] keywords = keyword.toLowerCase().split(",");
		Server.keyword = String.join("|", keywords);
	}

	public Server(String URL, String keyword, int maxDepth) {
		this(URL, keyword);
		this.maxDepth = maxDepth;
	}

	public int getThreadPoolCap() {
		return executor.getPoolSize();
	}

	public void start() throws Exception {
		executor.submit(new Worker(URL));
		crawler = new SimpleWebCrawler(URL, executor, maxDepth);
		if (crawler == null) {
			throw new Exception("crawler is not successfully init!");
		}
		crawler.getPageLinks();
		executor.shutdown();
		while (!executor.isTerminated()) {}
		System.out.println("Finished all threads");
	}

	public static void main(String[] args) throws Exception {
		Logger.getGlobal().setLevel(Level.WARNING);
		Server server = new Server(
				"https://www.artnet.com/artists/",
				"painting,sculpture,art", 0
				);
		server.start();
    }
}
