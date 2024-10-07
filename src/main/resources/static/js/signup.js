 document.getElementById('signupForm').addEventListener('submit', function(event) {
            // 유효성 검사 결과 초기화
            let valid = true;

            // 입력 필드와 오류 메시지 요소 가져오기
            const username = document.getElementById('username').value;
            const password1 = document.getElementById('password1').value;
            const password2 = document.getElementById('password2').value;
            const email = document.getElementById('email').value;

            const usernameError = document.getElementById('usernameError');
            const password1Error = document.getElementById('password1Error');
            const password2Error = document.getElementById('password2Error');
            const emailError = document.getElementById('emailError');

            // 사용자ID 유효성 검사
            if (username.trim() === '') {
                usernameError.textContent = '사용자ID를 입력하세요.';
                valid = false;
            } else {
                usernameError.textContent = '';
            }

            // 비밀번호 유효성 검사
            if (password1.length < 3) {
                password1Error.textContent = '비밀번호는 최소 3자 이상이어야 합니다.';
                valid = false;
            } else {
                password1Error.textContent = '';
            }

            // 비밀번호 확인 유효성 검사
            if (password1 !== password2) {
                password2Error.textContent = '비밀번호가 일치하지 않습니다.';
                valid = false;
            } else {
                password2Error.textContent = '';
            }

            // 이메일 유효성 검사
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(email)) {
                emailError.textContent = '유효한 이메일 주소를 입력하세요.';
                valid = false;
            } else {
                emailError.textContent = '';
            }

            // 폼 제출 막기
            if (!valid) {
                event.preventDefault();
            }
        });