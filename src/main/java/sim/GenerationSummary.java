package sim;

/**
 * The endpoint selection result of one completed generation, automatic or manually advanced.
 * Evaluation ticks record actual evaluation time, including shorter manual generations.
 * Counts describe qualification for reproduction, not deaths during evaluation.
 *
 * @immutable
 */
public final class GenerationSummary {
    private final int generation;
    private final long evaluationTicks;
    private final int initialZoneOccupancy;
    private final int selectedParentCount;

    /**
     * @pre | generation >= 1 && evaluationTicks >= 0
     * @pre | initialZoneOccupancy >= 0 && selectedParentCount >= 0
     * @post | getGeneration() == generation
     * @post | getEvaluationTicks() == evaluationTicks
     * @post | getInitialZoneOccupancy() == initialZoneOccupancy
     * @post | getSelectedParentCount() == selectedParentCount
     */
    GenerationSummary(int generation, long evaluationTicks, int initialZoneOccupancy, int selectedParentCount) {
        this.generation = generation;
        this.evaluationTicks = evaluationTicks;
        this.initialZoneOccupancy = initialZoneOccupancy;
        this.selectedParentCount = selectedParentCount;
    }

    /**
     * @post | result >= 1
     */
    public int getGeneration() { return generation; }

    /**
     * @post | result >= 0
     */
    public long getEvaluationTicks() { return evaluationTicks; }

    /**
     * @post | result >= 0
     */
    public int getInitialZoneOccupancy() { return initialZoneOccupancy; }

    /**
     * @post | result >= 0
     */
    public int getSelectedParentCount() { return selectedParentCount; }
}
