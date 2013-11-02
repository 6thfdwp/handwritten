package bitmap;

import java.io.IOException;
import java.util.*;

public class MLNNTester {

		public HashSet<Integer> trainSet; // randomly selected file number (1-10) set for training
		public HashSet<Integer> testSet;
		public ClassifiedBitmap[] trainBitmaps;
		public ClassifiedBitmap[] testBitmaps;
		public ArrayList<Double> learnRates = new ArrayList<Double> ();
		public ArrayList<Integer> presentations = new ArrayList<Integer> ();
		public Random  rand;

//		public ID3Classifier c;
		public MLNNTester() {
			trainSet = new HashSet<Integer> ();
			testSet = new HashSet<Integer> ();
			rand = new Random(System.currentTimeMillis());
//			learnRates.add( 0.2 );
//			presentations.add( 160000 );
			this.getRates();
			this.getPresentations();
		}
		
		public ClassifiedBitmap[] loadBitmaps(HashSet<Integer> fileset) {
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
			ClassifiedBitmap[] bitmaps = result.toArray( new ClassifiedBitmap[result.size()] ) ;
			return bitmaps;
//			return result;
		}
		public void train(String cFile, int nHidden, int present, double rate ) {
			MLNNClassifier c = new MLNNClassifier(32, 32, nHidden);
			c.train(trainBitmaps, present, rate);
			try {
				Classifier.save(c, cFile);
			} catch (Exception ex) {
				System.err.println("Failed to serialize and save file: "+ex.getMessage());
			}
		}
		public double validate(String cFile) {
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
//	            System.out.format("#errors %d\n", numErrs );
		    }
	        return errRate;
		}
		
		public void crossValidate( HashSet<Integer> set ) {
			HashSet<Integer> trainFileset = new HashSet<Integer> ();
			HashSet<Integer> validateFileset = new HashSet<Integer> ();
			for (int i : set) {
				validateFileset.clear();
				trainFileset.clear();
				validateFileset.add( i );
				for (int j : set) {
					if ( i != j)
						trainFileset.add( j );
				}
				this.loadBitmaps( trainFileset );
				this.loadBitmaps(validateFileset);
				System.out.println(trainFileset);
				System.out.println(validateFileset);
			}
		}
		public void split(int numPart) {
			trainSet.clear();
			testSet.clear();
			for ( int p=0; p<numPart; p++) {
				trainSet.add( p+1 );
			}
			for ( int i=0; i<10; i++) {
				if ( !trainSet.contains(i+1) ) {
					testSet.add( i+1 );
				}
			}
			System.out.println(trainSet);
			System.out.println(testSet);
			trainBitmaps = this.loadBitmaps( trainSet );
			testBitmaps = this.loadBitmaps( testSet );
		}
		public void splitRandom(int numPart) {
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
			trainBitmaps = this.loadBitmaps( trainSet );
			testBitmaps = this.loadBitmaps( testSet );
		}
		public void getRates() {
			ArrayList<Double> rates = new ArrayList<Double> ();
			for (double r=0.1; r<0.3; r+=0.1) {
				rates.add( r );
			}
			learnRates = rates;
//			System.out.println(rates);
		}
		public void getPresentations() {
			ArrayList<Integer> presents = new ArrayList<Integer> ();
			for (int r=150000; r<=150000; r++) {
				presents.add( r );
			}
//			for (int r=5000; r<=320000; r*=2) {
//				presents.add( r );
//			}
			presentations = presents;
//			System.out.println(presents);
		}

		public void run(int numPart) {
			this.split(numPart);
//			this.splitRandom(numPart);
			System.out.format("%d / %d\n", trainBitmaps.length, testBitmaps.length);
			int[] nHiddens = {10, 20, 30, 40};
			for (int i=0; i<nHiddens.length; i++) {
				int nHidden = nHiddens[i];
				for ( int present : presentations ) {
					for ( double rate : learnRates ) {
						//for ( int i=0; i<5; i++) {}
						String cFile = String.format("mlnn%d_%d_%.2f.ser", nHidden, present, rate);
						Date start = new Date();
						this.train(cFile, nHidden, present, rate);
						Date end = new Date();
						Long trainTime = end.getTime() - start.getTime();

						double errRate = this.validate(cFile);

						System.out.format("%d,%d,%f  %f  %.2f\n", nHidden, present, rate, errRate, (double)trainTime/1000);
					}			
				}
			}
		}
		/**
		 * @param args
		 */
		public static void main(String[] args) {
			MLNNTester tester = new MLNNTester();
//			tester.split( 9 );
//			tester.crossValidate( tester.trainSet );
			tester.run(6);
//			tester.getRates();
//			tester.getPresentations();
//			tester.split(5);
//			List list = tester.loadBitmaps(tester.trainSet);
//			System.out.println(list.size());
//			tester.run();
//			for (int i=0; i<1; i++) {
//				tester.run(9);
//			}
		}
}
