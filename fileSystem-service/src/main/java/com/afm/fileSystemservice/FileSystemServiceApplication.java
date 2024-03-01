package com.afm.fileSystemservice;

import com.afm.fileSystemservice.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageProperties.class
})
public class FileSystemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSystemServiceApplication.class, args);
	}

}
