package org.alexburchak.place.frontend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest(classes = FrontendApplication.class)
@WebAppConfiguration
public class FrontendApplicationTests extends AbstractTestNGSpringContextTests {
	@org.testng.annotations.Test
	public void contextLoads() {
	}
}
