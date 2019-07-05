package business.action;

import static io.webfolder.cdp.type.constant.DownloadBehavior.Allow;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import frame.util.ADSLUtil;
import frame.util.ExecUtil;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.command.Network;
import io.webfolder.cdp.command.Page;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class MiaoBoLiveAction {

	public void downLoadGotoLimit() {
		for (int i = 0; i < 300; i++) {
			ADSLUtil.restartADSL("宽带连接", "f5mgsm99m", "wdjpnphl", "utf-8");
			System.out.println(i);
			downLoadGoto();
		}
	}

	public void downLoadGoto() {
		ExecUtil.runExec("taskkill /f /t /im chrome.exe", "utf-8");
		Launcher launcher = new Launcher();
		SessionFactory sessionFactory = launcher.launch(Arrays.asList("--incognito", "--headless"));
		Session session = sessionFactory.create().installSizzle();
		session.setUserAgent(
				"MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		Network network = session.getCommand().getNetwork();
		network.enable();
		session.wait(1000);
		network.clearBrowserCookies();
		session.clearCookies();
		session.navigate(
				"http://www.miaobolive.com/loginp.aspx?channel=6&subChannel=yaowang&timeTicks=636449795818586187&key=AB9812E4CF66EA9075A85A8565C62C5D9B65864A7F51D51301B363BF853FAD19");
		session.wait(4000);
		session.evaluate("javascript:htmlLode.goto();");
		System.out.println(session.getContent());

		session.wait(4000);
		session.close();
		sessionFactory.close();

	}

	@Test
	public void downLoadAppLimit() {
		for (int i = 0; i < 500; i++) {
			ADSLUtil.restartADSL("宽带连接", "f5mgsm99m", "wdjpnphl", "utf-8");
			ExecUtil.runExec("taskkill /f /t /im chrome.exe", "utf-8");
			System.out.println(i);
			downLoadApp();
		}
	}

	public void downLoadApp() {
		ArrayList<String> command = new ArrayList<String>();
		// 不显示google 浏览器
		command.add("--incognito");// 进入隐身模式——保证浏览网页时，不留下任何痕迹。
		command.add("--start-maximized");// 浏览器启动后，窗口默认为最大化
		command.add("--disable-popup-blocking");// 关闭弹窗拦截
		Launcher launcher = new Launcher();
		SessionFactory sessionFactory = launcher.launch(command);
		Session session = sessionFactory.create().installSizzle();
		session.setUserAgent(
				"MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		Network network = session.getCommand().getNetwork();
		network.enable();
		session.wait(2000);
		network.clearBrowserCookies();
		Map<String, Object> headers = new HashMap<>();
		network.setExtraHTTPHeaders(headers);
		session.navigate(
				"http://www.miaobolive.com/loginp.aspx?channel=6&subChannel=yaowang&timeTicks=636449795818586187&key=AB9812E4CF66EA9075A85A8565C62C5D9B65864A7F51D51301B363BF853FAD19");
//		session.waitDocumentReady(30000);
		session.wait(8000);
		session.evaluate("javascript:htmlLode.goto();");
		session.wait(2000);
		session.getCommand().getNetwork().enable();
		Page page = session.getCommand().getPage();
		Path downloadPath = Paths.get("C:\\\\MiaoBoLive").toAbsolutePath();
		page.setDownloadBehavior(Allow, downloadPath.toString());
		session.evaluate("document.getElementById('btn-normal-download').click();");
		session.wait(3000);
		session.evaluate("document.getElementsByClassName('btn-ok w-btn btn-medium clickable is-gray')[0].click();");
		session.wait(13000);
		session.close();
		sessionFactory.close();
	}

}
