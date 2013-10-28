package machl;

import java.util.Random;

public class NN2 {

	  double[] y;
	  // weights from input to hidden layer
	  double[][] iw;
	  double[] ibias;
	  // output from each node of hidden layer
	  double[] hy;
	  // weights from hidden to output layer
	  double[][] hw;
	  double[] hbias;
	  Random rand;  
	  
	  public NN2(int nInput, int nOutput, int nHidden, int seed) {
		  y = new double[nOutput];
		  iw = new double[nHidden][nInput];
		  ibias = new double[nHidden];
		  
		  hw = new double[nOutput][nHidden];
		  hbias = new double[nOutput];
		  hy = new double[nHidden];
		  
		  rand = new Random(seed); 
	  }
	  
	  public double outputFunction(double net) {
		    return 1.0/(1.0+Math.exp(-net));
	  }
	  
	  public double outputFunctionDerivative(double x) {
		    return x*(1.0-x);
	  }
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
