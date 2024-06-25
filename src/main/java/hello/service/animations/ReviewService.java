package hello.service.animations;

import hello.dto.animation.AniReviewDTO;
import hello.entity.animation.Animation;
import hello.entity.review.Review;
import hello.entity.user.User;
import hello.repository.db.AnimationRepository;
import hello.repository.db.ReviewRepository;
import hello.repository.db.UserRepository;
import hello.service.basic.ExpService;
import hello.service.basic.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AnimationRepository animationRepository;
    private final UserRepository userRepository;
    private final PointService pointService;
    private final ExpService expService;

    public void addReview(AniReviewDTO aniReviewDTO, User user) {
        Optional<Animation> animationOpt = animationRepository.findById(aniReviewDTO.getAnimationId());
        Optional<User> userOpt = userRepository.findById(aniReviewDTO.getUserId());

        if (animationOpt.isEmpty()) {
            throw new IllegalArgumentException("Animation id: " + aniReviewDTO.getAnimationId() + " 가 없습니다.");
        }
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("사용자 id: " + aniReviewDTO.getUserId() + " 가 없습니다.");
        }

        LocalDate today = LocalDate.now();
        long reviewCountToday = reviewRepository.countReviewByUserAndDate(user, today);

        if (reviewCountToday >= 3) {
            throw new ReviewLimitExceed("하루에 3번만 리뷰를 작성 할 수 있습니다.");
        }

        Review review = new Review();
        review.setAnimation(animationOpt.get());
        review.setUser(userOpt.get());
        review.setContent(aniReviewDTO.getContent());
        review.setScore(aniReviewDTO.getScore());
        review.setSpoiler(aniReviewDTO.getIsSpoiler());

        int currentReviewCount = user.getReviewCount();
        user.setReviewCount(currentReviewCount + 1);
        reviewRepository.save(review);

        if (reviewCountToday == 0) {
            pointService.increasePoint(user, 5);
            expService.increaseExp(user, 5);
        } else {
            pointService.increasePoint(user, 1);
            expService.increaseExp(user, 3);
        }
    }

    // @ 리뷰 수정
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }

    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    // @ 리뷰 삭제
    public void deleteReview(long reviewId) {
        Optional<Review> reviewOpt = reviewRepository.findById(reviewId);

        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            User user = review.getUser();

            user.setReviewCount(user.getReviewCount() - 1);
            userRepository.save(user);

            reviewRepository.deleteById(reviewId);
        } else {
            System.out.println("Review not found for id: " + reviewId);
        }
    }

    // 매일 자정 실행
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetReviewCount() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            user.setReviewCount(0);
        }
        userRepository.saveAll(users);
    }

    public static class ReviewLimitExceed extends RuntimeException {
        public ReviewLimitExceed(String message) {
            super(message);
        }
    }
}
