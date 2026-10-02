package simpleui;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import sim.Constants;
import sim.Simulation;

/** A simulation window with playback controls and window-wide key bindings. */
@SuppressWarnings("serial")
public class SimLifeWindow extends JFrame {
    private final Movie mov;

    public static void create(String title, int width, int height, Simulation sim) {
        SwingUtilities.invokeLater(() -> new SimLifeWindow(title, width, height, sim));
    }

    private SimLifeWindow(String title, int width, int height, Simulation sim) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mov = new Movie(Constants.DEFAULT_FRAME_RATE, sim);
        add(mov, BorderLayout.CENTER);
        JPanel controls = new JPanel();
        JButton pause = button("Pause (P)");
        JButton next = button("Next generation (Space)");
        JButton reset = button("Restart (R)");
        Runnable toggle = () -> {
            mov.setPaused(!mov.isPaused());
            pause.setText(mov.isPaused() ? "Resume (P)" : "Pause (P)");
        };
        pause.addActionListener(event -> toggle.run());
        next.addActionListener(event -> mov.advanceGeneration());
        Runnable restart = () -> { mov.restart(); pause.setText("Pause (P)"); };
        reset.addActionListener(event -> restart.run());
        controls.add(pause);
        controls.add(next);
        controls.add(reset);
        add(controls, BorderLayout.SOUTH);
        bind("P", toggle);
        bind("SPACE", mov::advanceGeneration);
        bind("R", restart);
        // Let the window binding handle Space even while a button has keyboard focus.
        for (JButton button : new JButton[] {pause, next, reset}) {
            button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"), "none");
            button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("released SPACE"), "none");
        }
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        return button;
    }

    private void bind(String key, Runnable action) {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        getRootPane().getActionMap().put(key, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { action.run(); }
        });
    }

    /**
     * @post | result != null
     */
    public Movie getMov() { return mov; }
}
