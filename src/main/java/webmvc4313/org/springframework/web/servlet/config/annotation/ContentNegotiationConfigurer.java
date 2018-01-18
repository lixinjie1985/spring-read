/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.config.annotation;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;

import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;

/**
 * Creates a {@code ContentNegotiationManager} and configures it with
 * one or more {@link ContentNegotiationStrategy} instances.
 *
 * <p>As an alternative you can also rely on the set of defaults described below
 * which can be turned on or off or customized through the methods of this
 * builder:
 *
 * <table>
 * <tr>
 * <th>Configurer Property</th>
 * <th>Underlying Strategy</th>
 * <th>Default Setting</th>
 * </tr>
 * <tr>
 * <td>{@link #favorPathExtension}</td>
 * <td>{@link PathExtensionContentNegotiationStrategy Path Extension strategy}</td>
 * <td>On</td>
 * </tr>
 * <tr>
 * <td>{@link #favorParameter}</td>
 * <td>{@link ParameterContentNegotiationStrategy Parameter strategy}</td>
 * <td>Off</td>
 * </tr>
 * <tr>
 * <td>{@link #ignoreAcceptHeader}</td>
 * <td>{@link HeaderContentNegotiationStrategy Header strategy}</td>
 * <td>On</td>
 * </tr>
 * <tr>
 * <td>{@link #defaultContentType}</td>
 * <td>{@link FixedContentNegotiationStrategy Fixed content strategy}</td>
 * <td>Not set</td>
 * </tr>
 * <tr>
 * <td>{@link #defaultContentTypeStrategy}</td>
 * <td>{@link ContentNegotiationStrategy}</td>
 * <td>Not set</td>
 * </tr>
 * </table>
 *
 * <p>The order in which strategies are configured is fixed. You can only turn
 * them on or off.
 *
 * <p>For the path extension and parameter strategies you may explicitly add
 * {@link #mediaType MediaType mappings}. Those will be used to resolve path
 * extensions and/or a query parameter value such as "json" to a concrete media
 * type such as "application/json".
 *
 * <p>The path extension strategy will also use {@link ServletContext#getMimeType}
 * and the Java Activation framework (JAF), if available, to resolve a path
 * extension to a MediaType. You may however {@link #useJaf suppress} the use
 * of JAF.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Juergen Hoeller
 * @since 3.2
 * @see ContentNegotiationManagerFactoryBean
 */ //内容协商配置器，客户端如何指定自己想要的内容类型，可通过路径扩展名，请求参数，请求Header
public class ContentNegotiationConfigurer {

	private final ContentNegotiationManagerFactoryBean factory = new ContentNegotiationManagerFactoryBean();

	private final Map<String, MediaType> mediaTypes = new HashMap<String, MediaType>();


	/**
	 * Class constructor with {@link javax.servlet.ServletContext}.
	 */
	public ContentNegotiationConfigurer(ServletContext servletContext) {
		this.factory.setServletContext(servletContext);
	}


	/**
	 * Whether the path extension in the URL path should be used to determine
	 * the requested media type.
	 * <p>By default this is set to {@code true} in which case a request
	 * for {@code /hotels.pdf} will be interpreted as a request for
	 * {@code "application/pdf"} regardless of the 'Accept' header.
	 */ //开启采用路径中的扩展名来决定内容类型（其实已经默认开启）
	public ContentNegotiationConfigurer favorPathExtension(boolean favorPathExtension) {
		this.factory.setFavorPathExtension(favorPathExtension);
		return this;
	}

	/**
	 * Add a mapping from a key, extracted from a path extension or a query
	 * parameter, to a MediaType. This is required in order for the parameter
	 * strategy to work. Any extensions explicitly registered here are also
	 * whitelisted for the purpose of Reflected File Download attack detection
	 * (see Spring Framework reference documentation for more details on RFD
	 * attack protection).
	 * <p>The path extension strategy will also try to use
	 * {@link ServletContext#getMimeType} and JAF (if present) to resolve path
	 * extensions. To change this behavior see the {@link #useJaf} property.
	 * @param extension the key to look up
	 * @param mediaType the media type
	 * @see #mediaTypes(Map)
	 * @see #replaceMediaTypes(Map)
	 */ //从这里加进去的文件扩展名，同时也作为白名单，可以通过入侵检测
	public ContentNegotiationConfigurer mediaType(String extension, MediaType mediaType) {
		this.mediaTypes.put(extension, mediaType);
		return this;
	}

	/**
	 * An alternative to {@link #mediaType}.
	 * @see #mediaType(String, MediaType)
	 * @see #replaceMediaTypes(Map)
	 */ //和上面的方法一样
	public ContentNegotiationConfigurer mediaTypes(Map<String, MediaType> mediaTypes) {
		if (mediaTypes != null) {
			this.mediaTypes.putAll(mediaTypes);
		}
		return this;
	}

	/**
	 * Similar to {@link #mediaType} but for replacing existing mappings.
	 * @see #mediaType(String, MediaType)
	 * @see #mediaTypes(Map)
	 */ //替换掉所有已经存在的
	public ContentNegotiationConfigurer replaceMediaTypes(Map<String, MediaType> mediaTypes) {
		this.mediaTypes.clear();
		mediaTypes(mediaTypes);
		return this;
	}

	/**
	 * Whether to ignore requests with path extension that cannot be resolved
	 * to any media type. Setting this to {@code false} will result in an
	 * {@code HttpMediaTypeNotAcceptableException} if there is no match.
	 * <p>By default this is set to {@code true}.
	 */ //是否忽略未知的路径扩展名，不忽略的话会导致一个异常
	public ContentNegotiationConfigurer ignoreUnknownPathExtensions(boolean ignore) {
		this.factory.setIgnoreUnknownPathExtensions(ignore);
		return this;
	}

	/**
	 * When {@link #favorPathExtension} is set, this property determines whether
	 * to allow use of JAF (Java Activation Framework) to resolve a path
	 * extension to a specific MediaType.
	 * <p>By default this is not set in which case
	 * {@code PathExtensionContentNegotiationStrategy} will use JAF if available.
	 */ //是否使用JAF来解析路径扩展名，默认没有开启
	public ContentNegotiationConfigurer useJaf(boolean useJaf) {
		this.factory.setUseJaf(useJaf);
		return this;
	}

	/**
	 * Whether a request parameter ("format" by default) should be used to
	 * determine the requested media type. For this option to work you must
	 * register {@link #mediaType(String, MediaType) media type mappings}.
	 * <p>By default this is set to {@code false}.
	 * @see #parameterName(String)
	 */ //是否启用从请求参数中获取扩展名来解析内容类型，默认为开启
	public ContentNegotiationConfigurer favorParameter(boolean favorParameter) {
		this.factory.setFavorParameter(favorParameter);
		return this;
	}

	/**
	 * Set the query parameter name to use when {@link #favorParameter} is on.
	 * <p>The default parameter name is {@code "format"}.
	 */ //设置请求参数中表示扩展名的参数名称，默认是format
	public ContentNegotiationConfigurer parameterName(String parameterName) {
		this.factory.setParameterName(parameterName);
		return this;
	}

	/**
	 * Whether to disable checking the 'Accept' request header.
	 * <p>By default this value is set to {@code false}.
	 */ //是否禁用从header中解析内容类型，默认没有禁用
	public ContentNegotiationConfigurer ignoreAcceptHeader(boolean ignoreAcceptHeader) {
		this.factory.setIgnoreAcceptHeader(ignoreAcceptHeader);
		return this;
	}

	/**
	 * Set the default content type to use when no content type is requested.
	 * <p>By default this is not set.
	 * @see #defaultContentTypeStrategy
	 */ //设置默认的内容类型，当没有内容类型被请求时使用，默认没有设置
	public ContentNegotiationConfigurer defaultContentType(MediaType defaultContentType) {
		this.factory.setDefaultContentType(defaultContentType);
		return this;
	}

	/**
	 * Set a custom {@link ContentNegotiationStrategy} to use to determine
	 * the content type to use when no content type is requested.
	 * <p>By default this is not set.
	 * @see #defaultContentType
	 * @since 4.1.2
	 */ //设置一个默认的内容协商策略，当没有内容类型被请求时使用，默认没有设置
	public ContentNegotiationConfigurer defaultContentTypeStrategy(ContentNegotiationStrategy defaultStrategy) {
		this.factory.setDefaultContentTypeStrategy(defaultStrategy);
		return this;
	}


	/**
	 * Build a {@link ContentNegotiationManager} based on this configurer's settings.
	 * @since 4.3.12
	 * @see ContentNegotiationManagerFactoryBean#getObject()
	 */ //根据配置构建一个内容协商管理器
	protected ContentNegotiationManager buildContentNegotiationManager() {
		this.factory.addMediaTypes(this.mediaTypes);
		this.factory.afterPropertiesSet();
		return this.factory.getObject();
	}

	/**
	 * @deprecated as of 4.3.12, in favor of {@link #buildContentNegotiationManager()}
	 */
	@Deprecated
	protected ContentNegotiationManager getContentNegotiationManager() throws Exception {
		return buildContentNegotiationManager();
	}

}
