package com.anti.png;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.PngChunkZTXT;

public class PNGEncoder extends Object {

    /** black and white image mode. */    
    public static final byte BW_MODE = 0;
    /** grey scale image mode. */    
    public static final byte GREYSCALE_MODE = 1;
    /** full color image mode. */    
    public static final byte COLOR_MODE = 2;
    
    public static final byte MY_MODE = 3;
    
    OutputStream out;
    CRC32 crc;
    byte mode;
    byte[] originalData;

    /** public constructor of PNGEncoder class.
     * @param out output stream for PNG image format to write into
     * @param mode BW_MODE, GREYSCALE_MODE or COLOR_MODE
     */    
    public PNGEncoder(OutputStream out, byte mode, byte[] originalData) {
        crc=new CRC32();
        this.out = out;
        if (mode<0 || mode>3)
            throw new IllegalArgumentException("Unknown color mode");
        this.mode = mode;
        this.originalData = originalData;
    }

    //int 转 byte 并写入, 写入后更新crc : 注意写数字直接长度=4，是特地为了写长度和宽度啊。。。
    //0xff = 255.   
    void write(int i) throws IOException {
        byte b[]={(byte)((i>>24)&0xff),(byte)((i>>16)&0xff),(byte)((i>>8)&0xff),(byte)(i&0xff)};
        write(b);
    }
    
    void write0(int i) throws IOException {
        byte b[]={(byte)(i&0xff)};
        write(b);
    }
    
    //写入byte并更新crc
    void write(byte b[]) throws IOException {
        out.write(b);
        crc.update(b);
    }
    
    void writeOffsetCrc(byte b[], int off, int len) throws IOException {
    	out.write(b);
    	crc.update(b, off, len);
    }
    
    void writeX(byte b[]) throws IOException {
        out.write(b);
    }
    
    /** main encoding method (stays blocked till encoding is finished).
     * @param image BufferedImage to encode
     * @throws IOException IOException
     */    
    public void encode(BufferedImage image) throws IOException {
        int width = image.getWidth(null);
        int height = image.getHeight(null);

        //写入头文件 和 IHDR文件的长度
        //声明 89 50 4E 47 0D 0A 1A 0A 头文件 00 00 00 0D 文件长度13
        final byte id[] = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13};
        write(id);	
        
        //写入IHDR
        crc.reset();
        write("IHDR".getBytes());	//写入IHDR, 至此，第一行写完
        write(width);
        write(height);	//write 宽度高度
        byte head[]=null;
        switch (mode) {
            case BW_MODE: head=new byte[]{1, 0, 0, 0, 0}; break;
            case GREYSCALE_MODE: head=new byte[]{8, 0, 0, 0, 0}; break;
            case COLOR_MODE: head=new byte[]{8, 2, 0, 0, 0}; break;
            case MY_MODE: head=new byte[]{2, 3, 0, 0, 0}; break;
        }                 
        write(head);	//写入头文件
        write((int) crc.getValue());	//写入CRC  头文件和头文件CRC写完
        
        
        //写gAMA块 
        //00 00 00 04 数据块长度 = 4
        write(4);
        //67 41 4D 41 gAMA 
        //00 00 B1 8F gama矫正信息  0.45455 = 45455
        //0B FC 61 05 CRC值
        crc.reset();
        write("gAMA".getBytes());
        write(45445);
        write((int) crc.getValue());
        
        //写入zTXt 1
//        String authid = "-375512830";
//        ByteArrayOutputStream t1 = new ByteArrayOutputStream();
//        BufferedOutputStream bos1 = new BufferedOutputStream(new DeflaterOutputStream(t1));
//        bos1.write(authid.getBytes("iso-8859-1"));
//        bos1.close();
//        //写入length
//        int len1 = t1.size() + 8;
//        write(len1);
//        crc.reset();
//        write("zTXt".getBytes());
//        write("authid".getBytes());
//        write(0);
//        write(0);
//        write(t1.toByteArray());
//        System.out.println(t1);
//        write((int) crc.getValue());
//        
//        System.out.println("t1 = " + t1.size());
        
        
        //static zTXt 1
        byte[] len1 = new byte[]{0, 0, 0, 26};
        byte[] chunk1 = new byte[]{122, 84, 88, 116};
        byte[] key1 = new byte[]{97, 117, 116, 104, 105, 100, 0, 0};
        byte[] value1 = new byte[]{120, -38, -45, 53, -79, 52, -73, 52, 48, 52, -76, 52, 1, 0, 11, 44, 2, 10, -97, 48, -128, -97};
        writeX(len1);
        writeX(chunk1);
        writeX(key1);
        writeX(value1);
        
        write(27);
        crc.reset();
        write("zTXt".getBytes());
        write("author".getBytes());
        write0(0);
        write0(0);
        String author ="希尔瓦娜斯";
//        byte[] authorbyte = ChunkHelper.compressBytes(ChunkHelper.toBytes(author), true);
        ByteArrayInputStream inb = new ByteArrayInputStream(ChunkHelper.toBytesUTF8(author));
        InputStream inx = inb;
        ByteArrayOutputStream outb = new ByteArrayOutputStream();
        OutputStream outx = new DeflaterOutputStream(outb);
        inx.close();
        outx.close();
        write(outb.toByteArray());
        write((int) crc.getValue());
        
        printHexString(outb.toByteArray());
        
//        ByteArrayInputStream inb = new ByteArrayInputStream(ori, offset, len);
//		InputStream in = compress ? inb : new InflaterInputStream(inb, getInflater());
//		ByteArrayOutputStream outb = new ByteArrayOutputStream();
//		OutputStream out = compress ? new DeflaterOutputStream(outb) : outb;
//		shovelInToOut(in, out);
//		in.close();
//		out.close();
//		return outb.toByteArray();
        
//        printHexString(authorbyte);

        //写入zTxt 2
//        String authorStr = "希尔瓦娜斯";
//////        authorStr = new String(authorStr.getBytes("GBK"));
////        ByteArrayOutputStream t2 = new ByteArrayOutputStream();
////        BufferedOutputStream bos2 = new BufferedOutputStream(new DeflaterOutputStream(t2));
////        bos2.write(authorStr.getBytes("ISO-8859-1"));
//       
//        byte[] authorStrbyte = ChunkHelper.toBytesUTF8(authorStr);
//        ByteArrayOutputStream t2 = new ByteArrayOutputStream();
//        DeflaterOutputStream o2 = new DeflaterOutputStream(t2);
//        o2.write(authorStrbyte);
//        o2.close();
//          
////        bos2.close();
//        write(t2.size() + 8);
//        crc.reset();
//        write("zTXt".getBytes());
//        write("author".getBytes());
//        write(0);
//        write(0);
//        write(t2.toByteArray());
//        write((int) crc.getValue());
        
//        String author = "";
//        PngChunkZTXT z2 = new PngChunkZTXT(null);
//		z2.setKeyVal("author", new String(author.getBytes("ISO-8859-1")));
//		byte[] zdata = z2.createRawChunk().data;
//		int len = zdata.length;
//		write(len + 8);
//		crc.reset();
//		write("zTXt".getBytes());
//		write(zdata);
//		write((int) crc.getValue());
//		
//		System.out.println("length = " + len);
//		this.printHexString(zdata);
//		System.out.println();
        
//			example        
//        @Override
//        public ChunkRaw createRawChunk() {
//                if (val.isEmpty() || key.isEmpty())
//                        return null;
//                try {
//                        ByteArrayOutputStream ba = new ByteArrayOutputStream();
//                        ba.write(key.getBytes(PngHelperInternal.charsetLatin1));
//                        ba.write(0); // separator
//                        ba.write(0); // compression method: 0
//                        byte[] textbytes = ChunkHelper.compressBytes(val.getBytes(PngHelperInternal.charsetLatin1), true);
//                        ba.write(textbytes);
//                        byte[] b = ba.toByteArray();
//                        ChunkRaw chunk = createEmptyChunk(b.length, false);
//                        chunk.data = b;
//                        return chunk;
//                } catch (IOException e) {
//                        throw new PngjException(e);
//                }
//        }

        
        //写入PLTE  每个调色板项占用3个字节 R 1byte G 1byte B 1byte
        //00 00 00 0C 长度12
        //50 4C 54 45 PLTE
        //F1 E1 B2 56 3F 2C B6 B2 8B FA F4 D4	 2bit/sample ^ 2 = 4 4色图像, 有4个调色板项，也就是 4 * 3 = 12字节
        //06 30 C6 1F  CRC
        write(12);
        crc.reset();
        write("PLTE".getBytes());
        byte plte[] = {-16, -31, -78, 86, 63, 44, -74, -78, -117, -6, -12, -44};
        write(plte);
        write((int) crc.getValue());
        
        //写入IDAT
        int offset = 8;
        int chunkLen = 0;
        while(true){
        	if(originalData[offset + 4] == 0x49 && originalData[offset + 5] == 0x44
                    && originalData[offset + 6] == 0x41 && originalData[offset + 7] == 0x54){
        		chunkLen = readInt(originalData, offset);
//        		System.out.println(chunkLen);	//1234_IDAT_DATA_CRC_ 这是data的长度
//        		System.out.println("offset=" + offset);		//这个是1234之前的数据块长度，实际长度从 58 - 58+4+4+chunkLen+4  
        		//for(58 ~ 58+4+4+chunkLen+4) --> write(byte[i])  --> 无损写入原数据
        		break;
        	}else{
        		chunkLen = readInt(originalData, offset);
        		offset += (4 + 4 + chunkLen + 4);
        	}
        }
        byte[] xx = new byte[chunkLen+12];
        for(int i = offset; i <= (offset+11+chunkLen); i++){
        	xx[i-offset] = originalData[i];
        }
        writeX(xx);
        
        //写入IEND
        write(0);
        crc.reset();
        write("IEND".getBytes());
        write((int) crc.getValue()); 
        out.close();
    }
    
    private int readInt(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 24)
                | ((data[offset + 1] & 0xFF) << 16)
                | ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
    }
    public static void printHexString( byte[] b) {  
 	   for (int i = 0; i < b.length; i++) { 
 	     String hex = Integer.toHexString(b[i] & 0xFF); 
 	     if (hex.length() == 1) { 
 	       hex = '0' + hex; 
 	     } 
 	     System.out.print(hex.toUpperCase() + " " ); 
 	   } 
 	}
}
