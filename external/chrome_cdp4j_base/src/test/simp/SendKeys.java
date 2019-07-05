/**
 * cdp4j - Chrome DevTools Protocol for Java
 * Copyright © 2017, 2018 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package test.simp;

import java.util.ArrayList;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class SendKeys {

	public static void main(String[] args) {

		ArrayList<String> command = new ArrayList<String>();
		// 不显示google 浏览器
		command.add("--incognito");// 进入隐身模式——保证浏览网页时，不留下任何痕迹。
		command.add("--start-maximized");// 浏览器启动后，窗口默认为最大化
		command.add("--process-per-tab");// --process-per-site 一个站点，一个进程 --process-per-tab：一个标签一个进程
		command.add("--disable-popup-blocking");// 关闭弹窗拦截
		Launcher launcher = new Launcher();
		SessionFactory sessionFactory = launcher.launch(command);
		Session session = sessionFactory.create().installSizzle();

		session.getCommand().getNetwork().enable();
		session.navigate("https://www.baidu.com");
		session.waitDocumentReady();
		session.sendKeys("webfolder.io");
		session.sendEnter();
		session.wait(2000);
	}
}
