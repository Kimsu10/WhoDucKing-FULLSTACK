package hello.controller.animations;

import hello.dto.animation.AniReviewDTO;
import hello.dto.user.CustomOAuth2User;
import hello.entity.review.Review;
import hello.entity.user.ProfileImage;
import hello.entity.user.User;
import hello.exception.ReviewLimitExceedException;
import hello.repository.db.ReviewRepository;
import hello.service.animations.ReviewService;
import hello.service.basic.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    /* @ POST 리뷰 작성 */
    @PostMapping("/animations/{AnimationId}/review")
    public ResponseEntity<String> writeReview(
            @PathVariable long AnimationId,
            @ModelAttribute AniReviewDTO aniReviewDTO,
            @AuthenticationPrincipal CustomOAuth2User user,
            HttpSession session) {

        aniReviewDTO.setAnimationId(AnimationId);
        User loginUser = userService.getLoginUserDetail(user);

        try {
            reviewService.addReview(aniReviewDTO, loginUser, session);
            String levelImageName = loginUser.getLevel().getImageName();
            ProfileImage profileImage = loginUser.getProfileImage();
            String profileImageName = null;

            if (profileImage != null) {
                profileImageName = profileImage.getStoreImageName();
            }
            session.setAttribute("levelImageName", levelImageName);
            session.setAttribute("profileImageName", profileImageName);

            return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/animations/" + AnimationId).build();

        } catch (ReviewLimitExceedException e) {
            String errorMessage = e.getMessage();
            String encodedErrorMessage = UriUtils.encode(errorMessage, StandardCharsets.UTF_8);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/animations/" + AnimationId + "?error=" + encodedErrorMessage)
                    .build();
        }
    }

    /* @ 리뷰 수정
    *  @ id = reviewId */
    @PatchMapping("/reviews/{id}")
    public ResponseEntity<String> updateReview(@PathVariable long id, @RequestBody Review review) {

        try {
            Review existingReview = reviewService.findById(id);
            if (existingReview != null) {
                existingReview.setContent(review.getContent());
                existingReview.setScore(review.getScore());
                reviewService.save(existingReview);

                return ResponseEntity.ok("Review updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating review: " + e.getMessage());
        }
    }


    /* @ 리뷰 삭제
    *  @ id = reviewId */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    /* 좋아요 요청 */
    @PatchMapping("/reviews/{reviewId}/like")
    public ResponseEntity<Integer> likeReview(@PathVariable("reviewId") Long id, @AuthenticationPrincipal CustomOAuth2User user) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }

        User loginUser = userService.getLoginUserDetail(user);
        Long userId = loginUser.getId();

        reviewService.likeReview(id, userId);

        return ResponseEntity.ok().body(review.getLikeCount());
    }

    /* 싫어요 요청 */
    @PatchMapping("/reviews/{reviewId}/dislike")
    public ResponseEntity<Integer> dislikeReview(@PathVariable("reviewId") Long id, @AuthenticationPrincipal CustomOAuth2User user) {
        Review review = reviewRepository.findById(id).orElse(null);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }

        User loginUser = userService.getLoginUserDetail(user);
        Long userId = loginUser.getId();

        reviewService.dislikeReview(id, userId);

        return ResponseEntity.ok().body(review.getDislikeCount());
    }
}
