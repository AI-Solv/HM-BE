package com.example.HM.Domain.Member.Controller;

import com.example.HM.Domain.Member.DTO.MemberDTO;
import com.example.HM.Domain.Member.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller  // RestController 대신 Controller 사용
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // 회원가입 페이지 출력 요청
    @GetMapping("/save")
    public String saveForm() {
        return "redirect:/save.html"; // save.html 뷰 반환
    }

    // 로그인 페이지 출력 요청
    @GetMapping("/login")
    public String loginForm() {
        return "redirect:/login.html"; // login.html 뷰 반환
    }

    // 회원가입 처리
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> save(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.save(memberDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 전체 에러 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패");
        }
    }

    // 회원가입 처리 : 이메일 중복 확인
    @GetMapping("/check-email")
    @ResponseBody
    public ResponseEntity<String> checkEmail(@RequestParam("email") String email) {
        boolean isDuplicate = memberService.checkEmailDuplicate(email);
        if (isDuplicate) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }
        return ResponseEntity.ok("사용 가능한 이메일입니다.");
    }

    // 회원가입 처리 : 비밀번호 일치 불일치 확인
    @PostMapping("/check-passwords")
    @ResponseBody
    public ResponseEntity<String> checkPasswords(@RequestBody Map<String, String> passwords) {
        String password = passwords.get("password");
        String confirmPassword = passwords.get("confirmPassword");

        if (password.equals(confirmPassword)) {
            return ResponseEntity.ok("비밀번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
    }

    // 로그인 처리
    @PostMapping("/login")
    @ResponseBody  // JSON 응답 반환
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        boolean isAuthenticated = memberService.login(email, password);
        return isAuthenticated ? ResponseEntity.ok("로그인 성공")
                : ResponseEntity.badRequest().body("이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
