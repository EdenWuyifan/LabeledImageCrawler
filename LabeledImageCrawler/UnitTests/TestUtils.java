package UnitTests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class TestUtils {

	@Test
	void test() {
		String s = "<div>=art-shtiabsdhfine223<dude/div>";
		Matcher m = Pattern.compile("\\b(art|fine|dude)\\b").matcher(s);
		while(m.find()) {
		     System.out.println(m.group());
		}
		fail("Not yet implemented");
	}

}
