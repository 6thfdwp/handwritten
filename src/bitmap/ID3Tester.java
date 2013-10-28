package bitmap;

import java.io.IOException;
import java.util.*;

public class ID3Tester {
	public String[] trainFiles = {"train50.txt", "train60.txt"};
	public String[] testFiles = {"test50.txt", ""};
	public HashSet<Integer> trainSet; // randomly selected file number (1-10) set for training
	public HashSet<Integer> testSet;
	public double[] proportions = {0.5, 0.55, 0.6};
	public int[] samples = {5, 5, 6};
	public Random  rand;

//	public ID3Classifier c;
	public ID3Tester() {
		trainSet = new HashSet<Integer> ();
		testSet = new HashSet<Integer> ();
		rand = new Random(System.currentTimeMillis());
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
//		try {
//
//			ClassifiedBitmap[] bitmaps=LetterClassifier.loadLetters( trainFile );
//
//			c.train(bitmaps, proportionThresh, samplesThresh);
//		} catch (IOException ex) {
//			System.err.println("Error loading data.txt: "+ex.getMessage());
//		}
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
	public void run() {
		this.split(6);
		
		for ( int i=0; i<proportions.length; i++) {
//			if (i == 1) break;
			double proportion = proportions[i];
			int sample = samples[i];
			String cFile = "id3_" + proportion + "_" + sample + ".ser";
			this.train(cFile, proportion, sample);
			double errRate = this.evaluate(cFile);
			System.out.format("id3 parameters:%.2f %d error:%f\n", proportion, sample, errRate);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ID3Tester tester = new ID3Tester();
//		tester.collect(5);
//		List list = tester.loadBitmaps(tester.trainSet);
//		System.out.println(list.size());
//		tester.run();
		for (int i=0; i<2; i++) {
			tester.run();
		}
	}

}
