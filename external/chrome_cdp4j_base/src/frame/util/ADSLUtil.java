package frame.util;

public class ADSLUtil {
	public static void restartADSL(String ADSLname, String account, String password, String charSet) {
		ExecUtil.runExec("RASDIAL " + ADSLname + " /DISCONNECT", charSet);
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			System.exit(0);// 退出程序
		}
		ExecUtil.runExec("RASDIAL " + ADSLname + "  " + account + " " + password, charSet);
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			System.exit(0);// 退出程序
		}
	}
}
