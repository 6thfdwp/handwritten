package bitmap;

import java.io.IOException;
import java.util.*;

public class ID3Tester {
	public HashSet<Integer> trainSet; // randomly selected file number (1-10) set for training
	public HashSet<Integer> testSet;
	public ArrayList<Double> proportions;
	public ArrayList<Integer> samples;
	public Random  rand;

//	public ID3Classifier c;
	public ID3Tester() {
		trainSet = new HashSet<Integer> ();
		testSet = new HashSet<Integer> ();
		rand = new Random(System.currentTimeMillis());
		this.getProportions();
		this.getSamples();
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
	public void train(String cFile, double proportionThresh, int samplesThresh) {
		ID3Classifier c=new ID3Classifier(32, 32);
		List<ClassifiedBitmap> list = this.loadBitmaps( trainSet );
		ClassifiedBitmap[] bitmaps = list.toArray( new ClassifiedBitmap[list.size()] ) ;
		c.train( bitmaps, proportionThresh, samplesThresh);
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
            System.out.format("# errors %d\n", numErrs );
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
	
	public void getProportions() {
		ArrayList<Double> p = new ArrayList<Double> ();
		for (double r=0.5; r<=0.8; r+=0.05) {
			p.add( r );
		}
		proportions = p;
//		System.out.println(proportions);
	}
	public void getSamples() {
		ArrayList<Integer> s = new ArrayList<Integer> ();
		for (int r=5; r<=10; r++) {
			s.add( r );
		}
		samples = s;
//		System.out.println(samples);
	}
	public void run() {
		for ( double proportion : proportions ) {
			//if (i == 1) break;
			for ( int sample : samples ) {
				String cFile = String.format("id3_%.2f_%d.ser", proportion, sample);
				this.train(cFile, proportion, sample);
				double errRate = this.evaluate(cFile);
				System.out.format("id3 parameters:%.2f %d error:%f\n", proportion, sample, errRate);
			}
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ID3Tester tester = new ID3Tester();
		tester.split(5);
//		List list = tester.loadBitmaps(tester.trainSet);
//		System.out.println(list.size());
//		tester.run();
		for (int i=0; i<1; i++) {
			tester.run();
		}
	}

}
