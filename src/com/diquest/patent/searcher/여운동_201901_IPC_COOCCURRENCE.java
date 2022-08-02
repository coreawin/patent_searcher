package com.diquest.patent.searcher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class 여운동_201901_IPC_COOCCURRENCE {

	/**
	 * IPC 복잡성 구하기.
	 * 
	 * @author 정승한
	 * @date 2019. 1. 14.
	 * @param ipcList
	 *            특허가 가지고 있는 IPC 목록
	 * @param m1
	 *            IPC Matrix Set 1
	 * @param m2
	 *            IPC Matrix Set 2 (Set 1번의 역 Matrix)
	 * @param cooccurrence
	 *            발생행렬 매트릭스 key는 ipc|ipc로 구성함 values는 발생빈도.
	 * @return 특허의 복잡성
	 * @throws Exception
	 */
	public double getComplexityIPC(Set<String> ipcList, Map<String, Map<String, Integer>> m1,
			Map<String, Map<String, Integer>> m2, SortedMap<String, Integer> cooccurrence) throws Exception {

		double complexity = 0d; /* 복잡성 */

		double combination = 0d; /* IPC의 조합성 */
		for (String ipc : ipcList) {
			int countSelfIPC = 0;
			/* 특정 IPC에 대한 전체 IPC Matrix를 얻는다. */
			Map<String, Integer> ipcCooccurrence = m1.get(ipc);
			if (ipcCooccurrence != null) {
				/* IPC 자신의 출현빈도를 얻는다. (a1값) */
				if (ipcCooccurrence.containsKey(ipc)) {
					countSelfIPC = ipcCooccurrence.get(ipc);
					System.out.println("발생빈도수 m1 (분모) " + ipc + " : " + countSelfIPC);
				} else {
					/* 없으면 m2에서 찾아본다. */
					ipcCooccurrence = m2.get(ipc);
					if (ipcCooccurrence.containsKey(ipc)) {
						countSelfIPC = ipcCooccurrence.get(ipc);
						System.out.println("발생빈도수 m2 (분모) " + ipc + " : " + countSelfIPC);
					} else {
						String key = ipc + "|" + ipc;
						if (cooccurrence.containsKey(key)) {
							countSelfIPC = cooccurrence.get(key);
							System.out.println("발생빈도수 co (분모) " + ipc + " : " + countSelfIPC);
						} else {

							/* 없으면 이건 이상있는 값임, 오류처리해서 데이터 확인 필요. */
							throw new Exception("자신의 발생빈도를 찾는다. 이 IPC값이 없으면 안된다. " + ipc);
						}

					}
				}
			}

			/* 하나의 문서에서 함께 등장했던 다른 IPC와의 Matrix를 구해본다. (분자값) */
			int summation = 0;

			if (ipcCooccurrence == null) {
				/* 없으면 이건 이상있는 값임, 오류처리해서 데이터 확인 필요. */
				throw new Exception("자신의 발생빈도를 찾는다. 이 IPC값이 없으면 안된다. " + ipc);
			} else {
				Set<String> datasKeys = ipcCooccurrence.keySet();
				for (String k : datasKeys) {
					int s = ipcCooccurrence.get(k);
					if (s > 0) {
						summation += 1;
					}
				}
			}

			if (countSelfIPC == 0) {
				// 분모가 0이라면 이상하다.
				throw new Exception("분모가 0이다. . " + ipcList);
			}

			if (summation == 0) {
				// 분자가 0이라면 이상하다.
				throw new Exception("분자가 0이다. . " + ipcList);
			}

			combination += new Double(summation) / new Double(countSelfIPC);

		}

		if (combination == 0) {
			// 조합성이 0이라면 이상하다.
			throw new Exception("조합성이 0이다. . " + ipcList);
		}
		complexity = 1d / combination;
		return complexity;
	}

	public static void main(String[] args) {
		Set<String> kSet = new TreeSet<String>();
		SortedMap<String, Integer> data = new TreeMap<String, Integer>();
		Map<String, Map<String, Integer>> matrix1 = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> matrix2 = new HashMap<String, Map<String, Integer>>();

		String dataStream = "d:/data/yeo.us.ip.data.ser";
		String path = "d:/data/yeo.us.ipc.result.txt";
		String outputPath = "d:/data/yeo.us.ipc.result_cooccu.txt";
		BufferedReader br = null;

		File dataFile = new File(dataStream);
		int progress = 0;

		if (dataFile.isFile() && dataFile.length() > 100) {
			ObjectInputStream ois = null;
			try {
				System.out.println("기존 데이터 파일을 읽는다. " + dataFile.getAbsolutePath());
				ois = new ObjectInputStream(new DataInputStream(new FileInputStream(dataFile)));
				try {
					kSet = (Set<String>) ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				try {
					data = (SortedMap<String, Integer>) ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				try {
					matrix1 = (Map<String, Map<String, Integer>>) ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				try {
					matrix2 = (Map<String, Map<String, Integer>>) ois.readObject();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (ois != null)
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} else {
			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "utf-8"));
				String line = null;

				try {
					while ((line = br.readLine()) != null) {
						// System.out.println(line);
						String[] ds = line.split("\t");
						if (line.indexOf(",") == -1)
							continue;

						String[] ipcs = ds[1].split(",");
						Set<String> listSet = new TreeSet<String>();
						for (String ipc : ipcs) {
							listSet.add(ipc);
						}
						kSet.addAll(listSet);
						for (String ls1 : listSet) {
							for (String ls2 : listSet) {
								String key = ls1 + "|" + ls2;
								int cooccu = 0;
								if (data.containsKey(key)) {
									cooccu = data.get(key) + 1;
								} else {
									cooccu = 1;
								}
								data.put(key, cooccu);

								Map<String, Integer> _ms1 = null;

								if (matrix1.containsKey(ls1)) {
									_ms1 = matrix1.get(ls1);
								} else {
									_ms1 = new HashMap<String, Integer>();
								}
								_ms1.put(ls2, cooccu);
								matrix1.put(ls1, _ms1);

								Map<String, Integer> _ms2 = null;
								if (matrix2.containsKey(ls2)) {
									_ms2 = matrix2.get(ls2);
								} else {
									_ms2 = new HashMap<String, Integer>();
								}
								_ms2.put(ls1, cooccu);
								matrix2.put(ls2, _ms2);
							}
						}
						progress++;
						if (progress % 100000 == 0) {
							System.out.println("동시발생행렬 계산 진행상황 " + progress);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("동시발생행렬 계산 완료. " + kSet.size() + "\t" + data.size());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			ObjectOutputStream oos = null;
			try {
				System.out.println("데이터 파일을  만든다.. " + dataFile.getAbsolutePath());
				oos = new ObjectOutputStream(new DataOutputStream(new FileOutputStream(new File(dataStream))));
				oos.writeObject(kSet);
				oos.writeObject(data);
				oos.writeObject(matrix1);
				oos.writeObject(matrix2);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} finally {
				if (oos != null) {
					try {
						oos.flush();
						oos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		String targetPath = "d:/data/yeo.target.complexities";
		Map<String, Set<String>> targetPatent = new HashMap<String, Set<String>>();
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(targetPath)), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] ts = line.split("\t");
				String seq = ts[0];
				String ipcs = ts[1].replaceAll("\\s{1,}", "");
				String[] ipcss = ipcs.split(";");
				Set<String> ipcSet = new HashSet<String>();
				for (String i : ipcss) {
					if ("".equals(i.trim()))
						continue;
					String[] abc = i.split("/");
					if (abc[1].equals("00")) {
						ipcSet.add(i.trim());
						continue;
					}
					if (i.trim().indexOf("/0") != -1) {
						i = i.replaceAll("/0", "/");
					}
					ipcSet.add(i.trim());
				}
				System.out.println(seq + "\t" + ipcSet);
				targetPatent.put(seq.trim(), ipcSet);
			}

		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		여운동_201901_IPC_COOCCURRENCE yeo = new 여운동_201901_IPC_COOCCURRENCE();
		String resultPath = targetPath + ".result.txt";
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(resultPath))));
			Set<String> seqSet = targetPatent.keySet();
			for (String seq : seqSet) {
				Set<String> ipcList = targetPatent.get(seq);
				if (ipcList.size() == 0) {
					System.out.println("ipc list 가 없다?/ " + seq);
					continue;
				}
				boolean isContains = true;
				StringBuffer buf = new StringBuffer();
				try {
					for (String ipc : ipcList) {
						if (isContains) {
							isContains = kSet.contains(ipc);
						}
						System.out.println(seq + "\t" + "check " + ipc + "\t" + kSet.contains(ipc));
					}
					double complexity = yeo.getComplexityIPC(ipcList, matrix1, matrix2, data);
					System.out.println("result ==== " + seq + "\t" + ipcList + "\t" + complexity + "\n");
					bw.write(seq + "\t" + ipcList + "\t" + complexity + "\n");
				} catch (Exception e) {
					e.printStackTrace();
					try {
						if (!isContains) {

						}
						bw.write(seq + "\t" + ipcList + "\t" + 0d + "\t전체셋(290만건에서)에 존재하지 않는 IPC가 있다..\n");
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// System.exit(0);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		/*
		 * BufferedWriter bw = null; try { bw = new BufferedWriter(new
		 * OutputStreamWriter(new FileOutputStream(new File(outputPath)),
		 * "utf-8")); final String TAB = " "; final String ENTER = "\n";
		 * StringBuilder buf = new StringBuilder(); for (String k : kSet) {
		 * buf.append(TAB); buf.append(k); } buf.append(ENTER);
		 * bw.write(buf.toString()); System.out.println("AAAAA");
		 * buf.setLength(0); int totalSize = kSet.size(); progress = 0; for
		 * (String k1 : kSet) { buf.append(k1); for (String k2 : kSet) { String
		 * key = k1 + "|" + k2; int cooccu = 0; if (data.containsKey(key)) {
		 * cooccu = data.get(key); } buf.append(TAB); buf.append(cooccu); }
		 * buf.append(ENTER); bw.write(buf.toString()); buf.setLength(0);
		 * progress++;
		 * 
		 * if (progress % 100 == 0) { System.out.println("데이터 쓰기  진행상황 " +
		 * progress + "/" + totalSize); } } } catch (Exception e) {
		 * e.printStackTrace(); } finally { try { bw.close(); } catch
		 * (IOException e) { e.printStackTrace(); } }
		 */

	}
}
