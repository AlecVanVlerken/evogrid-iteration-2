package sim;

/**
 * @immutable
 * LEGIT
 * 
 * A class gathering constants
 */
public class Constants {
	
	private Constants() {}
	
	//DNA related
	public static int CHROM_SIZE = 24;
	public static int GENE_MIN = -1000;
	public static int GENE_MAX = 1000; //min and max should be different.
	public static int GENE_DELTA = 200; //typical "mutation" value. should be < (GENE_MAX - GENE_MIN)/2
	public static int MUT_RATE = 20; // Mutation rate. 0<= MUT_RATE <= 100
	
	
	//general purpose numbers
	public static int WSIZE = 240; //length of side of (square) world
	public static int POPU_SIZE = 350;
	
	
	public final static int DEFAULT_FRAME_RATE = 225;
	public final static int GENERATION_TICKS = 900; // about 4 seconds at the default playback target
	
	

}
