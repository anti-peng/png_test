
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s): Alexandre Iline.
 *
 * The Original Software is the Jemmy library.
 * The Initial Developer of the Original Software is Alexandre Iline.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 *
 *
 * $Id$ $Revision$ $Date$
 *
 */

package com.anti.png;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.OutputStream;

import java.util.zip.CRC32;

import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.chunks.ChunkHelper;

/** This class allows to encode BufferedImage into B/W, greyscale or true color PNG
 * image format with maximum compression.<br>
 * It also provides complete functionality for capturing full screen, part of
 * screen or single component, encoding and saving captured image info PNG file.
 * @author Adam Sotona
 * @version 1.0 */
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
    //写入byte并更新crc
    void write(byte b[]) throws IOException {
        out.write(b);
        crc.update(b);
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
        
        //写入zTxt 1
        String authid = "-375512830";
        byte[] idbytes = ChunkHelper.compressBytes(authid.getBytes(PngHelperInternal.charsetLatin1), true);
        int idlength = "authid".length() + idbytes.length + 2;
        write(idlength);
        crc.reset();
        write("zTXt".getBytes());
        write("authid".getBytes());
        write(0);	//separator
        write(0); 	//compression method: 0
        write(idbytes);
        write((int) crc.getValue());

        //写入zTxt 2
        String authorStr = "希尔瓦娜斯";
        byte[] authorbytes = ChunkHelper.compressBytes(authorStr.getBytes(PngHelperInternal.charsetLatin1), true);
        int aulength = "author".length() + authorbytes.length + 2;
        write(aulength);
        crc.reset();
        write("zTxt".getBytes());
        write("author".getBytes());
        write(0);
        write(0);
        write(authorbytes);
        write((int) crc.getValue());
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
        		byte[] x = new byte[]{originalData[offset+4], originalData[offset+5], originalData[offset+6], originalData[offset+7]};
        		chunkLen = readInt(originalData, offset);
        		System.out.println(chunkLen);	//1234_IDAT_DATA_CRC_ 这是data的长度
        		System.out.println("offset=" + offset);		//这个是1234之前的数据块长度，实际长度从 58 - 58+4+4+chunkLen+4  
        		//for(58 ~ 58+4+4+chunkLen+4) --> write(byte[i])  --> 无损写入原数据
        		break;
        	}else{
        		chunkLen = readInt(originalData, offset);
        		offset += (4 + 4 + chunkLen + 4);
        	}
        }
        byte[] xx = new byte[chunkLen+12];
        for(int i = offset; i <= (offset+11+chunkLen); i++){
//        	writeX(originalData[i]);
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
}
