package com.zxl.config;

import com.zxl.service.MyFilterSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * Created by zxl on 19/5/2.
 */

/*1.首先，权限管理离不开登陆验证的，所以登陆验证拦截器AuthenticationProcessingFilter要讲；
2.还有就是对访问的资源管理吧，所以资源管理拦截器AbstractSecurityInterceptor要讲；

3.但拦截器里面的实现需要一些组件来实现，所以就有了AuthenticationManager、accessDecisionManager等组件来支撑。

4.现在先大概过一遍整个流程，用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现,
        而且AuthenticationManager会调用ProviderManager来获取用户验证信息（不同的Provider调用的服务不同，因为这些信息可以是在数据库上，
        可以是在LDAP服务器上，可以是xml配置文件上等），如果验证通过后会将用户的权限信息封装一个User放到spring的全局缓存SecurityContextHolder中，
        以备后面访问资源时使用。
5.访问资源（即授权管理），访问url时，会通过AbstractSecurityInterceptor拦截器拦截，
        其中会调用FilterInvocationSecurityMetadataSource的方法来获取被拦截url所需的全部权限，在调用授权管理器AccessDecisionManager，
        这个授权管理器会通过spring的全局缓存SecurityContextHolder获取用户的权限信息，还会获取被拦截的url和被拦截url所需的全部权限，
        然后根据所配的策略（有：一票决定，一票否定，少数服从多数等），如果权限足够，则返回，权限不够则报错并调用权限不足页面。*/


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;
    @Autowired
    UserDetailsService customUserService;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现
        //而AuthenticationManager调用Provider，provider调用userDetaisService（customUserService）来根据username获取真实的数据库信息。
        auth.userDetailsService(customUserService); //user Details Service验证
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**").permitAll()
                .anyRequest().authenticated() //任何请求,登录后可以访问(这样的请求才会执行CustomUserService类的方法)
                .and()
                .formLogin() //用户登陆，会被AuthenticationProcessingFilter拦截，调用AuthenticationManager的实现，而AuthenticationManager调用Provider，provider调用userDetaisService（customUserService类）来根据username获取真实的数据库信息。
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error")
                .permitAll() //登录页面用户任意访问
                .and()
                .logout().permitAll(); //注销行为任意访问
        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }

}

