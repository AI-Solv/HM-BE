package com.example.HM;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Test
    public void testSignup() throws Exception {
        // Given: 회원가입 요청 데이터
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail("test@example.com");
        memberDTO.setPassword("password123");
        memberDTO.setName("Test User");

        // When: 회원가입 요청
        mockMvc.perform(post("/member/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDTO)))
                // Then: 회원가입 성공 확인
                .andExpect(status().isOk())
                .andExpect(content().string("회원가입이 완료되었습니다."));
    }

    @Test
    public void testLogin() throws Exception {
        // Given: 회원가입 후 로그인 요청 데이터
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail("test@example.com");
        memberDTO.setPassword("password123");
        memberDTO.setName("Test User");
        memberService.save(memberDTO); // 미리 저장된 사용자

        // When: 로그인 요청
        mockMvc.perform(post("/member/login")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                // Then: 로그인 성공 확인
                .andExpect(status().isOk())
                .andExpect(content().string("로그인 성공"));
    }

    @Test
    public void testSendEmail() throws Exception {
        // When: 이메일 인증번호 요청
        mockMvc.perform(post("/member/send-email")
                        .param("email", "test@example.com"))
                // Then: 이메일 인증번호 전송 확인
                .andExpect(status().isOk())
                .andExpect(content().string("인증번호가 이메일로 전송되었습니다."));
    }

    @Test
    public void testVerifyEmail() throws Exception {
        // Given: 이메일 인증번호 요청 및 확인 데이터
        String email = "test@example.com";
        String code = "123456"; // 테스트용 인증번호
        memberService.sendEmailVerification(email); // 인증번호 전송
        memberService.verifyEmail(email, code);     // 인증번호 확인

        // When: 이메일 인증번호 확인
        mockMvc.perform(post("/member/verify-email")
                        .param("email", email)
                        .param("code", code))
                // Then: 인증 성공 확인
                .andExpect(status().isOk())
                .andExpect(content().string("이메일 인증이 완료되었습니다."));
    }


    @Test
    public void testFindPassword() throws Exception {
        // Given: 비밀번호 찾기 요청 데이터
        String email = "test@example.com";
        String code = "123456";
        memberService.sendEmailVerification(email);
        memberService.verifyEmail(email, code);

        // When: 비밀번호 찾기 요청
        mockMvc.perform(post("/member/find-password")
                        .param("email", email)
                        .param("code", code))
                // Then: 비밀번호 반환 확인
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호: password123"));
    }
}
