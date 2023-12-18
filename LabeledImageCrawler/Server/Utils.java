package Server;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.jsoup.Jsoup;

public class Utils {
	private static String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

	public static boolean checkUrlValid(String url) {
		try {
			Jsoup.connect(url)
					.userAgent(userAgent)
					.header("Accept", "text/html")
					.header("Accept-Encoding", "gzip,deflate")
					.header("Accept-Language", "it-IT,en;q=0.8,en-US;q=0.6,de;q=0.4,it;q=0.2,es;q=0.2")
					.header("Connection", "keep-alive")
					.ignoreContentType(true)
					.get();
			return true;
		} catch (UnknownHostException e) {
		    System.err.println("Unknown host");
		    e.printStackTrace(); // I'd rather (re)throw it though.
		} catch (SocketTimeoutException e) {
		    System.err.println("IP cannot be reached");
		    e.printStackTrace(); // I'd rather (re)throw it though.
		} catch (IOException e) {
			System.err.println("IO Exception");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("IllegalArgumentException: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}
