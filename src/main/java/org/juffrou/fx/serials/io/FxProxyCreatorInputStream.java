package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.juffrou.fx.serials.JFXProxy;
import org.juffrou.fx.serials.core.FXProxyCache;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.CannotInitializeFxPropertyListException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.juffrou.reflect.BeanWrapperFactory;
import net.sf.juffrou.reflect.DefaultBeanWrapperFactory;

/**
 * Deserializes traditional Java Beans into JavaFX2 Beans.<br>
 * The source Java Beans must declare implementation of the FxSerials interface.
 * 
 * @author Carlos Martins
 */
public class FxProxyCreatorInputStream extends ObjectInputStream {

	private static final Logger logger = LoggerFactory.getLogger(FxProxyCreatorInputStream.class);

	// The builder who builds java fx proxys
	private final FxSerialsProxyBuilder proxyBuilder;

	// A Bimap with Class as key and Proxy Class as Value
	private final FXProxyCache proxyCache;

	// Factory for creating bean wrapper contexts to read the normal classes
	private final BeanWrapperFactory bwFactory;

	protected FxProxyCreatorInputStream() throws IOException, SecurityException {
		super();
		this.proxyBuilder = new FxSerialsProxyBuilder();
		this.proxyCache = new FXProxyCache();
		this.bwFactory = new DefaultBeanWrapperFactory();

	}

	public FxProxyCreatorInputStream(InputStream in) throws IOException {
		this(in, new FxSerialsProxyBuilder(), new FXProxyCache(), new DefaultBeanWrapperFactory());
	}

	public FxProxyCreatorInputStream(InputStream in, FxSerialsProxyBuilder proxyBuilder,
			FXProxyCache builderCache, BeanWrapperFactory bwFactory) throws IOException {
		super(in);
		this.proxyBuilder = proxyBuilder;
		this.proxyCache = builderCache;
		this.bwFactory = bwFactory;
		enableResolveObject(true);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {

		Class<?> proxyClass;
		
		if(proxyBuilder.isFXProxy(desc.getName())) {
			
			proxyClass = proxyCache.getProxyFromProxyClassName(desc.getName());
			
			if (proxyClass == null) {
				if (logger.isDebugEnabled())
					logger.debug("resolving original of " + desc.getName());
				
				Class<?> originalClass = proxyBuilder.cleanFXSerialsProxy(desc.getName());
				proxyClass = proxyBuilder.buildFXSerialsProxy(originalClass, desc.getSerialVersionUID());
				
				proxyCache.put(originalClass, proxyClass);
				
				if (logger.isDebugEnabled())
					logger.debug("resolved: " + proxyClass.getName());
			}
			
		}
		else
			proxyClass = super.resolveClass(desc);
		
		return proxyClass;
	}

	@Override
	protected Object resolveObject(Object obj) throws IOException {

		// If the object is an FxSerialsProxy instance, then initialize its
		// properties list
		if(JFXProxy.class.isAssignableFrom(obj.getClass())) {
			try {

				Class<?> proxyClass = obj.getClass();
				
				// initialize the properties map
				Method method = proxyClass.getMethod("initPropertiesList", null);
				method.invoke(obj, null);

			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new CannotInitializeFxPropertyListException("Error calling initPropertiesList() on "
						+ obj.getClass().getSimpleName() + ": " + e.getMessage(), e);
			}
		}

		return obj;
	}

}
