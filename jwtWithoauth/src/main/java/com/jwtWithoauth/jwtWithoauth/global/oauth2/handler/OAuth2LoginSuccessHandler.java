package com.jwtWithoauth.jwtWithoauth.global.oauth2.handler;

import com.jwtWithoauth.jwtWithoauth.domain.member.constant.Role;
import com.jwtWithoauth.jwtWithoauth.global.jwt.service.JwtService;
import com.jwtWithoauth.jwtWithoauth.global.oauth2.CustomOAuth2Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2Member oAuth2Member = (CustomOAuth2Member) authentication.getPrincipal();

            // Member의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if (oAuth2Member.getRole() == Role.GUEST) {
                String accessToken = jwtService.createAccessToken(oAuth2Member.getEmail());
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.sendRedirect("/oauth2/signup"); // 추가정보 입력 폼으로 리다이렉트
                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
            } else {
                loginSuccess(response, oAuth2Member); // 로그인에 성공한 경우 access, refresh 토큰 생성
                System.out.println("기존 회원");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2Member oAuth2Member) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2Member.getEmail());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2Member.getEmail(), refreshToken);
    }
}
