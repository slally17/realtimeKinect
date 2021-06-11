prOctree<Point3D> octree;
PImage[] img;
int[][][] depthData;

int FRAMERATE = 10;
int FRAMENUM = 1;
int currentFrame = 0;
boolean REALTIME = true;
boolean DELETENORM = true;
boolean DELETEBODY = true;
boolean SAVEFRAME = false;

int BACKGROUND = 0;
int FILL = 0;
boolean NOFILL = false;
boolean NOSTROKE = true;
int STROKE = 0;
float STROKEWEIGHT = 32;

boolean RANDOMDATA = false;
int STARTDATA = 5000;
int NUMDATA = 5000;
int COUNT = 0;
int COUNTDIVIDER = 1;

int WIDTH = -1;
int HEIGHT = -1;
int DEPTH = -1;

int RESOLUTION = 3;
int LEVEL = 5;
boolean INTERNAL = false;
boolean DISTLIMIT = false;
boolean PULSETRAN = false;
float TRANMOD = 2;
boolean PULSESIZE = true;
float SIZEMOD = 4;
boolean MIRROR = true;

// 6000 max
float DEPTHMAX = 6000;
String COLORMODE = "DEFAULT";
boolean ROTATE = false;
boolean DISTORT = false;
int CAMERADEPTH = -4; 


int FRAMECOUNT = 180;
boolean RENDER = false;
String folderName = "exports";


public void setup(){
  // Choose size
  //size(1000, 1000, P3D);
  fullScreen(P3D);
  
  // Import images
  img = new PImage[FRAMENUM];  
  for(int i=0; i<FRAMENUM; i++){
    String filename = "data/colorImage" + i + ".jpg";    
    String testFilename = "colorImage" + i + ".jpg";
    File testFile = new File(dataPath(testFilename));
    if(!testFile.exists()){
      filename = "data/colorImageBody" + i + ".jpg";
    }
    
    img[i] = loadImage(filename);
    img[i].loadPixels();    
  }
  readDepthImages();  
  resizeImages();
  WIDTH = img[0].width;
  HEIGHT = img[0].height;
  DEPTH = min(img[0].width, img[0].height);  
  
  // Choose random seed
  String seedString = "tz1YQs3qHgQGGQm14vX8vMDWwzY1DFXHCzJB";
  char[] seedChar = seedString.toCharArray();
  int seed = 0;
  for(int i=0; i<seedChar.length; i++){
    seed += (int) seedChar[i];
  }
  noiseSeed(seed);
  randomSeed(seed);  
  
  // Apply Mode
  setMode("default");
  
  // Create octree
  createOctree(currentFrame);
  
  // Display starting data
  displayData();
  
  // Record frame
  if(RENDER){
    saveFrame(folderName + "/frame000.png");
  }  
}


public void draw(){  
  // Create new data 
  if(frameCount % 6 == 0 && !REALTIME){     
    currentFrame++;
    if(currentFrame == FRAMENUM){
      currentFrame = 0;
    }
    createOctree(currentFrame);
    
    //LEVEL++;
    if(octree.getSize() < NUMDATA){
      //addData(frameCount^2);
    }     
  }
  // Read frame
  else if(checkNextFrame() && REALTIME){
    if(checkFrame()){
      COUNT = 0;
      currentFrame++;
      String filename = "data/colorImage" + currentFrame + ".jpg";
      img[0] = loadImage(filename);
      if(COLORMODE == "BANDW" || COLORMODE == "BANDWTRANS"){
        img[0].filter(GRAY);
      }
      else if(COLORMODE == "INVERT" || COLORMODE == "INVERTTRANS"){
        img[0].filter(INVERT);
      }
      img[0].loadPixels();    
      readDepthImage(currentFrame, false);  
      resizeImages();    
      
      createOctree(0);
    }
    else if(checkFrameBody()){
      COUNT = 0;
      currentFrame++;
      String filename = "data/colorImageBody" + currentFrame + ".jpg";
      img[0] = loadImage(filename);
      if(COLORMODE == "BANDW" || COLORMODE == "BANDWTRANS"){
        img[0].filter(GRAY);
      }
      else if(COLORMODE == "INVERT" || COLORMODE == "INVERTTRANS"){
        img[0].filter(INVERT);
      }
      img[0].loadPixels();    
      readDepthImage(currentFrame, true);  
      resizeImages();    
      
      createOctree(0);
    }
    else{
      println("ERROR");
    }
    
    // delete old frame files
    if(DELETENORM){      
      String filename = "colorImage" + currentFrame + ".jpg";
      File file = new File(dataPath(filename));
      file.delete();
      filename = "depthImage" + currentFrame + ".txt";
      file = new File(dataPath(filename));
      file.delete();
    }
    if(DELETEBODY){
      String filename = "colorImageBody" + currentFrame + ".jpg";
      File file = new File(dataPath(filename));
      file.delete();
      filename = "depthImageBody" + currentFrame + ".txt";
      file = new File(dataPath(filename));
      file.delete();
    }  
  }
  else if(REALTIME){
    println("WAITING" + currentFrame);
  }   
  
  // Display data
  displayData();
  
  // Record frame
  if(frameCount < FRAMECOUNT && RENDER){
    saveFrame(folderName + "/frame###.png");
  }
  else if(RENDER){
    println("FINISHED.");
  }
  if(SAVEFRAME){
    SAVEFRAME = false;
    saveFrame(folderName + "/frame###.png");
  }
}

// Read in keyboard inputs
public void keyPressed(){
  // Save
  if(key == 115){
    SAVEFRAME = true;
  }
  // Background
  else if(key == 98){
    DEPTHMAX -= 1000;
    if(DEPTHMAX == 1000){
      DEPTHMAX = 6000;
    }
  }
  // Color Mode
  else if(key == 109){
    switch(COLORMODE){
      case "DEFAULT":
        COLORMODE = "TRANSPARENT";
        break;
      case "TRANSPARENT":
        COLORMODE = "BANDW";
        break;
      case "BANDW":
        COLORMODE = "BANDWTRANS";
        break;
      case "BANDWTRANS":
        COLORMODE = "INVERT";
        break;
      case "INVERT":
        COLORMODE = "INVERTTRANS";
        break;
      case "INVERTTRANS":
        COLORMODE = "DEFAULT";
        break;
    }
  }
  // Rotate
  else if(key == 114){
    ROTATE = !ROTATE;
  }
  // Distort
  else if(key == 100){
    DISTORT = !DISTORT;
  }
  // Camera
  else if(key == 99){
    CAMERADEPTH++;
    if(CAMERADEPTH == 1){
      CAMERADEPTH = -4;
    }
  }
}

// Check if the next frame of octree data is available
public boolean checkFrame(){
  int imageNum = currentFrame + 1;
  String filename = "colorImage" + imageNum + ".jpg";
  File file = new File(dataPath(filename));
  //println(dataPath(filename));
  return file.exists();
}

// Check if the next frame of octree data is available
public boolean checkFrameBody(){
  int imageNum = currentFrame + 1;
  String filename = "colorImageBody" + imageNum + ".jpg";
  File file = new File(dataPath(filename));
  //println(dataPath(filename));
  return file.exists();
}

// Check if the next frame of octree data is available
public boolean checkNextFrame(){
  int imageNum = currentFrame + 2;
  String filename1 = "colorImage" + imageNum + ".jpg";
  String filename2 = "colorImageBody" + imageNum + ".jpg";
  File file1 = new File(dataPath(filename1));
  File file2 = new File(dataPath(filename2));
  //println(dataPath(filename));
  return (file1.exists() || file2.exists());
}

// Create octree data
public void createOctree(int frameNum){
  // Change settings
  //NUMDATA = floor(random(10000));
  //COUNTDIVIDER = floor(random(6)+1);    
  
  // Create octree
  octree = new prOctree(0, WIDTH, 0, HEIGHT, 0, DEPTH);
  
  // Create random starting data  
  while(COUNT < STARTDATA && RANDOMDATA){
    Point3D newPoint = new Point3D(int(random(WIDTH)), int(random(HEIGHT)), int(random(DEPTH)), COUNT);
    if(octree.insert(newPoint)){
      COUNT++;
    }
  }
  
  // Create picture data
  // 327-3742
  if(!RANDOMDATA){
    for(int i=0; i<WIDTH; i+=RESOLUTION){
      for(int j=0; j<HEIGHT; j+=RESOLUTION){
        if(depthData[frameNum][j][i]<DEPTHMAX && depthData[frameNum][j][i] != 0){          
          Point3D newPoint = new Point3D(i, j, int(((DEPTHMAX-depthData[frameNum][j][i])/(DEPTHMAX))*720), COUNT);
          if(octree.insert(newPoint)){
            COUNT++;
            //println(depthData[frameNum][j][i]);
          }
          else{
            println("FAILED: " + depthData[frameNum][j][i]);
          }
        }        
      }
    }
  }
}


// Display data function
public void displayData(){  
  background(BACKGROUND);
  pushMatrix();
  translate((width/2), (height/2), CAMERADEPTH*DEPTH);
  //rotateX(radians(-45));
  if(ROTATE){
    rotateY(radians(frameCount*2));
  }
  //rotateY(radians(10*sin(PI*frameCount/90)));  
  //rotateZ(radians(frameCount*3));
  prOctNode<Point3D> currentNode = octree.getRoot();
  drawNodeScreen(currentNode, 1);
  popMatrix(); 
}


// Recursively draw nodes of octree to screen
public int drawNodeScreen(prOctNode sRoot, int level){
  if(!NOSTROKE){
    strokeWeight(STROKEWEIGHT / pow(level, 2));
  }
  else{
    noStroke();
  }
  int numPoints = 0;
  
  if(sRoot == null){
    
  }
  else if( sRoot.getClass().equals(octree.Leaf.getClass()) ){    
    prOctLeaf<Point3D> currentNode = (prOctLeaf) sRoot;   
    
    float xWidth = currentNode.xMax-currentNode.xMin;
    float yWidth = currentNode.yMax-currentNode.yMin;
    float zWidth = currentNode.zMax-currentNode.zMin;
    float xCenter = currentNode.xMax - (xWidth)/2;
    float yCenter = currentNode.yMax - (yWidth)/2;
    float zCenter = currentNode.zMax - (zWidth)/2;
    
    if(REALTIME){    
      color c = img[0].pixels[floor(xCenter) + floor(yCenter)*WIDTH];
      if(COLORMODE == "TRANSPARENT" || COLORMODE == "BANDWTRANS" || COLORMODE == "INVERTTRANS"){
        c = color(red(img[0].pixels[floor(xCenter) + floor(yCenter)*WIDTH]), green(img[0].pixels[floor(xCenter) + floor(yCenter)*WIDTH]), blue(img[0].pixels[floor(xCenter) + floor(yCenter)*WIDTH]), 255*noise(COUNT));
      }
      fill(c);
    }
    else{
      fill(img[currentFrame].pixels[floor(xCenter) + floor(yCenter)*WIDTH]);  
    }
    
    float modifierTran = TRANMOD;
    float modifierSize = SIZEMOD;
    if(PULSETRAN){
      modifierTran *= (1 - noise(currentNode.Elements.get(0).getCount())/2) * sin(PI*frameCount/90);
      //modifierTran = abs(modifierTran);
    }
    if(PULSESIZE) {
      modifierSize *= (1 - noise(currentNode.Elements.get(0).getCount())/2) * sin(2*PI*frameCount/90 - 10 * noise(currentNode.Elements.get(0).getCount()/2));
    }
    
    if(currentNode.Elements.get(0).getCount() % COUNTDIVIDER == 0 && level > LEVEL && (!DISTLIMIT || dist(xCenter, yCenter, zCenter, WIDTH/2, HEIGHT/2, DEPTH/2) < WIDTH/2)){
      pushMatrix();      
      translate((xCenter-WIDTH/2) * pow(modifierTran, 2), (yCenter-HEIGHT/2) * pow(modifierTran, 2), (zCenter - DEPTH/2) * pow(modifierTran, 2.5));      
      box(xWidth * pow(modifierSize, 2));    
      popMatrix(); 
      
      numPoints++;
    }      
  }
  else{       
    prOctInternal currentNode = (prOctInternal) sRoot;   
    
    numPoints += drawNodeScreen(currentNode.NWF, level+1);
    numPoints += drawNodeScreen(currentNode.SWF, level+1);
    numPoints += drawNodeScreen(currentNode.SEF, level+1);
    numPoints += drawNodeScreen(currentNode.NEF, level+1);
    numPoints += drawNodeScreen(currentNode.NWB, level+1);
    numPoints += drawNodeScreen(currentNode.SWB, level+1);
    numPoints += drawNodeScreen(currentNode.SEB, level+1);
    numPoints += drawNodeScreen(currentNode.NEB, level+1);
    
    if(INTERNAL){
      stroke(STROKE);
      strokeWeight(STROKEWEIGHT / pow(level*2, 2));
      noFill();
    
      float xWidth = currentNode.xMax-currentNode.xMin;
      float yWidth = currentNode.yMax-currentNode.yMin;
      float zWidth = currentNode.zMax-currentNode.zMin;
      float xCenter = currentNode.xMax - (xWidth)/2;
      float yCenter = currentNode.yMax - (yWidth)/2;
      float zCenter = currentNode.zMax - (zWidth)/2;
      
      float modifierTran = TRANMOD;
      float modifierSize = SIZEMOD+1;
      if(PULSETRAN){
        modifierTran *= sin(2*PI*frameCount/90);
        //modifierTran = abs(modifierTran);
      }
      if(PULSESIZE&&false) {
        modifierSize *= sin(2*PI*frameCount/90);
      }
      
      if(numPoints > 2 && level == 1 && COUNT % COUNTDIVIDER == 0 && (!DISTLIMIT || dist(xCenter, yCenter, zCenter, WIDTH/2, HEIGHT/2, DEPTH/2) < HEIGHT/2)){
        pushMatrix();
        translate((xCenter-WIDTH/2) * pow(modifierTran, 2), (yCenter-HEIGHT/2) * pow(modifierTran, 2), (zCenter-DEPTH/2) * pow(modifierTran, 2));      
        box(xWidth * pow(modifierSize, 2), yWidth * pow(modifierSize, 2), zWidth * pow(modifierSize, 2));   
        popMatrix();
      }
    }
  }
  return numPoints;
}


// Set mode
public void setMode(String mode){
  if(mode == "default"){
    frameRate(FRAMERATE);
    background(BACKGROUND);
    fill(FILL);
    if(NOFILL){
      noFill();
    }
    stroke(STROKE);
    strokeWeight(STROKEWEIGHT);
    if(NOSTROKE){
      noStroke();
    }
  }
}

// Read depth images
public void readDepthImages(){
  depthData = new int[FRAMENUM][img[0].height][img[0].width];
  
  for(int frameNum=0; frameNum<FRAMENUM; frameNum++){
    String filename = "data/depthImage" + frameNum + ".txt";
    String testFilename = "depthImage" + frameNum + ".txt";
    File testFile = new File(dataPath(testFilename));
    if(!testFile.exists()){
      filename = "data/depthImageBody" + frameNum + ".txt";
    }
    String[] depthFile = loadStrings(filename);
    
    for(int i=0; i<img[frameNum].height; i++){
      for(int j=0; j<img[frameNum].width; j++){
        depthData[frameNum][i][j] = Integer.parseInt(depthFile[i*img[frameNum].width + j]);
      }
    }
  }  
}

// Read depth image
public void readDepthImage(int frameNum, boolean bodyFile){
  depthData = new int[FRAMENUM][img[0].height][img[0].width];
  
  String filename = "data/depthImage" + frameNum + ".txt";
  if(bodyFile){
    filename = "data/depthImageBody" + frameNum + ".txt";
  }
  String[] depthFile = loadStrings(filename);
  
  for(int i=0; i<img[0].height; i++){
    for(int j=0; j<img[0].width; j++){
      depthData[0][i][j] = Integer.parseInt(depthFile[i*img[0].width + j]);
    }
  } 
}

// Resize images to squares
public void resizeImages(){
  PImage[] tempImg = img;
  int[][][] tempDepth = depthData;
  depthData = new int[FRAMENUM][img[0].height][img[0].height];
  img = new PImage[FRAMENUM];
  for(int frameNum=0; frameNum<FRAMENUM; frameNum++){    
    img[frameNum] = createImage(tempImg[frameNum].height, tempImg[frameNum].height, RGB);
    img[frameNum].loadPixels();
    
    int min = (tempImg[frameNum].width-tempImg[frameNum].height)/2;
    int max = tempImg[frameNum].width-min;
    int count = 0;
    
    for(int i=0; i<tempImg[frameNum].height; i++){
      for(int j=min; j<max; j++){
        if(DISTORT){
          img[frameNum].pixels[count] = tempImg[frameNum].pixels[(tempImg[frameNum].height-1-i)*tempImg[frameNum].width+j];
          depthData[frameNum][i][j-min] = tempDepth[frameNum][j-min][i];
        }
        else if(MIRROR){
          img[frameNum].pixels[count] = tempImg[frameNum].pixels[(i+1)*tempImg[frameNum].width-1-j];
          depthData[frameNum][i][j-min] = tempDepth[frameNum][i][tempImg[frameNum].width-1-j];
        }
        else{
          img[frameNum].pixels[count] = tempImg[frameNum].pixels[i*tempImg[frameNum].width+j];
          depthData[frameNum][i][j-min] = tempDepth[frameNum][i][j];
        }          
        count++;
      }
    }
    img[frameNum].updatePixels();
  }  
}
