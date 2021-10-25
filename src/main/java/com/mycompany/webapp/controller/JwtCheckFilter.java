package com.mycompany.webapp.controller;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mycompany.webapp.security.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtCheckFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("실행");
		//JWT 얻기
		String jwt = null;
		if(request.getHeader("Authorization")!=null && 
				request.getHeader("Authorization").startsWith("Bearer")) {
			jwt = request.getHeader("Authorization").substring(7);
		}
		log.info("jwt : " + jwt );
		//JWT 유효성 검사
		if(jwt!=null) {
			Claims claims = JwtUtil.validateToken(jwt);
			if(claims !=null) {
				log.info("유효한 토큰");
				String mid = JwtUtil.getMid(claims);
				String authority = JwtUtil.getAuthority(claims);
				log.info("mid : " + mid);
				log.info("authority + " + authority );
				//Security 인증 처리
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(mid,null,AuthorityUtils.createAuthorityList(authority));
				SecurityContext securityContext = SecurityContextHolder.getContext();
				securityContext.setAuthentication(authentication);
			}else {
				log.info("유효하지 않은 토큰");
			}
		}
		//다음 필터를 실행
		filterChain.doFilter(request, response);
	}
	
}