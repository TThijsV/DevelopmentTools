package mv.development.gametools.wallsegments;

import java.awt.*;
import java.awt.image.BufferedImage;

public class WallSegmentCreator {

    int colorSide   = 993300;
    int colorFront  = 663300;
    int colorTop    = 330000;
    int colorBorder = 000000;

    int sideBorderWidth = 6;
    int topBorderWidth  = 2;
    int sideWallWidth   = 8;
    int frontWallHeight = 14;

    int segmentSize = 32;

    public static void main(String[] args) {

    }

    public BufferedImage createBackgroundSegment() {
        // Create combied image
        BufferedImage combined = new BufferedImage(segmentSize, segmentSize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = combined.getGraphics();
    }
}
