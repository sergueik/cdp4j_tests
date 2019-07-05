package frame.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.command.Network;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class WeiboOauth2AuthorizeAction {

	@Test
	public void signUpByAmount() {

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

		// 微博登陆
		// session.evaluate("document.querySelector(\"code\").scrollIntoView()");//滚动到目标位置
		session.evaluate("weibologin();");// session.callFunction("weibologin");
		session.waitDocumentReady();
		session.evaluate("document.getElementById('userId').value='18454357874';");
		session.wait(2000);
		session.evaluate("document.getElementById('passwd').value='q123123';");
		session.waitDocumentReady();
		session.evaluate("document.getElementsByClassName('WB_text2')[0].click();");// 呼出验证码
		session.waitDocumentReady();
		System.out.println(
				session.evaluate("document.getElementsByClassName('code_img')[0].getElementsByTagName('img')[0].src"));

		session.evaluate("document.getElementsByClassName('WB_btn_login formbtn_01')[0].click();");

		session.waitDocumentReady(3000);

	}
}
