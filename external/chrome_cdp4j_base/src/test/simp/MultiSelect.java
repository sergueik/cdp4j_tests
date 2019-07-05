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

import static java.util.Arrays.asList;

import java.net.URL;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;

public class MultiSelect {

	public static void main(String[] args) {
		URL url = MultiSelect.class.getResource("multi-select.html");

		Launcher launcher = new Launcher();

		try (SessionFactory factory = launcher.launch(); Session session = factory.create()) {
			session.navigate(url.toString());
			session.waitDocumentReady();

			System.out.println("Selected options:");
			session.getOptions("select").stream().forEach(o -> {
				if (o.isSelected()) {
					System.out.println(o);
				}
			});

			session.wait(1000);
			session.clearOptions("select");

			System.out.println("Selected Option count: "
					+ session.getOptions("select").stream().filter(o -> o.isSelected()).count());

			session.wait(1000);
			session.setSelectedOptions("select", asList(0, 1, 2));
			System.out.println("Selected options:");
			session.getOptions("select").stream().forEach(o -> {
				if (o.isSelected()) {
					System.out.println(o);
				}
			});

			System.out.println("Selected Option count: "
					+ session.getOptions("select").stream().filter(o -> o.isSelected()).count());

			session.wait(2000);
		}
	}
}
