/**
 * 
 */
package com.diquest.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.tqk.common.util.array.ByteArrayUtil;


public class CompressUtil {
	
	private final static int _BUFFER_SIZE = 1024 * 4;

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private static CompressUtil instance = new CompressUtil();

	private CompressUtil() {
	}

	public static CompressUtil getInstance() {
		return instance;
	}

	public byte[] compress(String input) throws IOException {
		baos.reset();
		BufferedOutputStream buffos = null;
		try {
			buffos = new BufferedOutputStream(new GZIPOutputStream(baos));
			buffos.write(ByteArrayUtil.stringToByte(input));
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (baos != null)
				baos.close();
			if (buffos != null)
				buffos.close();
		}
		return baos.toByteArray();
	}

	private static byte[] buffer = new byte[_BUFFER_SIZE];

	/**
	 * ??�??? 문�???? byte�? ?��? 문�???��? �???????.<br>
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public String unCompress(byte[] bytes) throws IOException {
		baos.reset();
		BufferedInputStream buffis = null;
		try {
			buffis = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)));
			int length;
			while (true) {
				synchronized (buffer) {
					length = buffis.read(buffer);
					if (length > 0) {
						baos.write(buffer, 0, length);
					} else {
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (buffis != null)
				buffis.close();
			if (baos != null)
				baos.close();
		}
		return ByteArrayUtil.readString(baos.toByteArray(), 0, baos.size()).trim(); 
	}

	/**
	 * ??�??? 문�???? byte�? ?��? 문�???��? �???????.<br>
	 * 
	 * @param is
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public InputStream unCompressInputStream(byte[] bytes) throws IOException {
		baos.reset();
		BufferedInputStream buffis = null;
		try {
			buffis = new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes)));
			int length;
			while (true) {
				synchronized (buffer) {
					length = buffis.read(buffer);
					if (length > 0) {
						baos.write(buffer, 0, length);
					} else {
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (buffis != null)
				buffis.close();
			if (baos != null)
				baos.close();
		}
		
		String result = ByteArrayUtil.readString(baos.toByteArray(), 0, baos.size()).trim();
		return new ByteArrayInputStream(result.getBytes("UTF-8"));
	}
	
}
