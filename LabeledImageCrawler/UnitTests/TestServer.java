package UnitTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Server.Server;

class TestServer {

	private Server server;
//	private final String url = "https://wp.nyu.edu/eden_wuyifan/";
	private final String url = "https://www.google.com/search?sca_esv=587346141&q=art&tbm=isch&source=lnms&sa=X&ved=2ahUKEwjpuPuq1PGCAxXtk4kEHcwVASAQ0pQJegQIDxAB&biw=1440&bih=733&dpr=2";
	private final String keyword = "eden|wu";

	@Test
	void testServerInit() {
		server = new Server(url, keyword, 0);
		assertEquals(server.keyword, keyword);
	}


	@Test
	void testServerStart() {
		server = new Server(url, keyword, 1);
		assertAll(() -> server.start());
		assertEquals(server.getThreadPoolCap(), 0);
	}
}
