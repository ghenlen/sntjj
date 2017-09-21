package com.yawei.interceptor;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.modules.sys.security.SystemAuthorizingRealm.Principal;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.yawei.pso.BaseUtils;
import com.yawei.pso.PSORequest;
import com.yawei.pso.SSOResponse;
import com.yawei.pso.TicketManager;

/**
 * 自定义拦截器，拦截符合条件url的请求 身份验证拦截
 * (是否有登录用户信息)
 */
public class IdentityInterceptor extends HandlerInterceptorAdapter
{
	// 加载日志
	static Logger log = Logger.getLogger(IdentityInterceptor.class);
	// 读取ssoToken参数
	private static final String strToken = BaseUtils.getConfigValue("ssoKey");

	public final static String SEESION_USER = "seesion_user";

	/**
	 * 在业务处理器处理请求之前被调用 如果返回false 从当前的拦截器往回执行所有拦截器的afterCompletion(),再退出拦截器链
	 * 如果返回true 执行下一个拦截器,直到所有的拦截器都执行完毕 再执行被拦截的Controller 然后进入拦截器链,
	 * 从最后一个拦截器往回执行所有的postHandle() 接着再从最后一个拦截器往回执行所有的afterCompletion()
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception
	{

		/* 20170919 Tianqing Start */
		// 用户是否验证
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()){
			return true;
		}
		/* 20170919 Tianqing End */
		
		log.debug("==============执行顺序: 1、preHandle================");

		// 获取当前请求的url
		String requestUri = request.getRequestURI();
		
		Validator validator = Validator.getInstance();
		// 注入当前session
		// validator.init(request);

		String strResponse = request.getParameter(strToken);

		if (strResponse != null)
		{
			// 如果服务器端通过认证后，会返回后执行改操作，然后写入cookie
			SSOResponse ssoResp = new SSOResponse(strResponse);
			TicketManager tm = ssoResp.CreatePSOTicket();
			if (tm == null)
			{
				PSORequest psoRequest = new PSORequest(request);
				String requeststr = psoRequest.CreateHash();

				String keeperUrl = BaseUtils.getConfigValue("keeperUrl");
				keeperUrl = keeperUrl + "?" + strToken + "="
						+ URLEncoder.encode(requeststr, "UTF-8");
				response.sendRedirect(keeperUrl);
			}
			else
			{
				String domainName = BaseUtils.getConfigValue("domain");
				tm.SaveTicket(response, domainName);

				Iterator<Entry<String, String[]>> iterator = request
						.getParameterMap().entrySet().iterator();
				StringBuffer param = new StringBuffer();
				int i = 0;
				while (iterator.hasNext())
				{
					Entry<String, String[]> entry = (Entry<String, String[]>) iterator
							.next();
					if (entry.getKey().equals(strToken))
						continue;
					else
					{
						i++;
						if (i == 1)
							param.append("?").append(entry.getKey())
									.append("=");
						else
							param.append("&").append(entry.getKey())
									.append("=");

						if (entry.getValue() instanceof String[])
						{
							param.append(((String[]) entry.getValue())[0]);
						}
						else
						{
							param.append(entry.getValue());
						}
					}
				}
				response.sendRedirect(requestUri + param.toString());
				return false;
			}
		}
		else
		{
			TicketManager tm = new TicketManager();
			if (!tm.LoadTicket(request))
			{
				PSORequest psoRequest = new PSORequest(request);
				String requeststr = psoRequest.CreateHash();

				String keeperUrl = BaseUtils.getConfigValue("keeperUrl");
				keeperUrl = keeperUrl + "?" + strToken + "="
						+ URLEncoder.encode(requeststr, "UTF-8");
				response.sendRedirect(keeperUrl);
				return false;
			}
		}

		return validator.SetUserTicket(request,response);
	}

	/**
	 * 在业务处理器处理请求执行完成后,生成视图之前执行的动作 可在modelAndView中加入数据，比如当前时间
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception
	{
		log.debug("==============执行顺序: 2、postHandle================");
	}

	/**
	 * 在DispatcherServlet完全处理完请求后被调用,可用于清理资源等
	 * 
	 * 当有拦截器抛出异常时,会从当前拦截器往回执行所有的拦截器的afterCompletion()
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
					throws Exception
	{
		log.debug("==============执行顺序: 3、afterCompletion================");
	}
}
