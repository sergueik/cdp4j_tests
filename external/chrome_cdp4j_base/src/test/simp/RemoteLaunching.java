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

import io.webfolder.cdp.RemoteLauncher;
import io.webfolder.cdp.RemoteLauncherBuilder;
import io.webfolder.cdp.session.SessionFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static java.util.Arrays.asList;

public class RemoteLaunching {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("path/to/your/private/key")), StandardCharsets.UTF_8))) {
			String s;
			while ((s = br.readLine()) != null) {
				pw.println(s);
			}
		}

		RemoteLauncher l = new RemoteLauncherBuilder().withHost("1.2.3.4").withChromePort(12345).withUser("chromeuser")
				.withPrivateKey(sw.toString()).create();

		SessionFactory sf = l.launch(asList("--headless", "--disable-gpu"));

		l.kill();
	}
}
