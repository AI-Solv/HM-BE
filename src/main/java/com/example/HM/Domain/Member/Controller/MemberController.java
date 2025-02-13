package com.example.HM.Domain.Member.Controller;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.MailException;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import java.util.Map;

@Slf4j
//@RestController // JSON 응답을 반환하는 컨트롤러
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // 🔹 로그인 페이지 반환 (GET 요청 허용)
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // login.html 페이지로 리디렉트
    }

    // 🔹 회원가입 페이지 반환 (GET 요청 허용)
    @GetMapping("/save")
    public String saveForm() {
        return "save"; // save.html 페이지로 리디렉트
    }

    // 🔹 회원가입 처리 (이메일 인증 확인 필수)
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.save(memberDTO);
            return ResponseEntity.ok("✅ 회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            log.warn("🚫 회원가입 실패 - 사유: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("❌ 회원가입 중 예외 발생", e);
            return ResponseEntity.internalServerError().body("회원가입 실패");
        }
    }

    // 🔹 이메일 중복 확인
    @GetMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        return memberService.checkEmailDuplicate(email)
                ? ResponseEntity.badRequest().body("🚫 이미 존재하는 이메일입니다.")
                : ResponseEntity.ok("✅ 사용 가능한 이메일입니다.");
    }

    // 🔹 비밀번호 확인
    @PostMapping("/check-passwords")
    public ResponseEntity<String> checkPasswords(@RequestBody Map<String, String> passwords) {
        String password = passwords.get("password");
        String confirmPassword = passwords.get("confirmPassword");

        return password.equals(confirmPassword)
                ? ResponseEntity.ok("✅ 비밀번호가 일치합니다.")
                : ResponseEntity.badRequest().body("🚫 비밀번호가 일치하지 않습니다.");
    }

    // 🔹 로그인 처리 (POST 요청만 가능)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        return memberService.login(email, password)
                ? ResponseEntity.ok("✅ 로그인 성공")
                : ResponseEntity.badRequest().body("🚫 이메일 또는 비밀번호가 올바르지 않습니다.");
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam("email") String email) {
        try {
            // 1️⃣ 이메일 형식 검증
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                log.warn("🚫 잘못된 이메일 형식 입력: {}", email);
                return ResponseEntity.badRequest().body("잘못된 이메일 형식입니다. 올바른 이메일을 입력해주세요.");
            }

            // 2️⃣ 이메일 전송 시도
            memberService.sendEmailVerificationCode(email);
            return ResponseEntity.ok("📩 인증번호가 이메일로 전송되었습니다.");

        } catch (IllegalArgumentException e) {
            log.warn("🚫 이메일 인증 요청 실패 - 사유: {}", e.getMessage());
            return ResponseEntity.badRequest().body("이메일 인증 요청 실패: " + e.getMessage());

        } catch (MailAuthenticationException e) {
            // ✅ SMTP 인증 실패 (앱 비밀번호 오류 가능성)
            log.error("❌ SMTP 인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 전송 실패: SMTP 인증 실패 (앱 비밀번호 확인 필요)");

        } catch (MailSendException e) {
            // ✅ SMTP 서버 응답 없음 또는 전송 실패
            if (e.getMessage().contains("Could not connect to SMTP host")) {
                log.error("❌ SMTP 서버에 연결할 수 없음: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("이메일 전송 실패: 네이버 메일 서버에 연결할 수 없습니다.");
            }
            log.error("❌ 이메일 전송 실패 - SMTP 문제: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패: SMTP 전송 실패");

        } catch (MailException e) {
            // ✅ 기타 Spring Mail 관련 예외 처리
            log.error("❌ 이메일 전송 중 MailException 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패: 메일 서버 오류");

        } catch (Exception e) {
            // ✅ 일반적인 예외 처리
            log.error("❌ 이메일 전송 중 알 수 없는 예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 전송 실패: 서버 내부 오류 발생");
        }
    }

    // 🔹 인증번호 검증
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        return memberService.verifyEmailCode(email, code)
                ? ResponseEntity.ok("✅ 인증 성공")
                : ResponseEntity.badRequest().body("🚫 인증 실패");
    }
}
