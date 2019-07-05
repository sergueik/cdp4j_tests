package frame.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ExecUtil {

	// 运行命令行
	public static Boolean runExec(String cmdStr, String charSet) {
		System.out.println("-->" + cmdStr);
		try {

			Process process = Runtime.getRuntime().exec(cmdStr);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream(), charSet));
			String line = "";

			while ((line = input.readLine()) != null) {
				System.out.println("" + line);
			}
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
