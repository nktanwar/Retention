package com.retention.backend

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootTest
@ComponentScan(
	excludeFilters = [
		ComponentScan.Filter(
			type = FilterType.REGEX,
			pattern = ["com\\.retention\\.backend\\.repository\\..*"]
		)
	]
)
class BackendApplicationTests {

	@Test
	fun contextLoads() {}
}
