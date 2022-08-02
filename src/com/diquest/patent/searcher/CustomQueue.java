package com.diquest.patent.searcher;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CustomQueue {
	public static BlockingQueue<String> bQueue = new ArrayBlockingQueue<String>(5);
}
