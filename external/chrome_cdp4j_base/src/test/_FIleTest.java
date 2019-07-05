package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.command.Network;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import io.webfolder.cdp.type.target.TargetInfo;

public class _FIleTest {
	@Test
	public void fun1() {
		ArrayList<String> command = new ArrayList<String>();
		// 不显示google 浏览器
		command.add("--incognito");// 进入隐身模式——保证浏览网页时，不留下任何痕迹。
		command.add("--start-maximized");// 浏览器启动后，窗口默认为最大化
		command.add("--disable-popup-blocking");// 关闭弹窗拦截
		Launcher launcher = new Launcher();
		SessionFactory sessionFactory = launcher.launch(command);
		Session session = sessionFactory.create().installSizzle();

		session.setUserAgent(
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36");

		Network network = session.getCommand().getNetwork();
		network.enable();
		session.wait(1000);
		network.clearBrowserCookies();
		Map<String, Object> headers = new HashMap<>();
		network.setExtraHTTPHeaders(headers);
		session.waitDocumentReady();

		session.navigate("http://cf.qingka.fm/qklive/live/html/livelist.html?src=yaowang25");
		session.waitDocumentReady();
		session.evaluate("qqlogin();");
		session.waitDocumentReady();

		session.getCommand().getPage().bringToFront();// 把当前的页面选项卡放置到最前
		String tid = session.getTargetId();

		List<TargetInfo> targetList = session.getCommand().getTarget().getTargets();

		for (int i = 0; i < targetList.size(); i++) {
			if (targetList.get(i).getOpenerId() != null && targetList.get(i).getOpenerId().equals(tid)) {
				session.wait(5000);
				session.useSizzle();
				session.wait(5000);
				session = sessionFactory.connect(targetList.get(i).getTargetId());
				session.wait(5000);
				session.waitDocumentReady();
				System.out.println(session.getLocation());
				System.out.println(session.getTitle());
				session.getCommand().getPage().bringToFront();
				session.wait(5000);
				session.close();
				break;
			}
		}

//	session.getCommand().getTarget().detachFromTarget();

//	session1.getTargetId().toString()+":2";
//	session2 = session2.navigate(s);

//	session1.getCommand().getNetwork().getResponseBody(s);

//	GetResponseBodyResult ss = session1.getCommand().getNetwork().getResponseBody(s);

//	System.out.println(ss.getBase64Encoded());

	}
}
