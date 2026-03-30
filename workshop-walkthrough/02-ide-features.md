# Module 02 — IDE Feature Implementation with Copilot Chat

> **Goal**: Implement visually impactful features using **Copilot Chat in VS Code** — prompt engineering (Part A demo), then hands-on full-UI changes (Parts B & C). Every feature produces a **dramatic, browser-visible result**.

> ⏱️ **Duration**: ~45 minutes (10 min demo + 20 min hands-on + 15 min optional)

---

## 🎯 Objectives

- See how vague vs. precise prompts produce dramatically different output (App of the Day demo)
- Implement a Dark Mode toggle that transforms the entire application with one click
- (Optional) Add a screenshot lightbox gallery with keyboard navigation
- Experience Copilot Chat for cross-layer changes: Java backend + Thymeleaf templates + CSS + JavaScript

---

## Part A — App of the Day Hero Banner (Instructor Demo, ~10 min)

> 💡 **Instructor Note**: This is a live demo. Attendees watch — they won't implement this themselves. The point is to show the prompt engineering concept and demonstrate a dramatic homepage transformation.

### Context — What Already Exists

| Component | Status |
|-----------|--------|
| `AppListing` entity with `featured` boolean | ✅ Exists |
| `HomeController` serving `/` with `featuredApps` list | ✅ Exists |
| Hero section in `home.html` | ✅ Exists — generic "Discover Amazing Apps" text |
| App of the Day hero banner | ❌ Missing — no featured spotlight |
| CSS for hero banner card | ❌ Missing |

### The Concept

Transform the generic homepage hero into an "App of the Day" spotlight — a large, eye-catching card showcasing one specific app with its icon, name, description, developer, and a prominent "GET" button. Think Apple's App Store "Today" tab.

### Demo: Vague vs. Precise Prompt

**Vague prompt** (on purpose):

> *"Add app of the day"*

Show the output — Copilot likely produces fragmented or generic code. It doesn't know *where* to put it, *what data* to use, whether it should be a random app or a curated pick, or how it should look visually.

**Precise prompt**:

<details>
<summary>🔑 Precise Prompt — App of the Day</summary>

```
Add an "App of the Day" hero banner to the homepage. Here's the full plan:

1. **HomeController.java**: Pick the first featured app as "App of the Day" — 
   add `model.addAttribute("appOfTheDay", appListingService.findFeatured().get(0))` 
   in the home() method. The findFeatured() method already returns featured apps.

2. **home.html**: Replace the current hero section's left column (the generic 
   "Discover Amazing Apps" text + buttons) with an App of the Day card. Keep the 
   right column featured carousel as-is. The card should show:
   - A small "APP OF THE DAY" label in caps with a date (use Thymeleaf #dates.format)
   - The app's headerImageUrl as a large background image (gradient overlay for text readability)
   - App icon (small, bottom-left corner of the card)
   - App name (large, bold, white text)
   - App subtitle
   - Developer name
   - A "GET" button linking to /app/{id}
   
   The card should be a clickable link to the app detail page. Use rounded corners 
   and a subtle shadow. Height: ~300px.

3. **style.css**: Add styles for the App of the Day card:
   - `.app-of-the-day` — relative position, border-radius: 20px, overflow: hidden, 
     background-size: cover, min-height: 300px
   - Gradient overlay from transparent to rgba(0,0,0,0.7) at bottom for text contrast
   - White text on dark gradient
   - Hover effect: slight scale-up (transform: scale(1.02))
   - The "APP OF THE DAY" label: small, uppercase, letter-spacing, font-weight 600

Make sure the existing featured carousel on the right side is NOT removed — 
only the left column changes.
```

</details>

### Key Differences — Vague vs. Precise

| Technique | Vague | Precise |
|-----------|-------|---------|
| **States the goal** | ❌ "add app of the day" | ✅ "Add an App of the Day hero banner to the homepage" |
| **Describes what exists** | ❌ | ✅ "`findFeatured()` method already returns featured apps" |
| **Covers all layers** | ❌ | ✅ Controller + Template + CSS (numbered list) |
| **References exact files** | ❌ | ✅ `HomeController.java`, `home.html`, `style.css` |
| **Specifies visual design** | ❌ | ✅ "gradient overlay", "300px height", "rounded corners" |
| **Protects existing code** | ❌ | ✅ "existing featured carousel is NOT removed" |

### Expected Result

The homepage transforms from a generic text hero to a visually striking "App of the Day" spotlight card with a background image, gradient overlay, and the featured app's details — similar to Apple App Store's "Today" tab.

### The Three Changes (Reference)

**1. HomeController.java** — Add one line:

```java
@GetMapping("/")
public String home(Model model) {
    List<AppListing> featured = appListingService.findFeatured();
    model.addAttribute("appOfTheDay", featured.isEmpty() ? null : featured.get(0));
    model.addAttribute("featuredApps", featured);
    model.addAttribute("topFreeApps", appListingService.findTopFree(8));
    model.addAttribute("topPaidApps", appListingService.findTopPaid(8));
    model.addAttribute("recentApps", appListingService.findRecent(8));
    model.addAttribute("categories", categoryRepository.findAll());
    return "home";
}
```

**2. home.html** — Replace the left column with App of the Day card:

```html
<div class="col-lg-6">
    <div th:if="${appOfTheDay != null}" class="app-of-the-day"
         th:style="'background-image: url(' + ${appOfTheDay.headerImageUrl != null ? appOfTheDay.headerImageUrl : appOfTheDay.iconUrl} + ')'">
        <a th:href="@{/app/{id}(id=${appOfTheDay.id})}" class="aotd-overlay">
            <div class="aotd-label">APP OF THE DAY</div>
            <div class="aotd-content">
                <img th:src="${appOfTheDay.iconUrl}" class="aotd-icon"/>
                <div>
                    <div class="aotd-name" th:text="${appOfTheDay.name}">App</div>
                    <div class="aotd-subtitle" th:text="${appOfTheDay.subtitle}">Subtitle</div>
                    <div class="aotd-developer" th:text="${appOfTheDay.developer}">Developer</div>
                </div>
                <span class="btn-get btn-get-lg ms-auto">GET</span>
            </div>
        </a>
    </div>
</div>
```

**3. style.css** — New App of the Day styles:

```css
/* --- App of the Day --- */
.app-of-the-day {
    position: relative;
    border-radius: 20px;
    overflow: hidden;
    background-size: cover;
    background-position: center;
    min-height: 300px;
    transition: transform 0.3s ease;
}
.app-of-the-day:hover { transform: scale(1.02); }
.aotd-overlay {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 300px;
    padding: 24px;
    background: linear-gradient(to bottom, rgba(0,0,0,0.1) 0%, rgba(0,0,0,0.75) 100%);
    text-decoration: none;
    color: white;
}
.aotd-label {
    font-size: 0.7rem; font-weight: 600; letter-spacing: 2px;
    text-transform: uppercase; opacity: 0.9;
}
.aotd-content { display: flex; align-items: center; gap: 12px; }
.aotd-icon { width: 48px; height: 48px; border-radius: 12px; }
.aotd-name { font-size: 1.2rem; font-weight: 700; }
.aotd-subtitle { font-size: 0.85rem; opacity: 0.85; }
.aotd-developer { font-size: 0.75rem; opacity: 0.7; }
```

### Key Prompt Engineering Takeaways

| Technique | Example from the precise prompt |
|-----------|---------|
| **State the goal first** | "Add an App of the Day hero banner to the homepage" |
| **Describe what exists** | "`findFeatured()` method already returns featured apps" |
| **Cover all layers** | Numbered list: 1. Controller 2. Template 3. CSS |
| **Reference exact files** | "In `HomeController.java`", "In `home.html`", "In `style.css`" |
| **Specify visual design** | "gradient overlay", "300px", "rounded corners", "hover: scale(1.02)" |
| **Protect existing code** | "existing featured carousel is NOT removed" |

> 💡 **Key takeaway**: A good prompt describes the **full feature** across all layers. It tells Copilot *what exists*, *what's missing*, *which files to change*, and *what it should look like*. The homepage went from generic to visually striking in one prompt.

---

## Part B — Dark Mode Toggle (Hands-on, ~20 min)

Now it's your turn. You'll implement a **dark mode toggle** that transforms the entire application's appearance with a single click. This is the most visually dramatic feature in the workshop — every page changes instantly.

### What You're Building

- A **moon/sun icon toggle button** in the navbar
- A **complete dark theme** using CSS custom properties  
- **Persistent preference** via `localStorage` — survives page reloads
- **Smooth transition** — colors fade between themes

### What Already Exists

| Component | Status |
|-----------|--------|
| Navbar in `fragments/navbar.html` | ✅ Exists — needs toggle button added |
| CSS in `static/css/style.css` | ✅ Exists — uses some CSS variables already |
| JavaScript in `static/js/app.js` | ✅ Exists — we'll add toggle logic |
| Dark theme styles | ❌ Missing |

### Step 2B.1 — Write Your Prompt

Open Copilot Chat and try crafting a prompt. Think about what Copilot needs to know:

- Which files to change
- What the toggle should look like and where it goes  
- How the dark theme should be implemented (CSS variables)
- How persistence works (localStorage)

<details>
<summary>💡 Hint — Need help with the prompt?</summary>

Think about these aspects:
1. **Where** does the toggle go? (navbar, before the user menu) 
2. **How** does the theme switch work? (CSS `[data-theme="dark"]` on `<html>`)
3. **What** colors change? (background, text, cards, navbar, borders, shadows)
4. **How** does it persist? (localStorage + check on page load)
5. **What** files need changing? (navbar.html, style.css, app.js)

</details>

<details>
<summary>🔑 Reference Prompt (use if stuck)</summary>

```
Implement a dark mode toggle for the entire AppVault application. Three files need changes:

1. **fragments/navbar.html**: Add a toggle button in the navbar, right before the 
   user dropdown (authenticated) or login button (anonymous). Use a moon icon 
   (fa-moon) that switches to sun icon (fa-sun) when dark mode is active. 
   The button should be a simple `<button>` with id="darkModeToggle", 
   class="btn btn-link nav-link", no form submission.

2. **static/css/style.css**: Implement dark mode using a `[data-theme="dark"]` 
   attribute selector on the `html` element. Override these with dark equivalents:
   - Body background: #1a1a2e → use a deep navy/dark gray
   - Text color: #e0e0e0 (light gray)
   - Cards and panels (.bg-white): #16213e (dark blue-gray)
   - Navbar: dark background with light text
   - .hero-section: darker gradient
   - Borders: rgba(255,255,255,0.1)
   - Shadows: darker shadows
   - Input fields: dark background, light text, subtle border
   - Links and interactive elements: keep the primary blue (#007AFF) but brighten slightly
   - Category pills: dark background
   - App cards: dark background with subtle borders
   
   Add a smooth transition on `html`: `transition: background-color 0.3s ease, color 0.3s ease`
   
   Also style #darkModeToggle to have no border, font-size 1.2rem.

3. **static/js/app.js**: Add dark mode toggle logic inside the DOMContentLoaded handler:
   - On page load: check localStorage.getItem('darkMode'). If 'true', set 
     document.documentElement.setAttribute('data-theme', 'dark') and update the 
     icon to fa-sun. This must run EARLY to prevent flash of light theme.
   - On toggle click: flip the data-theme attribute, update the icon, save to localStorage.
   - The toggle should work on every page (navbar is shared via fragment).

The result should be: click the moon icon → entire app switches to dark theme instantly. 
Refresh the page → stays in dark mode. Click sun icon → back to light theme.
```

</details>

### Step 2B.2 — Apply and Verify

After Copilot generates the code:

1. **Apply** the changes to all three files
2. **Build** to check for errors:
   ```bash
   mvn clean package -DskipTests
   ```
3. **Run** the application:
   ```bash
   mvn spring-boot:run
   ```
4. **Open** http://localhost:8080 in your browser
5. **Click** the moon icon in the navbar — the entire page should transform to dark theme
6. **Navigate** to different pages — dark mode should persist
7. **Refresh** the page — dark mode should still be active
8. **Click** the sun icon — back to light theme

### What to Look For

| Check | Expected |
|-------|----------|
| Toggle button visible in navbar | Moon icon next to user menu |
| Click toggle → dark theme | Background turns dark, text becomes light, cards darken |
| Navigate to `/browse` | Still in dark mode |
| Navigate to `/app/1` | Still in dark mode |
| Refresh page | Still in dark mode (localStorage) |
| Click again → light theme | Returns to original appearance |
| Smooth transition | Colors fade (0.3s), no jarring flash |

### Common Issues & Fixes

| Issue | Fix |
|-------|-----|
| Flash of light theme on page load | Move the localStorage check to `<head>` or top of `app.js`, before DOM renders |
| Some elements not darkening | Add more `[data-theme="dark"]` selectors for `.bg-white`, `.shadow-sm`, etc. |
| Navbar text invisible in dark mode | Ensure navbar text color overrides are included |
| Icon not switching | Check the classList toggle between `fa-moon` and `fa-sun` |

> 💡 If Copilot's first attempt misses some elements (e.g., the admin dashboard), ask a follow-up: *"Some elements still have white backgrounds in dark mode. Add dark mode overrides for the admin dashboard, form inputs, and table elements."*

---

## Part C — Screenshot Lightbox Gallery (Optional, ~15 min)

> If time permits. This feature adds interactivity to the app detail page without touching backend code — pure frontend.

### What You're Building

- Click any app screenshot → **fullscreen overlay modal** with the image
- **Previous/Next navigation** arrows
- **Keyboard support** (arrow keys, Escape to close)
- **Click outside** to close
- Smooth **fade-in animation**

### What Already Exists

| Component | Status |
|-----------|--------|
| Screenshots section in `app/detail.html` | ✅ Exists — horizontal scroll of images |
| Screenshot images with `screenshot-img` class | ✅ Exists |
| Lightbox overlay | ❌ Missing |
| JavaScript for lightbox | ❌ Missing |

### Write Your Prompt

<details>
<summary>🔑 Reference Prompt</summary>

```
Add a screenshot lightbox gallery to the app detail page. Two files need changes:

1. **app/detail.html**: After the screenshots section, add a lightbox overlay div:
   - Full-screen fixed overlay with dark semi-transparent background (rgba(0,0,0,0.9))
   - Centered image (max-width: 90vw, max-height: 90vh, object-fit: contain)
   - Left/right arrow buttons for navigation (<i class="fa-solid fa-chevron-left/right">)  
   - Close button (X) in top-right corner
   - Caption text below the image (from the screenshot's caption/alt text)
   - Initially hidden (display: none), shown when a screenshot is clicked
   - Add data-index attribute to each .screenshot-img to track position

2. **static/js/app.js**: Add lightbox logic in DOMContentLoaded:
   - Click handler on .screenshot-img → opens the lightbox, shows that image
   - Track current index, total images array
   - Previous/Next buttons update the displayed image
   - Keyboard: ArrowLeft = prev, ArrowRight = next, Escape = close
   - Click on the overlay background (not the image) = close
   - Fade-in animation: add 'active' class with CSS transition (opacity 0→1)
   - Prevent scrolling when lightbox is open (body overflow: hidden)

The lightbox should match the Apple App Store screenshot viewer aesthetic — 
minimal, dark background, large centered image, subtle controls.
```

</details>

### Apply and Verify

1. Apply the changes
2. Restart the app: `mvn spring-boot:run`
3. Navigate to any app detail page (e.g., http://localhost:8080/app/1)
4. Click a screenshot → lightbox should open
5. Use arrow keys to navigate between screenshots
6. Press Escape or click outside to close

---

## Files Created / Modified (Session 1)

### Part A — App of the Day

```
Modified:
├── src/main/java/com/appvault/controller/HomeController.java    ← appOfTheDay attribute
├── src/main/resources/templates/home.html                        ← hero banner card
└── src/main/resources/static/css/style.css                       ← .app-of-the-day styles
```

### Part B — Dark Mode

```
Modified:
├── src/main/resources/templates/fragments/navbar.html            ← toggle button
├── src/main/resources/static/css/style.css                       ← [data-theme="dark"] rules
└── src/main/resources/static/js/app.js                           ← toggle logic + persistence
```

### Part C — Screenshot Lightbox (Optional)

```
Modified:
├── src/main/resources/templates/app/detail.html                  ← lightbox overlay HTML
└── src/main/resources/static/js/app.js                           ← lightbox JS
```

---

## Reflection — What Did We Learn?

| Lesson | Detail |
|--------|--------|
| **Precise prompts = complete features** | The App of the Day prompt produced a 3-file change in one interaction |
| **Visual features are more satisfying** | Dark mode gives an immediate "wow" — everyone can see the result |
| **Copilot excels at CSS generation** | Complex dark theme rules with proper selectors and transitions |
| **Cross-file consistency matters** | The prompt named exact files and described how they connect |
| **Iterative refinement works** | First pass might miss some elements — follow-up prompts fix edge cases |

### Prompt Engineering Tips Learned

| Technique | Example |
|-----------|---------|
| **Name exact files** | "In `fragments/navbar.html`", "In `style.css`" |
| **Describe visual outcomes** | "gradient overlay", "smooth transition", "dark navy background" |
| **Reference existing patterns** | "same hero section", "existing CSS variables" |
| **Specify persistence behavior** | "localStorage", "survives page reload", "no flash" |
| **Protect existing functionality** | "do NOT remove the featured carousel" |

---

## Checkpoint ✅

- [ ] **App of the Day** — homepage shows a hero spotlight card (instructor demo)
- [ ] **Dark Mode** — moon icon toggles entire app, persists across pages/reloads
- [ ] (Optional) **Lightbox** — click screenshot → fullscreen overlay with navigation
- [ ] `mvn test` → all existing tests pass
- [ ] App runs without errors at http://localhost:8080
- [ ] You can articulate the difference between vague and precise prompts

---

👉 Continue to **[Module 03 — Copilot CLI](03-cli-features.md)** to build data-rich features with the terminal agent's plan and autopilot modes.
