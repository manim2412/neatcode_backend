package com.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {
	@Value("${admin.username}")
	private String adminUsername;

	@Value("${admin.email}")
	private String adminEmail;

	@Test
	void test01() {
		Assertions.assertEquals("admin", adminUsername);
		Assertions.assertEquals("admin@gmail.com", adminEmail);
	}
}
