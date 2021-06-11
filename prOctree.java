import java.util.ArrayList;


abstract class prOctNode< T extends Compare3D<? super T> > {

}
class prOctLeaf< T extends Compare3D<? super T> > extends prOctNode {

	public prOctLeaf() {
		Elements = new ArrayList<T>();
	}

	public prOctLeaf(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		Elements = new ArrayList<T>();

		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
	}

	public float xMin, xMax, yMin, yMax, zMin, zMax;
	public ArrayList<T>  Elements;
}
class prOctInternal< T extends Compare3D<? super T> > extends prOctNode {

	public prOctInternal() {

	}

	public prOctInternal(float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
	}

	public float xMin, xMax, yMin, yMax, zMin, zMax;
	public prOctNode NWF, SWF, SEF, NEF, NWB, SWB, SEB, NEB;
}


public class prOctree< T extends Compare3D<? super T> > {

   prOctNode root; //Root node of prOctree
   int size = 0; //Number of elements in prOctree
   long xMin, xMax, yMin, yMax, zMin, zMax; //Boundaries for the prOctree
   int bucketSize = 1; //Bucket size for each leaf node in the prOctree

   public prOctLeaf<T> Leaf; //Empty reference leaf node for comparison
   public prOctInternal<T> Internal; //Empty reference internal node for comparison

   boolean insertResult; // Boolean to store insert result

   // Initialize octree to empty state.
   // Pre: xMin < xMax and yMin < yMax and zMin < zMax
   public prOctree(long xMin, long xMax, long yMin, long yMax, long zMin, long zMax) {
	   this.xMin = xMin;
	   this.xMax = xMax;
	   this.yMin = yMin;
	   this.yMax = yMax;
		 this.zMin = zMin;
		 this.zMax = zMax;

	   Leaf = new prOctLeaf();
	   Internal = new prOctInternal();
   }

   public prOctNode getRoot() {
	   return root;
   }
   public int getSize() {
     return size;
   }

   // Pre:   elem != null
   // Post:  If elem lies within the tree's region, and elem is not already
   //		present in the tree, elem has been inserted into the tree.
   // Return true iff elem is inserted into the tree.
   public boolean insert(T elem) {
	   if(elem.inBox(xMin, xMax, yMin, yMax, zMin, zMax)) {
		   insertResult = true;
		   root = insert(elem, root, xMin, xMax, yMin, yMax, zMin, zMax); //Call recursive helper function
       if(insertResult){
         size++;
         return true;
       }
       return false;
	   }
	   else {
		   return false; //Return false if out of bounds
	   }
   }

   // Pre: elem != null and is within the prOctree bounds
   //		sRoot is the current node within the prOctree
   //		xLo, xHi, yLo, yHi, zLo, zHi are the boundaries of the current node
   // Post: Elem is recursively inserted into tree as long as it isn't a duplicate.
   // Return true if inserted into the tree and false if a duplicate.
   // Recursive helper for public insert function
   private prOctNode insert(T elem, prOctNode sRoot, float xLo, float xHi, float yLo, float yHi, float zLo, float zHi) {
	   if(sRoot == null) { //sRoot is an abstract prOctNode and needs to be made into a leaf
		   prOctLeaf currentNode = new prOctLeaf<T>(xLo, xHi, yLo, yHi, zLo, zHi);
		   currentNode.Elements.add(elem);
		   return currentNode;
	   }
	   else if(sRoot.getClass().equals(Leaf.getClass())) { //sRoot is leaf
		   prOctLeaf currentNode = (prOctLeaf) sRoot; 

       // Check if element is already in octree
       for(int i=0; i<currentNode.Elements.size(); i++) {
           if(elem.equals(currentNode.Elements.get(i))) {
             insertResult = false;
             return currentNode;
           }
       }
       
       // Check if leaf is full
		   if(currentNode.Elements.size() < bucketSize) { //Leaf is less than bucket size			   
         currentNode.Elements.add(elem);
			   return currentNode;
		   }
		   else { //Leaf is full
			   //Convert leaf node to internal node and add leaf node elements
			   prOctInternal newNode = new prOctInternal<T>(xLo, xHi, yLo, yHi, zLo, zHi);
			   for(int i=0; i<bucketSize; i++) {
				   T existingElem = (T) currentNode.Elements.get(i);

				   if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEF) { //NEF Quadrant
					   newNode.NEF = insert(existingElem, newNode.NEF, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
				   }
				   else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWF) { //NWF Quadrant
					   newNode.NWF = insert(existingElem, newNode.NWF, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
				   }
				   else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWF) { //SWF Quadrant
					   newNode.SWF = insert(existingElem, newNode.SWF, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
				   }
				   else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEF) { //SEF Quadrant
					   newNode.SEF = insert(existingElem, newNode.SEF, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
				   }
					 else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEB) { //NEB Quadrant
					   newNode.NEB = insert(existingElem, newNode.NEB, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
				   }
				   else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWB) { //NWB Quadrant
					   newNode.NWB = insert(existingElem, newNode.NWB, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
				   }
				   else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWB) { //SWB Quadrant
					   newNode.SWB = insert(existingElem, newNode.SWB, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
				   }
				   else if(existingElem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEB) { //SEB Quadrant
					   newNode.SEB = insert(existingElem, newNode.SEB, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
				   }
			   }

			   // Add the element
			   if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEF) { //NEF Quadrant
				   newNode.NEF = insert(elem, newNode.NEF, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
			   }
			   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWF) { //NWF Quadrant
				   newNode.NWF = insert(elem, newNode.NWF, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
			   }
			   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWF) { //SWF Quadrant
				   newNode.SWF = insert(elem, newNode.SWF, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
			   }
			   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEF) { //SEF Quadrant
				   newNode.SEF = insert(elem, newNode.SEF, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
			   }
				 else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEB) { //NEB Quadrant
				   newNode.NEB = insert(elem, newNode.NEB, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
			   }
			   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWB) { //NWB Quadrant
				   newNode.NWB = insert(elem, newNode.NWB, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
			   }
			   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWB) { //SWB Quadrant
				   newNode.SWB = insert(elem, newNode.SWB, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
			   }
			   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEB) { //SEB Quadrant
				   newNode.SEB = insert(elem, newNode.SEB, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
			   }

			   return newNode;
			}
	   }
	   else if(sRoot.getClass().equals(Internal.getClass())){ //sRoot is internal node
		   prOctInternal currentNode = (prOctInternal) sRoot;
		   if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEF) { //NEF Quadrant
			   currentNode.NEF = insert(elem, currentNode.NEF, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWF) { //NWF Quadrant
			   currentNode.NWF = insert(elem, currentNode.NWF, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWF) { //SWF Quadrant
			   currentNode.SWF = insert(elem, currentNode.SWF, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEF) { //SEF Quadrant
			   currentNode.SEF = insert(elem, currentNode.SEF, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
		   }
			 else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEB) { //NEB Quadrant
			   currentNode.NEB = insert(elem, currentNode.NEB, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWB) { //NWB Quadrant
			   currentNode.NWB = insert(elem, currentNode.NWB, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWB) { //SWB Quadrant
			   currentNode.SWB = insert(elem, currentNode.SWB, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEB) { //SEB Quadrant
			   currentNode.SEB = insert(elem, currentNode.SEB, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
		   }
		   return currentNode;
	   }
	   else { //Error
		   return sRoot;
	   }
   }

   // Pre:  elem != null
   // Returns reference to an element x within the tree such that elem.equals(x)
   // is true, provided such a matching element occurs within the tree; returns
   // null otherwise.
   public T find(T Elem) {
	   if(Elem.inBox(xMin, xMax, yMin, yMax, zMin, zMax)) {
		   return find(Elem, root, xMin, xMax, yMin, yMax, zMin, zMax);
	   }
	   else {
		   return null;
	   }
   }

   // Pre: elem != null and is within the prOctree bounds
   //		sRoot is the current node within the prOctree
   //		xLo, xHi, yLo, yHi are the boundaries of the current node
   // Returns recursively a reference to an element x within the tree such that
   // elem.equals(x) is true, provided it exists; returns null otherwise.
   // Recursive helper for public find function
   private T find(T elem, prOctNode sRoot, float xLo, float xHi, float yLo, float yHi, float zLo, float zHi) {
	   if(sRoot == null) { //sRoot is an abstract prOctNode
		   return null;
	   }
	   else if(sRoot.getClass().equals(Leaf.getClass())) { //sRoot is leaf
		   prOctLeaf currentNode = (prOctLeaf) sRoot;
		   for(int i=0; i<bucketSize; i++) {
			   if(currentNode.Elements.get(i).equals(elem)) {
				   return (T) currentNode.Elements.get(i);
			   }
		   }
		   return null;
	   }
	   else if(sRoot.getClass().equals(Internal.getClass())){ //sRoot is internal node
		   prOctInternal currentNode = (prOctInternal) sRoot;
		   if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEF) { //NEF Quadrant
			   return find(elem, currentNode.NEF, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWF) { //NWF Quadrant
			   return find(elem, currentNode.NWF, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, (zHi-zLo)/2 + zLo, zHi);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWF) { //SWF Quadrant
			   return find(elem, currentNode.SWF, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEF) { //SEF Quadrant
			   return find(elem, currentNode.SEF, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, (zHi-zLo)/2 + zLo, zHi);
		   }
			 else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NEB) { //NEB Quadrant
			   return find(elem, currentNode.NEB, (xHi-xLo)/2 + xLo, xHi, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.NWB) { //NWB Quadrant
			   return find(elem, currentNode.NWB, xLo, (xHi-xLo)/2 + xLo, (yHi-yLo)/2 + yLo, yHi, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SWB) { //SWB Quadrant
			   return find(elem, currentNode.SWB, xLo, (xHi-xLo)/2 + xLo, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else if(elem.inQuadrant(xLo, xHi, yLo, yHi, zLo, zHi) == Direction3D.SEB) { //SEB Quadrant
			   return find(elem, currentNode.SEB, (xHi-xLo)/2 + xLo, xHi, yLo, (yHi-yLo)/2 + yLo, zLo, (zHi-zLo)/2 + zLo);
		   }
		   else {
			   return null;
		   }
	   }
	   else { //Error
		   return null;
	   }
   }

   // Pre:  xLo, xHi, yLo, yHi, zLo, and zHi define a rectangular region
   // Returns a collection of (references to) all elements x such that x is in
   // the tree and x lies at coordinates within the defined rectangular region,
   // including the boundary of the region.
   public ArrayList<T> find(long xLo, long xHi, long yLo, long yHi, long zLo, long zHi) {
	   ArrayList<T> results = new ArrayList<T>();
	   return find(results, root, xLo, xHi, yLo, yHi, zLo, zHi, xMin, xMax, yMin, yMax, zMin, zMax);
   }

   // Pre: sRoot is the current node within the prOctree
   //	xLo, xHi, yLo, yHi, zLo, zHi define a rectangular region
   //   xMin, xMax, yMin, yMax, zMin, zMax define the bounds of the current node
   // Returns a collection of (references to) all elements x such that x is in
   // the tree and x lies at coordinates within the defined rectangular region,
   // including the boundary of the region.
   // Recursive helper for public find function
   private ArrayList<T> find(ArrayList<T> results, prOctNode sRoot, long xLo, long xHi, long yLo, long yHi, long zLo, long zHi, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
	   if(sRoot == null) { //sRoot is an abstract prOctNode
		   return results;
	   }
	   else if(sRoot.getClass().equals(Leaf.getClass())) { //sRoot is leaf
		   prOctLeaf currentNode = (prOctLeaf) sRoot;
		   for(int i=0; i<bucketSize; i++) {
			   T currentElement = (T) currentNode.Elements.get(i);
			   // Check if current element is within the rectangle
			   if(currentElement.inBox(xLo, xHi, yLo, yHi, zLo, zHi)) {
				   results.add(currentElement);
			   }
		   }
		   return results;
	   }
	   else if(sRoot.getClass().equals(Internal.getClass())){ //sRoot is internal node
		   prOctInternal currentNode = (prOctInternal) sRoot;
		   if(xLo < (xMax-xMin)/2 + xMin && xHi > xMin && yLo < yMax && yHi > (yMax-yMin)/2 + yMin && zLo < zMax && zHi > (zMax-zMin)/2 + zMin) { //NWF Quadrant
			   results = find(results, currentNode.NWF, xLo, xHi, yLo, yHi, zLo, zHi, xMin, (xMax-xMin)/2 + xMin, (yMax-yMin)/2 + yMin, yMax, (zMax-zMin)/2 + zMin, zMax);
		   }
		   if(xLo < (xMax-xMin)/2 + xMin && xHi > xMin && yLo < (yMax-yMin)/2 + yMin && yHi > yMin && zLo < zMax && zHi > (zMax-zMin)/2 + zMin) { //SWF Quadrant
			   results = find(results, currentNode.SWF, xLo, xHi, yLo, yHi, zLo, zHi, xMin, (xMax-xMin)/2 + xMin, yMin, (yMax-yMin)/2 + yMin, (zMax-zMin)/2 + zMin, zMax);
		   }
		   if(xLo < xMax && xHi > (xMax-xMin)/2 + xMin && yLo < yMax && yHi > (yMax-yMin)/2 + yMin && zLo < zMax && zHi > (zMax-zMin)/2 + zMin) { //NEF Quadrant
			   results = find(results, currentNode.NEF, xLo, xHi, yLo, yHi, zLo, zHi, (xMax-xMin)/2 + xMin, xMax, (yMax-yMin)/2 + yMin, yMax, (zMax-zMin)/2 + zMin, zMax);
		   }
		   if(xLo < xMax && xHi > (xMax-xMin)/2 + xMin && yLo < (yMax-yMin)/2 + yMin && yHi > yMin && zLo < zMax && zHi > (zMax-zMin)/2 + zMin) { //SEF Quadrant
			   results = find(results, currentNode.SEF, xLo, xHi, yLo, yHi, zLo, zHi, (xMax-xMin)/2 + xMin, xMax, yMin, (yMax-yMin)/2 + yMin, (zMax-zMin)/2 + zMin, zMax);
		   }
			 if(xLo < (xMax-xMin)/2 + xMin && xHi > xMin && yLo < yMax && yHi > (yMax-yMin)/2 + yMin && zLo < (zMax-zMin)/2 + zMin && zHi > zMin) { //NWB Quadrant
			   results = find(results, currentNode.NWB, xLo, xHi, yLo, yHi, zLo, zHi, xMin, (xMax-xMin)/2 + xMin, (yMax-yMin)/2 + yMin, yMax, zMin, (zMax-zMin)/2 + zMin);
		   }
		   if(xLo < (xMax-xMin)/2 + xMin && xHi > xMin && yLo < (yMax-yMin)/2 + yMin && yHi > yMin && zLo < (zMax-zMin)/2 + zMin && zHi > zMin) { //SWB Quadrant
			   results = find(results, currentNode.SWB, xLo, xHi, yLo, yHi, zLo, zHi, xMin, (xMax-xMin)/2 + xMin, yMin, (yMax-yMin)/2 + yMin, zMin, (zMax-zMin)/2 + zMin);
		   }
		   if(xLo < xMax && xHi > (xMax-xMin)/2 + xMin && yLo < yMax && yHi > (yMax-yMin)/2 + yMin && zLo < (zMax-zMin)/2 + zMin && zHi > zMin) { //NEB Quadrant
			   results = find(results, currentNode.NEB, xLo, xHi, yLo, yHi, zLo, zHi, (xMax-xMin)/2 + xMin, xMax, (yMax-yMin)/2 + yMin, yMax, zMin, (zMax-zMin)/2 + zMin);
		   }
		   if(xLo < xMax && xHi > (xMax-xMin)/2 + xMin && yLo < (yMax-yMin)/2 + yMin && yHi > yMin && zLo < (zMax-zMin)/2 + zMin && zHi > zMin) { //SEB Quadrant
			   results = find(results, currentNode.SEB, xLo, xHi, yLo, yHi, zLo, zHi, (xMax-xMin)/2 + xMin, xMax, yMin, (yMax-yMin)/2 + yMin, zMin, (zMax-zMin)/2 + zMin);
		   }
		   return results;
	   }
	   else { //ERROR
		   return results;
	   }
   }
}
