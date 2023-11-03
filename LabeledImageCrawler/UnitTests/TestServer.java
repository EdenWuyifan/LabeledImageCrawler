package UnitTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Server.Server;

class TestServer {

	private Server server;
	private final String url = "https://wp.nyu.edu/eden_wuyifan/";
	private final String keyword = "eden|wu";

	@Test
	void testServerInit() {
		server = new Server(url, keyword, 0);
		assertEquals(server.keyword, keyword);
	}


	@Test
	void testServerStart() {
		server = new Server(url, keyword, 0);
		assertAll(() -> server.start());
		assertEquals(server.getThreadPoolCap(), 0);
	}
}
