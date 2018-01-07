/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.web.accept;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * A {@code ContentNegotiationStrategy} that resolves a query parameter to a key
 * to be used to look up a media type. The default parameter name is {@code format}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */ //从请求参数中解析出文件扩展名
public class ParameterContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

	private static final Log logger = LogFactory.getLog(ParameterContentNegotiationStrategy.class);

	private String parameterName = "format"; //默认的请求参数名称


	/**
	 * Create an instance with the given map of file extensions and media types.
	 */
	public ParameterContentNegotiationStrategy(Map<String, MediaType> mediaTypes) {
		super(mediaTypes);
	}


	/**
	 * Set the name of the parameter to use to determine requested media types.
	 * <p>By default this is set to {@code "format"}.
	 */ //可以修改默认请求参数的名称
	public void setParameterName(String parameterName) {
		Assert.notNull(parameterName, "'parameterName' is required");
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return this.parameterName;
	}


	@Override //从请求中获取指定参数名称的值，作为文件扩展名，该扩展名最后会映射成MediaType
	protected String getMediaTypeKey(NativeWebRequest request) {
		return request.getParameter(getParameterName());
	}

	@Override //可以映射出一个MediaType，打印个日志
	protected void handleMatch(String mediaTypeKey, MediaType mediaType) {
		if (logger.isDebugEnabled()) {
			logger.debug("Requested media type: '" + mediaType + "' based on '" +
					getParameterName() + "'='" + mediaTypeKey + "'");
		}
	}

	@Override //无法映射出一个MediaType，抛出异常
	protected MediaType handleNoMatch(NativeWebRequest request, String key)
			throws HttpMediaTypeNotAcceptableException {

		throw new HttpMediaTypeNotAcceptableException(getAllMediaTypes());
	}

}
