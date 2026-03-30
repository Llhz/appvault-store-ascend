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

    /* ===== Live Search Auto-suggest ===== */
    var searchInput = document.getElementById('searchInput');
    var searchSuggest = document.getElementById('searchSuggest');
    var searchForm = document.getElementById('searchForm');
    if (searchInput && searchSuggest) {
        var debounceTimer = null;
        var RECENT_KEY = 'appvault_recent_searches';
        var MAX_RECENT = 5;
        var activeIndex = -1;

        function getRecentSearches() {
            try {
                return JSON.parse(localStorage.getItem(RECENT_KEY)) || [];
            } catch (e) { return []; }
        }

        function saveRecentSearch(query) {
            var q = query.trim();
            if (!q) return;
            var recent = getRecentSearches().filter(function (r) { return r !== q; });
            recent.unshift(q);
            if (recent.length > MAX_RECENT) recent = recent.slice(0, MAX_RECENT);
            localStorage.setItem(RECENT_KEY, JSON.stringify(recent));
        }

        function clearRecentSearches() {
            localStorage.removeItem(RECENT_KEY);
            closeSuggest();
        }

        function closeSuggest() {
            searchSuggest.classList.remove('active');
            searchSuggest.innerHTML = '';
            activeIndex = -1;
        }

        function renderSuggestions(items) {
            activeIndex = -1;
            var html = '<div class="search-suggest-header">Suggestions</div>';
            items.forEach(function (item) {
                var iconHtml = item.iconUrl
                    ? '<img src="' + escapeHtml(item.iconUrl) + '" alt=""/>'
                    : '<div class="suggest-icon-placeholder"><i class="fa-solid fa-cube"></i></div>';
                var catHtml = item.categoryName
                    ? '<span class="suggest-category">' + escapeHtml(item.categoryName) + '</span>'
                    : '';
                html += '<div class="search-suggest-item" data-id="' + item.id + '">'
                    + iconHtml
                    + '<span class="suggest-name">' + escapeHtml(item.name) + '</span>'
                    + catHtml
                    + '</div>';
            });
            searchSuggest.innerHTML = html;
            searchSuggest.classList.add('active');
            bindSuggestClicks();
        }

        function renderRecent(recent) {
            activeIndex = -1;
            var html = '<div class="search-suggest-header">Recent Searches</div>';
            recent.forEach(function (q) {
                html += '<div class="search-suggest-item recent-item" data-query="' + escapeHtml(q) + '">'
                    + '<div class="suggest-icon-placeholder"><i class="fa-solid fa-clock-rotate-left"></i></div>'
                    + '<span class="suggest-name">' + escapeHtml(q) + '</span>'
                    + '</div>';
            });
            html += '<button class="search-suggest-clear" type="button">Clear Recent Searches</button>';
            searchSuggest.innerHTML = html;
            searchSuggest.classList.add('active');
            bindSuggestClicks();
            var clearBtn = searchSuggest.querySelector('.search-suggest-clear');
            if (clearBtn) clearBtn.addEventListener('click', function (e) {
                e.preventDefault();
                e.stopPropagation();
                clearRecentSearches();
            });
        }

        function escapeHtml(str) {
            var div = document.createElement('div');
            div.appendChild(document.createTextNode(str || ''));
            return div.innerHTML;
        }

        function bindSuggestClicks() {
            searchSuggest.querySelectorAll('.search-suggest-item').forEach(function (el) {
                el.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    if (this.dataset.id) {
                        window.location = '/app/' + this.dataset.id;
                    } else if (this.dataset.query) {
                        searchInput.value = this.dataset.query;
                        searchForm.submit();
                    }
                });
            });
        }

        function fetchSuggestions(query) {
            var csrfToken = document.querySelector('meta[name="_csrf"]');
            var csrfHeader = document.querySelector('meta[name="_csrf_header"]');
            var headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader.getAttribute('content')] = csrfToken.getAttribute('content');
            }
            fetch('/search/suggest?q=' + encodeURIComponent(query), {
                method: 'GET',
                headers: headers,
                credentials: 'same-origin'
            })
            .then(function (res) { return res.json(); })
            .then(function (data) {
                if (data && data.length > 0) {
                    renderSuggestions(data);
                } else {
                    closeSuggest();
                }
            })
            .catch(function () { closeSuggest(); });
        }

        searchInput.addEventListener('input', function () {
            clearTimeout(debounceTimer);
            var q = this.value.trim();
            if (q.length >= 2) {
                debounceTimer = setTimeout(function () { fetchSuggestions(q); }, 300);
            } else {
                closeSuggest();
            }
        });

        searchInput.addEventListener('focus', function () {
            var q = this.value.trim();
            if (q.length >= 2) {
                fetchSuggestions(q);
            } else {
                var recent = getRecentSearches();
                if (recent.length > 0) renderRecent(recent);
            }
        });

        searchInput.addEventListener('keydown', function (e) {
            if (!searchSuggest.classList.contains('active')) return;
            var items = searchSuggest.querySelectorAll('.search-suggest-item');
            if (e.key === 'Escape') {
                closeSuggest();
                searchInput.blur();
            } else if (e.key === 'ArrowDown') {
                e.preventDefault();
                activeIndex = Math.min(activeIndex + 1, items.length - 1);
                highlightItem(items);
            } else if (e.key === 'ArrowUp') {
                e.preventDefault();
                activeIndex = Math.max(activeIndex - 1, -1);
                highlightItem(items);
            } else if (e.key === 'Enter' && activeIndex >= 0 && items[activeIndex]) {
                e.preventDefault();
                var el = items[activeIndex];
                if (el.dataset.id) {
                    window.location = '/app/' + el.dataset.id;
                } else if (el.dataset.query) {
                    searchInput.value = el.dataset.query;
                    searchForm.submit();
                }
            }
        });

        function highlightItem(items) {
            items.forEach(function (el, i) {
                el.classList.toggle('active', i === activeIndex);
            });
        }

        // Submit → save recent search
        if (searchForm) {
            searchForm.addEventListener('submit', function () {
                var q = searchInput.value.trim();
                if (q) saveRecentSearch(q);
            });
        }

        // Click outside → close
        document.addEventListener('click', function (e) {
            if (!searchSuggest.contains(e.target) && e.target !== searchInput) {
                closeSuggest();
            }
        });
    }
    /* ===== End Live Search Auto-suggest ===== */

    /* ===== Notifications ===== */
    var notifBell = document.getElementById('notificationBell');
    if (notifBell) {
        var notifBadge = document.getElementById('notifBadge');
        var notifList = document.getElementById('notifList');
        var notifEmpty = document.getElementById('notifEmpty');
        var markAllReadBtn = document.getElementById('markAllReadBtn');
        var notifDropdownToggle = document.getElementById('notifDropdownToggle');

        function getCsrfHeaders() {
            var csrfToken = document.querySelector('meta[name="_csrf"]');
            var csrfHeader = document.querySelector('meta[name="_csrf_header"]');
            var headers = {};
            if (csrfToken && csrfHeader) {
                headers[csrfHeader.getAttribute('content')] = csrfToken.getAttribute('content');
            }
            return headers;
        }

        function fetchNotifCount() {
            fetch('/user/notifications/count', { credentials: 'same-origin' })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    if (data.count && data.count > 0) {
                        notifBadge.textContent = data.count > 99 ? '99+' : data.count;
                        notifBadge.classList.remove('d-none');
                    } else {
                        notifBadge.classList.add('d-none');
                    }
                })
                .catch(function () {});
        }

        function renderNotifications(notifications) {
            if (!notifications || notifications.length === 0) {
                notifList.innerHTML = '<div class="text-center text-muted py-3">No notifications</div>';
                return;
            }
            var html = '';
            notifications.forEach(function (n) {
                var readClass = n.read ? 'bg-white' : 'bg-light';
                var dot = n.read ? '' : '<span class="text-primary me-1">&bull;</span>';
                html += '<a href="#" class="list-group-item list-group-item-action px-3 py-2 notif-item ' + readClass + '"'
                    + ' data-notif-id="' + n.id + '" data-link="' + escapeAttr(n.link || '/') + '">'
                    + '<div class="d-flex align-items-start">'
                    + dot
                    + '<small class="text-wrap">' + escapeHtmlNotif(n.message) + '</small>'
                    + '</div>'
                    + '<small class="text-muted">' + formatTimeAgo(n.createdAt) + '</small>'
                    + '</a>';
            });
            notifList.innerHTML = html;
            bindNotifClicks();
        }

        function escapeHtmlNotif(str) {
            var div = document.createElement('div');
            div.appendChild(document.createTextNode(str || ''));
            return div.innerHTML;
        }

        function escapeAttr(str) {
            return (str || '').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
        }

        function formatTimeAgo(dateStr) {
            if (!dateStr) return '';
            var date = new Date(dateStr);
            var now = new Date();
            var diff = Math.floor((now - date) / 1000);
            if (diff < 60) return 'just now';
            if (diff < 3600) return Math.floor(diff / 60) + 'm ago';
            if (diff < 86400) return Math.floor(diff / 3600) + 'h ago';
            return Math.floor(diff / 86400) + 'd ago';
        }

        function bindNotifClicks() {
            notifList.querySelectorAll('.notif-item').forEach(function (el) {
                el.addEventListener('click', function (e) {
                    e.preventDefault();
                    var notifId = this.dataset.notifId;
                    var link = this.dataset.link || '/';
                    var headers = getCsrfHeaders();
                    headers['Content-Type'] = 'application/json';
                    fetch('/user/notifications/' + notifId + '/read', {
                        method: 'POST',
                        headers: headers,
                        credentials: 'same-origin'
                    }).then(function () {
                        window.location.href = link;
                    }).catch(function () {
                        window.location.href = link;
                    });
                });
            });
        }

        // Load notifications when dropdown opens
        notifDropdownToggle.addEventListener('click', function () {
            fetch('/user/notifications', { credentials: 'same-origin' })
                .then(function (res) { return res.json(); })
                .then(function (data) {
                    renderNotifications(data);
                })
                .catch(function () {});
        });

        // Mark all read
        markAllReadBtn.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();
            var headers = getCsrfHeaders();
            headers['Content-Type'] = 'application/json';
            fetch('/user/notifications/read-all', {
                method: 'POST',
                headers: headers,
                credentials: 'same-origin'
            }).then(function () {
                notifBadge.classList.add('d-none');
                notifList.querySelectorAll('.notif-item').forEach(function (el) {
                    el.classList.remove('bg-light');
                    el.classList.add('bg-white');
                    var dot = el.querySelector('.text-primary');
                    if (dot) dot.remove();
                });
            }).catch(function () {});
        });

        // Initial count fetch + poll every 60s
        fetchNotifCount();
        setInterval(fetchNotifCount, 60000);
    }
    /* ===== End Notifications ===== */

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
