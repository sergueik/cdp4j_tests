package test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import frame.util.ADSLUtil;

public class ADSLTest {

	public static void main(String[] args) {

		ADSLUtil.restartADSL("宽带连接", "f5mgsm99m", "wdjpnphl", "utf-8");
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			System.out.println(addr.getHostAddress());

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
