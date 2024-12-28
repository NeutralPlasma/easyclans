package eu.virtusdevelops.easyclans.storage;

import java.net.URL;
import java.net.URLClassLoader;

public class IsolatedClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public IsolatedClassLoader(URL[] urls) {

        /*
         * ClassLoader#getSystemClassLoader returns the AppClassLoader
         *
         * Calling #getParent on this returns the ExtClassLoader (Java 8) or
         * the PlatformClassLoader (Java 9). Since we want this classloader to
         * be isolated from the Minecraft server (the app), we set the parent
         * to be the platform class loader.
         */
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }
}
