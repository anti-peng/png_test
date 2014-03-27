package com.anti.main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.image.IndexColorModel;
import java.io.File;

import javax.imageio.ImageIO;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

public class Main {

	/*
	 * args[0] source file to get zTXt chunks
	 * args[1] file to be processed
	 */
	public static void main(String[] args) throws Exception{
		
		//1. process image 
		BufferedImage src = ImageIO.read(new File(args[1])); 
        // here goes custom palette 
        IndexColorModel cm = new IndexColorModel(
                2, 4,
                new byte[]{-16,     86,    -74,	   -6},
                new byte[]{-31,     63,    -78,    -12},
                new byte[]{-78,     44,    -117,    -44});
        BufferedImage img = new BufferedImage(
                src.getWidth(), src.getHeight(), 
                BufferedImage.TYPE_BYTE_BINARY, 
                cm); 
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();          
        ImageIO.write(img, "png", new File("file_we_want.png"));
		
		//2. analysis original png file
		File source_img = new File(args[0]);
		PngReader source_reader = new PngReader(source_img);
		PngReader data_reader = new PngReader(new File("file_we_want.png"));
		
		PngWriter writer = new PngWriter(new File("final.png"), source_reader.imgInfo, true);
		writer.copyChunksFrom(source_reader.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
		
		for(int row = 0; row < data_reader.imgInfo.rows; row++){
			IImageLine l = data_reader.readRow();
			writer.writeRow(l);
		}
		
		source_reader.end();
		data_reader.end();
		writer.end();
		System.out.println("done");
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
