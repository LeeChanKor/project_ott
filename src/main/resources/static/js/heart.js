document.addEventListener('DOMContentLoaded', function() {
    const heartButton = document.getElementById('heart-image');
    const dramaNo = document.getElementById('dramaNo') ? document.getElementById('dramaNo').value : null;
    const movieNo = document.getElementById('movieNo') ? document.getElementById('movieNo').value : null;
    const varietyNo = document.getElementById('varietyNo') ? document.getElementById('varietyNo').value : null;
    const username = document.getElementById('username').value;

    if (heartButton && username) {
        let checkUrl = '';
        let type = '';

        // 타입과 URL 결정
        if (dramaNo) {
            type = 'drama';
            checkUrl = `/api/${dramaNo}/is-hearted?type=${type}&username=${encodeURIComponent(username)}`;
        } else if (movieNo) {
            type = 'movie';
            checkUrl = `/api/${movieNo}/is-hearted?type=${type}&username=${encodeURIComponent(username)}`;
        } else if (varietyNo) {
            type = 'variety';
            checkUrl = `/api/${varietyNo}/is-hearted?type=${type}&username=${encodeURIComponent(username)}`;
        }

        if (checkUrl) {
            // 찜 상태 확인 API 요청
            fetch(checkUrl)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // 찜 상태에 따라 하트 이미지 업데이트
                        const heartImage = heartButton.querySelector('img');
                        heartImage.src = data.isHearted ? '/img/빨간 하트.png' : '/img/svg_heart-removebg-preview.png';

                        // 찜 등록 시간이 있으면 출력
                        if (data.isHearted && data.favoriteAt) {
                            const favoriteAt = new Date(data.favoriteAt);
                            console.log(`찜 등록 시간: ${favoriteAt.toLocaleString()}`);
                        }
                    } else {
                        console.error('Error checking heart status:', data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                });
        }
    }

    heartButton.addEventListener('click', function() {
        let toggleUrl = '';
        let type = '';

        // 타입과 URL 결정
        if (dramaNo) {
            type = 'drama';
            toggleUrl = `/api/${dramaNo}/toggle-heart?type=${type}`;
        } else if (movieNo) {
            type = 'movie';
            toggleUrl = `/api/${movieNo}/toggle-heart?type=${type}`;
        } else if (varietyNo) {
            type = 'variety';
            toggleUrl = `/api/${varietyNo}/toggle-heart?type=${type}`;
        }

        if (toggleUrl) {
            // 찜 상태 토글 API 요청
            fetch(toggleUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username: username })
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message);
                    // 버튼의 하트 이미지를 업데이트
                    const heartImage = heartButton.querySelector('img');
                    heartImage.src = data.isHearted ? '/img/빨간 하트.png' : '/img/svg_heart-removebg-preview.png';

                    // 찜 등록 시간이 있으면 출력
                    if (data.isHearted && data.favoriteAt) {
                        const favoriteAt = new Date(data.favoriteAt);
                        console.log(`찜 등록 시간: ${favoriteAt.toLocaleString()}`);
                    }
                } else {
                    alert('오류가 발생했습니다: ' + data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('오류가 발생했습니다: Failed to fetch heart status');
            });
        }
    });
});
