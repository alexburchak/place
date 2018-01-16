package org.alexburchak.place.backend;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest(classes = BackendApplication.class)
public class BackendApplicationTests extends AbstractTestNGSpringContextTests {
	@Test
	public void contextLoads() {
	}
}
