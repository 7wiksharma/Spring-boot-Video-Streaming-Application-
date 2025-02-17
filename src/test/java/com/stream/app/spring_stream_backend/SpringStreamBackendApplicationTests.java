package com.stream.app.spring_stream_backend;

import com.stream.app.spring_stream_backend.services.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringStreamBackendApplicationTests {
@Autowired
	VideoService videoService;

	@Test
	void contextLoads() {
		videoService.processVideo("66432b88-457f-4343-97a3-c9aa1f4f7c47");
	}

}
