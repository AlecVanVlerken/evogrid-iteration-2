package sim;

import java.util.Arrays;
import java.util.stream.IntStream;

import util.RandomUtil;
import static util.Logic.*;

/**
 * @immutable
 */
public class Chromosome
{
    /**
     * @invar | weights != null 
     * @invar | weights.length == Constants.CHROM_SIZE
     * @invar | Arrays.stream(weights).allMatch(Chromosome::isValidGene)
     */
    private final int[] weights;

    /**
     * @throws IllegalArgumentException | weights == null
     * @throws IllegalArgumentException | weights.length != Constants.CHROM_SIZE
     * @throws IllegalArgumentException | Arrays.stream(weights).anyMatch(g -> !isValidGene(g))
     * @inspects | weights
     * @post | IntStream.range(0, Constants.CHROM_SIZE).allMatch(i -> weights[i] == this.getGene(i))
     */
	public Chromosome(int[] weights)
	{
		if (weights == null) {throw new IllegalArgumentException();}
		if (weights.length != Constants.CHROM_SIZE) { throw new IllegalArgumentException(); }
		if (Arrays.stream(weights).anyMatch(w -> !isValidGene(w))) { throw new IllegalArgumentException(); }
	    
		this.weights = weights;
	}

	/**
	 * Gives 1 randomly generated chromosome
	 * 
	 * @creates | result
	 * @post | result != null
	 */
	public static Chromosome createRandom()
    {
        int[] genes =
        		IntStream.generate(() -> RandomUtil.integer(Constants.GENE_MIN, Constants.GENE_MAX + 1))
        		.limit(Constants.CHROM_SIZE).toArray();

        return new Chromosome(genes);
    }

	/**
	 * Gives `count` randomly generated chromosomes
	 * 
	 * @pre | count > 0
	 * @creates | result
	 * @post | result != null
	 * @post | result.length == count
	 * @post | Arrays.stream(result).allMatch(c -> c != null)
	 */
    public static Chromosome[] createRandom(int count)
    {
        Chromosome[] result = new Chromosome[count];

        for ( int i = 0; i != count; ++i )
        {
            result[i] = createRandom();
        }

        return result;
    }
    
    /**
     * @post | result == (Constants.GENE_MIN <= gene && gene <= Constants.GENE_MAX)
     */
    public static boolean isValidGene(int gene)
    {
    	return Constants.GENE_MIN <= gene && gene <= Constants.GENE_MAX;
    }

    /**
     * @pre | 0 <= index && index < Constants.CHROM_SIZE
     * @post | isValidGene(result)
     */
    public int getGene(int index)
    {
    	return weights[index];
    }
    
    
    
    /**
     * LEGIT
     * 
     * A new version of crossover.
     */
    public Chromosome crossover2(Chromosome other) {
    	int[] offspringGenes = new int[Constants.CHROM_SIZE];
    	int sectionSize = 6;
    	boolean leftParent = true;
    	for (int i = 0 ; i < Constants.CHROM_SIZE / sectionSize ; i++) {
    		leftParent = RandomUtil.bool();
    		for (int j = 0 ; j < sectionSize ; j++) {
    			if (leftParent) { offspringGenes[ sectionSize * i + j] = weights[sectionSize * i + j]; }
    			else { offspringGenes[ sectionSize * i + j] = other.weights[sectionSize * i + j]; }
    		}
    	
    	}
    	return new Chromosome(offspringGenes);
    }
    
    /**
     * @pre | other != null
     * @pre | 0 <= index && index <= Constants.CHROM_SIZE
     * @inspects | other
     * @post | result == IntStream.range(0, index).allMatch(i -> getGene(i) == other.getGene(i))
     */
    public boolean matchesUntil(Chromosome other, int index)
    {
    	return IntStream.range(0, index).allMatch(i -> getGene(i) == other.getGene(i));
    }
    
    /**
     * @pre | other != null
     * @pre | 0 <= index && index <= Constants.CHROM_SIZE
     * @inspects | other
     * @post | result == IntStream.range(index, Constants.CHROM_SIZE).allMatch(i -> getGene(i) == other.getGene(i))
     */
    public boolean matchesFrom(Chromosome other, int index)
    {
    	return IntStream.range(index, Constants.CHROM_SIZE).allMatch(i -> getGene(i) == other.getGene(i));
    }

    /**
     * @pre | 0 <= index && index < Constants.CHROM_SIZE
     * @post the gene is modified with += delta if the modif. remains in bounds Constants.GENE_MIN, Constants.GENE_MAX
     * 	| implies(isValidGene(getGene(index) + delta), result.getGene(index) == getGene(index) + delta)
     * @post the gene remains unchanged if mutating it with delta causes it to become invalid
     *  | implies(!isValidGene(getGene(index) + delta), result.getGene(index) == getGene(index))
     * @post only the gene with the given index may change 
     *  | onlyDiffersAt(result, index)
     */
    public Chromosome mutate(int index, int delta)
    {

    	int[] res = new int[weights.length];
    	for (int i = 0 ; i < weights.length ; i++) {
    		res[i] = weights[i];
    	}
    	if (Constants.GENE_MIN <= weights[index] + delta && weights[index] + delta <= Constants.GENE_MAX)
    		res[index] += delta;
    	return new Chromosome(res);
    }

    /**
     * @post | IntStream.range(0, Constants.CHROM_SIZE).anyMatch(i -> onlyDiffersAt(result, i))
     */
    public Chromosome randomlyMutate()
    {
        var index = RandomUtil.integer(weights.length);
        var delta = RandomUtil.integer(-Constants.GENE_DELTA, Constants.GENE_DELTA);

        return mutate(index, delta);
    }
    
    /**
     * @pre | other != null
     * @pre | 0 <= index && index < Constants.CHROM_SIZE
     * @post | result == IntStream.range(0, Constants.CHROM_SIZE).allMatch(i -> implies(i != index, getGene(i) == other.getGene(i)))
     */
    public boolean onlyDiffersAt(Chromosome other, int index)
    {
    	return IntStream.range(0, Constants.CHROM_SIZE).allMatch(i -> implies(i != index, getGene(i) == other.getGene(i)));
    }
    
    
    /**
     * @inspects | other
     * @post | result == (other != null && IntStream.range(0, Constants.CHROM_SIZE).allMatch(i -> getGene(i) == other.getGene(i)))
     */
    public boolean isEqual(Chromosome other) {
    	boolean res = (other != null);
    	for (int i = 0 ; i < weights.length ; i ++) {
    		res = res && (weights[i] == other.getGene(i));
    	}
    	return res;
    }
}
