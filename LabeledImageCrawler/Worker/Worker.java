package Worker;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import WebCrawler.SimpleWebCrawler;
import Server.Server;

public class Worker implements Runnable {
	private Logger logger = Logger.getLogger(SimpleWebCrawler.class.getName());
	private String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)";
	private static int workerID = 1;
	private final String name = "worker#"+workerID;
	private ImageDownloader downloader = new ImageDownloader();

	private String url;
	private String keyword = Server.keyword;

	public Worker(String URL) {
		this.url = URL;
		workerID++;
	}

	public Elements grepImagesFromUrl() {
		Document doc;
		try {
			doc = Jsoup.connect(this.url).userAgent(userAgent).get();

			// Get all img tags
	        Elements img = doc.getElementsByTag("img");

	        // Loop through img tags
            for (Element el : img) {

            	if (!Pattern.compile(keyword).matcher(el.toString()).find()) {
            		continue;
            	}

            	String strImageURL = el.attr("abs:src");
            	System.out.println("Image src: " + el.attr("abs:src") + " Alt: " + el.attr("alt"));
            	downloader.downloadImage(strImageURL);
            }
	        return img;
		} catch (IOException e) {
        	logger.log(Level.SEVERE, "IOException: " + e.getMessage());
        }
		return null;
	}

	public void run() {
		try {
			logger.info(this.name + " started!");
			logger.info(this.name + " processing [" + this.url + "]");
			grepImagesFromUrl();

		} catch (Exception e) {
			logger.warning("Exception caught in worker: " + e.getMessage());
		}
	}
}
