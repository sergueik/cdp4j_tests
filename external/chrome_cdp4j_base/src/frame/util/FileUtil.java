package frame.util;

import java.io.File;
import java.io.RandomAccessFile;

public class FileUtil {

	public static String FileUtilPath = "";
	public static String FileUtilSeparator = "";
	static {
		try {
			FileUtilPath = (new File(".")).getCanonicalPath();
			FileUtilSeparator = File.separator;
		} catch (Throwable e) {

		}

	}

	public static FileUtil getInstance() {
		return new FileUtil();
	}

	public byte[] readFile(String sFileName) {
		// StringBuffer sbStr = new StringBuffer();
		byte[] sbStr = new byte[2];

		int rpos = 0;
		try {

			long alth = 0;

			int rsize = 102400;
			byte[] tmpbt = new byte[rsize];
			RandomAccessFile raf = new RandomAccessFile(sFileName, "r");

			alth = raf.length();

			sbStr = new byte[(int) alth];
			while (alth > rpos) {
				raf.seek(rpos);
				tmpbt = new byte[rsize];

				if (alth - rpos < rsize) {
					tmpbt = new byte[(int) (alth - rpos)];
					// raf.read(tmpbt, rpos, (int)(alth-rpos));
				} else {
					// tmpbt=new byte[rsize];
					// raf.read(tmpbt, rpos, rsize);
				}
				raf.read(tmpbt);
				for (int i = 0, j = tmpbt.length; i < j; i++) {
					sbStr[rpos + i] = tmpbt[i];
				}
				rpos = rpos + rsize;

				// sbStr += new String(tmpbt, chatedcode);

			}

			raf.close();
		} catch (Throwable e) {
			sbStr = null;
		}
		return sbStr;
	}

}
