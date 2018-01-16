package ca.utoronto.utm.paint;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintSaveFileParser {
	
	private String Path; // the path of the file
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private ArrayList<PaintCommand> commands; // created as a result of the parse
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("Paint\\s*Save\\s*File\\s*Version\\s*1.0");
	private Pattern pFileEnd=Pattern.compile("^End\\s*Paint\\s*Save\\s*File$");
	
	private Pattern pColor=Pattern.compile("\\s*color:([0-9]?[0-9]?[0-9]?),([0-9]?[0-9]?[0-9]?),([0-9]?[0-9]?[0-9]?)");
	private Pattern pFilled=Pattern.compile("\\s*filled:(false|true)");
	private Pattern pCenter=Pattern.compile("\\s*center:\\(([0-9]?[0-9]?[0-9]?),([0-9]?[0-9]?[0-9]?)\\)");
	private Pattern pRadius=Pattern.compile("\\s*radius:([0-9]?[0-9]?[0-9]?)");
	private Pattern pPoint1=Pattern.compile("\\s*p1:\\(([0-9]?[0-9]?[0-9?]),([0-9]?[0-9]?[0-9]?)\\)");
	private Pattern pPoint2=Pattern.compile("\\s*p2:\\(([0-9]?[0-9]?[0-9?]),([0-9]?[0-9]?[0-9]?)\\)");
	private Pattern pLineStart=Pattern.compile("\\s*points");
	private Pattern pSquigglePoint=Pattern.compile("\\s*point:\\(([0-9]?[0-9]?[0-9]?),([0-9]?[0-9]?[0-9]?)\\)");
	private Pattern pLineEnd=Pattern.compile("\\s*end\\s*points");
	private Pattern pCircleStart=Pattern.compile("Circle");
	private Pattern pCircleEnd=Pattern.compile("^End\\s*Circle$");
	private Pattern pRectangleStart=Pattern.compile("^Rectangle$");
	private Pattern pRectangleEnd=Pattern.compile("^End\\s*Rectangle$");
	private Pattern pSquiggleStart=Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd=Pattern.compile("^End\\s*Squiggle$");
	// ADD MORE!!
	
	/**
	 * constructor of the class
	 */
	public PaintSaveFileParser(){
		
	}
	
	/**
	 * update the file path
	 * @param path the path of the file that is going to be opened
	 */
	public void updatePath(String path){
		this.Path=path; // get the file path from paint
	}
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	/**
	 * 
	 * @return the PaintCommands resulting from the parse
	 */
	public ArrayList<PaintCommand> getCommands(){
		return this.commands;
	}
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream) throws IOException {
		inputStream = new BufferedReader(new FileReader(this.Path)); 
		this.commands = new ArrayList<PaintCommand>();
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following shapes. As we parse the file, we modify 
		// the appropriate shape.
		
		Circle circle = null;
		Rectangle rectangle = null;
		Squiggle squiggle = null;
	
		try {
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l); // check if the object is a circle
						if(m.matches()){
							// ADD CODE!!!
							circle = new Circle();
							state=5;
							break;
						}
						state=2;
						// ADD CODE
				
					case 2: // check if the object is a rectangle
						m=pRectangleStart.matcher(l);
						if(m.matches()){
							rectangle = new Rectangle();
							state=5;
							break;
						}
						// ADD CODE
						state=3;
					case 3: // check if the object is a squiggle
						m=pSquiggleStart.matcher(l);
						if(m.matches()){
							squiggle = new Squiggle();
							state=5;
							break;
						}
						state=4;
					// ...
					case 4: // check if the line matches the end format
						m=pFileEnd.matcher(l);
						if(m.matches()){
							return true;
						}
						error("Expected End of Paint Save File");
						return false;
					case 5: // check the color of the object
						m=pColor.matcher(l);
						if(m.matches()){
							int num1=Integer.parseInt(m.group(1));
							int num2=Integer.parseInt(m.group(2));
							int num3=Integer.parseInt(m.group(3));
							Color color=new Color(num1, num2, num3); // set color
							if(circle!=null){
								circle.setColor(color);
							}else if(rectangle!=null){
								rectangle.setColor(color);
							}else if(squiggle!=null){
								squiggle.setColor(color);
							}
							state=6;
							break;
						}error("Expected Color of one of the object");
						return false;
					case 6: // check whether the object is filled
						m=pFilled.matcher(l);
						if(m.matches()){
							boolean fill=Boolean.valueOf(m.group(1)); // set filled status
							if(circle!=null){
								circle.setFill(fill);
								state=7;
								break;
							}else if(rectangle!=null){
								rectangle.setFill(fill);
								state=10;
								break;
							}else if(squiggle!=null){
								squiggle.setFill(fill);
								state=13;
								break;
							}
						}error("Expected Filled status of one of the object");
						return false;
					case 7: // check the center of one of the circle
						m=pCenter.matcher(l);
						if(m.matches()){
							int x=Integer.parseInt(m.group(1));
							int y=Integer.parseInt(m.group(2));
							circle.setCentre(new Point(x, y)); // set center
							state=8;
							break;
						}error("Expected Center of one of the circle");
						return false;
					case 8: // check the radius of one of the circle
						m=pRadius.matcher(l);
						if(m.matches()){
							int radius=Integer.parseInt(m.group(1));
							circle.setRadius(radius); //set radius
							state=9;
							break;
						}error("Expected Radius of one of the circle");
						return false;
					case 9: // check the end format of a circle
						m=pCircleEnd.matcher(l);
						if(m.matches()){
							CircleCommand circleCommand = new CircleCommand(circle);
							this.commands.add(circleCommand); // add the circle to circlecommand 
																//and add the circlecommand to command list
							circle=null; // reset the circle variable
							state=1;
							break;
						}error("Expected End format of one of the circle");
						return false;
					case 10: // check point1 of a rectangle
						m=pPoint1.matcher(l);
						if(m.matches()){
							int p1x=Integer.parseInt(m.group(1));
							int p1y=Integer.parseInt(m.group(2));
							rectangle.setP1(new Point(p1x, p1y)); // set one corner of a rectangle
							state=11;
							break;
						}error("Expected p1 of one of the rectangle");
						return false;
					case 11: // check point2 of a rectangle
						m=pPoint2.matcher(l);
						if(m.matches()){
							int p2x=Integer.parseInt(m.group(1));
							int p2y=Integer.parseInt(m.group(2));
							rectangle.setP2(new Point(p2x, p2y)); // set one corner of a rectangle
							state=12;
							break;
						}error("Expected p2 of one of the rectangle");
						return false;
					case 12: // check end format of a rectangle
						m=pRectangleEnd.matcher(l);
						if(m.matches()){
							RectangleCommand rectangleCommand = new RectangleCommand(rectangle);
							this.commands.add(rectangleCommand); // add the rectanglecommand to command list
							rectangle=null; // reset the rectangle variable
							state=1;
							break;
						}error("Expected End format of one of the rectangle");
						return false;
					case 13: // check the start line of point
						m=pLineStart.matcher(l);
						if(m.matches()){
							state=14;
							break;
						}error("Expected Point line of one of the Squiggle");
						return false;
					case 14: // check points of a squiggle
						m=pSquigglePoint.matcher(l);
						if(m.matches()){
							int x=Integer.parseInt(m.group(1));
							int y=Integer.parseInt(m.group(2));
							squiggle.add(new Point(x, y)); // add point to squiggle object
							break;
						}state=15;
					case 15: // check end point format of a squiggle
						m=pLineEnd.matcher(l);
						if(m.matches()){
							state=16;
							break;
						}error("Expected End Point line of one of the Squiggle");
						return false;
					case 16: // check end format of a squiggle
						m=pSquiggleEnd.matcher(l);
						if(m.matches()){
							SquiggleCommand squiggleCommand = new SquiggleCommand(squiggle);
							this.commands.add(squiggleCommand);
							squiggle=null;
							state=1;
							break;
						}error("Expected End format line of one of the Squiggle");
						return false;
				}
			}
		}  catch (Exception e){
			
		}
		inputStream.close();
		return true;
	}
}
