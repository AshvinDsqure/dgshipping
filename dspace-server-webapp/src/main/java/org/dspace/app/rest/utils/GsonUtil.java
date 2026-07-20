/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.utils;

/**
 *
 * @author root
 */

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;
import java.util.List;

/**
 *
 * @author root
 */
public class GsonUtil {

	private GsonUtil() {
	}

	public static Gson getGSon() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("d/M/yyyy HH:mm ");
		return gsonBuilder.create();
	}

	public static Gson build(final List<String> fieldExclusions, final List<Class<?>> classExclusions) {
		GsonBuilder b = new GsonBuilder();
		b.addSerializationExclusionStrategy(new ExclusionStrategy() {

			@Override
			public boolean shouldSkipClass(Class<?> clazz) {
				return classExclusions == null ? false : classExclusions.contains(clazz);
			}

			@Override
			public boolean shouldSkipField(FieldAttributes fa) {
				return fieldExclusions == null ? false : fieldExclusions.contains(fa.getName());
			}
		});
		b.setDateFormat("d/M/yyyy HH:mm ");
		return b.create();

	}
}
