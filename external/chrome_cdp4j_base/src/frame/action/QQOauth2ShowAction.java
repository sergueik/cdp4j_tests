package frame.action;

import static java.util.Arrays.asList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import business.service.MiaoBoLiveService;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.command.Network;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class QQOauth2ShowAction {

	public void runFlowDownload() {
		MiaoBoLiveService flowDownloadService = new MiaoBoLiveService();

		for (int i = 0; i < 5000; i++) {

			new Launcher().launch(asList(" --incognito", " --start-maximized"));
			Session session = session = new SessionFactory().create().installSizzle();
			session.setUserAgent(
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");

			Network network = session.getCommand().getNetwork();
			network.enable();
			session.wait(1000);
			network.clearBrowserCookies();
			Map<String, Object> headers = new HashMap<>();
			network.setExtraHTTPHeaders(headers);
			session.wait(500);
			session.waitDocumentReady();

			session.navigate("http://cf.qingka.fm/qklive/live/html/livelist.html?src=yaowang25");
			session.wait(10000);
			session.waitDocumentReady();
			session.evaluate("qqlogin();");
			session.wait(4000);

			// runEXEC("taskkill /f /t /im chrome.exe", "utf-8");

		}
	}

	public String[] runEXEC(String cmdstr, String charset) {
		String[] res = { "0", "0" };
		try {
			System.out.println("-->" + cmdstr);

			Process process = Runtime.getRuntime().exec(cmdstr);
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
			String line1 = "";
			String line = "";

			while ((line1 = input.readLine()) != null) {
				line = line + "\r\n" + line1;
			}
			input.close();

			int hasError = process.waitFor();
			res[0] = String.valueOf(hasError);
			res[1] = line;
		} catch (Throwable t) {
		}

		return res;
	}

}
