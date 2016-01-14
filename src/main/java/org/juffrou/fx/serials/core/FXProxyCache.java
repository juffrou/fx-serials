package org.juffrou.fx.serials.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a cache of objects and proxies processed during serialization / deserialization
 * @author cem
 *
 */
public class FXProxyCache {

	private Map<Class<?>, Class<?>> proxyClassCache = new HashMap<>();
	private Map<String, Class<?>> proxyClassNameCache = new HashMap<>();
	
	public void put(Class<?> originalClass, Class<?> proxyClass) {
		proxyClassCache.put(originalClass, proxyClass);
		proxyClassNameCache.put(proxyClass.getName(), proxyClass);
	}
	
	public Class<?> getProxyFromOriginalClass(Class<?> originalClass) {
		return proxyClassCache.get(originalClass);
	}

	public Class<?> getProxyFromProxyClassName(String proxyClassName) {
		return proxyClassNameCache.get(proxyClassName);
	}

	public Class<?> getOriginalFromProxyClass(Class<?> proxyClass) {
		return proxyClass.getSuperclass();
	}
}
