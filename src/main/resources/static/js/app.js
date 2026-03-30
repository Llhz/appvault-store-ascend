/* AppVault — app.js (Vanilla JS) */

/* Dark mode — apply early to prevent flash of light theme */
(function () {
    if (localStorage.getItem('darkMode') === 'true') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }
})();

/* Password show/hide toggle */
function togglePassword(inputId, btn) {
    var input = document.getElementById(inputId);
    var icon = btn.querySelector('i');
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.replace('fa-eye', 'fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.replace('fa-eye-slash', 'fa-eye');
    }
}

/* Password strength indicator */
function checkPasswordStrength(password) {
    var fill = document.getElementById('strengthFill');
    var text = document.getElementById('strengthText');
    if (!fill || !text) return;

    var strength = 0;
    if (password.length >= 8) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;

    var pct = (strength / 4) * 100;
    fill.style.width = pct + '%';

    if (strength <= 1) {
        fill.style.background = '#FF3B30';
        text.textContent = 'Weak';
        text.style.color = '#FF3B30';
    } else if (strength <= 2) {
        fill.style.background = '#FF9500';
        text.textContent = 'Fair';
        text.style.color = '#FF9500';
    } else if (strength <= 3) {
        fill.style.background = '#34C759';
        text.textContent = 'Good';
        text.style.color = '#34C759';
    } else {
        fill.style.background = '#007AFF';
        text.textContent = 'Strong';
        text.style.color = '#007AFF';
    }
}

/* Star Rating Selector */
document.addEventListener('DOMContentLoaded', function () {
    /* Dark mode toggle */
    var darkToggle = document.getElementById('darkModeToggle');
    if (darkToggle) {
        var icon = darkToggle.querySelector('i');
        // Sync icon with current state
        if (document.documentElement.getAttribute('data-theme') === 'dark' && icon) {
            icon.classList.replace('fa-moon', 'fa-sun');
        }
        darkToggle.addEventListener('click', function () {
            var isDark = document.documentElement.getAttribute('data-theme') === 'dark';
            if (isDark) {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('darkMode', 'false');
                if (icon) icon.classList.replace('fa-sun', 'fa-moon');
            } else {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('darkMode', 'true');
                if (icon) icon.classList.replace('fa-moon', 'fa-sun');
            }
        });
    }

    var stars = document.querySelectorAll('.star-select');
    var ratingInput = document.getElementById('ratingInput');

    function setRating(val) {
        if (ratingInput) ratingInput.value = val;
        stars.forEach(function (s) {
            var sVal = parseInt(s.getAttribute('data-val'));
            if (sVal <= val) {
                s.classList.remove('fa-regular');
                s.classList.add('fa-solid');
                s.style.color = '#FF9500';
            } else {
                s.classList.remove('fa-solid');
                s.classList.add('fa-regular');
                s.style.color = '#ccc';
            }
        });
    }

    stars.forEach(function (star) {
        star.addEventListener('click', function () {
            setRating(parseInt(this.getAttribute('data-val')));
        });
        star.addEventListener('mouseenter', function () {
            var val = parseInt(this.getAttribute('data-val'));
            stars.forEach(function (s) {
                if (parseInt(s.getAttribute('data-val')) <= val) {
                    s.style.color = '#FF9500';
                } else {
                    s.style.color = '#ccc';
                }
            });
        });
        star.addEventListener('mouseleave', function () {
            var current = ratingInput ? parseInt(ratingInput.value) : 0;
            setRating(current);
        });
    });

    // Default to 5 stars
    if (stars.length > 0) setRating(5);

    /* Helpful Vote (AJAX) */
    document.querySelectorAll('.helpful-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            var reviewId = this.getAttribute('data-review-id');
            var countEl = this.querySelector('.helpful-count');
            var csrfToken = document.querySelector('meta[name="_csrf"]');
            var csrfHeader = document.querySelector('meta[name="_csrf_header"]');

            var headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            if (csrfToken && csrfHeader) {
                headers[csrfHeader.getAttribute('content')] = csrfToken.getAttribute('content');
            }

            var btn = this;
            btn.disabled = true;

            fetch('/review/' + reviewId + '/helpful', {
                method: 'POST',
                headers: headers,
                credentials: 'same-origin'
            })
            .then(function (res) { return res.json(); })
            .then(function (data) {
                if (data.success && countEl) {
                    countEl.textContent = parseInt(countEl.textContent) + 1;
                }
            })
            .catch(function () { btn.disabled = false; });
        });
    });

    /* Admin: Add screenshot row */
    window.addScreenshotRow = function () {
        var container = document.getElementById('screenshotFields');
        if (!container) return;
        var row = document.createElement('div');
        row.className = 'row mb-2 screenshot-row';
        row.innerHTML = '<div class="col-8"><input type="url" class="form-control rounded-3" name="screenshotUrls" placeholder="Screenshot URL"/></div>' +
            '<div class="col-3"><input type="text" class="form-control rounded-3" name="screenshotCaptions" placeholder="Caption (optional)"/></div>' +
            '<div class="col-1 d-flex align-items-center"><button type="button" class="btn btn-outline-danger btn-sm rounded-circle" onclick="removeRow(this)"><i class="fa-solid fa-minus"></i></button></div>';
        container.appendChild(row);
    };

    window.removeRow = function (btn) {
        var row = btn.closest('.screenshot-row');
        if (row) row.remove();
    };

    /* Screenshot Lightbox */
    var lightbox = document.getElementById('screenshotLightbox');
    if (lightbox) {
        var lbImage = lightbox.querySelector('.lightbox-image');
        var lbCaption = lightbox.querySelector('.lightbox-caption');
        var lbPrev = lightbox.querySelector('.lightbox-prev');
        var lbNext = lightbox.querySelector('.lightbox-next');
        var lbClose = lightbox.querySelector('.lightbox-close');
        var screenshotImgs = document.querySelectorAll('.screenshot-img');
        var lbIndex = 0;

        function openLightbox(index) {
            lbIndex = index;
            updateLightboxImage();
            lightbox.style.display = 'flex';
            // Force reflow then add active class for fade-in
            lightbox.offsetHeight;
            lightbox.classList.add('active');
            document.body.style.overflow = 'hidden';
        }

        function closeLightbox() {
            lightbox.classList.remove('active');
            // Wait for transition then hide
            setTimeout(function () {
                lightbox.style.display = 'none';
            }, 250);
            document.body.style.overflow = '';
        }

        function updateLightboxImage() {
            var img = screenshotImgs[lbIndex];
            lbImage.src = img.src;
            lbImage.alt = img.alt;
            lbCaption.textContent = img.alt !== 'Screenshot' ? img.alt : '';
        }

        function showPrev() {
            lbIndex = (lbIndex - 1 + screenshotImgs.length) % screenshotImgs.length;
            updateLightboxImage();
        }

        function showNext() {
            lbIndex = (lbIndex + 1) % screenshotImgs.length;
            updateLightboxImage();
        }

        screenshotImgs.forEach(function (img) {
            img.addEventListener('click', function () {
                openLightbox(parseInt(this.getAttribute('data-index'), 10));
            });
        });

        lbClose.addEventListener('click', closeLightbox);
        lbPrev.addEventListener('click', showPrev);
        lbNext.addEventListener('click', showNext);

        lightbox.addEventListener('click', function (e) {
            if (e.target === lightbox) closeLightbox();
        });

        document.addEventListener('keydown', function (e) {
            if (lightbox.classList.contains('active')) {
                if (e.key === 'Escape') closeLightbox();
                else if (e.key === 'ArrowLeft') showPrev();
                else if (e.key === 'ArrowRight') showNext();
            }
        });
    }

    /* App of the Day — auto-cycling carousel */
    var aotdCarousel = document.getElementById('aotdCarousel');
    if (aotdCarousel) {
        var slides = aotdCarousel.querySelectorAll('.aotd-slide');
        var dots = aotdCarousel.querySelectorAll('.aotd-dot');
        var current = 0;
        var timer;

        function showSlide(index) {
            slides.forEach(function (s) { s.classList.remove('active'); });
            dots.forEach(function (d) { d.classList.remove('active'); });
            current = index;
            slides[current].classList.add('active');
            if (dots[current]) dots[current].classList.add('active');
        }

        function nextSlide() {
            showSlide((current + 1) % slides.length);
        }

        function startTimer() {
            timer = setInterval(nextSlide, 5000);
        }

        dots.forEach(function (dot) {
            dot.addEventListener('click', function () {
                clearInterval(timer);
                showSlide(parseInt(dot.getAttribute('data-index'), 10));
                startTimer();
            });
        });

        if (slides.length > 1) startTimer();
    }
});
