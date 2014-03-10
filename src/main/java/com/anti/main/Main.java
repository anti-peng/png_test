package com.anti.main;

import java.awt.Image;
import java.awt.PageAttributes.ColorType;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.objectplanet.image.PngEncoder;

public class Main {

	public static void main(String[] args) throws Exception{
		
//		OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//		
//		BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/test.png"));
//		
//		PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
//		encoder.encode(bufferedImage);
		Main m = new Main();
		m.pngMethod();
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
