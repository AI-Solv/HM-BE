package com.example.HM;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";

    @BeforeEach
    public void setUp() {
        // 기존 회원 정보 삭제 후 새로운 회원 추가
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail(TEST_EMAIL);
        memberDTO.setPassword(TEST_PASSWORD);
        memberDTO.setName("Test User");

        try {
            memberService.save(memberDTO);
        } catch (Exception ignored) {
            // 회원이 이미 존재할 경우 예외 무시
        }
    }

    @Test
    public void testSignup() throws Exception {
        // Given: 회원가입 요청 데이터
        MemberDTO newMember = new MemberDTO();
        newMember.setEmail("newuser@example.com");
        newMember.setPassword("newpassword123");
        newMember.setName("New User");

        // When: 회원가입 요청
        mockMvc.perform(post("/member/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMember)))
                // Then: 회원가입 성공 확인
                .andExpect(status().isOk())
                .andExpect(content().string("✅ 회원가입이 완료되었습니다."));
    }

    @Test
    public void testLogin() throws Exception {
        // Given: 저장된 사용자 정보
        MemberDTO loginMember = new MemberDTO();
        loginMember.setEmail(TEST_EMAIL);
        loginMember.setPassword(TEST_PASSWORD);

        // When: 로그인 요청
        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginMember)))
                // Then: 로그인 성공 확인
                .andExpect(status().isOk())
                .andExpect(content().string("✅ 로그인 성공"));
    }

    @Test
    public void testSendEmail() throws Exception {
        // When: 이메일 인증번호 요청
        mockMvc.perform(post("/member/send-email")
                        .param("email", TEST_EMAIL))
                // Then: 이메일 인증번호 전송 확인
                .andExpect(status().isOk())
                .andExpect(content().string("📩 인증번호가 이메일로 전송되었습니다."));
    }

    @Test
    public void testVerifyEmail() throws Exception {
        // Given: 이메일 인증번호 요청 및 확인 데이터
        String code = "123456"; // 테스트용 인증번호
        memberService.sendEmailVerificationCode(TEST_EMAIL);
        memberService.verifyEmailCode(TEST_EMAIL, code);

        // When: 이메일 인증번호 확인
        mockMvc.perform(post("/member/verify-code")
                        .param("email", TEST_EMAIL)
                        .param("code", code))
                // Then: 인증 성공 확인
                .andExpect(status().isOk())
                .andExpect(content().string("✅ 인증 성공"));
    }

    @Test
    public void testFindPassword() throws Exception {
        // Given: 비밀번호 찾기 요청 데이터
        String code = "123456";
        memberService.sendEmailVerificationCode(TEST_EMAIL);
        memberService.verifyEmailCode(TEST_EMAIL, code);

        // When: 비밀번호 찾기 요청
        mockMvc.perform(post("/member/find-password")
                        .param("email", TEST_EMAIL)
                        .param("code", code))
                // Then: 비밀번호 반환 확인
                .andExpect(status().isOk())
                .andExpect(content().string("비밀번호: " + TEST_PASSWORD));
    }
}
