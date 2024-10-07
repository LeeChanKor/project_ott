document.addEventListener('DOMContentLoaded', function () {
    const slides = document.querySelector('.drama-slide-list');
    const slideItems = slides.querySelectorAll('.slide-item');
    const totalSlides = slideItems.length;
    const slidesPerPage = 7;
    let currentPage = 0;

    // 드라마 슬라이드 리스트의 너비 설정
    const slideItemWidth = 245; // 각 슬라이드 항목의 너비 (예: 245px)
    slides.style.width = `${slideItemWidth * totalSlides}px`;

    function updateSlidePosition() {
        const newPosition = -currentPage * (100 / slidesPerPage) * slidesPerPage;
        slides.style.transform = `translateX(${newPosition}%)`;
    }

    function prevSlide() {
        if (currentPage > 0) {
            currentPage--;
            updateSlidePosition();
        }
    }

    function nextSlide() {
        if ((currentPage + 1) * slidesPerPage < totalSlides) {
            currentPage++;
            updateSlidePosition();
        }
    }

    document.getElementById('prevBtn').addEventListener('click', prevSlide);
    document.getElementById('nextBtn').addEventListener('click', nextSlide);

    // 초기 슬라이드 위치 설정
    updateSlidePosition();
});


document.addEventListener('DOMContentLoaded', function () {
    const slides = document.querySelector('.movie-slide-list');
    const slideItems = slides.querySelectorAll('.slide-item');
    const totalSlides = slideItems.length;
    const slidesPerPage = 7;
    let currentPage = 0;

    // 영화 슬라이드 리스트의 너비 설정
    const slideItemWidth = 245; // 각 슬라이드 항목의 너비 (예: 245px)
    slides.style.width = `${slideItemWidth * totalSlides}px`;

    function updateSlidePosition() {
        const newPosition = -currentPage * (100 / slidesPerPage) * slidesPerPage;
        slides.style.transform = `translateX(${newPosition}%)`;
    }

    function prevSlide() {
        if (currentPage > 0) {
            currentPage--;
            updateSlidePosition();
        }
    }

    function nextSlide() {
        if ((currentPage + 1) * slidesPerPage < totalSlides) {
            currentPage++;
            updateSlidePosition();
        }
    }


    document.getElementById('prevBtn2').addEventListener('click', prevSlide);
    document.getElementById('nextBtn2').addEventListener('click', nextSlide);

    // 초기 슬라이드 위치 설정
    updateSlidePosition();
});



document.addEventListener('DOMContentLoaded', function () {
    const slides = document.querySelector('.variety-slide-list');
    const slideItems = slides.querySelectorAll('.variety_slide-item');
    const totalSlides = slideItems.length;
    const slidesPerPage = 7;
    let currentPage = 0;

    // 버라이어티 슬라이드 리스트의 너비 설정
    const slideItemWidth = 245; // 각 슬라이드 항목의 너비 (예: 245px)
    slides.style.width = `${slideItemWidth * totalSlides}px`;

    function updateSlidePosition() {
        const newPosition = -currentPage * (100 / slidesPerPage) * slidesPerPage;
        slides.style.transform = `translateX(${newPosition}%)`;
    }

    function prevSlide() {
        if (currentPage > 0) {
            currentPage--;
            updateSlidePosition();
        }
    }

    function nextSlide() {
        if ((currentPage + 1) * slidesPerPage < totalSlides) {
            currentPage++;
            updateSlidePosition();
        }
    }

    document.getElementById('prevBtn3').addEventListener('click', prevSlide);
    document.getElementById('nextBtn3').addEventListener('click', nextSlide);

    // 초기 슬라이드 위치 설정
    updateSlidePosition();
});