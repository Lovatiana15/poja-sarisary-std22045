package hei.school.sarisary.service;

import hei.school.sarisary.file.BucketComponent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class SaryService {
  private final BucketComponent bucketComponent;

  public SaryService(BucketComponent bucketComponent) {
    this.bucketComponent = bucketComponent;
  }

  public File convertToBlackAndWhite(File originalImage) throws IOException {
    BufferedImage image = ImageIO.read(originalImage);
    BufferedImage blackAndWhiteImage =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int rgb = image.getRGB(x, y);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        int gray = (red + green + blue) / 3;

        blackAndWhiteImage.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
      }
    }
    File blackAndWhiteFile = File.createTempFile("blackAndWhite", ".png");
    ImageIO.write(blackAndWhiteImage, "png", blackAndWhiteFile);
    return blackAndWhiteFile;
  }
}
