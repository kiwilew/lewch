package org.lewch.timeout;

public class TimeoutThreadTest {
	public static void main(String[] args) {
		// 初始化超时类
		TimeoutThread t = new TimeoutThread

		(5000, new TimeoutException("超时"));

		try {
			System.out.println("--------- begin ------------");
			t.start();
			// .....要检测超时的程序段....//
			try {
				Thread.sleep(40000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t.cancel();
			System.out.println("--------- end ------------");
		}

		catch (TimeoutException e)

		{
			// ...对超时的处理...//
		}
		// TimeoutException可以更换为其他未检查异常类。
	}
}