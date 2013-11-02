package machl;

import java.io.Serializable;
import java.util.Random;

public class NN2 implements Serializable {

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

		  hy = new double[nHidden];
		  hw = new double[nOutput][nHidden];
		  hbias = new double[nOutput];
		  
		  rand = new Random(seed);
		  for (int j=0; j<nHidden; j++) {
			  for (int i=0; i<nInput; i++) {
				  iw[j][i] = rand.nextGaussian() * .1;
			  }
			  ibias[j] = rand.nextGaussian() * .1;
		  }
		  for (int k=0; k<nOutput; k++) {
			  for (int j=0; j<nHidden; j++) {
				  hw[k][j]=rand.nextGaussian() * .1;
			  }
			  hbias[k]=rand.nextGaussian() * .1;
		  }
	  }
	  
	  public double outputFunction(double net) {
		    return 1.0/(1.0+Math.exp(-net));
	  }
	  
	  public double outputFunctionDerivative(double x) {
		    return x*(1.0-x);
	  }
	  
	  public double[] feedforward(double[] x) {
		  for (int j=0; j<hy.length; j++) {
			  double sum = 0.0;
			  for (int i=0; i<x.length; i++) {
				  sum += x[i] * iw[j][i];
			  }
			  hy[j] = outputFunction( sum + ibias[j] );
		  }
		  for (int k=0; k<y.length; k++) {
			  double sum = 0.0;
			  for (int j=0; j<hy.length; j++) {
				  sum += hy[j] * hw[k][j];
			  }
			  y[k] = outputFunction( sum + hbias[k] );
		  }
		  return y;
	  }
	  
	  public void train(double[] x, double[] d, double eta) {
		  feedforward( x );
		  
		  double deltaOutput[] = new double[ y.length ];
		  for (int k=0; k<y.length; k++) {
			  double diff = d[k] - y[k];
			  deltaOutput[k] = outputFunctionDerivative( y[k] ) * diff;
		  }
		  // adjust hw and hbias and back propagate to adjust iw
		  this.adjustHiddenWeight(deltaOutput, eta);
		  
		  double deltaHidden[] = new double[ hy.length ];
		  for (int j=0; j<hy.length; j++) {
			  double sum = 0.0;
			  for (int k=0; k<y.length; k++) {
				  sum += hw[k][j] * deltaOutput[k];
			  }
			  deltaHidden[j] = outputFunctionDerivative( hy[j] ) * sum;
		  }
		  this.adjustInputWeight(x, deltaHidden, eta);
	  }
	  
	  public void adjustHiddenWeight(double[] deltaOutput, double eta) {
		  for (int k=0; k<y.length; k++) {
			  for (int j=0; j<hy.length; j++) {
				  hw[k][j] += deltaOutput[k] * hy[j] * eta;
			  }
			  hbias[k] += deltaOutput[k] * 1.0 * eta;
		  }
	  }
	  public void adjustInputWeight(double[] x, double[] deltaHidden, double eta) {
		  for (int j=0; j<hy.length; j++) {
			  for (int i=0; i<x.length; i++) {
				  iw[j][i] += deltaHidden[j] * x[i] * eta;
			  }
			  ibias[j] += deltaHidden[j] * 1.0 * eta;
		  }
	  }
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
