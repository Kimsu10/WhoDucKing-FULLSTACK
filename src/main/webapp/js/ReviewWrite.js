// 별점 -> (o)
document.addEventListener("DOMContentLoaded", function () {
    const totalStars = 5;
    const starsContainer = document.querySelector(".stars-container");
    const scoreInput = document.getElementById("score-number");

    const starsDiv = document.createElement("div");
    starsDiv.classList.add("stars");

    let selectedRating = 0;
    let scoreSpan;

    for (let i = 0; i < totalStars; i++) {
        const starIcon = document.createElement("i");
        starIcon.classList.add("fa-solid", "fa-star");
        starIcon.dataset.index = i;

        starIcon.addEventListener("click", function () {
            selectedRating = parseInt(this.dataset.index) + 1;
            updateStars();
        });

        starsDiv.appendChild(starIcon);
    }

    scoreSpan = document.createElement("span");
    scoreSpan.classList.add("score");
    scoreSpan.textContent = `${selectedRating} `;

    starsContainer.appendChild(starsDiv);
    starsContainer.appendChild(scoreSpan);

    function updateStars() {
        const stars = starsDiv.querySelectorAll("i");
        stars.forEach((star, index) => {
            if (index < Math.floor(selectedRating)) {
                star.classList.add("filled");
                star.classList.remove("half");
            } else if (index === Math.floor(selectedRating) && selectedRating % 1 !== 0) {
                star.classList.add("half");
                star.classList.remove("filled");
            } else {
                star.classList.remove("filled");
                star.classList.remove("half");
            }
        });
        scoreSpan.textContent = `${selectedRating} `;
        scoreInput.value = selectedRating;
    }
});


// 글자수 체크 -> (o)
document.addEventListener("DOMContentLoaded", function () {
    function countText() {
        let reviewContent = document.getElementById("review-content");
        let countSpan = document.getElementById("count");
        let count = reviewContent.value.length;

        countSpan.textContent = count + "/500";

        if (count > 500) {
            reviewContent.value = reviewContent.value.substring(0, 500);
            reviewContent.style.border = "1px solid red";
        } else {
            reviewContent.style.border = "";
        }
    }

    document
        .getElementById("review-content")
        .addEventListener("input", countText);
});



// 최신순 인기순 버튼 활성 -> (o) / 기능 -> (o)
document.addEventListener('DOMContentLoaded', function() {
    const recentBtn = document.getElementById('recent');
    const likeBtn = document.getElementById('like');

    recentBtn.classList.add('active');

    function handleButtonClick(event) {

        recentBtn.classList.remove('active');
        likeBtn.classList.remove('active');

        event.target.classList.add('active');
    }

    recentBtn.addEventListener('click', handleButtonClick);
    likeBtn.addEventListener('click', handleButtonClick);
});


// 클릭시 수정 삭제 모달창 -> (o)
document.addEventListener("DOMContentLoaded", function () {
    const showModalButtons = document.querySelectorAll("[id^='show-modal-']");
    const modalContainers = document.querySelectorAll("[id^='modal-container-']");

    showModalButtons.forEach((showModalButton, index) => {
        const modalContainer = modalContainers[index];

        showModalButton.addEventListener("click", function () {
            modalContainer.style.display = "block";
        });

        document.addEventListener("click", function (event) {
            if (event.target === showModalButton || modalContainer.contains(event.target)) {
                return;
            }
            modalContainer.style.display = "none";
        });
    });
});




// 리뷰 삭제 -> (o)
document.addEventListener("DOMContentLoaded", function () {
    const deleteButtons = document.querySelectorAll(".delete-review-btn");

    deleteButtons.forEach(deleteButton => {
        deleteButton.addEventListener("click", function () {
            const reviewId = this.getAttribute("data-review-id");
            deleteReview(reviewId);
        });
    });

    function deleteReview(reviewId) {
        if (confirm("정말로 이 리뷰를 삭제하시겠습니까?")) {
            fetch(`/deleteReview/${reviewId}`, {
                method: "DELETE"
            })
                .then(res => {
                    if (res.ok) {
                        alert("리뷰가 삭제되었습니다.");
                        window.location.reload();
                    } else {
                        alert("리뷰 삭제에 실패했습니다.");
                    }
                })
                .catch(error => {
                    console.error("server Error deleting review:", error);
                });
        }
    }
});



// 좋아요 요청 함수 ->(o)
function likeReview(reviewId) {
    let url = `/reviews/${reviewId}/like`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                let error = new Error(text);
                error.status = res.status;
                throw error;
            });
        }
        return res.json();
    }).then(data => {
        window.location.reload();
    }).catch(error => {
        console.error('서버 에러: 좋아요 액션 중 오류 발생', error);
    });
}


// 싫어요 요청 함수
function dislikeReview(reviewId) {
    let url = `/reviews/${reviewId}/dislike`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        }
    }).then(res => {
        if (!res.ok) {
            return res.text().then(text => {
                let error = new Error(text);
                error.status = res.status;
                throw error;
            });
        }
        return res.json();
    }).then(data => {
        window.location.reload();
    }).catch(error => {
        console.error('서버 에러: 싫어요 액션 중 오류 발생', error.status);
    });
}


// 리뷰 작성 시 빈 값 요청 불가
document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector(".write-box");
    const reviewContent = document.getElementById("review-content");

    form.addEventListener("submit", function(event) {
        let isValid = true;
        let errorMessage = "";

        if (reviewContent.value.trim() === "") {
            isValid = false;
            errorMessage += "리뷰 내용을 입력해주세요.\n";
        }

        if (!isValid) {
            event.preventDefault();
            alert(errorMessage);
        }
    });
});


// 스포일러 체크 여부에따라 보여주기
document.addEventListener('DOMContentLoaded', function () {
    const spoilerCheckbox = document.getElementById('show-spoiler');

    spoilerCheckbox.addEventListener('change', function () {
        const showSpoiler = this.checked;
        const reviews = document.querySelectorAll('.review-list');

        reviews.forEach(review => {
            const isSpoiler = review.querySelector('.is-reviews-spoiler').value === 'true';
            if (showSpoiler) {
                review.classList.remove('hidden');
            } else {
                if (isSpoiler) {
                    review.classList.add('hidden');
                } else {
                    review.classList.remove('hidden');
                }
            }
        });
    });

    spoilerCheckbox.dispatchEvent(new Event('change'));
});


// 리뷰 수정 하기 -> 이슈: 좋아요 순에서는 작동하지 않움
document.addEventListener("DOMContentLoaded", function() {
    function changeReviewTag(reviewId) {
        let recentReviewBox = document.querySelector("#recent-reviews #review-" + reviewId);
        let likeReviewBox = document.querySelector("#like-reviews #review-" + reviewId);

        if (!recentReviewBox && !likeReviewBox) {
            console.error('Review box not found for review ID:', reviewId);
            return;
        }

        let reviewBox = recentReviewBox || likeReviewBox;

        let reviewComment = reviewBox.querySelector(".review-comment");
        if (!reviewComment) {
            console.error('Review comment element not found for review ID:', reviewId);
            return;
        }

        let currentContent = reviewComment.innerText;
        let currentText = document.createElement("textarea");
        currentText.value = currentContent;
        currentText.className = "update-comment-textarea";

        reviewComment.replaceWith(currentText);

        let saveButton = document.createElement("button");
        saveButton.innerText = "수정";
        saveButton.className = "save-review-btn";

        saveButton.addEventListener("click", function() {
            saveUpdatedReview(reviewId);
        });

        currentText.after(saveButton);
    }

    function saveUpdatedReview(reviewId) {
        let recentReviewBox = document.querySelector("#recent-reviews #review-" + reviewId);
        let likeReviewBox = document.querySelector("#like-reviews #review-" + reviewId);

        if (!recentReviewBox && !likeReviewBox) {
            console.error('Review box not found for review ID:', reviewId);
            return;
        }

        let reviewBox = recentReviewBox || likeReviewBox;
        let currentText = reviewBox.querySelector(".update-comment-textarea");

        if (!currentText) {
            console.error('Modified comment textarea not found for review ID:', reviewId);
            return;
        }

        let updatedContent = currentText.value;

        fetch(`/reviews/patch/${reviewId}`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                content: updatedContent
            })
        })
            .then(res => res.text())
            .then(data => {
                console.log('Response text:', data);

                if (data === "Review updated successfully") {
                    let reviewComment = document.createElement("p");
                    reviewComment.className = "review-comment";
                    reviewComment.innerText = updatedContent;

                    currentText.replaceWith(reviewComment);

                    let saveButton = reviewBox.querySelector(".save-review-btn");
                    if (saveButton) {
                        saveButton.remove();
                    }
                } else {
                    console.error('Error updating review:', data);
                }
            })
            .catch(error => {
                console.error('Failed to fetch when updating review:', error);
            });
    }

    document.querySelectorAll(".update-review-btn").forEach(function(button) {
        button.addEventListener("click", function() {
            let reviewId = this.dataset.reviewId;
            changeReviewTag(reviewId);
        });
    });
});
