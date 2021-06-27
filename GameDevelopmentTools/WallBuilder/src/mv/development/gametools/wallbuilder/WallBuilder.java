package mv.development.gametools.wallbuilder;

import mv.development.logger.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class WallBuilder {

    private static String extension = "png";

    public static void main(String[] args) throws Exception {

        String input = "./wallSegments";
        String output = "./combinedWalls";

        // Verify argument with path to location of the wall piece images
//        if (args.length < 1) {
//            System.err.println("Expected path to wall piece images");
//            return;
//        }

//        String path = args[0];
        Logger.debug("Reading wall pieces in", input);

        String[] pieceNames = {
                "background",
                "cornerLeft",
                "cornerRight",
                "innerCornerLeft",
                "innerCornerRight",
                "wallHorizontal",
                "wallVerticalLeft",
                "wallVerticalRight"
        };

        HashMap<String, BufferedImage> pieceImgsMap = new HashMap<>();
        Dimension pieceDimension = null;
        for (String name: pieceNames) {
            String path = input + "/" + name + "." + extension;
            try {
                BufferedImage img = ImageIO.read(new File(path));
                pieceImgsMap.put(name, img);
                Logger.debug("Loaded image", path);

                Dimension currentDimension = new Dimension(img.getWidth(), img.getHeight());
                if (pieceDimension == null) {
                    pieceDimension = currentDimension;
                } else if (currentDimension.getWidth() != pieceDimension.getWidth() || currentDimension.getHeight() != pieceDimension.getHeight()) {
                    throw new Exception("Dimension " + currentDimension +" of image " + name + " not equal to previous dimension " + pieceDimension);
                }
            } catch (IOException e) {
                Logger.error("Could not find image on location " + path);
                return;
            }
        }

        if (pieceDimension.width != pieceDimension.height) {
            Logger.warn("Width", pieceDimension.width, "and height", pieceDimension.height, "are not equal!");
        }

        HashMap<Integer, Integer> duplicatesMap = new HashMap<Integer, Integer>();
        FileWriter duplicates = new FileWriter(output +"/duplicates.txt");

        for (int i = 0; i < 256; ++i) {
            String binaryString = getBinaryString(i);
            Logger.trace("Creating wall image", i, "aka", binaryString);

            // Determine bits
            int upperLeftBit = binaryString.charAt(0) == '1' ? 1 : 0;
            int upperBit = binaryString.charAt(1) == '1' ? 1 : 0;
            int upperRightBit = binaryString.charAt(2) == '1' ? 1 : 0;
            int rightBit = binaryString.charAt(3) == '1' ? 1 : 0;
            int rightBottomBit = binaryString.charAt(4) == '1' ? 1 : 0;
            int bottomBit = binaryString.charAt(5) == '1' ? 1 : 0;
            int leftBottomBit = binaryString.charAt(6) == '1' ? 1 : 0;
            int leftBit = binaryString.charAt(7) == '1' ? 1 : 0;

            // Determine which image to use for each corner
            String upperLeft = leftBit == 1 ? "wallVerticalLeft" : "background";
            String bottomLeft = leftBit == 1 && bottomBit == 1 ? "cornerLeft" : (leftBit == 1 ? "wallVerticalLeft" : (bottomBit == 1 ? "wallHorizontal" : "background"));
            String upperRight = rightBit == 1 ? "wallVerticalRight" : "background";
            String bottomRight = rightBit == 1 && bottomBit == 1 ? "cornerRight" : (rightBit == 1 ? "wallVerticalRight" : (bottomBit == 1 ? "wallHorizontal" : "background"));
            bottomLeft = leftBit == 0 && bottomBit == 0 && leftBottomBit == 1 ? "innerCornerLeft" : bottomLeft;
            bottomRight = rightBit == 0 && bottomBit == 0 && rightBottomBit == 1 ? "innerCornerRight" : bottomRight;

            // Create combied image
            BufferedImage combined = new BufferedImage(pieceDimension.width*2, pieceDimension.height*2, BufferedImage.TYPE_INT_ARGB);
            Graphics g = combined.getGraphics();
            g.drawImage(pieceImgsMap.get(upperLeft), 0, 0, null);
            g.drawImage(pieceImgsMap.get(upperRight), pieceDimension.width, 0, null);
            g.drawImage(pieceImgsMap.get(bottomLeft), 0, pieceDimension.height, null);
            g.drawImage(pieceImgsMap.get(bottomRight), pieceDimension.width, pieceDimension.height, null);

            int borderHeight = 6;
            int borderWidth = 16;
            int SIZE = pieceDimension.width * 2;
            g.setColor(Color.BLACK);
            if (upperLeftBit == 1 && leftBit == 0) {
                g.fillRect(0, 0, borderWidth, borderHeight);
            }
            if (upperRightBit == 1 && rightBit == 0) {
                g.fillRect(SIZE-borderWidth, 0, borderWidth, borderHeight);
            }
            if (upperBit == 1) {
                g.fillRect(0, 0, SIZE, borderHeight);
            }

            System.out.println("Hash: " + i + ": "+ hash(combined));

            int imgHash = hash(combined);
            int duplicate = duplicatesMap.containsKey(imgHash) ? duplicatesMap.get(imgHash) : i;
            duplicatesMap.put(imgHash, duplicate);
            duplicates.write(i+":"+duplicate +"\n");

            g.dispose();
            // Save as new image
            ImageIO.write(combined, "PNG", new File(output, i + "." + extension));
        }
        duplicates.close();
    }

    private static int hash(BufferedImage i) {
        return Arrays.hashCode(i.getRGB(0, 0, i.getWidth(), i.getHeight(), null, 0, i.getWidth()));
    }

        private static String getBinaryString(int i) {
        String binaryString = Integer.toBinaryString(i);
        while (binaryString.length() < 8) {
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }
}
