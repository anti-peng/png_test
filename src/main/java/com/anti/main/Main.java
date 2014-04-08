package com.anti.main;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.awt.Graphics2D;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

public class Main {

	public static void main(String[] args) throws Exception{
		
		File f1 = new File("/Users/Anti/Desktop/mmluna/application/temp1.png");
		File f2 = new File("/Users/Anti/Desktop/mmluna/application/temp2.png");
		
		//1. process image 
		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/mmluna/application/tobe.png")); 	//png to be decoded
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
        ImageIO.write(img, "png", f1);
        
		//2. analysis original png file
		File source_img = new File("/Users/Anti/Desktop/mmluna/application/source.png");	//source.png
		PngReader source_reader = new PngReader(source_img);
		PngReader data_reader = new PngReader(f1);
		
		PngWriter writer = new PngWriter(f2, data_reader.imgInfo, true);
		writer.copyChunksFrom(source_reader.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
		
		for(int row = 0; row < data_reader.imgInfo.rows; row++){
			IImageLine l = data_reader.readRow();
			writer.writeRow(l);
		}
		
		source_reader.end();
		data_reader.end();
		writer.end();
		
		//3. chunk sequence
		FileInputStream fis = new FileInputStream(f2);
		int l = (int) f2.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(l);
		byte[] b = new byte[l];
		int n;
		while((n = fis.read(b)) != -1){
			bos.write(b, 0, n);
		}
		fis.close();
		bos.close();
		
		pngSequence(bos.toByteArray());
		
		f1.delete();
		f2.delete();
		
		System.out.println("all done");
	}

	private static int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
    }
	
	private static void pngSequence(byte[] b) throws Exception{
		
		int offset = 0;
		offset += 8;
		
		int index_IHDR = 0;
		int length_IHDR = 0;
		
		int index_gAMA = 0;
		int length_gAMA = 0;
		
		int index_zTXt1 = 0;
		int length_zTXt1 = 0;
		
		int index_zTXt2 = 0;
		int length_zTXt2 = 0;
		
		int index_PLTE = 0;
		int length_PLTE = 0;
		
		int index_IDAT = 0;
		int length_IDAT = 0;
		
		int index_IEND = 0;
		int length_IEND = 4;
		
		int count_ztxt = 0;
		
		while(true){
			int chunk_len = readInt(b, offset);	//chunk length
			int chunk_offset = offset;
			
			//print chunk name, development only
			byte[] xx = new byte[]{b[chunk_offset+4], b[chunk_offset+5], b[chunk_offset+6], b[chunk_offset+7]};
			String chunk_name = new String(xx);
			System.out.println(chunk_name);
			System.out.println("index: " + offset);
			System.out.println("length: " + chunk_len);
			System.out.println();
			
			switch(chunk_name){
			case "IHDR": index_IHDR = offset; length_IHDR = readInt(b, offset); break;
			case "gAMA": index_gAMA = offset; length_gAMA = readInt(b, offset); break;
			case "PLTE": index_PLTE = offset; length_PLTE = readInt(b, offset); break;
			case "IDAT": index_IDAT = offset; length_IDAT = readInt(b, offset); break;
			case "zTXt": count_ztxt += 1; if(count_ztxt == 1){index_zTXt1 = offset; length_zTXt1 = readInt(b, offset);}else{index_zTXt2 = offset; length_zTXt2 = readInt(b, offset);} break;
			case "IEND": index_IEND = offset; length_IEND = readInt(b, offset); break;
			}
			
			if(chunk_name.equals("IEND"))
				break;
			
			//recount offset
			offset += (4 + 4 + chunk_len + 4);
		}
		
		//rewrite byte
		File doneFile = new File("/Users/Anti/Desktop/mmluna/application/this.png");
		FileOutputStream fos = new FileOutputStream(doneFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		//write to stream
		bos.write(Arrays.copyOfRange(b, 0, 8));	//start
		bos.write(Arrays.copyOfRange(b, index_IHDR, index_IHDR + length_IHDR + 12));	//IHDR
		if(length_gAMA != 0) bos.write(Arrays.copyOfRange(b, index_gAMA, index_gAMA + length_gAMA + 12));	//gAMA
		if(length_zTXt1 != 0) bos.write(Arrays.copyOfRange(b, index_zTXt1, index_zTXt1 + length_zTXt1 + 12));	//zTXt1
		if(length_zTXt2 != 0) bos.write(Arrays.copyOfRange(b, index_zTXt2, index_zTXt2 + length_zTXt2 + 12));	//zTXt2
		if(length_PLTE != 0) bos.write(Arrays.copyOfRange(b, index_PLTE, index_PLTE + length_PLTE + 12));	//PLTE
		bos.write(Arrays.copyOfRange(b, index_IDAT, index_IDAT + length_IDAT + 12));	//IDAT
		bos.write(Arrays.copyOfRange(b, index_IEND, index_IEND + length_IEND + 12));	//IEND
		
		bos.close();
		fos.close();
	}
}
