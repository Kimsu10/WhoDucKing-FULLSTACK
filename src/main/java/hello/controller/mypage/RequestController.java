package hello.controller.mypage;

import hello.dto.user.CustomOAuth2User;
import hello.dto.user.MyRequestDTO;
import hello.dto.user.RequestDTO;
import hello.entity.user.User;
import hello.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/myPage")
@RequiredArgsConstructor
public class RequestController {

    private final UserService userService;

    // 건의 내역
    @GetMapping("/request-list")
    public String requestList(Model model, @RequestParam(name = "page", defaultValue = "0") int page) {
        Page<MyRequestDTO> requestPage = userService.getMyRequest(page, 5);
        model.addAttribute("requestPage", requestPage);
        return "myRequest";
    }

    @PostMapping("/request")
    public String request(@AuthenticationPrincipal CustomOAuth2User user, @ModelAttribute RequestDTO requestDTO) {
        User loginUser = userService.getLoginUserDetail(user);
        userService.receiveRequest(requestDTO, loginUser);
        return "redirect:/myPage/request-list";
    }

    @PostMapping("/request-delete")
    public String deleteRequest(@RequestParam("requestId") Long requestId) {
        userService.deleteRequest(requestId);
        return "redirect:/myPage/request-list";
    }
}