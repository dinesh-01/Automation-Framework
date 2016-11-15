package environment;

import java.io.IOException;
import java.net.InetAddress;

import common.Log;

public class Network {

	/**
	 * ping the provided IP address & if not reachable wait for given time out
	 * 
	 * @param ipAddress
	 *            - IP address to ping
	 * @param timeOut
	 *            - time out to wait for ping to response
	 * @throws IOException
	 */
	public void ping(String ipAddress, int timeOut) throws IOException {

		Log.info("Pinging IP Address: " + ipAddress + " with Timeout set to: "
				+ timeOut);
		InetAddress address = InetAddress.getByName(ipAddress);
		// Try to reach the specified address within the timeout
		// periode. If during this periode the address cannot be
		// reach then the method returns false.
		int defaultTimeOut = 5000;
		boolean reachable = false;
		while (defaultTimeOut <= timeOut) {
			reachable = address.isReachable(defaultTimeOut);
			if (reachable) {
				Log.info("Host is reachable");
				break;
			}
			Log.info("waiting for 5 seconds");
			defaultTimeOut = defaultTimeOut + 5000;
		}
		if (!reachable) {
			Log.info("Host is NOT reachable");
		}
	}

	public static String getLocalIPAddress() throws Exception {
		InetAddress ip = InetAddress.getLocalHost();
		return ip.getHostAddress();
	}
}
