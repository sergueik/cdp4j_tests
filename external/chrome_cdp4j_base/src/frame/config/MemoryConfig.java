package frame.config;

import frame.util.FileUtil;

public class MemoryConfig {
	public static boolean test = true;
	public static String kdName = "";
	public static String kdPass = "";
	public static String kdFail = "C:\\kd.txt";
	public static final String MYBATIS_CONFIG = "config\\mybatis-config.xml";

	public static void config() {
		if (!test) {
			String kd[] = new String(FileUtil.getInstance().readFile(MemoryConfig.kdFail)).split("----");
			kdName = kd[0];
			kdPass = kd[1];
		}
	}
}
