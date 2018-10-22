package a2;

import graphicslib3D.*;
import graphicslib3D.Shape3D.*;
import graphicslib3D.shape.Sphere;
import graphicslib3D.GLSLUtils.*;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.nio.*;
import java.util.Random;

import javax.swing.*;
import javax.swing.AbstractAction;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.common.nio.Buffers;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.io.File;
import java.awt.event.KeyEvent;
import com.jogamp.opengl.GLContext;

public class Code extends JFrame implements GLEventListener
{	private GLCanvas myCanvas;
	private int rendering_program;
	private int count = 0;
	private int vao[] = new int[1];
	private int vbo[] = new int[100];
	private int vco[] = new int[100];
	private Sphere sphere = new Sphere(20);
	private int sun, moon, earth, red, blue, green, jupiter, asteroid;
	private float cameraU = 0.0f, cameraV = 0.0f;
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float axisLocX, axisLocY, axisLocZ;
	private float pyrLocX, pyrLocY, pyrLocZ;
	private boolean showVertices = true;
	private Random r = new Random();
	private Texture joglSunTexture, joglMoonTexture, joglEarthTexture, redTexture, blueTexture, greenTexture, jupiterTexture, asteroidTexture;
	private GLSLUtils util = new GLSLUtils();
	
	private	MatrixStack mvStack = new MatrixStack(20);

	public Code()
	{	setTitle("Chris Dong Assignment 2");
		setSize(600, 600);
		
		
		Container pane = new Container();
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		
		
		JComponent contentPane = (JComponent) this.getContentPane();
        // get the "focus is in the window" input map for the content pane
        int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap imap = contentPane.getInputMap(mapName);
        // create a keystroke object to represent the "w" key
        KeyStroke qKey = KeyStroke.getKeyStroke('q');
        KeyStroke eKey = KeyStroke.getKeyStroke('e');
        KeyStroke wKey = KeyStroke.getKeyStroke('w');
        KeyStroke sKey = KeyStroke.getKeyStroke('s');
        KeyStroke aKey = KeyStroke.getKeyStroke('a');
        KeyStroke dKey = KeyStroke.getKeyStroke('d');
        KeyStroke leftKey = KeyStroke.getKeyStroke("LEFT");
        KeyStroke rightKey = KeyStroke.getKeyStroke("RIGHT");
        KeyStroke upKey = KeyStroke.getKeyStroke("UP");
        KeyStroke downKey = KeyStroke.getKeyStroke("DOWN");
        KeyStroke spaceKey = KeyStroke.getKeyStroke("SPACE");
        
        // put the "cKey" keystroke object into the content pane’s "when focus is
        // in the window" input map under the identifier name "color“
        imap.put(qKey, "up");
        imap.put(eKey, "down");
        imap.put(wKey, "forward");
        imap.put(sKey, "backward");
        imap.put(aKey, "left");
        imap.put(dKey, "right");
        imap.put(leftKey, "leftRotate");
        imap.put(rightKey, "rightRotate");
        imap.put(downKey, "downRotate");
        imap.put(upKey, "upRotate");
        imap.put(spaceKey, "axisToggle");
        
        // get the action map for the content pane
        ActionMap amap = contentPane.getActionMap();
        // put the "myCommand" command object into the content pane's action map
        amap.put("up", new UpAction(this));
        amap.put("down", new DownAction(this));
        amap.put("forward", new ForwardAction(this));
        amap.put("backward", new BackwardAction(this));
        amap.put("left", new LeftAction(this));
        amap.put("right", new RightAction(this));
        amap.put("leftRotate", new RotateLeftAction(this));
        amap.put("rightRotate", new RotateRightAction(this));
        amap.put("downRotate", new RotateDownAction(this));
        amap.put("upRotate", new RotateUpAction(this));
        amap.put("axisToggle", new AxisVisionAction(this));
        //have the JFrame request keyboard focus
        this.requestFocus();


		FPSAnimator animator = new FPSAnimator(myCanvas, 50);
		animator.start();
	}
	
	
	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(rendering_program);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
		//Push the View Matrix onto the Stack push view matrix onto the stack
	    mvStack.pushMatrix();
	   
	    mvStack.rotate(cameraU, 1.0f, 0.0f, 0.0f);
	    mvStack.rotate(cameraV, 0.0f, 1.0f, 0.0f);
	    mvStack.translate(-cameraX, -cameraY, -cameraZ);
	    
		double amt = (double)(System.currentTimeMillis())/1000.0;

		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
		
		
		// ----------------------  pyramid == sun  
		 mvStack.pushMatrix();
	        mvStack.translate(pyrLocX, pyrLocY, pyrLocZ);
	        mvStack.pushMatrix();
	        mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
	        mvStack.scale(5.0, 5.0, 5.0);
	        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
	        //Vertices, defined for model
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        //Textures, applied to model
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
	        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(1);
	        gl.glActiveTexture(GL_TEXTURE0);
	        gl.glBindTexture(GL_TEXTURE_2D, sun);
	        gl.glEnable(GL_CULL_FACE);
	        gl.glFrontFace(GL_CCW);
	        int verticesCount = sphere.getIndices().length;
	        gl.glDrawArrays(GL_TRIANGLES, 0, verticesCount); 
	        mvStack.popMatrix();
		
		if(showVertices == true){
			//-----------------------  X Axis
	        mvStack.pushMatrix();
	        mvStack.translate(axisLocX, axisLocY, axisLocZ);
	        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glDrawArrays(GL_LINES, 0, 9);
	      //Texture
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vco[3]);
		    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		    gl.glEnableVertexAttribArray(1);
		    gl.glActiveTexture(GL_TEXTURE0);
		    gl.glBindTexture(GL_TEXTURE_2D, red);
		    gl.glEnable(GL_DEPTH_TEST);
		    gl.glDepthFunc(GL_LEQUAL);
		    gl.glDrawArrays(GL_LINES, 0, 9);
	        mvStack.popMatrix();
        
	        //-----------------------  Y Axis
	        mvStack.pushMatrix();
	        mvStack.translate(axisLocX, axisLocY, axisLocZ);
	        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glDrawArrays(GL_LINES, 0, 9);
	      //Texture
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vco[4]);
		    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		    gl.glEnableVertexAttribArray(1);
		    gl.glActiveTexture(GL_TEXTURE0);
		    gl.glBindTexture(GL_TEXTURE_2D, blue);
		    gl.glEnable(GL_DEPTH_TEST);
		    gl.glDepthFunc(GL_LEQUAL);
		    gl.glDrawArrays(GL_LINES, 0, 9);
	        mvStack.popMatrix();
        
	        //-----------------------  Z Axis
	        mvStack.pushMatrix();
	        mvStack.translate(axisLocX, axisLocY, axisLocZ);
	        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
	        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
	        gl.glEnableVertexAttribArray(0);
	        gl.glDrawArrays(GL_LINES, 0, 9);
	        //Texture
	        gl.glBindBuffer(GL_ARRAY_BUFFER, vco[5]);
		    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		    gl.glEnableVertexAttribArray(1);
		    gl.glActiveTexture(GL_TEXTURE0);
		    gl.glBindTexture(GL_TEXTURE_2D, green);
		    gl.glEnable(GL_DEPTH_TEST);
		    gl.glDepthFunc(GL_LEQUAL);
		    gl.glDrawArrays(GL_LINES, 0, 9);
	        mvStack.popMatrix();
		}
		
		//------------------------ Triangle == Asteroid Belt
		for(int i = 0; i<50; i++)
		{
			int randomNumber = r.nextInt(30);
			mvStack.pushMatrix();
			mvStack.translate(Math.sin(amt*randomNumber/6)*18.0f, 5.0f, Math.cos(amt*randomNumber/6)*18.0f);
			mvStack.pushMatrix();
			mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,1.0);
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
			gl.glDrawArrays(GL_TRIANGLES, 0, 3);
			//Texture
			gl.glBindBuffer(GL_ARRAY_BUFFER, vco[7]);
		    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		    gl.glEnableVertexAttribArray(1);
		    gl.glActiveTexture(GL_TEXTURE0);
		    gl.glBindTexture(GL_TEXTURE_2D, asteroid);
		    gl.glEnable(GL_DEPTH_TEST);
		    gl.glDepthFunc(GL_LEQUAL);
		    gl.glDrawArrays(GL_TRIANGLES, 0, 3); 
	        mvStack.popMatrix();  
	        mvStack.popMatrix();  
        }
		
		
		
		//-----------------------  cube == planet  
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt)*12.0f, 0.0f, Math.cos(amt)*12.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
		//Texture
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[2]);
	    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
	    gl.glEnableVertexAttribArray(1);
	    gl.glActiveTexture(GL_TEXTURE0);
	    gl.glBindTexture(GL_TEXTURE_2D, earth);
	    gl.glEnable(GL_DEPTH_TEST);
	    gl.glDepthFunc(GL_LEQUAL);
	    gl.glDrawArrays(GL_TRIANGLES, 0, 36); 
        mvStack.popMatrix();  
		
		
		//-----------------------  smaller cube == moon
		mvStack.pushMatrix();
        mvStack.translate(0.0f, Math.sin(amt)*2.0f, Math.cos(amt)*2.0f);
        mvStack.rotate((System.currentTimeMillis())/10.0,0.0,0.0,1.0);
        mvStack.scale(0.25, 0.25, 0.25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        //Texture
        gl.glBindBuffer(GL_ARRAY_BUFFER, vco[1]);
	    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
	    gl.glEnableVertexAttribArray(1);
	    gl.glActiveTexture(GL_TEXTURE0);
	    gl.glBindTexture(GL_TEXTURE_2D, moon);
	    gl.glEnable(GL_DEPTH_TEST);
	    gl.glDepthFunc(GL_LEQUAL);
	    gl.glDrawArrays(GL_TRIANGLES, 0, 36); 
        mvStack.popMatrix();  
        mvStack.popMatrix(); 
        
      //-----------------------  cube == planet  
      		mvStack.pushMatrix();
      		mvStack.translate(Math.sin(amt*2)*25.0f, 0.0f, Math.cos(amt*2)*25.0f);
      		mvStack.pushMatrix();
      		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
      		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
      		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
      		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
      		gl.glEnableVertexAttribArray(0);
      		gl.glDrawArrays(GL_TRIANGLES, 0, 36);
      		//Texture
      		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[6]);
      	    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
      	    gl.glEnableVertexAttribArray(1);
      	    gl.glActiveTexture(GL_TEXTURE0);
      	    gl.glBindTexture(GL_TEXTURE_2D, jupiter);
      	    gl.glEnable(GL_DEPTH_TEST);
      	    gl.glDepthFunc(GL_LEQUAL);
      	    gl.glDrawArrays(GL_TRIANGLES, 0, 36); 
            mvStack.popMatrix();  
      		
      		//-----------------------  smaller cube == moon
      		mvStack.pushMatrix();
            mvStack.translate(0.0f, Math.sin(amt*6)*5.0f, Math.cos(amt)*5.0f);
            mvStack.rotate((System.currentTimeMillis())/10.0,0.0,0.0,1.0);
            mvStack.scale(0.20, 0.20, 0.20);
            gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
            gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            gl.glEnableVertexAttribArray(0);
            gl.glDrawArrays(GL_TRIANGLES, 0, 36);
            //Texture
            gl.glBindBuffer(GL_ARRAY_BUFFER, vco[1]);
      	    gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
      	    gl.glEnableVertexAttribArray(1);
      	    gl.glActiveTexture(GL_TEXTURE0);
      	    gl.glBindTexture(GL_TEXTURE_2D, moon);
      	    gl.glEnable(GL_DEPTH_TEST);
      	    gl.glDepthFunc(GL_LEQUAL);
      	    gl.glDrawArrays(GL_TRIANGLES, 0, 36); 
            mvStack.popMatrix();  
        
        mvStack.popMatrix();  mvStack.popMatrix();mvStack.popMatrix();
        
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		float[] cube_positions =
		{	-1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f,  1.0f, -1.0f, -1.0f,  1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, 1.0f, -1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f, 1.0f,  1.0f, -1.0f,
			1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f,  1.0f,  1.0f, 1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f, -1.0f, -1.0f,  1.0f, -1.0f, -1.0f,  1.0f,  1.0f,
			-1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f, -1.0f,
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,  1.0f,
			-1.0f,  1.0f, -1.0f, 1.0f,  1.0f, -1.0f, 1.0f,  1.0f,  1.0f,
			1.0f,  1.0f,  1.0f, -1.0f,  1.0f,  1.0f, -1.0f,  1.0f, -1.0f
		};
		
		
	    float[] cubeTex =
	    {
	        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Front
	        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Right
	        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Left
	        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Back
	        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Bottom
	        0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Top
	    };
	    
	    float[] triangle_positions = 
	    {
	    	0.0f,  0.5f, // Vertex 1 (X, Y)
	        0.5f, -0.5f, // Vertex 2 (X, Y)
	       -0.5f, -0.5f  // Vertex 3 (X, Y)
//	    	0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Front
//	    	0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,    //Back
	    };
	    
	    float[] triangleTex = 
	    {
	    	0.0f, 0.5f, 1.0f, 0.0f, 0.0f,
		    0.0f, -0.5f, 0.0f, 1.0f, 0.0f,
		    -0.5f, -0.5f, 0.0f, 0.0f, 1.0f,
	    };
		
		float[] pyramid_positions =
		{	-1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,    //front
			1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,    //right
			1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,  //back
			-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,  //left
			-1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, //LF
			1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f  //RR
		};
		
		float[] pyramidTex =
	    {    
	            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
	            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
	            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
	            0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
	            0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
	            1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
	    };
		
		float[] x_axis_positions  = 
		{	
			-100.0f, 0.0f, 0.0f, 100.0f, 0.0f, 0.0f
			
		};
		
		float[] y_axis_positions  = 
			{	
				0.0f, -100.0f, 0.0f, 0.0f, 100.0f, 0.0f
				
			};
		
		float[] z_axis_positions  = 
			{	
				0.0f, 0.0f, -100.0f, 0.0f, 0.0f, 100.0f
				
			};
	
		float[] lineTex = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f };
		
		Vertex3D[] vertices = sphere.getVertices();
        int[] indices = sphere.getIndices();
        
        float[] pvalues = new float[indices.length*3];
        float[] tvalues = new float[indices.length*2];
        float[] nvalues = new float[indices.length*3];
        
        for (int i = 0; i < indices.length; i++)
        {
            pvalues[i*3] = (float) (vertices[indices[i]]).getX();
            pvalues[i*3+1] = (float) (vertices[indices[i]]).getY();
            pvalues[i*3+2] = (float) (vertices[indices[i]]).getZ();
            tvalues[i*2] = (float) (vertices[indices[i]]).getS();
            tvalues[i*2+1] = (float) (vertices[indices[i]]).getT();
            nvalues[i*3] = (float) (vertices[indices[i]]).getNormalX();
            nvalues[i*3+1]= (float)(vertices[indices[i]]).getNormalY();
            nvalues[i*3+2]=(float) (vertices[indices[i]]).getNormalZ();
        }
		
		//
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);

		//MODELS************************************************************************************************
		gl.glGenBuffers(vbo.length, vbo, 0);

		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);   //Cube
		FloatBuffer cubeBuf = Buffers.newDirectFloatBuffer(cube_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, cubeBuf.limit()*4, cubeBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);	//Pyramid
		FloatBuffer pyrBuf = Buffers.newDirectFloatBuffer(pyramid_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, pyrBuf.limit()*4, pyrBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);	//X Axis
		FloatBuffer axisBuf1 = Buffers.newDirectFloatBuffer(x_axis_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, axisBuf1.limit()*4, axisBuf1, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);	//Y Axis
		FloatBuffer axisBuf2 = Buffers.newDirectFloatBuffer(y_axis_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, axisBuf2.limit()*4, axisBuf2, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);	//Z Axis
		FloatBuffer axisBuf3 = Buffers.newDirectFloatBuffer(z_axis_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, axisBuf3.limit()*4, axisBuf3, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(pvalues);
        gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit()*4, vertBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        FloatBuffer texBuf = Buffers.newDirectFloatBuffer(tvalues);
        gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit()*4, texBuf, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        FloatBuffer norBuf = Buffers.newDirectFloatBuffer(nvalues);
        gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit()*4,norBuf, GL_STATIC_DRAW);
        
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        FloatBuffer triangleBuf = Buffers.newDirectFloatBuffer(triangle_positions);
        gl.glBufferData(GL_ARRAY_BUFFER, triangleBuf.limit()*4,triangleBuf, GL_STATIC_DRAW);
		
		//TEXTURES************************************************************************************************
		gl.glGenBuffers(vco.length, vco, 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[0]);	//Sun Texture
		FloatBuffer pyramidBuf = Buffers.newDirectFloatBuffer(pyramidTex);
		gl.glBufferData(GL_ARRAY_BUFFER, pyramidBuf.limit()*4, pyramidBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[1]);	//Moon Texture
		FloatBuffer moonBuf = Buffers.newDirectFloatBuffer(cubeTex);
		gl.glBufferData(GL_ARRAY_BUFFER, moonBuf.limit()*4, pyramidBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[2]);	//Earth Texture
		FloatBuffer earthBuf = Buffers.newDirectFloatBuffer(cubeTex);
		gl.glBufferData(GL_ARRAY_BUFFER, earthBuf.limit()*4, earthBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[3]);	//Red Texture
		FloatBuffer redBuf = Buffers.newDirectFloatBuffer(cubeTex);
		gl.glBufferData(GL_ARRAY_BUFFER, earthBuf.limit()*4, earthBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[4]);	//Blue Texture
		FloatBuffer blueBuf = Buffers.newDirectFloatBuffer(cubeTex);
		gl.glBufferData(GL_ARRAY_BUFFER, earthBuf.limit()*4, earthBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[5]);	//Green Texture
		FloatBuffer greenBuf = Buffers.newDirectFloatBuffer(cubeTex);
		gl.glBufferData(GL_ARRAY_BUFFER, earthBuf.limit()*4, earthBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[6]);	//Jupiter Texture
		FloatBuffer jupiterBuf = Buffers.newDirectFloatBuffer(cubeTex);
		gl.glBufferData(GL_ARRAY_BUFFER, jupiterBuf.limit()*4, jupiterBuf, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vco[7]);	//Asteroid Texture
		FloatBuffer asteroidBuf = Buffers.newDirectFloatBuffer(triangleTex);
		gl.glBufferData(GL_ARRAY_BUFFER, asteroidBuf.limit()*4, asteroidBuf, GL_STATIC_DRAW);
		
		
	}

	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		r.setElementAt(3,3,0.0f);
		return r;
	}
	
	
	
	public void moveUp(){
		cameraY += 1.0f;
		System.out.println("Up");
	}
	
	public void moveDown(){
		cameraY -= 1.0f;
		System.out.println("Down");
	}
	
	public void moveForward(){
		cameraZ -= 1.0f;
		System.out.println("Forward");
	}
	
	public void moveBackward(){
		cameraZ += 1.0f;
		System.out.println("Backward");
	}
	
	public void moveLeft(){
		cameraX -= 1.0f;
		System.out.println("Left");
	}
	
	public void moveRight(){
		cameraX += 1.0f;
		System.out.println("Right");
	}
	
	public void rotateLeft(){
		cameraV -= 1.0f;
		System.out.println("Rotate Left");
	}

	public void rotateRight(){
		cameraV += 1.0f;
		System.out.println("Rotate Right");
	}
	
	public void rotateUp(){
		cameraU -= 1.0f;
		System.out.println("Rotate Up");
	}

	public void rotateDown(){
		cameraU += 1.0f;
		System.out.println("Rotate Down");
	}
	
	
	public void axisToggle(){
		showVertices = !showVertices;
	}
	
	public Texture loadTexture(String textureFile)
	{
	    Texture tex = null;
	    try { tex = TextureIO.newTexture(new File(textureFile), false); }
	    catch (Exception e) { e.printStackTrace(); }
	    return tex;
	    }
	
	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		setupVertices();
		cameraX = 0.0f; cameraY = 0.0f; cameraZ =  	20.0f;
		cubeLocX = 0.0f; cubeLocY = 0.0f; cubeLocZ = 0.0f;
		pyrLocX = 0.0f; pyrLocY = 0.0f; pyrLocZ = 0.0f;
		axisLocX = 0.0f; axisLocY = 0.0f; axisLocZ = 0.0f;
		
		joglSunTexture = loadTexture("sun.jpg");
		sun = joglSunTexture.getTextureObject();
		joglMoonTexture = loadTexture("moon.jpg");
		moon = joglMoonTexture.getTextureObject();
		joglEarthTexture = loadTexture("earth.jpg");
		earth = joglEarthTexture.getTextureObject();
		redTexture = loadTexture("red.jpg");
		red = redTexture.getTextureObject();
		blueTexture = loadTexture("blue.jpg");
		blue = blueTexture.getTextureObject();
		greenTexture = loadTexture("green.jpg");
		green = greenTexture.getTextureObject();
		jupiterTexture = loadTexture("jupiter.jpg");
		jupiter = jupiterTexture.getTextureObject();
		asteroidTexture = loadTexture("asteroid.jpg");
		asteroid = asteroidTexture.getTextureObject();
		
		System.out.println("OpenGL Version: " + gl.glGetString(gl.GL_VERSION));
        System.out.println("JOGL Version: " + (Package.getPackage("com.jogamp.opengl")).getImplementationVersion());
        System.out.println("Java Version: " + System.getProperty("java.version"));
	}
	
	
	public static void main(String[] args) { new Code();}
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("a2/vert1.shader");
		String fshaderSource[] = util.readShaderSource("a2/frag1.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}

	
}