package com.diquest.patent.searcher._2021.RnD;

import java.io.File;
import java.io.IOException;

import com.diquest.util.AReadFile;

/**
 * FindPnoSearcher_2021에서 찾은 출원번호를 머징한다.
 * @author coreawin
 * @date 2021. 3. 5.
 */
public class FindPnoMerge extends AReadFile{
	
	
	
	public FindPnoMerge(String path) throws IOException {
		super(path);
	}

	static StringBuilder buf = new StringBuilder();
	public static void main(String[] args) {
		
		
		
		
		String targetPath = "d:\\data\\2021\\박진현-미소\\download\\20210129\\app-pno\\patent\\";
		
		File[] files = new File(targetPath).listFiles();
		System.out.println(files.length);
		for(File file : files){
			FindPnoMerge instance = null;
			buf.setLength(0);
			try {
				System.out.println(file.getAbsolutePath());
				instance = new FindPnoMerge(file.getAbsolutePath());
				instance.loadFile();
			} catch (IOException e) {
				e.printStackTrace();
			}  finally{
			}
			break;
		}
		

	}

	@Override
	public void readline(String line) {
		if(line.startsWith("pno")) return ;
		buf.append(line);
	}

}
