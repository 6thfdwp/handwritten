package bitmap;

import java.util.Random;

import machl.NN2;

public class MLNNClassifier extends LetterClassifier {
	  private static String name="MLNN Classifier";
	  private NN2 nn=null;
	  private Random rand;
	  private double[][] targets=null;
	  
	  public MLNNClassifier(int nRows, int nCols, int nHidden) {
		    rand=new Random(System.currentTimeMillis());
		    nn=new NN2( nRows*nCols, getClassCount(), nHidden, rand.nextInt() );
		    targets=new double[getClassCount()][getClassCount()];
		    for (int c=0; c<getClassCount(); c++)
		      targets[c][c]=1;
	  }
	  
	  public String getName() {
		  return this.name;
	  }
	  public double[] test(Bitmap map) {
		  return nn.feedforward( map.toDoubleArray() );
	  }
	  public void train(ClassifiedBitmap[] maps, int nPresentations, double eta) {
		  for (int p=0; p<nPresentations; p++) {
			  int sample=rand.nextInt(maps.length);
			  nn.train( ((Bitmap)maps[sample]).toDoubleArray(), targets[maps[sample].getTarget()], eta);
		  }
	  }
}
