package com.takeout.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.takeout.reggie.common.BaseContext;
import com.takeout.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经登录
 */
@Slf4j
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String requestURI = request.getRequestURI();

        log.info("拦截到请求:{}", requestURI);

        //判断本次请求是否需要处理,urls存不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);

        //如果不需要则直接放行
        if(check){
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //判断登录状态，如果已登录则直接放行(后台页面)
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为{}", request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }

        //判断登录状态，如果已登录则直接放行(移动端)
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为{}", request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //如果未登录则返回未登录结果,通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match)
                return true;
        }
        return false;
    }
}
