const arrows = document.querySelector(".arrows");
const slides = document.querySelector(".slide-list");
const paginationElem = document.querySelector(".pagination");

const slideLength = slides.children.length; // 슬라이드 개수
const addClass = (element, className) => element.classList.add(className);
const removeClass = (element, className) => element.classList.remove(className);

let x = 100; // 처음 위치를 첫 슬라이드로 설정
let currentIndex = 1; // 현재 슬라이드 인덱스
let prevPagination;

// 첫 번째 슬라이드와 마지막 슬라이드를 복제하여 양 끝에 추가
const firstSlideClone = slides.children[0].cloneNode(true);
const lastSlideClone = slides.children[slideLength - 1].cloneNode(true);

slides.appendChild(firstSlideClone);
slides.insertBefore(lastSlideClone, slides.children[0]);

slides.style.transform = `translateX(-${x}%)`;

function rightClickHandler() {
    if (currentIndex >= slideLength) {
        slides.style.transition = 'none';
        x = 100;
        currentIndex = 1;
        slides.style.transform = `translateX(-${x}%)`;
        setTimeout(() => {
            slides.style.transition = '0.5s';
            x += 100;
            currentIndex++;
            updatePagination();
            moveSlide();
        }, 50);
    } else {
        x += 100;
        currentIndex++;
        updatePagination();
        moveSlide();
    }
}

function leftClickHandler() {
    if (currentIndex <= 1) {
        slides.style.transition = 'none';
        x = slideLength * 100;
        currentIndex = slideLength;
        slides.style.transform = `translateX(-${x}%)`;
        setTimeout(() => {
            slides.style.transition = '0.5s';
            x -= 100;
            currentIndex--;
            updatePagination();
            moveSlide();
        }, 50);
    } else {
        x -= 100;
        currentIndex--;
        updatePagination();
        moveSlide();
    }
}

function updatePagination() {
    const index = (currentIndex - 1) % slideLength;
    const newPagination = document.querySelectorAll(".pagination li")[index];

    if (prevPagination) {
        removeClass(prevPagination, "active");
    }

    addClass(newPagination, "active");
    prevPagination = newPagination;
}

const moveSlide = () => slides.style.transform = `translateX(-${x}%)`;

function pagination() {
    for (let i = 0; i < slideLength; i++) {
        const createLI = document.createElement("li");
        createLI.setAttribute("class", `pagination-${i}`);
        paginationElem.insertAdjacentElement("beforeend", createLI);
    }
    const firstPagination = document.querySelector(".pagination li");

    prevPagination = firstPagination;

    addClass(firstPagination, "active");
}

function paginationHandler(event) {
    if (event.target.nodeName === "UL") return;
    if (prevPagination) removeClass(prevPagination, "active");

    const index = parseInt(event.target.getAttribute("class").slice(11)) + 1;

    x = index * 100;
    currentIndex = index;

    prevPagination = event.target;

    addClass(event.target, "active");
    moveSlide();
}

function slideHandler(event) {
    if (event.target.classList.contains("left-btn")) {
        leftClickHandler();
    } else {
        rightClickHandler();
    }
}

// 자동 슬라이드 기능 추가
let autoSlide = setInterval(rightClickHandler, 3000);

function resetAutoSlide() {
    clearInterval(autoSlide);
    autoSlide = setInterval(rightClickHandler, 3000);
}

arrows.addEventListener("click", (event) => {
    slideHandler(event);
    resetAutoSlide();
});

paginationElem.addEventListener("click", (event) => {
    paginationHandler(event);
    resetAutoSlide();
});

pagination();

// JavaScript 코드
document.addEventListener('DOMContentLoaded', function() {
    const findButton = document.querySelector('.find-button');
    const myButton = document.querySelector('.my-button');
    const loginMenu = document.querySelector('.login-menu');
    const searchForm = document.getElementById('searchForm'); // 검색 폼 추가

    findButton.addEventListener('click', function(event) {
        // 기본 이벤트를 막지 않도록 변경
        // event.preventDefault(); // 이 줄을 주석 처리 또는 제거하세요

        // 검색 폼을 제출
        searchForm.submit();
    });

    myButton.addEventListener('click', function() {
        loginMenu.style.display = loginMenu.style.display === 'block' ? 'none' : 'block';
    });

    // 다른 곳을 클릭했을 때 로그인 메뉴가 닫히도록 합니다.
    document.addEventListener('click', function(event) {
        const target = event.target;
        if (!target.closest('.search')) {
            loginMenu.style.display = 'none';
        }
    });
});

// 위로 가기 버튼
document.addEventListener("DOMContentLoaded", function() {
    var backToTopButton = document.getElementById("backToTop");

    window.addEventListener("scroll", function() {
        if (window.scrollY > 200) {
            backToTopButton.style.display = "block";
        } else {
            backToTopButton.style.display = "none";
        }
    });

    backToTopButton.addEventListener("click", function() {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    });
});

