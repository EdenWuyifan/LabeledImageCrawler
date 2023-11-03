package WebCrawler;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import Worker.Worker;


public class SimpleWebCrawler {
	private Logger logger = Logger.getLogger(SimpleWebCrawler.class.getName());
	private int MAX_DEPTH = 5;
	private int depth = 0;

    private Queue<String> links;
    private static HashSet<String> visited = new HashSet<String>();
    private final ThreadPoolExecutor executor;

    public SimpleWebCrawler(String URL, ThreadPoolExecutor executor) {
    	this.executor = executor;
        this.links = new LinkedList<String>();
        this.links.add(URL);
    }

    public SimpleWebCrawler(String URL, ThreadPoolExecutor executor, int max_depth) {
    	this(URL, executor);
    	this.MAX_DEPTH = max_depth;
    }

    public Queue<String> getPageLinks() {
        //4. Check if you have already crawled the URLs
        //(we are intentionally not checking for duplicate content in this example)
        while (!links.isEmpty() && (this.depth < this.MAX_DEPTH)) {
            try {
            	String URL = links.poll();
                //4. (i) If not add it to the index
                if (visited.add(URL)) {
                	logger.log(Level.INFO, "[" + this.depth+ "]" + URL);
                }

                //2. Fetch the HTML code
                Document document = Jsoup.connect(URL).get();

                //3. Parse the HTML to extract links to other URLs
                Elements linksOnPage = document.select("a[href]");

                this.depth++;
                for (int i = 0; i < linksOnPage.size(); i++) {
                	Element page = linksOnPage.get(i);
                	String link = page.attr("abs:href");
                	if (!visited.contains(link)) {
                		links.add(link);
                		executor.submit(new Worker(link));
                	}
                }
            } catch (IOException e) {
            	logger.log(Level.SEVERE, "IOException: " + e.getMessage());
            	System.exit(0);
            }
        }
        return links;
    }

    public static void main(String[] args) {
    }
}

