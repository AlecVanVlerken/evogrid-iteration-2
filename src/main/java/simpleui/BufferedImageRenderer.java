package simpleui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import sim.World;
import sim.naturalselection.NaturalSelection;
import util.Color;
import util.Point;

/** Draws the fixed selection zone and creatures using simulation coordinates. */
public class BufferedImageRenderer {
    public static final java.awt.Color BACKGROUND = new java.awt.Color(18, 27, 40);
    public static final java.awt.Color TEAL = new java.awt.Color(83, 211, 192);
    public static final java.awt.Color ZONE = new java.awt.Color(55, 75, 100);
    private final int width, height, scale;

    public BufferedImageRenderer(int width, int height) { this(width, height, 2); }

    /**
     * @throws IllegalArgumentException | width <= 0 || height <= 0 || scale <= 0
     * @post | getWidth() == width && getHeight() == height
     */
    public BufferedImageRenderer(int width, int height, int scale) {
        if (width <= 0 || height <= 0 || scale <= 0) { throw new IllegalArgumentException(); }
        this.width = width;
        this.height = height;
        this.scale = scale;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isValidPosition(Point position) {
        if (position == null) { throw new IllegalArgumentException(); }
        return Point.isWithin(position, width, height);
    }

    /**
     * @mutates | bufferedImage
     */
    public void clearPixels(BufferedImage bufferedImage) {
        Graphics2D g = bufferedImage.createGraphics();
        try {
            g.setColor(BACKGROUND);
            g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        } finally { g.dispose(); }
    }

    public void renderCreature(BufferedImage image, Point position, Color color) {
        Graphics2D g = image.createGraphics();
        try { renderCreature(g, position, color, false); }
        finally { g.dispose(); }
    }

    /**
     * Draws a circle; a white outline indicates current reproductive eligibility.
     * Behavior colors remain distinct, with neural green presented as teal.
     *
     * @inspects | position, color
     * @mutates | graphics
     * @pre | isValidPosition(position) && color != null
     */
    public void renderCreature(Graphics2D graphics, Point position, Color color, boolean eligible) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int x = position.getX() * scale + scale / 2;
        int y = position.getY() * scale + scale / 2;
        graphics.setColor(color.equals(Color.GREEN) ? TEAL : new java.awt.Color(color.asInteger(), true));
        graphics.fillOval(x - 3, y - 3, 7, 7);
        if (eligible) {
            graphics.setColor(java.awt.Color.WHITE);
            graphics.drawOval(x - 3, y - 3, 7, 7);
        }
    }

    /**
     * Draws selected cells blue. The application caches this fixed zone independently of population changes.
     *
     * @inspects | world, naturalSelection
     * @mutates | image
     */
    public void renderSurvivalZone(BufferedImage image, World world, NaturalSelection naturalSelection) {
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(ZONE);
            for (int x = 0; x < world.getWidth(); x++) {
                for (int y = 0; y < world.getHeight(); y++) {
                    if (naturalSelection.survives(world, new Point(x, y))) {
                        g.fillRect(x * scale, y * scale, scale, scale);
                    }
                }
            }
        } finally { g.dispose(); }
    }
}
