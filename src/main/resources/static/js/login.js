document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const usernameError = document.getElementById('usernameError');
    const passwordError = document.getElementById('passwordError');

    form.addEventListener('submit', (event) => {
        let valid = true;

        // Clear previous error messages
        usernameError.textContent = '';
        passwordError.textContent = '';

        // Validate username
        if (usernameInput.value.trim() === '') {
            usernameError.textContent = '사용자ID를 입력해 주세요.';
            valid = false;
        }

        // Validate password
        if (passwordInput.value.trim() === '') {
            passwordError.textContent = '비밀번호를 입력해 주세요.';
            valid = false;
        }

        if (!valid) {
            event.preventDefault(); // Prevent form submission if validation fails
        }
    });

    // Optional: Real-time validation (as the user types)
    usernameInput.addEventListener('input', () => {
        if (usernameInput.value.trim() === '') {
            usernameError.textContent = '사용자ID를 입력해 주세요.';
        } else {
            usernameError.textContent = '';
        }
    });

    passwordInput.addEventListener('input', () => {
        if (passwordInput.value.trim() === '') {
            passwordError.textContent = '비밀번호를 입력해 주세요.';
        } else {
            passwordError.textContent = '';
        }
    });
});