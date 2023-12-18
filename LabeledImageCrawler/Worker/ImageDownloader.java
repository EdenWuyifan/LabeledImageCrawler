package Worker;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import DB.LabeledImage;
import GUI.LICApp;
import WebCrawler.SimpleWebCrawler;
import Server.Server;

public class ImageDownloader {
	private Logger logger = Logger.getLogger(SimpleWebCrawler.class.getName());
	private static String path = "./output/";
	private int tmpName = 1;
	private long timeStamp;

	public ImageDownloader() {}

	public void setDownloadPath(String path) {
		ImageDownloader.path = path;
	}

	public void downloadImage(String imageUrl) {
		String savedPath;
		String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		if (!imageName.endsWith(".jpg") && !imageName.endsWith(".png")
				&& !imageName.endsWith(".jpeg") && !imageName.endsWith(".gif")
				&& !imageUrl.startsWith("https://encrypted-tbn0")
//				&& !imageUrl.startsWith("data:")
		) {
			return;
		}

		logger.log(Level.INFO, "Saving " + imageName + " from [" + imageUrl + "].");

		try {
			InputStream in;
			if (imageUrl.startsWith("data:")) {
				System.out.println("We ARE HERE!!!!!!!:" + imageUrl);
				if (!imageUrl.contains("base64,")) {
					return;
				}
				imageName = imageUrl.substring(imageUrl.lastIndexOf("base64,") + "base64,".length());
				byte[] decoded = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageName);
				in = new ByteArrayInputStream(decoded);
				imageName = tmpName + ".png";
				tmpName++;
			} else {
				if (imageUrl.startsWith("//")) {
					imageUrl = "https:" + imageUrl;
					System.out.println("[!!!!!!!!!!!!!!!!!]" + imageUrl);
				}
				URL urlImage = new URL(imageUrl);
				in = urlImage.openStream();
			}



			byte[] buffer = new byte[4096];
			int n = -1;

//			timeStamp = System.currentTimeMillis() / 1000L;
			savedPath = path + timeStamp + "_" + imageName;
			OutputStream os = new FileOutputStream(savedPath);

			while ((n = in.read(buffer)) != -1) {
				os.write(buffer, 0, n);
			}

			os.close();
			// Save to DB
			LabeledImage sqlObj = new LabeledImage(imageName, Server.keyword, imageUrl, savedPath);
			LICApp.sql.Create(sqlObj);

			logger.log(Level.INFO, "Image saved");
		} catch (IOException e) {
			logger.log(Level.SEVERE, "IOException: " + e.getMessage());
			e.printStackTrace();
		}


	}

	public static void main(String[] args)    {
//		String imageName = "";
//		String testBase64Url = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABYAAAAWCAYAAADEtGw7AAAAAXNSR0IArs4c6QAAASxJREFUSEvNlTFKA0EUhr9/bb2Bi9rY2eUCgoUHEAVtQ0yvViaFhSsICrYaU1voBcTGA0QrOxsNwQtoJWaeJGQDgczuZiSQaYf5eLz/e29EwInrjTUs2gE2Bs8fkLvtnFSeUpwm5fagsrlzsNLoWz2buocpfHJwrXkl2BtXkMF1JylXe3ch4HfBkgf80UnKy7MDXjy6WTfpElgt3IqspBeOG3H0E10gtgfAN2AlNzx/0rwYtITtgubBvoUSpJYz28rVLc5IOq3KjHvX/T34PKu2iyjatyKuNb1JC74wt9k+rTwWAY4MSBbYYKhQCNgrPfAqc/thFXvHlGH6QT3u93nMYomkO5wrmagHWZHXu2CP88DpfdDkFYUXNWimttuU9vG0fhCfmv/+84qG/AeWXskXo77XMQAAAABJRU5ErkJggg==";
//		if (testBase64Url.contains("base64,")) {
//			imageName = testBase64Url.substring(testBase64Url.lastIndexOf("base64,") + "base64,".length());
//			byte[] decoded = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageName);
//			String decodedString = decoded.toString();
//			try {
//				BufferedImage img = ImageIO.read(new ByteArrayInputStream(decoded));
//				System.out.println(img.toString());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//
//		System.out.println(imageName);
	}
}
