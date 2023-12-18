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
	private String userAgent = "Mozilla/5.0";
	private String userAgent2 = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
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
		Elements img = crawlImagesFromDocument(userAgent);
		if (img.isEmpty()) {
			img = crawlImagesFromDocument(userAgent2);
		}

	    // Loop through img tags
        for (Element el : img) {

        	if (keyword != null & !Pattern.compile("\\b("+keyword+")\\b").matcher(el.toString().toLowerCase()).find()) {
        		continue;
        	}

        	String strImageURL = el.absUrl("src");
        	if (strImageURL.isEmpty()) {
        		strImageURL = el.absUrl("data-lazysrc");
        	}

//        	System.out.println("Image src: " + el.absUrl("src") + " Alt: " + el.attr("alt"));
        	downloader.downloadImage(strImageURL);
        }
        return img;
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

	public Elements crawlImagesFromDocument(String agent) {
		try {
			Document doc = Jsoup.connect(this.url)
								.userAgent(agent)
								.header("Accept", "text/html")
								.header("Accept-Encoding", "gzip,deflate")
								.header("Accept-Language", "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2")
								.header("Connection", "keep-alive")
								.ignoreContentType(true)
								.get();
			return doc.select("img");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException: " + e.getMessage());
			return null;
		}
	}

	public static void main(String[] args) {
		Worker worker = new Worker("https://www.google.com/search?sa=N&sca_esv=591956038&cs=0&q=cat&tbm=isch&source=univ&fir=4NNfyTkjCxCMKM%252CBvzzy2OOLWm60M%252C_%253BSgUIr6w22-Q0eM%252CTql7n-yz4LY4MM%252C_%253B_ilKwl5-XUq23M%252Cw6C-28gX8VNFPM%252C_%253BzP4N2WyGyDNkkM%252CMZs71HIAHkZnCM%252C_%253BKpvRE_oYZSFlsM%252CgvGL9m_dcxIUyM%252C_%253B6zzUgaMgZxn6hM%252Car_3bxPTjeUC5M%252C_%253BElJkDgW7mXTrkM%252C2gaX5kZ0uwOxlM%252C_%253B35yNA5ZmjcRIPM%252CBcBpinuJ7Df69M%252C_%253BhP5l0XlcBBidaM%252CMvjG6ouDFwZplM%252C_%253BHVPqNWc859x2hM%252Cx3ma-qw1Ys6SeM%252C_%253B7Eyy2dLwKf-kmM%252C-_0H0yDN4l0_vM%252C_%253B0yxTNn5NI9_ciM%252CBvzzy2OOLWm60M%252C_%253BMqfcuD5Yoc5wiM%252CgvGL9m_dcxIUyM%252C_%253BhmQAmnuTgvN-nM%252Cx3ma-qw1Ys6SeM%252C_&usg=AI4_-kT5JKrpZzavWpHAyU7vRMP_4Fp6mw&biw=814&bih=729&dpr=2");
		Elements img = worker.crawlImagesFromDocument("Mozilla/5.0");
		for (Element el : img) {
			String strImageURL = el.absUrl("src");
        	if (strImageURL.isEmpty()) {
        		strImageURL = el.absUrl("data-lazysrc");
        	}
        	try {
				worker.downloader.downloadImage(strImageURL);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	System.out.println(strImageURL);
		}
	}
}
