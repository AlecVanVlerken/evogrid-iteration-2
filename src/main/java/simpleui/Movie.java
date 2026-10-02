package simpleui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.Timer;
import sim.Creature;
import sim.Simulation;

/**
 * A Swing timer advances evaluation; painting only observes the model.
 * Playback operations are performed on Swing's event thread.
 */
@SuppressWarnings("serial")
public class Movie extends JComponent {
    private static final int SCALE = 3, HEADER = 120, FOOTER = 92;
    private final Simulation sim;
    private final Timer timer;
    private final BufferedImage zone;
    private final BufferedImageRenderer renderer;
    private int targetFrameRate;
    private boolean paused;
    private long lastUpdate;
    private double pendingTicks;

    /**
     * @throws IllegalArgumentException | sim == null || targetFrameRate <= 0
     * @post | getSim() == sim
     * @post | getTargetFramesPerSecond() == targetFrameRate
     * @post | !isPaused()
     */
    public Movie(int targetFrameRate, Simulation sim) {
        if (sim == null || targetFrameRate <= 0) { throw new IllegalArgumentException(); }
        this.sim = sim;
        this.targetFrameRate = targetFrameRate;
        renderer = new BufferedImageRenderer(sim.getWorld().getWidth(), sim.getWorld().getHeight(), SCALE);
        zone = new BufferedImage(renderer.getWidth() * SCALE, renderer.getHeight() * SCALE, BufferedImage.TYPE_INT_ARGB);
        renderer.clearPixels(zone);
        renderer.renderSurvivalZone(zone, sim.getWorld(), sim.getNaturalSelection());
        setOpaque(true);
        setBackground(BufferedImageRenderer.BACKGROUND);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        timer = new Timer(delay(targetFrameRate), event -> updateSimulation());
        timer.setCoalesce(true);
    }

    private static int delay(int rate) { return Math.max(1, (int) Math.round(1000.0 / rate)); }

    /** Timer delivery can be coarse on Windows; retain fractional ticks to meet the target rate. */
    private void updateSimulation() {
        if (paused) { return; }
        long now = System.nanoTime();
        // Limit catch-up after a busy event thread to 100 ms of evaluation.
        pendingTicks += Math.min((now - lastUpdate) / 1_000_000_000.0, 0.1) * targetFrameRate;
        lastUpdate = now;
        int updates = (int) pendingTicks;
        pendingTicks -= updates;
        for (int i = 0; i < updates; i++) { sim.step(); }
        repaint();
    }

    private void resetTiming() {
        lastUpdate = System.nanoTime();
        pendingTicks = 0;
    }

    /**
     * @post | result != null
     */
    public Simulation getSim() { return sim; }

    /**
     * @post | result > 0
     */
    public int getTargetFramesPerSecond() { return targetFrameRate; }

    /**
     * @throws IllegalArgumentException | rate <= 0
     * @mutates | this
     * @post | getTargetFramesPerSecond() == rate
     */
    public void setTargetFrameRate(int rate) {
        if (rate <= 0) { throw new IllegalArgumentException(); }
        targetFrameRate = rate;
        timer.setDelay(delay(rate));
        timer.setInitialDelay(delay(rate));
        resetTiming();
    }

    public boolean isPaused() { return paused; }

    /**
     * Resuming starts with a fresh timer interval, without catching up paused time.
     *
     * @mutates | this
     * @post | isPaused() == value
     */
    public void setPaused(boolean value) {
        paused = value;
        resetTiming();
        if (paused) { timer.stop(); }
        else if (isDisplayable()) { timer.restart(); }
        repaint();
    }

    /**
     * Selects parents at current positions and retains the playback state.
     *
     * @mutates | this, getSim()
     * @post | isPaused() == old(isPaused())
     * @post | getSim().getGeneration() == old(getSim().getGeneration()) + 1
     */
    public void advanceGeneration() {
        sim.nextGeneration();
        resetTiming();
        if (!paused && isDisplayable()) { timer.restart(); }
        repaint();
    }

    /** Clears generation progress and resumes with a fresh random neural population. */
    public void restart() {
        sim.restart();
        setPaused(false);
    }

    @Override public void addNotify() {
        super.addNotify();
        resetTiming();
        if (!paused) { timer.start(); }
    }

    @Override public void removeNotify() {
        timer.stop();
        super.removeNotify();
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(Math.max(720, zone.getWidth() + 32), HEADER + zone.getHeight() + FOOTER);
    }

    /**
     * Draws the current state without advancing evaluation or consuming randomness.
     *
     * @inspects | getSim()
     */
    @Override public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(24, 35, 49));
            g.fillRoundRect(12, 8, getWidth() - 24, 98, 12, 12);
            g.fillRect(12, HEADER + zone.getHeight() + 8, getWidth() - 24, FOOTER - 12);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int left = (getWidth() - zone.getWidth()) / 2;
            drawHeader(g);
            g.drawImage(zone, left, HEADER, null);
            Graphics2D world = (Graphics2D) g.create();
            try {
                world.translate(left, HEADER);
                world.clipRect(0, 0, zone.getWidth(), zone.getHeight());
                for (Creature creature : sim.getWorld().getPopulation()) {
                    renderer.renderCreature(world, creature.getPosition(), creature.getBehavior().getColor(),
                            sim.getNaturalSelection().survives(sim.getWorld(), creature.getPosition()));
                }
            } finally { world.dispose(); }
            g.setColor(new Color(54, 67, 84));
            g.drawRect(left - 1, HEADER - 1, zone.getWidth() + 1, zone.getHeight() + 1);
            drawSummary(g, HEADER + zone.getHeight());
        } finally { g.dispose(); }
    }

    private void drawHeader(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(getFont().deriveFont(Font.BOLD, 21f));
        g.drawString("EvoGrid / Iteration 2", 20, 29);
        g.setFont(getFont());
        g.setColor(new Color(184, 196, 211));
        g.drawString("Neural movement rules are inherited from creatures inside the blue zone.", 20, 52);
        g.setColor(BufferedImageRenderer.TEAL);
        g.drawString(String.format("%s  /  Generation %d  /  Tick %d / %d  /  Eligible %d of %d",
                paused ? "Paused" : "Running", sim.getGeneration(), sim.getTickCount(), sim.getGenerationTicks(),
                sim.getEligibleParentCount(), sim.getPopulationSize()), 20, 78);
        int barWidth = getWidth() - 40;
        g.setColor(new Color(54, 67, 84));
        g.fillRect(20, 90, barWidth, 8);
        g.setColor(BufferedImageRenderer.TEAL);
        g.fillRect(20, 90, (int) (barWidth * sim.getTickCount() / sim.getGenerationTicks()), 8);
    }

    private void drawSummary(Graphics2D g, int top) {
        g.setFont(getFont());
        g.setColor(BufferedImageRenderer.TEAL);
        g.fillOval(21, top + 20, 8, 8);
        g.setColor(Color.WHITE);
        g.drawString("Creature", 37, top + 28);
        g.setColor(BufferedImageRenderer.TEAL);
        g.fillOval(128, top + 20, 8, 8);
        g.setColor(Color.WHITE);
        g.drawOval(128, top + 20, 8, 8);
        g.drawString("Eligible now", 144, top + 28);
        g.setColor(BufferedImageRenderer.ZONE);
        g.fillRect(257, top + 19, 12, 10);
        g.setColor(Color.WHITE);
        g.drawString("Selection zone", 277, top + 28);
        g.setColor(new Color(184, 196, 211));
        String summary = sim.getLastCompletedGeneration().map(last -> String.format(
                "Previous G%d: %d ticks  /  Initially inside %d  /  Selected parents %d",
                last.getGeneration(), last.getEvaluationTicks(), last.getInitialZoneOccupancy(), last.getSelectedParentCount()))
                .orElse("Generations advance automatically at the tick limit. Space advances early.");
        g.drawString(summary, 20, top + 53);
        g.drawString("Selection, crossover and mutation form the next population. Space advances early.", 20, top + 75);
    }
}
