package hei.school.sarisary.endpoint.rest.controller;

import hei.school.sarisary.file.BucketComponent;
import hei.school.sarisary.file.FileHash;
import hei.school.sarisary.service.SaryService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/black-and-white")
public class SaryController {

  private final SaryService saryService;
  private final BucketComponent bucketComponent;

  @Autowired
  public SaryController(SaryService saryService, BucketComponent bucketComponent) {
    this.saryService = saryService;
    this.bucketComponent = bucketComponent;
  }

  @PutMapping("/{id}")
  public ResponseEntity<Void> convertToBlackAndWhite(
      @PathVariable String id, @RequestBody MultipartFile image) throws IOException {
    File originalFile = convertMultipartFileToFile(image);
    File blackAndWhiteFile = saryService.convertToBlackAndWhite(originalFile);
    FileHash originalFileHash = bucketComponent.upload(originalFile, "original/" + id);
    FileHash transformedFileHash = bucketComponent.upload(blackAndWhiteFile, "transformed/" + id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Map<String, String>> getBlackAndWhiteImage(@PathVariable String id) {
    String originalUrl = bucketComponent.presign("original/" + id, Duration.ofHours(1)).toString();
    String transformedUrl =
        bucketComponent.presign("transformed/" + id, Duration.ofHours(1)).toString();

    Map<String, String> response = new HashMap<>();
    response.put("original_url", originalUrl);
    response.put("transformed_url", transformedUrl);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public File convertMultipartFileToFile(MultipartFile file) throws IOException {
    Path tempFile = Files.createTempFile("upload-", "-" + file.getOriginalFilename());
    Files.write(tempFile, file.getBytes());
    return tempFile.toFile();
  }
}
