package test;

import java.util.ArrayList;

import org.junit.Test;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class ImgTest {

	@Test
	public void imgtest() {
		ArrayList<String> command = new ArrayList<String>();
		command.add("--incognito");// 进入隐身模式——保证浏览网页时，不留下任何痕迹。
		Launcher launcher = new Launcher();
		SessionFactory sessionFactory = launcher.launch(command);
		sessionFactory.createBrowserContext();
		Session session = sessionFactory.create().installSizzle();
		session.wait(2000);
		session.activate();
		session.navigate(
				"https://login.sina.com.cn/cgi/pin.php?r=63219422&s=0&p=tc-3f018e00262ed9b6912cac2a9c250148d000");
		session.wait(3000);
		System.out.println(session.getContent());
		System.out.println();
		System.out.println((String) session.evaluate("window.document.body.textContent"));
		session.close();
		sessionFactory.close();
	}

}
