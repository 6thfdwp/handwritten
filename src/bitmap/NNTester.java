package bitmap;

import java.io.IOException;
import java.util.*;

public class NNTester {
	public HashSet<Integer> trainSet; // randomly selected file number (1-10) set for training
	public HashSet<Integer> testSet;
	public ClassifiedBitmap[] trainBitmaps;
	public ClassifiedBitmap[] testBitmaps;
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
		c.train(trainBitmaps, present, rate);
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
            int numErrs = 0;
            for (int i=0; i<testBitmaps.length; i++) {
            	int actual=c.index( (Bitmap)testBitmaps[i] );
            	int target=testBitmaps[i].getTarget();
            	if ( actual != target) {
            		numErrs += 1;
            	}
            }
            errRate = numErrs / (double)testBitmaps.length;
//            System.out.format("#errors %d\n", numErrs );
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
		this.loadBitmaps(trainSet);
		List<ClassifiedBitmap> list1 = this.loadBitmaps( trainSet );
		trainBitmaps = list1.toArray( new ClassifiedBitmap[list1.size()] ) ;

		this.loadBitmaps(testSet);
		List<ClassifiedBitmap> list2 = this.loadBitmaps( testSet );
		testBitmaps = list2.toArray( new ClassifiedBitmap[list2.size()] ) ;

	}
	public void getRates() {
		ArrayList<Double> rates = new ArrayList<Double> ();
		for (double r=0.01; r<1; r+=0.05) {
			rates.add( r );
		}
		learnRates = rates;
//		System.out.println(rates);
	}
	public void getPresentations() {
		ArrayList<Integer> presents = new ArrayList<Integer> ();
		
		for (int r=5000; r<=320000; r*=2) {
			presents.add( r );
		}
		presentations = presents;
//		System.out.println(presents);
	}

	public void run(int numPart) {
		this.split(numPart);
		System.out.format("%d / %d\n", trainBitmaps.length, testBitmaps.length);
		for ( int present : presentations ) {
			for ( double rate : learnRates ) {
//				for ( int i=0; i<5; i++) {}
				String cFile = String.format("nn_%d_%.2f.ser", present, rate);
				Date start = new Date();
				this.train(cFile, present, rate);
				Date end = new Date();
				Long trainTime = end.getTime() - start.getTime();
				
//				start = new Date();
				double errRate = this.evaluate(cFile);
				
				System.out.format("%d,%f  %f  %d\n",present, rate, errRate, trainTime);
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
//		tester.split(5);
//		List list = tester.loadBitmaps(tester.trainSet);
//		System.out.println(list.size());
//		tester.run();
//		for (int i=0; i<1; i++) {
			tester.run(9);
//		}
	}

}
