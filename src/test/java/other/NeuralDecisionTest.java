package other;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import sim.*;
import sim.behaviors.*;
import sim.naturalselection.*;
import sim.neuralnet.*;
import util.Orientation;
import util.Pair;
import util.Point;

class NeuralDecisionTest {
    private Creature creature(Point position, Orientation heading) {
        return new Creature(new ImmobileBehavior(new Chromosome(new int[Constants.CHROM_SIZE])), position, heading);
    }

    @Test
    void allEquivalentHeadingsHaveExactSensorValues() {
        int[] horizontal = {0, 500, 1000, 500, 0, -500, -1000, -500};
        int[] vertical = {-1000, -500, 0, 500, 1000, 500, 0, -500};
        World world = new World(7, 7, new Creature[0]);
        for (int i = 0; i < 8; i++) {
            for (Orientation heading : new Orientation[] {new Orientation(i), Orientation.orientations().get(i)}) {
                Creature subject = creature(new Point(3, 3), heading);
                assertEquals(horizontal[i], new HorizontalOrientationSensorNeuron().computeOutput(world, subject));
                assertEquals(vertical[i], new VerticalOrientationSensorNeuron().computeOutput(world, subject));
            }
        }
    }

    @Test
    void positionSensorsNormalizeTheirOwnAxisIncludingExactEndpoints() {
        World world = new World(5, 9, new Creature[0]);
        int[][] samples = {{0, 8, -1000, 1000}, {1, 2, -500, -500},
                {2, 4, 0, 0}, {4, 0, 1000, -1000}};
        for (int[] sample : samples) {
            Creature subject = creature(new Point(sample[0], sample[1]), Orientation.north());
            assertEquals(sample[2], new HorizontalPositionSensorNeuron().computeOutput(world, subject));
            assertEquals(sample[3], new VerticalPositionSensorNeuron().computeOutput(world, subject));
        }
    }

    @Test
    void passageSensorsRotateWithEveryHeadingAndDetectBoundaries() {
        Orientation[] offsets = {Orientation.north(), Orientation.northWest(), Orientation.northEast()};
        for (int heading = 0; heading < 8; heading++) {
            Creature subject = creature(new Point(3, 3), new Orientation(heading));
            for (int blocked = 0; blocked < offsets.length; blocked++) {
                Point obstacle = subject.getPosition().move(subject.getOrientation().compose(offsets[blocked]).toVector());
                World world = new World(7, 7, new Creature[] {subject, creature(obstacle, Orientation.north())});
                for (int sensor = 0; sensor < offsets.length; sensor++) {
                    assertEquals(sensor == blocked ? -750 : 750,
                            new FreePassageSensorNeuron(offsets[sensor]).computeOutput(world, subject));
                }
            }
        }
        Creature edge = creature(new Point(0, 0), Orientation.north());
        assertEquals(-750, new FreePassageSensorNeuron(Orientation.north())
                .computeOutput(new World(7, 7, new Creature[] {edge}), edge));
    }

    @Test
    void genesMapToEveryConnectionAndBiasInOutputOrder() {
        int[] genes = new int[Constants.CHROM_SIZE];
        for (int i = 0; i < genes.length; i++) { genes[i] = i * 17 - 200; }
        NeuralNetwork network = NeuralNetwork.fromChromosome(new Chromosome(genes));
        var inputs = network.getInputNeurons();
        var outputs = network.getOutputNeurons();
        assertSame(network.getMoveForwardNeuron(), outputs[0]);
        assertSame(network.getTurnCounterclockwiseNeuron(), outputs[1]);
        assertSame(network.getTurnClockwiseNeuron(), outputs[2]);
        for (int output = 0; output < 3; output++) {
            assertEquals(genes[21 + output], outputs[output].getBias());
            assertEquals(7, outputs[output].getDependencies().size());
            for (int input = 0; input < 7; input++) {
                var pair = outputs[output].getDependencies().get(input);
                assertSame(inputs[input], pair.getFirst());
                assertEquals(genes[7 * output + input], pair.getSecond());
            }
        }
    }

    @Test
    void outputsUsePerConnectionIntegerScalingAndInheritedClamps() {
        World world = new World(5, 5, new Creature[0]);
        Creature subject = creature(new Point(2, 2), Orientation.east());
        LinearFunctionNeuron neuron = new LinearFunctionNeuron();
        neuron.connect(new HorizontalOrientationSensorNeuron(), 333);
        neuron.connect(new FreePassageSensorNeuron(Orientation.north()), -335);
        neuron.setBias(-100);
        assertEquals(-18, neuron.computeOutput(world, subject)); // 333 - 251 - 100
        for (int input : new int[] {-2000, -500, -1, 0, 500, 1000, 2000}) {
            assertEquals(Math.max(-1000, Math.min(1000, input)), neuron.applyActivationFunction(input));
            assertEquals(Math.max(-500, Math.min(1000, input)),
                    new RectifiedLinearUnitFunctionNeuron().applyActivationFunction(input));
        }
    }

    @Test
    void chromosomeNetworkProducesExactNumericalOutputs() {
        int[] genes = new int[Constants.CHROM_SIZE];
        for (int i = 0; i < 7; i++) {
            genes[i] = 100;
            genes[i + 7] = -200;
            genes[i + 14] = 300;
        }
        genes[21] = 50;
        genes[22] = -75;
        genes[23] = 100;
        NeuralNetwork network = NeuralNetwork.fromChromosome(new Chromosome(genes));
        World world = new World(5, 5, new Creature[0]);
        Creature subject = creature(new Point(2, 2), Orientation.east());
        assertEquals(375, network.getMoveForwardNeuron().computeOutput(world, subject));
        assertEquals(-725, network.getTurnCounterclockwiseNeuron().computeOutput(world, subject));
        assertEquals(1000, network.getTurnClockwiseNeuron().computeOutput(world, subject));
    }

    @Test
    void neuralMovementRespectsOccupancyAndOnlyPositiveForwardOutputs() {
        for (int bias : new int[] {-500, 0, 1}) {
            NeuralNetworkBehavior behavior = new NeuralNetworkBehavior(new Chromosome(new int[Constants.CHROM_SIZE]));
            behavior.getNeuralNetwork().getMoveForwardNeuron().setBias(bias);
            World free = new World(5, 5, new Creature[] {
                    new Creature(behavior, new Point(2, 2), Orientation.east())});
            free.step();
            assertEquals(new Point(bias > 0 ? 3 : 2, 2), free.getPopulation()[0].getPosition());
            World blocked = new World(5, 5, new Creature[] {
                    new Creature(behavior, new Point(2, 2), Orientation.east()),
                    creature(new Point(3, 2), Orientation.north())});
            blocked.step();
            assertEquals(new Point(2, 2), blocked.getPopulation()[0].getPosition());
            assertEquals(new Point(3, 2), blocked.getPopulation()[1].getPosition());
        }
    }

    @Test
    void turningRequiresMoreThan150InEitherDirection() {
        for (int difference : new int[] {-151, -150, 150, 151}) {
            NeuralNetworkBehavior behavior = new NeuralNetworkBehavior(new Chromosome(new int[Constants.CHROM_SIZE]));
            behavior.getNeuralNetwork().getTurnClockwiseNeuron().setBias(difference);
            World world = new World(5, 5, new Creature[] {
                    new Creature(behavior, new Point(2, 2), Orientation.north())});
            world.step();
            Orientation expected = difference == 151 ? Orientation.northEast()
                    : difference == -151 ? Orientation.northWest() : Orientation.north();
            assertSame(expected, world.getPopulation()[0].getOrientation());
            assertEquals(new Point(2, 2), world.getPopulation()[0].getPosition());
        }
    }

    @Test
    void neuralCreatureTurnsAtEdgeThenMovesBackInward() {
        NeuralNetworkBehavior behavior = new NeuralNetworkBehavior(new Chromosome(new int[Constants.CHROM_SIZE]));
        var network = behavior.getNeuralNetwork();
        network.getMoveForwardNeuron().setBias(1);
        network.getTurnClockwiseNeuron().setBias(151);
        World world = new World(5, 5, new Creature[] {
                new Creature(behavior, new Point(0, 2), Orientation.west())});
        world.step();
        assertEquals(new Point(0, 2), world.getPopulation()[0].getPosition());
        assertSame(Orientation.northWest(), world.getPopulation()[0].getOrientation());
        world.step();
        world.step();
        world.step();
        assertEquals(new Point(1, 0), world.getPopulation()[0].getPosition());
        assertSame(Orientation.east(), world.getPopulation()[0].getOrientation());
    }

    @Test
    void movementOccursBeforeTurningAndTurningReadsNewPosition() {
        NeuralNetworkBehavior behavior = new NeuralNetworkBehavior(new Chromosome(new int[Constants.CHROM_SIZE]));
        var network = behavior.getNeuralNetwork();
        network.getMoveForwardNeuron().setBias(1);
        network.getTurnClockwiseNeuron().setDependencies(new ArrayList<>());
        network.getTurnClockwiseNeuron().connect(new HorizontalPositionSensorNeuron(), 1000);
        World world = new World(5, 5, new Creature[] {
                new Creature(behavior, new Point(2, 2), Orientation.east())});
        world.step();
        assertEquals(new Point(3, 2), world.getPopulation()[0].getPosition());
        assertSame(Orientation.southEast(), world.getPopulation()[0].getOrientation());
    }

    @Test
    void networkArraySnapshotsRetainEditableNeuronReferences() {
        NeuralNetwork network = new NeuralNetwork();
        var inputs = network.getInputNeurons();
        var input = inputs[0];
        inputs[0] = null;
        assertSame(input, network.getInputNeurons()[0]);
        var outputs = network.getOutputNeurons();
        outputs[0].setBias(321);
        outputs[0] = null;
        assertNotNull(network.getOutputNeurons()[0]);
        assertEquals(321, network.getMoveForwardNeuron().getBias());
    }

    @Test
    void dependencyListsAndPairsAreIsolatedButNeuronsRemainEditable() {
        LinearFunctionNeuron source = new LinearFunctionNeuron();
        source.setBias(100);
        var pair = new Pair<Neuron, Integer>(source, 500);
        var input = new ArrayList<Pair<Neuron, Integer>>();
        input.add(pair);
        LinearFunctionNeuron output = new LinearFunctionNeuron();
        output.setDependencies(input);
        pair.setSecond(1000);
        pair.setFirst(new LinearFunctionNeuron());
        input.clear();
        var snapshot = output.getDependencies();
        assertSame(source, snapshot.get(0).getFirst());
        assertEquals(500, snapshot.get(0).getSecond());
        snapshot.get(0).setSecond(-1000);
        snapshot.get(0).setFirst(new LinearFunctionNeuron());
        snapshot.clear();
        World world = new World(5, 5, new Creature[0]);
        Creature subject = creature(new Point(2, 2), Orientation.north());
        assertEquals(50, output.computeOutput(world, subject));
        source.setBias(200);
        assertEquals(100, output.computeOutput(world, subject));
        output.doubleSensor(0);
        assertEquals(200, output.computeOutput(world, subject));
    }

    @Test
    void borderSelectionHasExactSymmetricCutoffs() {
        World world = new World(9, 7, new Creature[0]);
        for (int size : new int[] {0, 1, 2, 4, 10}) {
            BorderHabitableZone zone = new BorderHabitableZone(size);
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 7; y++) {
                    boolean expected = Math.min(Math.min(x, 8 - x), Math.min(y, 6 - y)) < size;
                    assertEquals(expected, zone.survives(world, new Point(x, y)));
                }
            }
        }
    }

    @Test
    void disjunctionUsesOrForEveryCombination() {
        World world = new World(5, 5, new Creature[0]);
        for (boolean left : new boolean[] {false, true}) {
            for (boolean right : new boolean[] {false, true}) {
                assertEquals(left || right, new Disjunction((w, p) -> left, (w, p) -> right)
                        .survives(world, new Point(2, 2)));
            }
        }
    }
}
