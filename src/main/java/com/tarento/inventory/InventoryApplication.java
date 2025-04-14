//package com.tarento.inventory;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.mongodb.config.EnableMongoAuditing;
//
//@SpringBootApplication
//@EnableMongoAuditing
//
//
//public class InventoryApplication {
//
//	public static void main(String[] args) {
//		SpringApplication.run(InventoryApplication.class, args);
//	}
//
//}

package com.tarento.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.tarento.inventory.repo")
@EnableElasticsearchRepositories(basePackages = "com.tarento.inventory.repo")
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}
}

