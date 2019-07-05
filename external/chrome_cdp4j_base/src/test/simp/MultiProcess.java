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

import static io.webfolder.cdp.session.SessionFactory.DEFAULT_PORT;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.Random;

import static java.lang.System.getProperty;
import static java.nio.file.Paths.get;
import static java.util.Arrays.asList;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class MultiProcess {

	// port number and user-data-dir must be different for each chrome process
	// As an alternative @see IncognitoBrowsing.java for incognito mode (private
	// browsing).
	public static void main(String[] args) {
		new Thread() {

			public void run() {
				Launcher launcher = new Launcher(getFreePort(DEFAULT_PORT));
				Path remoteProfileData = get(getProperty("java.io.tmpdir"))
						.resolve("remote-profile-" + new Random().nextInt());
				System.out.println(remoteProfileData);
				SessionFactory factory = launcher.launch(asList("--user-data-dir=" + remoteProfileData.toString()));

				try (SessionFactory sf = factory) {
					try (Session session = sf.create()) {
						session.navigate("https://webfolder.io");
						session.waitDocumentReady();
						System.err.println("Content Length: " + session.getContent().length());
					}
				}
			}
		}.start();

		new Thread() {

			public void run() {
				Launcher launcher = new Launcher(getFreePort(DEFAULT_PORT));
				Path remoteProfileData = get(getProperty("java.io.tmpdir"))
						.resolve("remote-profile-" + new Random().nextInt());
				SessionFactory factory = launcher.launch(asList("--user-data-dir=" + remoteProfileData.toString()));

				try (SessionFactory sf = factory) {
					try (Session session = sf.create()) {
						session.navigate("https://webfolder.io");
						session.waitDocumentReady();
						System.err.println("Content Length: " + session.getContent().length());
					}
				}
			}
		}.start();
	}

	protected static int getFreePort(int portNumber) {
		try (ServerSocket socket = new ServerSocket(portNumber)) {
			int freePort = socket.getLocalPort();
			return freePort;
		} catch (IOException e) {
			return getFreePort(portNumber + 1);
		}
	}
}
