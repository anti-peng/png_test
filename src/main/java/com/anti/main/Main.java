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
		
//		OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//		BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/cat.png"));
//		PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
//		encoder.encode(bufferedImage);
		
		
//		Main m = new Main();
//		m.write(-11124948);
		
		/*
		 * 1. get to know if 0803 model is suitable for mabinogi
		 * 2. how PLTE and iDAT chunks co-works
		 */
		
		
		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/src2.png")); // 71 kb
		
        // here goes custom palette
        IndexColorModel cm = new IndexColorModel(
                2, 4,
                new byte[]{-16,     86,    -74,	   -6},
                new byte[]{-31,     63,    -78,    -12},
                new byte[]{-78,     44,    -117,    -44});
        BufferedImage img = new BufferedImage(
                src.getWidth(), src.getHeight(), // match source
                BufferedImage.TYPE_BYTE_BINARY, // required to work
                cm); // custom color model (i.e. palette)
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();          
        // output
        ImageIO.write(img, "png", new File("/Users/Anti/Desktop/test.png"));   // 2,5 kb        
		System.out.println("done 1");
		
        BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/test.png"));
        OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
        PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
        encoder.encode(bufferedImage);
        out.close();
        System.out.println("done 2");
		
//		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/cat.png")); // 71 kb
//		BufferedImage tempImage = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
//		Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
//		g2d.drawImage(src, 0, 0, null);
//		ImageIO.write(tempImage, "png", new File("/Users/Anti/Desktop/test.png"));
//		System.out.println("done");
		
		
//		Main m = new Main();
//		
//		for(int w = 0; w < src.getWidth(); w++){
//			for(int h = 0; h < src.getHeight(); h ++){
//				System.out.println("RGB = " + Integer.toHexString(src.getRGB(w, h)));
////				m.write(src.getRGB(w, h));
//				if(w == 1)
//					break;
//			}
//		}
		
		
		
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
	     System.out.println(hex.toUpperCase() ); 
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
