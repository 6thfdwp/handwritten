package bitmap;

import java.io.IOException;
import java.util.*;

public class NNTester {
	public HashSet<Integer> trainSet; // randomly selected file number (1-10) set for training
	public HashSet<Integer> testSet;
	public ArrayList<Double> learnRates;
	public ArrayList<Integer> presentations;
	public Random  rand;

//	public ID3Classifier c;
	public NNTester() {
		trainSet = new HashSet<Integer> ();
		testSet = new HashSet<Integer> ();
		rand = new Random(System.currentTimeMillis());
		this.getRates();
		this.getPresentations();
	}
	
	public List<ClassifiedBitmap> loadBitmaps(HashSet<Integer> fileset) {
		List<ClassifiedBitmap> result = new ArrayList<ClassifiedBitmap> ();
		for (Integer i : fileset) {
			String trainFile = "dataset/" + i.toString() + ".txt";
			try {
				ClassifiedBitmap[] bitmaps=LetterClassifier.loadLetters( trainFile );
				List<ClassifiedBitmap> cur = Arrays.asList( bitmaps );
				result.addAll( cur );
			} catch (IOException ex) {
				System.err.println("Error loading data.txt: "+ex.getMessage());
			}
		}
		return result;
	}
	public void train(String cFile, int present, double rate ) {
		NNClassifier c=new NNClassifier(32, 32);
		List<ClassifiedBitmap> list = this.loadBitmaps( trainSet );
		ClassifiedBitmap[] bitmaps = list.toArray( new ClassifiedBitmap[list.size()] ) ;
		c.train( bitmaps, present, rate);
		System.out.format("\ntraining size %d\n", bitmaps.length );
		try {
			Classifier.save(c, cFile);
		} catch (Exception ex) {
			System.err.println("Failed to serialize and save file: "+ex.getMessage());
		}
	}
	public double evaluate(String cFile) {
	    Classifier c=null;
	    Double errRate = null;
	    try {
	      c=Classifier.load(cFile);
	    } catch (IOException ex) {
	      System.err.println("Load of classifier failed: "+ex.getMessage());
	      System.exit(2);
	    } catch (ClassNotFoundException ex) {
	      System.err.println("Loaded classifier does not match available classes: "+ex.getMessage());
	      System.exit(3);
	    }
	    if ( c != null ) {
	    	List<ClassifiedBitmap> list = this.loadBitmaps(testSet);
	    	ClassifiedBitmap[] bitmaps = list.toArray( new ClassifiedBitmap[list.size()] );
	    	System.out.format("evaluating size %d\n", bitmaps.length );
            int numErrs = 0;
            for (int i=0; i<bitmaps.length; i++) {
            	int actual=c.index((Bitmap)bitmaps[i]);
            	int target=bitmaps[i].getTarget();
            	if ( actual != target) {
            		numErrs += 1;
            	}
            }
            errRate = numErrs / (double)bitmaps.length;
            System.out.format("#errors %d\n", numErrs );
	    }
        return errRate;
	}
	public void split(int numPart) {
		trainSet.clear();
		testSet.clear();
		for ( int p=0; p<numPart; p++) {
			int idx = rand.nextInt(10);
			while ( trainSet.contains( idx+1 ) ) {
				idx = rand.nextInt(10);
			}
			trainSet.add( idx+1 );
		}
		for ( int i=0; i<10; i++) {
			if ( !trainSet.contains(i+1) ) {
				testSet.add( i+1 );
			}
		}
		System.out.println(trainSet);
		System.out.println(testSet);
	}
	public void getRates() {
		ArrayList<Double> rates = new ArrayList<Double> ();
		for (double r=0.1; r<=0.6; r+=0.1) {
			rates.add( r );
		}
		learnRates = rates;
//		System.out.println(rates);
	}
	public void getPresentations() {
		ArrayList<Integer> presents = new ArrayList<Integer> ();
		for (int r=100000; r<=256000; r+=50000) {
			presents.add( r );
		}
		presentations = presents;
//		System.out.println(presents);
	}
	public void run() {
//		this.split(5);
		for ( int present : presentations ) {
			for ( double rate : learnRates ) {
				String cFile = String.format("nn_%d_%.2f.ser", present, rate);
				this.train(cFile, present, rate);
				double errRate = this.evaluate(cFile);
				System.out.format("nn parameters: %d %.2f error:%f\n", present, rate, errRate);
			}			
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NNTester tester = new NNTester();
//		tester.getRates();
//		tester.getPresentations();
		tester.split(1);
//		List list = tester.loadBitmaps(tester.trainSet);
//		System.out.println(list.size());
//		tester.run();
		for (int i=0; i<1; i++) {
			tester.run();
		}
	}

}
