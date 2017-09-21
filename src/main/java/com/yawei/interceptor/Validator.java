package com.yawei.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.thinkgem.jeesite.common.utils.CookieUtils;
import com.thinkgem.jeesite.common.utils.Encodes;
import com.thinkgem.jeesite.common.utils.SpringContextHolder;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.dao.UserDao;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.security.UsernamePasswordToken;
import com.yawei.pso.TicketManager;

/**
 * 验证器
 */
public class Validator
{
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	
	private static ThreadLocal<Validator> validatorHolder = new ThreadLocal<Validator>()
	{

		protected Validator initialValue()
		{
			return new Validator();
		}

	};

	// 当前请求的session
	private HttpSession session = null;

	// 当前的请求
	private HttpServletRequest request = null;

	private Validator()
	{
	}

	public static Validator getInstance()
	{
		return validatorHolder.get();
	}

	/**
	 * 执行初始化
	 * @param httpRequest
	 */
	public void init(HttpServletRequest httpRequest)
	{
		this.request = httpRequest;
		this.session = request.getSession();
	}

	/**
	 * 将凭证身份加入到session
	 * @param httpRequest
	 * @throws IOException 
	 */
	public boolean SetUserTicket(HttpServletRequest httpRequest,HttpServletResponse response) throws IOException
	{
		try
		{
			if (httpRequest.getSession()
					.getAttribute(IdentityInterceptor.SEESION_USER) == null)
			{
				TicketManager ticket = new TicketManager();
				if (ticket.LoadTicket(httpRequest))
				{
					// 登录用户姓名
					String userName = ticket.getUserName();
					// 登录用户账号
					String userAccount = ticket.getUserID();
					// 登录用户标识
					String userGuid = ticket.getADGUID();
					System.out.println("===userName===" + userName);
					System.out.println("===userAccount===" + userAccount);
					System.out.println("===userGuid===" + userGuid);
					
					/* 20170918 Tianqing 判断本地服务器是否存在该Seesion，不存在则创建  Start */
					User u = userDao.getByUserGuid(userGuid);
					if(u != null){
						String username = u.getLoginName();
						String password = Encodes.decodeBase64String(u.getUserKey());
						String host = StringUtils.getRemoteAddr(httpRequest);
						UsernamePasswordToken token =  new UsernamePasswordToken(username, password.toCharArray(), false, host, "", false);
						Subject subject = SecurityUtils.getSubject();
						subject.login(token);
						httpRequest.getSession().setAttribute("fromSource", true);
						return true;
					}
					/* 20170918 Tianqing 判断本地服务器是否存在该Seesion，不存在则创建 End */
				}
			}
			else
			{
			}
		}
		catch (Exception ex)
		{
		}
		//CookieUtils.getCookie(httpRequest,response,"UserID",true);
		response.sendError(403, "您无权访问此页面");
		return false;
	}

	/**
	 * 清除session
	 */
	public void cancel()
	{
		this.session = null;
	}

}
