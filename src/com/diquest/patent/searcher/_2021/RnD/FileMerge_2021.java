package com.diquest.patent.searcher._2021.RnD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileMerge_2021 {

	public static void main(String[] args) throws Exception {
		String path = "d:\\data\\2021\\박진현-미소\\download\\20210129\\app-pno\\patent_bak\\";
		File[] files = new File(path).listFiles();
		
		String writePath = "d:\\data\\2021\\박진현-미소\\download\\20210129\\app-pno\\APPNO-LIST.txt";
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(writePath)), "utf8"));
		int idx = 0;
		StringBuffer buf = new StringBuffer();
		for(File f : files){
			idx++;
			System.out.println(f.getAbsolutePath());
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf8"));
			buf.setLength(0);
			String line = null;
			int readIdx = 0;
			while((line=br.readLine())!=null){
				readIdx++;
				if("".equals(line.trim())) continue;
				if(idx==1 && readIdx==1){
					buf.append(line);
					System.out.println(line);
					buf.append("\r\n");
					continue;
				}
				if(line.startsWith("pno")) continue;
				buf.append(line);
				
				//GPASS검색출원번호항목추가
				buf.append("\t");
				buf.append(f.getName().replaceAll("\\.txt", ""));
				buf.append("\r\n");
				
				if(readIdx%10000==0){
					bw.write(buf.toString());
					buf.setLength(0);
				}
			}
			bw.write(buf.toString());
			buf.setLength(0);
			bw.flush();
			br.close();
			
		}
		bw.flush();
		bw.close();
	}

}
