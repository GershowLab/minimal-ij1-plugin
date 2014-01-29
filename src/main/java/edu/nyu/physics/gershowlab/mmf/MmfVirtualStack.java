package edu.nyu.physics.gershowlab.mmf;


import ij.*;
import ij.process.*;


/*
 *	Allows the MMF file to be accessed through ImageJ's ImageStack methods without loading all of the data into memory. 
 * 
 *  @author Natalie Bernat
 *  @author Marc Gershow
 *  @version 1.0
 *  @see VirtualStack
 *  @see MmfFile
 *  @see CommonBackgroundStack
 */
public class MmfVirtualStack extends VirtualStack {


	private String			fileName;
	private String			fileDir;
	/*
	 * The MMF file associated with the stack
	 */
	private MmfFile 		raf;	
	/*
	 * The image depth
	 */
	private int 			depth;
	
	/*
	 * The segment of the movie which is currently loaded in memory
	 */
	private CommonBackgroundStack currentStack;
	
	/*
	 * Creates an MmfVirtualStack from the file specified in the arguments, initially loading the first segment of the movie into memory. 
	 * 
	 */
	public MmfVirtualStack(String path, String fileName, String fileDir){
		this.fileName = fileName;
		this.fileDir  = fileDir;
		depth = -1;
		try{	
			raf = new MmfFile(path, "r");
			raf.parse();			
		} catch(Exception e){
			IJ.showMessage("MmfVirtualStack","Opening of: \n \n"+path+"\n \n was unsuccessful.\n\n Error: " +e);
			return;
		}
		
		currentStack = raf.getStackForFrame(1);
		
	}
	
	/*
	 * Indicates whether or not the Mmf file is invalid
	 * 
	 * @return		true if the file is invalid, false otherwise 
	 */
	public boolean fileIsNull(){
		//Check that the file isn't null
		if (raf == null || raf.getNumFrames()==0 || getProcessor(1)==null){
			IJ.showMessage("MmfVirtualStack","Error: Frames missing or empty");
			return true;
		}
		return false;
	}
	
	/*
	 * Does nothing
	 */
	public void addSlice(String name){
		return;
	}
	
	/*
	 * Does nothing
	 */
	public void deleteLastSlice(){
		return;
	}
	
	/*
	 * Does nothing
	 */
	public void deleteSlice(int n){
		return;
	}
	
	public int getBitDepth(){
		if (depth == -1){
			depth = currentStack.backgroundIm.getBitDepth();
		}
		return depth;
	}

	public String getDirectory(){
		return fileDir;
	}
	
	/*
	 * Returns the file name (not including the directory)
	 */
	public String getFileName(){
		return fileName;
	}

	//Returns the ImageProcessor for the specified frame number
	//	Overrides the method in ImageStack
	//	Ensures that the frame is in the current mmfStack, and then gets the image through CommonBackgroundStack methods

	public ImageProcessor getProcessor (int frameNumber) {
		
		if(frameNumber<0 || frameNumber>raf.getNumFrames()){
			return null; 
		}
		//check if current stack has frame
		//if not update current stack from mmf file
		if (!currentStack.containsFrame(frameNumber)) {
			currentStack = raf.getStackForFrame(frameNumber);
		}

		//then get specific frame
		if (currentStack == null){
			return null;
		}
		return currentStack.getImage(frameNumber);
	}
			
	public int getSize() {
		return raf.getNumFrames();
	}
	
	
	public String getSliceLabel(int n){
		String label = "MMF_Frame_"+n;
		return label;
	}
	
	/*
	 * Does nothing
	 */
	public void setPixels(){
		return;
	}
	
	
	
}
