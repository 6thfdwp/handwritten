package machl;

import java.util.*;
import java.io.*;
import java.lang.reflect.Array;

/**
 * <p>BinTree is a class for storing and generating binary trees (of nodes).</p>
 * @author Mikael Bod�n
 * @version 1.0
 */

/**
 * 27/09/13 - Added informationGain attribute to tree.
 */

public class BinTree implements Serializable {
  public BinTree[] subtrees=null;
  public String label=null;
  public String classValue=null;
  public double informationGain = 0;
  public double [] distribution = null;

  /** Constructs a tree node and attaches a classification label.
   * Intended to be a terminating node.
   * @param classValue the class label
   */
  public BinTree(String classValue, int classes) {
    this.classValue=classValue;
    distribution = new double [classes];
  }

  /** Constructs the tree and labels it with a feature and attaches two subtrees \
   * (one for feature=true and one for feature=false).
   *  @param  label  a string which identifies which feature that is tested at the node.
   *  @param  childTrue a subtree with further decisions to be made for all samples mathich feature=true
   *  @param  childFalse a subtree with further decisions to be made for all samples mathich feature=false
   */
  public BinTree(String label, BinTree childTrue, BinTree childFalse, int classes) {
    this.label=label;
    classValue=null;
    subtrees=new BinTree[2];
    subtrees[0]=childTrue;
    subtrees[1]=childFalse;
    distribution = new double[classes];
  }

  /** Determine the classification of a tuple
   * @param labels an array holding the labels of the features
   * @param value an array holding all the values of the sample to be classified according to the tree (same order as the labels above)
   * @return the classification label found at the node identified by this sample
   */
   public String getClassification(String[] labels, boolean[] value) {
     if (subtrees!=null) {
       for (int f=0; f<labels.length; f++) {
         if (label.equals(labels[f])) {
           return subtrees[value[f]?0:1].getClassification(labels, value);
         }
       }
       return null;
     } else
       return classValue;
   }
 }