import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

class EncodePreview {
    public static void main(String[] args) throws Exception {
        try (var files = Files.list(Path.of(args[0]))) {
            for (Path path : files.filter(p -> p.toString().endsWith(".png")).toList()) {
                BufferedImage screenshot = ImageIO.read(path.toFile());
                int width = 400;
                int height = screenshot.getHeight() * width / screenshot.getWidth();
                var scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D canvas = scaled.createGraphics();
                canvas.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                canvas.drawImage(screenshot, 0, 0, width, height, null);
                canvas.dispose();
                var jpeg = path.resolveSibling(path.getFileName().toString().replace(".png", ".jpg"));
                ImageIO.write(scaled, "jpeg", jpeg.toFile());
            }
        }
    }
}
