package test.simp;

import java.net.URL;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.command.Page;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class EvaluateOnNewDocument {

	public static void main(String[] args) {
		URL url = Select.class.getResource("inject-script.html");

		Launcher launcher = new Launcher();

		try (SessionFactory factory = launcher.launch(); Session session = factory.create()) {

			Page page = session.getCommand().getPage();
			// enable Page domain before using the addScriptToEvaluateOnNewDocument()
			page.enable();

			// addScriptToEvaluateOnNewDocument() must be called before Session.navigate()
			page.addScriptToEvaluateOnNewDocument("window.dummyMessage = 'hello, world!'");

			session.enableConsoleLog();

			session.navigate(url.toString());

			session.wait(500);
		}
	}
}
