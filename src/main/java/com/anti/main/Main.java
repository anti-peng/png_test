package com.anti.main;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
		
		//change the sequence of the chunks - put zTXt chunks ahead of PLTE
		//找到ztxt1, ztxt2
		//新建byte[]
		//找到gama，写入源头文件+GAMA
		//写入ztxt1, ztxt2
		//写入源IDAT-ztxt开始
		//写入end
		//流终止
		File f = new File("/Users/anti/Desktop/mmluna/application/final.png");
		BufferedImage img = ImageIO.read(f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(img, "png", out);
		System.out.println("file length = " + out.toByteArray().length);
//		getChunkName(out.toByteArray());
		antiPng(out.toByteArray());
		out.close();
		System.out.println("done");
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
	
//	int offset = 8;
//    int chunkLen = 0;
//    while(true){
//    	if(originalData[offset + 4] == 0x49 && originalData[offset + 5] == 0x44
//                && originalData[offset + 6] == 0x41 && originalData[offset + 7] == 0x54){
//    		chunkLen = readInt(originalData, offset);
////    		System.out.println(chunkLen);	//1234_IDAT_DATA_CRC_ 这是data的长度
////    		System.out.println("offset=" + offset);		//这个是1234之前的数据块长度，实际长度从 58 - 58+4+4+chunkLen+4  
//    		//for(58 ~ 58+4+4+chunkLen+4) --> write(byte[i])  --> 无损写入原数据
//    		break;
//    	}else{
//    		chunkLen = readInt(originalData, offset);
//    		offset += (4 + 4 + chunkLen + 4);
//    	}
//    }
//    byte[] xx = new byte[chunkLen+12];
//    for(int i = offset; i <= (offset+11+chunkLen); i++){
//    	xx[i-offset] = originalData[i];
//    }
//    writeX(xx);
	
	private static void getChunkName(byte[] b){
		int offset = 4;
		int chunkLen = 0;
		int flag = 0;
		while(true){
			//zTXt
			if(b[offset + 1] == 0x7A && b[offset + 2] == 0x54 && b[offset + 3] == 0x58 && b[offset + 4] == 0x74){
				System.out.println(offset);
				byte[] x = new byte[]{b[offset-3], b[offset-2], b[offset-1], b[offset]};
				printHexString(x);
				System.out.println("?");
				flag += 1;
				if(flag == 2)
					break;
			}
			chunkLen = readInt(b, offset);
			offset += (4 + 4 + chunkLen + 4);
		}
	}

	private static int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
    }
	private static long readLong(byte[] buf){
		return (((buf[0]&0xffL)<<56)|((buf[1]&0xffL)<<48)|
                ((buf[2]&0xffL)<<40)|((buf[3]&0xffL)<<32)|((buf[4]&0xffL)<<24)|
                  ((buf[5]&0xffL)<<16)|((buf[6]&0xffL)<<8)|(buf[7]&0xffL));
	}
	
	private static void antiPng(byte[] b){
		
		int offset = 0;
		
		offset += 8;//8 .PNG
		
		byte[] shit = new byte[]{b[37], b[38], b[39], b[40]};
		System.out.println(new String(shit));
		System.out.println("=====");
		printHexString(shit);
		
		while(true){
			int zTXt_count = 0;
			System.out.println("offset = " + offset);
			int chunk_len = readInt(b, offset);	//chunk length
			System.out.println("chunLen = " + chunk_len);
			int chunk_offset = offset;
			byte[] xx = new byte[]{b[chunk_offset+4], b[chunk_offset+5], b[chunk_offset+6], b[chunk_offset+7]};
			printHexString(xx);
			System.out.println();
			if(b[chunk_offset + 4] == 0x7A && b[chunk_offset + 5] == 0x54 && b[chunk_offset + 6] == 0x58 && b[chunk_offset + 7] == 0x74){
				//打印chunk_type
				byte[] x = new byte[]{b[chunk_offset+4], b[chunk_offset+5], b[chunk_offset+6], b[chunk_offset+7]};
				printHexString(x);
				zTXt_count += 1;
				if(zTXt_count == 2)
					break;
			}
			//recount offset
			offset += (4 + 4 + chunk_len + 4);
		}
		
		//游戏本来的图片结构是 png, IHDR, PLTE, ZTXT, ZTXT, IDAT, IEND
		//含有gAMA信息的图片，解析的时候好像被自动忽略了
		//现在写得PLTE没用上，都是从原图copy的，但是原图可能只有两种颜色-》因为是从游戏中直接画得话。。。
		
	}
}
