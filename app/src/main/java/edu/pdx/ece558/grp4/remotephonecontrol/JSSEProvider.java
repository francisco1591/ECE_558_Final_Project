package edu.pdx.ece558.grp4.remotephonecontrol;

// Code from https://medium.com/@ssaurel/how-to-send-an-email-with-javamail-api-in-android-2fc405441079

/////////////////////
// Android Imports //
/////////////////////

import java.security.AccessController;
import java.security.Provider;

//////////////////
// JSSEProvider //
//////////////////

public final class JSSEProvider extends Provider {

    /////////////////
    // Constructor //
    /////////////////

    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {

            public Void run() {
                put("SSLContext.TLS", "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                put("Alg.Alias.SSLContext.TLSv1", "TLS");
                put("KeyManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                put("TrustManagerFactory.X509", "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                return null;
            } // run

        }); // doPrivileged
    } // Constructor
} // JSSEProvider
