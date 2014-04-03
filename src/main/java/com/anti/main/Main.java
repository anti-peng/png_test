package com.anti.main;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

	/*
	 * args[0] get zTXt chunks
	 * args[1] file to be processed
	 */
	public static void main(String[] args) throws Exception{
		
//		//1. process image 
//		BufferedImage src = ImageIO.read(new File(args[1])); 
//        // here goes custom palette 
//        IndexColorModel cm = new IndexColorModel(
//                2, 4,
//                new byte[]{-16,     86,    -74,	   -6},
//                new byte[]{-31,     63,    -78,    -12},
//                new byte[]{-78,     44,    -117,    -44});
//        BufferedImage img = new BufferedImage(
//                src.getWidth(), src.getHeight(), 
//                BufferedImage.TYPE_BYTE_BINARY, 
//                cm); 
//        Graphics2D g2 = img.createGraphics();
//        g2.drawImage(src, 0, 0, null);
//        g2.dispose();          
//        ImageIO.write(img, "png", new File("file_we_want.png"));
//		
//		//2. analysis original png file
//		File source_img = new File(args[0]);
//		PngReader source_reader = new PngReader(source_img);
//		PngReader data_reader = new PngReader(new File("file_we_want.png"));
//		
//		PngWriter writer = new PngWriter(new File("final.png"), source_reader.imgInfo, true);
//		writer.copyChunksFrom(source_reader.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
//		
//		for(int row = 0; row < data_reader.imgInfo.rows; row++){
//			IImageLine l = data_reader.readRow();
//			writer.writeRow(l);
//		}
//		
//		source_reader.end();
//		data_reader.end();
//		writer.end();
		
		//3. 找到各个chunk
		File f = new File("/Users/anti/Desktop/mmluna/application/final.png");
		FileInputStream fis = new FileInputStream(f);
		int length = (int) f.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		byte[] b = new byte[length];
		int n;
		while((n = fis.read(b)) != -1){
			bos.write(b, 0, n);
		}
		fis.close();
		bos.close();
		antiPng(bos.toByteArray());
	}
	
	void write(int i) throws IOException {
        byte b[]={(byte)((i>>24)&0xff),(byte)((i>>16)&0xff),(byte)((i>>8)&0xff),(byte)(i&0xff)};
        this.printHexString(b);
    }
	
	private static void printHexString( byte[] b) {  
	   for (int i = 0; i < b.length; i++) { 
	     String hex = Integer.toHexString(b[i] & 0xFF); 
	     if (hex.length() == 1) { 
	       hex = '0' + hex; 
	     } 
	     System.out.print(hex.toUpperCase() ); 
	   } 
	}

	private static int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
    }
	
	private static void antiPng(byte[] b) throws Exception{
		
		int offset = 0;
		offset += 8;//8 .PNG
		System.out.println("=====");
		
		int index_plte = 0;
		int length_plte = 0;
		int index_ztxt1 = 0;
		int length_ztxt1 = 0;
		int index_ztxt2 = 0;
		int length_ztxt2 = 0;
		
		while(true){
			System.out.println("offset = " + offset);
			int chunk_len = readInt(b, offset);	//chunk length
			System.out.println("chunLen = " + chunk_len);
			int chunk_offset = offset;
			byte[] xx = new byte[]{b[chunk_offset+4], b[chunk_offset+5], b[chunk_offset+6], b[chunk_offset+7]};
			String chunk_name = new String(xx);
			System.out.println(new String(xx));	//print chunk name
			System.out.println();
			//还是改成放在IDAT前面吧  这样有保证  放在PLTE后面不能保证ZTXT一定在IDAT前面啊
			//先在游戏中测试一下ZTXT的什么位置才能跑得起来再做  基本框架已经成型
			//在b中删除ztxt的方法还没找到  不知道有木有Arrays.removeRange...
			if(chunk_name.equals("IDAT")){
				index_plte = offset;
				length_plte = readInt(b, offset);
			}
			else if(chunk_name.equals("zTXt")){
				if(index_ztxt1 == 0){
					index_ztxt1 = offset;
					length_ztxt1 = readInt(b, offset);
				}
				else{
					index_ztxt2 = offset;
					length_ztxt2 = readInt(b, offset);
				}
			}
			else if(chunk_name.equals("IEND"))
				break;
			
			//recount offset
			offset += (4 + 4 + chunk_len + 4);
		}	
		
		//rewrite byte
		File doneFile = new File("/Users/anti/Desktop/mmluna/application/this.png");
		FileOutputStream fos = new FileOutputStream(doneFile);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		
		//if chunk PLTE is before chunk zTXt
		//同时假设plte在idat的前面，
		if(index_plte < index_ztxt1){
			bos.write(Arrays.copyOfRange(b, 0, index_plte + 4 + 4 + length_plte + 4 - 1));
			bos.write(Arrays.copyOfRange(b, index_ztxt1, 4 + 4 + length_ztxt1 + 4 - 1));
			bos.write(Arrays.copyOfRange(b, index_ztxt2, 4 + 4 + length_ztxt2 + 4 - 1));
		}
	}
}
