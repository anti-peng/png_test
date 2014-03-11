package com.anti.main;

import java.awt.Image;
import java.awt.PageAttributes.ColorType;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javax.imageio.ImageIO;

import com.anti.png.PNGEncoder;

public class Main {

	public static void main(String[] args) throws Exception{
		//参考pngencoder 同样是bufferedimage  修改一下  不用GRAPHIC 2D
		
//		OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//		BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/cat.png"));
//		
//		PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
//		encoder.encode(bufferedImage);
		
		
//		Main m = new Main();
//		
//		m.write(-11124948);
		
//		Image img = Toolkit.getDefaultToolkit().getImage("/Users/Anti/Desktop/palette_test.png");
//		BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/palette_test.png"));
//		
//		System.out.println(bufferedImage.getPropertyNames());	//null
//		
//		System.out.println(bufferedImage.getColorModel().getPixelSize());
//		
//		System.out.println(bufferedImage.getType());
		
		//////////////////////////////////////////////////////////////////////////////////////
		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/palette_test.png")); // 71 kb
//		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/test2.png")); // 71 kb
		
		
//		System.out.println(src.getColorModel().getPixelSize());

        // here goes custom palette
        IndexColorModel cm = new IndexColorModel(
                2, 4,
                new byte[]{-16,     86,    -74,	   -6},
                new byte[]{-31,     63,    -78,    -12},
                new byte[]{-78,     44,    -117,    -44});
                //          RED  GREEN1 GREEN2  BLUE  WHITE BLACK              
//                new byte[]{-100,     0,     0,    0,    -1,     0},
//                new byte[]{   0,  -100,    60,    0,    -1,     0},
//                new byte[]{   0,     0,     0, -100,    -1,     0});

        // draw source image on new one, with custom palette
        BufferedImage img = new BufferedImage(
                src.getWidth(), src.getHeight(), // match source
                BufferedImage.TYPE_BYTE_INDEXED, // required to work
                cm); // custom color model (i.e. palette)
        
//        Graphics2D g2 = img.createGraphics();
//        g2.drawImage(src, 0, 0, null);
//        g2.dispose();
        
        // output
        ImageIO.write(img, "png", new File("/Users/Anti/Desktop/test2.png"));   // 2,5 kb
		
		System.out.println("done");
		
//		Main m = new Main();
//		m.write(-100);
//		System.out.println(-15 & 0xff);
		
	}
	
	
	
	void write(int i) throws IOException {
        byte b[]={(byte)((i>>24)&0xff),(byte)((i>>16)&0xff),(byte)((i>>8)&0xff),(byte)(i&0xff)};
        this.printHexString(b);
    }
	
	public static void printHexString( byte[] b) {  
	   for (int i = 0; i < b.length; i++) { 
	     String hex = Integer.toHexString(b[i] & 0xFF); 
	     if (hex.length() == 1) { 
	       hex = '0' + hex; 
	     } 
	     System.out.print(hex.toUpperCase() ); 
	   } 

	}
	
	public void pngMethod() throws Exception{
		
		//source image
//		Toolkit tk = Toolkit.getDefaultToolkit();
//		Image img = tk.getImage("/Users/Anti/Desktop/test.png");
		
		//create encoder once
		//width 	44
		//height	42
		//Bit Depth	2
		//ColorType	3
		//compress	0
		//filter	0
		//interlace	0
//		PngEncoder encoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR, PngEncoder.DEFAULT_COMPRESSION);
//		encoder.setIndexedColorMode(3);
//		
//		OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//		
//		encoder.encode(img, out);
		
		//palette length:12, 4 entries
		
	}
	
}
