package com.collegemarketplace.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class HelperService {
	
	public String saveFile(MultipartFile file, long userId)
	{
		try {
			String filename = Instant.now().toString().replaceAll(":", "-") + "---" + userId + "-" + file.getOriginalFilename().replaceAll(":", "-");
			File file2 = new ClassPathResource("static/uploads").getFile();
//			Path filePath = Paths.get("src/main/resources/static/uploads/" + filename);
			Path filePath = Paths.get(file2.getAbsolutePath() + File.separator + filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } 
		catch (IOException e) {
			throw new RuntimeException("Error while saving the file: " + e.getMessage(), e);
        }
	}
	
	public void deleteFile(String imageUrl)
	{
        try {
          // Path filePath = Paths.get("src/main/resources/static/uploads/" + imageUrl);
        	File file2 = new ClassPathResource("static/uploads").getFile();
//			Path filePath = Paths.get("src/main/resources/static/uploads/" + filename);
			Path filePath = Paths.get(file2.getAbsolutePath() + File.separator + imageUrl);
			Files.delete(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
