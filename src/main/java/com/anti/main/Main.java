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

import com.anti.png.PNGEncoder;

public class Main {

	public static void main(String[] args) throws Exception{
		
//		OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//		BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/cat.png"));
//		PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
//		encoder.encode(bufferedImage);
		
		
//		BufferedImage src = ImageIO.read(new File("/Users/Anti/Desktop/src2.png")); // 71 kb
		BufferedImage src = ImageIO.read(new File("C:\\Users\\Anti\\Desktop\\sample.png")); // 71 kb
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
        ImageIO.write(img, "png", new File("C:\\Users\\Anti\\Desktop\\test.png"));   // 2,5 kb
        
		System.out.println("done 1");
		
//        BufferedImage bufferedImage = ImageIO.read(new FileInputStream("/Users/Anti/Desktop/test.png"));
//        OutputStream out = new FileOutputStream("/Users/Anti/Desktop/test2.png");
//        PNGEncoder encoder = new PNGEncoder(out, PNGEncoder.MY_MODE);
//        encoder.encode(bufferedImage);
//        out.close();
//        System.out.println("done 2");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);   // 2,5 kb
		byte[] alldata = out.toByteArray();
		
		Main m = new Main();
		m.analyze(alldata);
		
	}
	
	private void analyze(byte[] data) {
        int offset = 8;
        int chunkLen = 0;
        while(true){
        	if(data[offset + 4] == 0x49 && data[offset + 5] == 0x44
                    && data[offset + 6] == 0x41 && data[offset + 7] == 0x54){
        		byte[] x = new byte[]{data[offset+4], data[offset+5], data[offset+6], data[offset+7]};
        		chunkLen = readInt(data, offset);
        		
        		System.out.println(chunkLen);	//1234_IDAT_DATA_CRC_ 这是data的长度
        		System.out.println("offset=" + offset);		//这个是1234之前的数据块长度，实际长度从 58 - 58+4+4+chunkLen+4  
        		//for(58 ~ 58+4+4+chunkLen+4) --> write(byte[i])  --> 无损写入原数据
        		printHexString(x);
        		break;
        	}else{
        		chunkLen = readInt(data, offset);
        		offset += (4 + 4 + chunkLen + 4);
        	}
        }
//        para[2] = chunkLen / 3;
//        para[0] = offset + 8;
//        para[1] = offset + 8 + chunkLen;
    }
//	private void analyze(byte[] data){
//    	int offset = 0;
//    	int chunkLen = 0;
//    	 while (data[offset + 1] == 0x49 && data[offset + 2] == 0x44
//    			 && data[offset + 3] == 0x41 && data[offset + 4] == 0x54) {
//              chunkLen = readInt(data, offset);
//              System.out.println(chunkLen);
//              offset += 4;
//          }
//    }
    private int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
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
