package bitmap;

import java.io.IOException;
import java.util.*;

public class CrossValidator {

	private class MLNNModel {
		
		private class Para {
			public int nHidden;
			public int presentation;
			public double learnRate = 0.1;
			public Para(int h, int p) {
				nHidden = h;
				presentation = p;
			}
			public String toString() {
				String s = String.format("%d,%d,%.2f", nHidden, presentation, learnRate);
				return s;
			}
			public boolean equals(Object o) {
				if ( ! (o instanceof Para) ) 
					return false;
				Para other = (Para) o;
				return (other.nHidden == this.nHidden 
						&& other.presentation == this.presentation && other.learnRate == this.learnRate);
			}
			public int hashcode() {
				int hashcode = 0;
				hashcode += new Integer(nHidden).hashCode();
				hashcode += hashcode * 31 + new Integer(presentation).hashCode();
				hashcode += new Double(learnRate).hashCode();
				return hashcode;
			}
		}
		public ArrayList<Integer> hiddens;
		public ArrayList<Integer> presentations;
		public List<Para> models = new ArrayList<Para> ();
		public int index = 0;
		
		public MLNNModel() {
			this.getHiddens();
			this.getPresentations();
			for (int nHidden : hiddens) {
				for (int present : presentations) {
					models.add( new Para(nHidden, present) );
				}
			}
		}
		public void getHiddens() {
			ArrayList<Integer> hiddens = new ArrayList<Integer> ();
			for (int r=10; r<=30; r+=10) {
				hiddens.add( r );
			}
			this.hiddens = hiddens;
		}
		public void getPresentations() {
			ArrayList<Integer> presents = new ArrayList<Integer> ();
			for (int r=100000; r<=160000; r+=160000) {
				presents.add( r );
			}
			presentations = presents;
		}
//		public void getModels() {
//			
//		}
		public Para select() {
			return models.get( index++ );
		}
	}
	/************************ end of MLNNModel **********************/
	
	public Random rand;
	public HashSet<Integer> fileset = new HashSet<Integer> ();
	public HashSet<Integer> trainFileset = new HashSet<Integer> ();
	public HashSet<Integer> validateFileset = new HashSet<Integer> ();
	public List<Map.Entry<MLNNModel.Para, Double>> errT = new ArrayList<Map.Entry<MLNNModel.Para, Double>> ();
	public List<Map.Entry<MLNNModel.Para, Double>> errV = new ArrayList<Map.Entry<MLNNModel.Para, Double>> ();
//	public HashMap<MLNNModel.Para, Double> validateErr = new HashMap<MLNNModel.Para, Double> ();;
	public MLNNModel mlModel;
	public CrossValidator() {
		rand = new Random(System.currentTimeMillis());
		fileset = this.split( 9 );
		mlModel = new MLNNModel();
//		System.out.println( mlModel.models );
	}
	public void partition( HashSet<Integer> set, int fold) {
			validateFileset.clear();
			trainFileset.clear();
			
			validateFileset.add( fold );
			for (int j : set) {
				if ( fold != j)
					trainFileset.add( j );
			}
//			System.out.println(trainFileset);
//			System.out.println(validateFileset);
	}
	public String  train(ClassifiedBitmap[] bitmaps, MLNNModel.Para p) {
		MLNNClassifier c = new MLNNClassifier(32, 32, p.nHidden);
		c.train(bitmaps, p.presentation, p.learnRate);
		String cFile = String.format("mlnn_%s.ser", p.toString());
		try {
			Classifier.save(c, cFile);
		} catch (Exception ex) {
			System.err.println("Failed to serialize and save file: "+ex.getMessage());
		}
		return cFile;
	}
	public double validate(String cFile, ClassifiedBitmap[] bitmaps) {
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
            for (int i=0; i<bitmaps.length; i++) {
            	int actual=c.index( (Bitmap)bitmaps[i] );
            	int target=bitmaps[i].getTarget();
            	if ( actual != target) {
            		numErrs += 1;
            	}
            }
            errRate = numErrs / (double)bitmaps.length;
	    }
        return errRate;
	}
	public void run() {
		for (MLNNModel.Para p : this.mlModel.models) {
			double sumTrainerr = 0.0;
			double sumValidateerr = 0.0;
			System.out.format("%s ", p);
			for (int fold : fileset) {
				this.partition( fileset, fold );
				ClassifiedBitmap[] trainBitmaps = this.loadBitmaps( trainFileset );
				ClassifiedBitmap[] validateBitmaps = this.loadBitmaps( validateFileset );
//				System.out.format("%d / %d\n", trainBitmaps.length, validateBitmaps.length);
				String cFile = this.train(trainBitmaps, p);
				
				double errRateT = this.validate(cFile, trainBitmaps);
				sumTrainerr += errRateT;
//				System.out.format(" train: %f\n", fold, p.nHidden, p.presentation, errRateT);
				double errRateV = this.validate(cFile, validateBitmaps);
				System.out.format(" %f ", errRateV);
				sumValidateerr += errRateV;
			}
			errT.add( new AbstractMap.SimpleEntry<MLNNModel.Para, Double> ( p, sumTrainerr/fileset.size() ));
			System.out.format(" %f ", sumTrainerr/fileset.size() );
			errV.add( new AbstractMap.SimpleEntry<MLNNModel.Para, Double> ( p, sumValidateerr/fileset.size() ));
			System.out.format(" %f \n", sumValidateerr/fileset.size() );
		}
   }
	
	public HashSet<Integer> split(int numPart) {
		HashSet<Integer> trainSet = new HashSet<Integer> ();
		HashSet<Integer> testSet = new HashSet<Integer> ();
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
		return trainSet;
	}
	public HashSet<Integer> splitRandom(int numPart) {
		HashSet<Integer> trainSet = new HashSet<Integer> ();
		HashSet<Integer> testSet = new HashSet<Integer> ();
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
		return trainSet;
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
//		return result;
	}
	public List<Map.Entry<MLNNModel.Para, Double>> sort( HashMap<MLNNModel.Para, Double> map) {
		List<Map.Entry<MLNNModel.Para, Double>> list = new ArrayList<Map.Entry<MLNNModel.Para, Double>> (map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<MLNNModel.Para, Double>>() {
			public int compare(Map.Entry<MLNNModel.Para, Double> o1, Map.Entry<MLNNModel.Para, Double> o2) {
				return (o2.getValue()).compareTo( o1.getValue() );
			}
		});
		return list;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CrossValidator cv = new CrossValidator();
		
		cv.run();
	}

}
