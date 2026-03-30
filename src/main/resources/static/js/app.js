/* AppVault — app.js (Vanilla JS) */

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
});
