package hello.repository.db;

import hello.dto.animation.GetAniListDTO;
import hello.dto.animation.ReviewLikeDTO;
import hello.dto.main.RankedAnimationsDTO;
import hello.dto.playground.WorldCupDTO;
import hello.entity.animation.Animation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnimationRepository extends JpaRepository<Animation, Long> {

    // 애니 전체 리스트 조회 -> 최신순으로 조회 + 장르 id 추가
    @Query("SELECT new hello.dto.animation.GetAniListDTO(a.id, a.imageName, ROUND(COALESCE(AVG(r.score), 0.0), 1), COUNT(DISTINCT r.id), a.name, " +
            "MIN(ag.genre.id), MAX(ag.genre.id)) " +
            "FROM Animation a " +
            "LEFT JOIN a.reviews r " +
            "LEFT JOIN a.animationGenres ag " +
            "GROUP BY a.id, a.imageName, a.name " +
            "ORDER BY a.id DESC")
    List<GetAniListDTO> findAnimationsWithReviews();

    // 애니상세리스트 별 평점과 리뷰수 조회
    @Query("SELECT new hello.dto.animation.GetAniListDTO(a.id, NULL, ROUND(COALESCE(AVG(r.score), 0.0), 1), COUNT(r.id), a.name) " +
            "FROM Animation a LEFT JOIN a.reviews r " +
            "WHERE a.id = :animationId " +
            "GROUP BY a.id, a.name")
    List<GetAniListDTO> findAnimationDetailsById(long animationId);

    // 특정 애니메이션을 가져오면서 리뷰도 함께 가져오기 (FETCH JOIN)
    @Query("SELECT a FROM Animation a LEFT JOIN FETCH a.reviews r WHERE a.id = :animationId")
    Animation findAnimationWithReviewsById(@Param("animationId") Long animationId);

    @Query("SELECT new hello.dto.playground.WorldCupDTO(a.id, a.name, a.imageName) " +
            "FROM Animation a " +
            "ORDER BY a.id DESC")
    List<WorldCupDTO> findWorldCupAnimations();

    // 애니메이션의 리뷰만 가져오기
    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.animation.id = :animationId")
    int countReviewsByAnimationId(Long animationId);

    // 좋아요 여부 확인용
    @Query("SELECT new hello.dto.animation.ReviewLikeDTO(rl.id , rl.reviewId.id, rl.userId.id, rl.isLike, rl.isDislike) " +
            "FROM ReviewLike rl JOIN rl.reviewId r JOIN r.animation a " +
            "WHERE a.id = :animationId")
    List<ReviewLikeDTO> findReviewLikesByAnimationId(@Param("animationId") Long animationId);

    @Query(value = "SELECT a.id, a.name AS answer, a.image_name AS imageName FROM Animation a ORDER BY RAND()", nativeQuery = true)
    List<Object[]> findRandomQuizzes();

    @Query("SELECT a FROM Animation a WHERE REPLACE(a.name, ' ', '') LIKE %:name%")
    List<Animation> findByNameIgnoringSpaces(@Param("name") String name);

    @Query("SELECT a FROM Animation a WHERE REPLACE(a.name, ' ', '') LIKE %:name%")
    Page<Animation> findByNameIgnoringSpaces(@Param("name") String name, Pageable pageable);

    @Query("select new hello.dto.main.RankedAnimationsDTO(a.id, a.imageName) from Animation a")
    List<RankedAnimationsDTO> findRankedAnimations();

    @Query("SELECT a.id, a.name AS title, a.imageName FROM Animation a ORDER BY a.id DESC")
    List<Object[]> findLatestAnimations();
}
