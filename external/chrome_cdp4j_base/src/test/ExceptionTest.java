package test;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ExceptionTest {
	public static void main(String[] args) throws FileNotFoundException {
//		try {
//			int a = 4993;
//			int b = 0;
//			int c;
//			c = a / b;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.out.println("------------------");
//
//			try {
//				System.out.println(e.getStackTrace()[0]);
//				System.out.println(e.getStackTrace()[1]);
//				System.out.println("!!!!!!!!!!!!!!!!");
//			} catch (Exception x) {
//				x.printStackTrace();
//				System.out.println("------------------");
//			}
//
//			System.out.println("------------------");
//			e.printStackTrace();
//			System.out.println("------------------");
//			System.out.println("dddd");
//		}

//		int a = 4993;
//		int b = 0;
//		int c;
//		c = a / b;

//		for(int i=0;i<20;i++) {
//			System.out.println(i);
//		}

		ExceptionTest2 test2 = new ExceptionTest2();
//		try {
//			test2.test();
//		}catch (Exception e) {
//			System.out.println("dddd");
//			e.printStackTrace();
//			System.out.println("dddd");
//		}

		try {
			test2.test();
		} catch (NullPointerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		throw new ExceptionTest2();
//		
//		for(int i=0;i<20;i++) {
//			System.out.println(i);
//		}
	}
}

class ExceptionTest2 extends Throwable {
	public void test() throws NullPointerException, IOException {
//		try {
//			int a = 4993;
//			int b = 0;
//			int c;
//			c = a / b;
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//			System.out.println("------------------");
//
//			try {
//				System.out.println(e.getStackTrace()[0]);
//				System.out.println(e.getStackTrace()[1]);
//				System.out.println("!!!!!!!!!!!!!!!!");
//			} catch (Exception x) {
//				x.printStackTrace();
//				System.out.println("------------------");
//			}
//
//			System.out.println("------------------");
//			e.printStackTrace();
//			System.out.println("------------------");
//			System.out.println("dddd");
//		}

		int a = 4993;
		int b = 0;
		int c;
		c = a / b;

		throw new IOException();

//		
//		for(int i=0;i<20;i++) {
//			System.out.println(i);
//		}
//		

	}
}
