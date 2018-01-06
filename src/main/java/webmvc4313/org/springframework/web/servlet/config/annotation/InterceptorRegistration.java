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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * Assists with the creation of a {@link MappedInterceptor}.
 *
 * @author Rossen Stoyanchev
 * @author Keith Donald
 * @since 3.1
 */
public class InterceptorRegistration { //拦截器注册，包括一个拦截器和它将要应用的路径

	private final HandlerInterceptor interceptor; //处理器拦截器

	private final List<String> includePatterns = new ArrayList<String>(); //拦截的路径

	private final List<String> excludePatterns = new ArrayList<String>(); //排除的路径

	private PathMatcher pathMatcher; //路径匹配器，用来进行路径匹配


	/**
	 * Creates an {@link InterceptorRegistration} instance.
	 */
	public InterceptorRegistration(HandlerInterceptor interceptor) {
		Assert.notNull(interceptor, "Interceptor is required");
		this.interceptor = interceptor;
	}

	/**
	 * Add URL patterns to which the registered interceptor should apply to.
	 */
	public InterceptorRegistration addPathPatterns(String... patterns) { //添加需要被拦截的路径
		this.includePatterns.addAll(Arrays.asList(patterns));
		return this;
	}

	/**
	 * Add URL patterns to which the registered interceptor should not apply to.
	 */
	public InterceptorRegistration excludePathPatterns(String... patterns) { //添加需要被排除的路径
		this.excludePatterns.addAll(Arrays.asList(patterns));
		return this;
	}

	/**
	 * A PathMatcher implementation to use with this interceptor. This is an optional,
	 * advanced property required only if using custom PathMatcher implementations
	 * that support mapping metadata other than the Ant path patterns supported
	 * by default.
	 */
	public InterceptorRegistration pathMatcher(PathMatcher pathMatcher) { //设置路径匹配器
		this.pathMatcher = pathMatcher;
		return this;
	}

	/**
	 * Returns the underlying interceptor. If URL patterns are provided the returned type is
	 * {@link MappedInterceptor}; otherwise {@link HandlerInterceptor}.
	 */
	protected Object getInterceptor() {
		if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) { //如果没有添加过路径
			return this.interceptor; //直接返回拦截器
		}

		String[] include = toArray(this.includePatterns);
		String[] exclude = toArray(this.excludePatterns);
		MappedInterceptor mappedInterceptor = new MappedInterceptor(include, exclude, this.interceptor); //添加了路径

		if (this.pathMatcher != null) { //如果设置了路径匹配器
			mappedInterceptor.setPathMatcher(this.pathMatcher);
		}

		return mappedInterceptor; //返回的是一个拦截器的代理
	}

	private static String[] toArray(List<String> list) {
		return (CollectionUtils.isEmpty(list) ? null : list.toArray(new String[list.size()]));
	}

}
