package io.github.niterux.niterucks.niterucksfeatures;

public class ScreenshotThread implements Runnable {
	public static void main(String[] args) {
		ScreenshotThread obj = new ScreenshotThread();
		Thread thread = new Thread(obj);
		thread.start();
		System.out.println("This code is outside of the thread");
	}
	public void run() {
		System.out.println("This code is running in a thread");
	}
}
