package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;

import net.sf.juffrou.reflect.BeanWrapperFactory;

public class FxProxyCreatorOutputStream extends ObjectOutputStream {

	private static final Logger logger = LoggerFactory.getLogger(FxProxyCreatorOutputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;

	// A Bimap with Class as key and Proxy Class as Value
	private final BiMap<Class<?>, Class<?>> proxyCache;

	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	public FxProxyCreatorOutputStream(OutputStream out, FxSerialsProxyBuilder proxyBuilder,
			BiMap<Class<?>, Class<?>> builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(out);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableReplaceObject(true);
	}

	@Override
	protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
		super.writeClassDescriptor(desc);
	}
	
	@Override
	protected final void writeObjectOverride(Object obj) throws IOException {
		super.writeObject(obj);
	}
	
	@Override
	protected Object replaceObject(Object obj) throws IOException {
		return super.replaceObject(obj);
	}
}
