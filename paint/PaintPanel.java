package ca.utoronto.utm.paint;

import javax.swing.*;  
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

class PaintPanel extends JPanel {
	private static final long serialVersionUID = 3277442988868869424L;
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();
	private String Path; // the path of the file is going to be saved
	
	public PaintPanel(){
		this.setBackground(Color.white);
		this.setPreferredSize(new Dimension(300,300));
	}
	
	public void setCommands(ArrayList<PaintCommand> commands){
		this.commands=commands;
	}
	public void reset(){
		this.commands.clear();
		this.repaint();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
	}
	
	public void save(PrintWriter writer) throws IOException {
		try{
			writer = new PrintWriter(new FileWriter(this.Path));
			writer.println("Paint Save File Version 1.0");
			
			for(PaintCommand c : this.commands){ // for each command write distinct info on the file
				String r = Integer.toString(c.getShape().getColor().getRed());
				String g = Integer.toString(c.getShape().getColor().getGreen());
				String b = Integer.toString(c.getShape().getColor().getBlue());
				
				if(c.getType()=="Circle"){
					Circle circle=(Circle)c.getShape();
					String radius=Integer.toString(circle.getRadius());
					String centerX=Integer.toString(circle.getCentre().x);
					String centerY=Integer.toString(circle.getCentre().y);
					writer.println("Circle");
					writer.println("	color:"+r+","+g+","+b);
					writer.println("    filled:"+String.valueOf(circle.isFill()));
					writer.println("    center:("+centerX+","+centerY+")");
					writer.println("    radius:"+radius);
					writer.println("End Circle");
				}else if(c.getType()=="Rectangle"){
					Rectangle rectangle=(Rectangle)c.getShape();
					String p1X=Integer.toString(rectangle.getP1().x);
					String p1Y=Integer.toString(rectangle.getP1().y);
					String p2X=Integer.toString(rectangle.getP2().x);
					String p2Y=Integer.toString(rectangle.getP2().y);
					writer.println("Rectangle");
					writer.println("	color:"+r+","+g+","+b);
					writer.println("    filled:"+String.valueOf(rectangle.isFill()));
					writer.println("	p1:("+p1X+","+p1Y+")");
					writer.println("	p2:("+p2X+","+p2Y+")");
					writer.println("End Rectangle");
				}else if(c.getType()=="Squiggle"){
					Squiggle squiggle=(Squiggle)c.getShape();
					writer.println("Squiggle");
					writer.println("	color:"+r+","+g+","+b);
					writer.println("    filled:"+String.valueOf(squiggle.isFill()));
					writer.println("	points");
					for(Point p:squiggle.getPoints()){
						String pX=Integer.toString(p.x);
						String pY=Integer.toString(p.y);
						writer.println("		point:("+pX+","+pY+")");
					}
					writer.println("	end points");
					writer.println("End Squiggle");
				}
			}writer.println("End Paint Save File");
			
		}finally {
			if(writer!=null){
				writer.close();	
			}		
		}
		
	}
	
	public void updatepath(String path){ // update the path of the file before save method is called
		this.Path=path;
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g); //paint background
        Graphics2D g2d = (Graphics2D) g;		
		for(PaintCommand c: this.commands){
			c.execute(g2d);
		}
		g2d.dispose();
	}
}
