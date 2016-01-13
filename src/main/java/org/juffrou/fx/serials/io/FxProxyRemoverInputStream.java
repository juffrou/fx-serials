package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.DefaultBeanWrapperFactory;

/**
 * Deserializes JavaFX2 Beans into traditional Java Beans.<br>
 * The source JavaFX2 Beans must have been created by the FxInputStream class.
 * 
 * @author Carlos Martins
 */
public class FxProxyRemoverInputStream extends ObjectInputStream {

	private static final Logger logger = LoggerFactory.getLogger(FxProxyRemoverInputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;

	// A Bimap with original class as key and respective proxy class as value.
	private final BiMap<Class<?>, Class<?>> proxyCache;

	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	protected FxProxyRemoverInputStream() throws IOException, SecurityException {
		super();
		this.proxyBuilder = new FxSerialsProxyBuilder();
		this.proxyCache = HashBiMap.create();
		this.bwFactory = new DefaultBeanWrapperFactory();
	}

	public FxProxyRemoverInputStream(InputStream in) throws IOException {
		this(in, new FxSerialsProxyBuilder(), HashBiMap.create(), new DefaultBeanWrapperFactory());
	}

	public FxProxyRemoverInputStream(InputStream in, FxSerialsProxyBuilder proxyBuilder,
			BiMap<Class<?>, Class<?>> builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(in);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableResolveObject(true);
	}
}
