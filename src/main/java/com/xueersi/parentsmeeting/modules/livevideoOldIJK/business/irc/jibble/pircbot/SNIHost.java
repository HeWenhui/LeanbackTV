package com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.irc.jibble.pircbot;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SNIHost {
	static void setSNIHost(final SSLSocketFactory factory, final SSLSocket socket, final String hostname) {
		if (factory instanceof android.net.SSLCertificateSocketFactory
				&& android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			((android.net.SSLCertificateSocketFactory) factory).setHostname(socket, hostname);
		} else {
			try {
				socket.getClass().getMethod("setHostname", String.class).invoke(socket, hostname);
			} catch (Throwable e) {
				// ignore any error, we just can't set the hostname...
			}
		}
	}

}
