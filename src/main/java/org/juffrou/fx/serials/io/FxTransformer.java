package org.juffrou.fx.serials.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javassist.ClassPool;

import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.error.FxTransformerException;

/**
 * Transforms a traditional Java Bean into a JavaFX2 Bean.
 * 
 * @author Carlos Martins
 */
public class FxTransformer {
	
	private final ClassPool pool = ClassPool.getDefault();

	/**
	 * Transforms a traditional Java Bean into a JavaFX2 Bean.
	 * @param bean a traditional java bean implementing the FXSerials interface.
	 * @return a JavaFX2 Bean
	 */
	public <T> T transform(T bean) {
		if( ! FxSerials.class.isAssignableFrom(bean.getClass()))
			throw new IllegalArgumentException("bean must implement FxSerials");
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(bean);
			out.flush();
			out.close();
			
			FxInputStream fxInputStream = new FxInputStream(new ByteArrayInputStream(bos.toByteArray()), pool);
			return (T) fxInputStream.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new FxTransformerException("Error deserializing bean", e);
		}
	}
}
