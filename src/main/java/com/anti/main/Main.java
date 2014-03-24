package com.anti.main;

import java.awt.Image;
import java.awt.PageAttributes.ColorType;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.PngChunkZTXT;

import com.anti.png.PNGEncoder;

public class Main {

	public static void main(String[] args) throws Exception{
		
//		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/src2.png")); // 71 kb
		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/src2.png")); // 71 kb
//		BufferedImage src = ImageIO.read(new File("C:\\Users\\Anti\\Desktop\\src2.png")); // 71 kb
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
        //FOR development only, when online, # this line
        ImageIO.write(img, "png", new File("/Users/Anti/Desktop/tt.png"));   // 2,5 kb
        
		System.out.println("done image transform.");
		
//        BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/test.png"));
//        OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//        PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
//        encoder.encode(bufferedImage);
//        out.close();
//        System.out.println("done 2");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);   
		byte[] originalData = out.toByteArray();
		
//		OutputStream os = new FileOutputStream("/Users/Anti/Desktop/test.png");
		OutputStream os = new FileOutputStream("/Users/anti/Desktop/test.png");
//		OutputStream os = new FileOutputStream("C:\\Users\\Anti\\Desktop\\test.png");
		
		PNGEncoder encoder = new PNGEncoder(os, PNGEncoder.MY_MODE, originalData);
		encoder.encode(img);
		
		os.close();
		
		System.out.println("all done ~~~");
		
		//it seemed that it works !
		//try get tt.png, and rewrite tt.png
//		File imgfile = new File("/Users/Anti/Desktop/tt.png");
//		PngReader reader = new PngReader(imgfile);
//		
//		PngWriter writer = new PngWriter(new File("/Users/Anti/Desktop/t2.png"), reader.imgInfo, true);
//		writer.copyChunksFrom(reader.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
//		writer.getMetadata().setText("author", "希尔瓦娜斯", true, true);
//		
//		for(int row = 0; row < reader.imgInfo.rows; row++){
//			IImageLine l1 = reader.readRow();
//			writer.writeRow(l1);
//		}
//		
//		reader.end();
//		writer.end();
		
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
	

}
