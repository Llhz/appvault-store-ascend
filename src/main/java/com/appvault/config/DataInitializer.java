package com.appvault.config;

import com.appvault.model.*;
import com.appvault.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AppListingRepository appListingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.count() > 0) return;

        // Roles
        Role adminRole = createRole("ROLE_ADMIN");
        Role userRole = createRole("ROLE_USER");

        // Users
        User admin = createUser("Admin", "User", "admin@appvault.com", "Admin123!", true, adminRole, userRole);
        User demo = createUser("Demo", "User", "user@appvault.com", "User123!", true, userRole);
        User alice = createUser("Alice", "Smith", "alice@example.com", "Alice123!", true, userRole);
        User bob = createUser("Bob", "Jones", "bob@example.com", "Bob12345!", true, userRole);
        User carol = createUser("Carol", "Williams", "carol@example.com", "Carol123!", true, userRole);

        // Categories
        Category productivity = createCategory("Productivity", "fa-briefcase", "Apps to help you get things done");
        Category games = createCategory("Games", "fa-gamepad", "Fun and entertainment for all ages");
        Category education = createCategory("Education", "fa-graduation-cap", "Learn something new every day");
        Category entertainment = createCategory("Entertainment", "fa-film", "Movies, shows, and more");
        Category utilities = createCategory("Utilities", "fa-wrench", "Essential tools and utilities");
        Category social = createCategory("Social", "fa-users", "Connect with friends and family");
        Category photoVideo = createCategory("Photo & Video", "fa-camera", "Capture and edit your memories");
        Category music = createCategory("Music", "fa-music", "Music streaming and creation");
        Category finance = createCategory("Finance", "fa-chart-line", "Manage your money and investments");
        Category health = createCategory("Health & Fitness", "fa-heartbeat", "Stay healthy and active");

        // Apps
        AppListing focusFlow = createApp("FocusFlow", "Smart Task Manager",
            "FocusFlow is the ultimate productivity app that helps you stay organized and focused. With intelligent task prioritization and beautiful UI, you'll never miss a deadline again.\n\nFeatures:\n• Smart task prioritization with AI\n• Pomodoro timer integration\n• Team collaboration tools\n• Calendar sync\n• Daily goals and streaks",
            "ProductiveCo", "3.2.1", "28.4 MB",
            "https://placehold.co/200x200/007AFF/white?text=FF",
            "https://placehold.co/1200x400/007AFF/white?text=FocusFlow",
            BigDecimal.ZERO, 4.7, 1240, 85000L, true, productivity,
            "iOS 14+, macOS 11+", "4+",
            new String[]{"https://placehold.co/390x844/f5f5f7/333?text=Screenshot+1",
                         "https://placehold.co/390x844/f5f5f7/333?text=Screenshot+2",
                         "https://placehold.co/390x844/f5f5f7/333?text=Screenshot+3"});

        AppListing pixelCraft = createApp("PixelCraft", "Pro Photo Editor",
            "PixelCraft brings professional-grade photo editing to your fingertips. With hundreds of filters, advanced retouching tools, and one-tap enhancements, your photos will always look their best.\n\nFeatures:\n• 300+ premium filters\n• Advanced healing brush\n• Selective color adjustment\n• RAW file support\n• Cloud backup",
            "CreativeApps Inc", "5.1.0", "156.2 MB",
            "https://placehold.co/200x200/FF6B35/white?text=PC",
            "https://placehold.co/1200x400/FF6B35/white?text=PixelCraft",
            new BigDecimal("4.99"), 4.8, 3420, 220000L, true, photoVideo,
            "iOS 14+", "4+",
            new String[]{"https://placehold.co/390x844/f0f0f0/333?text=Edit+Screen",
                         "https://placehold.co/390x844/f0f0f0/333?text=Filters",
                         "https://placehold.co/390x844/f0f0f0/333?text=Export"});

        AppListing beatMaker = createApp("BeatMaker Pro", "Create Music Anywhere",
            "BeatMaker Pro is a powerful music production studio in your pocket. Create professional beats, compose melodies, and mix tracks with studio-quality tools.\n\nFeatures:\n• 808 drum machine\n• Virtual piano and guitar\n• Multi-track mixer\n• Sample library with 10,000+ sounds\n• Export to MP3/WAV",
            "AudioLab Studios", "4.0.3", "312.8 MB",
            "https://placehold.co/200x200/9B59B6/white?text=BM",
            "https://placehold.co/1200x400/9B59B6/white?text=BeatMaker+Pro",
            new BigDecimal("9.99"), 4.6, 890, 45000L, true, music,
            "iOS 14+, macOS 11+", "4+",
            new String[]{"https://placehold.co/390x844/2C2C2E/white?text=Drum+Machine",
                         "https://placehold.co/390x844/2C2C2E/white?text=Mixer",
                         "https://placehold.co/390x844/2C2C2E/white?text=Keyboard"});

        AppListing learnCode = createApp("CodeAcademy Go", "Learn Programming Fast",
            "CodeAcademy Go makes learning programming fun and accessible. With bite-sized lessons, interactive coding challenges, and project-based learning, you'll be coding in no time.\n\nFeatures:\n• 20+ programming languages\n• Interactive code editor\n• Real-world projects\n• Certificates on completion\n• Offline mode",
            "EduTech Solutions", "2.8.1", "67.3 MB",
            "https://placehold.co/200x200/27AE60/white?text=CG",
            "https://placehold.co/1200x400/27AE60/white?text=CodeAcademy+Go",
            BigDecimal.ZERO, 4.5, 2100, 125000L, true, education,
            "iOS 13+", "4+",
            new String[]{"https://placehold.co/390x844/1C1C1E/white?text=Lesson",
                         "https://placehold.co/390x844/1C1C1E/white?text=Code+Editor",
                         "https://placehold.co/390x844/1C1C1E/white?text=Projects"});

        AppListing galaxyRun = createApp("Galaxy Run", "Endless Space Adventure",
            "Race through the cosmos in this visually stunning endless runner. Dodge asteroids, collect power-ups, and compete against players worldwide in real-time leaderboards.\n\nFeatures:\n• Stunning 3D graphics\n• Daily challenges\n• 50+ unique power-ups\n• Global leaderboards\n• Offline play supported",
            "StarGames Studio", "1.9.4", "245.6 MB",
            "https://placehold.co/200x200/1A1A2E/white?text=GR",
            "https://placehold.co/1200x400/1A1A2E/white?text=Galaxy+Run",
            BigDecimal.ZERO, 4.4, 5600, 890000L, true, games,
            "iOS 13+", "4+",
            new String[]{"https://placehold.co/390x844/0F3460/white?text=Gameplay",
                         "https://placehold.co/390x844/0F3460/white?text=Power-ups",
                         "https://placehold.co/390x844/0F3460/white?text=Leaderboard"});

        AppListing moneyWise = createApp("MoneyWise", "Smart Budget Tracker",
            "Take control of your finances with MoneyWise. Track spending, set budgets, and achieve your financial goals with intelligent insights and beautiful charts.\n\nFeatures:\n• Automatic transaction categorization\n• Budget goals with alerts\n• Investment portfolio tracking\n• Bill reminders\n• Bank sync (256-bit encryption)",
            "FinTech Labs", "6.3.2", "44.1 MB",
            "https://placehold.co/200x200/2ECC71/white?text=MW",
            "https://placehold.co/1200x400/2ECC71/white?text=MoneyWise",
            new BigDecimal("2.99"), 4.7, 1870, 95000L, false, finance,
            "iOS 14+", "4+",
            new String[]{"https://placehold.co/390x844/F0FFF4/333?text=Dashboard",
                         "https://placehold.co/390x844/F0FFF4/333?text=Budgets",
                         "https://placehold.co/390x844/F0FFF4/333?text=Charts"});

        AppListing fitTrack = createApp("FitTrack Pro", "Your Personal Fitness Coach",
            "FitTrack Pro is your all-in-one fitness companion. Get personalized workout plans, track your nutrition, and monitor your health metrics all in one beautiful app.\n\nFeatures:\n• 500+ guided workouts\n• Calorie and macro tracking\n• Heart rate monitoring\n• Sleep analysis\n• Apple Health integration",
            "HealthFirst Apps", "8.1.0", "89.7 MB",
            "https://placehold.co/200x200/E74C3C/white?text=FT",
            "https://placehold.co/1200x400/E74C3C/white?text=FitTrack+Pro",
            new BigDecimal("3.99"), 4.6, 4320, 310000L, false, health,
            "iOS 14+, watchOS 7+", "4+",
            new String[]{"https://placehold.co/390x844/FFF0F0/333?text=Workouts",
                         "https://placehold.co/390x844/FFF0F0/333?text=Nutrition",
                         "https://placehold.co/390x844/FFF0F0/333?text=Sleep"});

        AppListing socialCircle = createApp("SocialCircle", "Connect & Share",
            "SocialCircle helps you stay connected with the people who matter most. Share moments, create group chats, and discover local events in your community.\n\nFeatures:\n• Private group spaces\n• End-to-end encrypted messages\n• Event planning tools\n• Photo albums shared privately\n• Location-based discovery",
            "ConnectApp Inc", "4.5.1", "78.3 MB",
            "https://placehold.co/200x200/3498DB/white?text=SC",
            "https://placehold.co/1200x400/3498DB/white?text=SocialCircle",
            BigDecimal.ZERO, 3.8, 2230, 450000L, false, social,
            "iOS 13+", "12+",
            new String[]{"https://placehold.co/390x844/EBF5FB/333?text=Feed",
                         "https://placehold.co/390x844/EBF5FB/333?text=Messages",
                         "https://placehold.co/390x844/EBF5FB/333?text=Events"});

        AppListing streamFlix = createApp("StreamFlix", "Movies & TV on Demand",
            "StreamFlix gives you access to thousands of movies and TV shows on demand. With 4K streaming, offline downloads, and personalized recommendations, your entertainment is always ready.\n\nFeatures:\n• 10,000+ titles\n• 4K HDR streaming\n• Download for offline viewing\n• Family profiles (up to 6)\n• No ads",
            "EntertainCo", "3.7.2", "98.5 MB",
            "https://placehold.co/200x200/C0392B/white?text=SF",
            "https://placehold.co/1200x400/C0392B/white?text=StreamFlix",
            new BigDecimal("7.99"), 4.3, 8750, 1200000L, false, entertainment,
            "iOS 13+, tvOS 13+", "12+",
            new String[]{"https://placehold.co/390x844/1C1C1E/white?text=Browse",
                         "https://placehold.co/390x844/1C1C1E/white?text=Player",
                         "https://placehold.co/390x844/1C1C1E/white?text=Downloads"});

        AppListing cleanDisk = createApp("CleanDisk Pro", "Storage Optimizer",
            "CleanDisk Pro helps you reclaim storage space and keep your device running fast. Smart scanning identifies junk files, duplicates, and unnecessary data with one tap.\n\nFeatures:\n• Smart junk file detection\n• Duplicate photo finder\n• Large file browser\n• App cache cleaner\n• Memory optimizer",
            "UtilityApps Dev", "2.4.0", "15.2 MB",
            "https://placehold.co/200x200/95A5A6/white?text=CD",
            "https://placehold.co/1200x400/95A5A6/white?text=CleanDisk+Pro",
            new BigDecimal("1.99"), 4.2, 960, 55000L, false, utilities,
            "iOS 14+, macOS 12+", "4+",
            new String[]{"https://placehold.co/390x844/F8F9FA/333?text=Scanner",
                         "https://placehold.co/390x844/F8F9FA/333?text=Results",
                         "https://placehold.co/390x844/F8F9FA/333?text=After"});

        AppListing linguaLearn = createApp("LinguaLearn", "Learn Any Language",
            "LinguaLearn makes language learning addictive and effective. Using spaced repetition and gamification, you'll build vocabulary, grammar, and speaking skills faster than any other method.\n\nFeatures:\n• 40+ languages\n• Daily streak system\n• Speech recognition\n• Native speaker recordings\n• Placement test",
            "PolyglotApps", "7.2.3", "52.8 MB",
            "https://placehold.co/200x200/F39C12/white?text=LL",
            "https://placehold.co/1200x400/F39C12/white?text=LinguaLearn",
            BigDecimal.ZERO, 4.8, 6540, 780000L, false, education,
            "iOS 13+", "4+",
            new String[]{"https://placehold.co/390x844/FEF9E7/333?text=Lessons",
                         "https://placehold.co/390x844/FEF9E7/333?text=Practice",
                         "https://placehold.co/390x844/FEF9E7/333?text=Progress"});

        AppListing puzzleKing = createApp("Puzzle King", "Classic Brain Teasers",
            "Puzzle King features hundreds of handcrafted puzzles across multiple categories. Challenge your mind with sudoku, crosswords, word search, and logic puzzles.\n\nFeatures:\n• 5 puzzle types\n• 10,000+ puzzles\n• Daily puzzle challenges\n• Hint system\n• No internet required",
            "BrainGames Co", "5.0.1", "34.6 MB",
            "https://placehold.co/200x200/8E44AD/white?text=PK",
            "https://placehold.co/1200x400/8E44AD/white?text=Puzzle+King",
            new BigDecimal("0.99"), 4.5, 3210, 190000L, false, games,
            "iOS 12+", "4+",
            new String[]{"https://placehold.co/390x844/F5EEF8/333?text=Sudoku",
                         "https://placehold.co/390x844/F5EEF8/333?text=Crossword",
                         "https://placehold.co/390x844/F5EEF8/333?text=Word+Search"});

        AppListing noteNinja = createApp("NoteNinja", "Smart Notes & Organizer",
            "NoteNinja is the most powerful and elegant note-taking app available. With markdown support, rich media notes, and smart organization, your ideas are always at your fingertips.\n\nFeatures:\n• Markdown and rich text\n• Embed images, audio, and files\n• Smart folders and tags\n• Full-text search\n• iCloud sync",
            "NinjaApps Inc", "4.1.0", "31.0 MB",
            "https://placehold.co/200x200/16A085/white?text=NN",
            "https://placehold.co/1200x400/16A085/white?text=NoteNinja",
            BigDecimal.ZERO, 4.6, 4100, 320000L, false, productivity,
            "iOS 14+, macOS 11+, watchOS 7+", "4+",
            new String[]{"https://placehold.co/390x844/E8F8F5/333?text=Notes+List",
                         "https://placehold.co/390x844/E8F8F5/333?text=Note+Editor",
                         "https://placehold.co/390x844/E8F8F5/333?text=Search"});

        AppListing weatherPro = createApp("WeatherPro", "Hyperlocal Forecasts",
            "WeatherPro delivers the most accurate hyperlocal weather forecasts with stunning visualizations. Know what to expect hour by hour with radar maps and severe weather alerts.\n\nFeatures:\n• Hour-by-hour forecasts\n• Animated radar maps\n• Severe weather alerts\n• Air quality index\n• 10-day forecast",
            "MeteoTech", "9.3.0", "42.7 MB",
            "https://placehold.co/200x200/2980B9/white?text=WP",
            "https://placehold.co/1200x400/2980B9/white?text=WeatherPro",
            new BigDecimal("1.99"), 4.4, 2870, 180000L, false, utilities,
            "iOS 14+", "4+",
            new String[]{"https://placehold.co/390x844/EBF5FB/333?text=Today",
                         "https://placehold.co/390x844/EBF5FB/333?text=Radar",
                         "https://placehold.co/390x844/EBF5FB/333?text=Weekly"});

        AppListing videoVault = createApp("VideoVault", "Edit & Share Videos",
            "VideoVault is a powerful video editor packed with professional features. Trim, splice, add music, apply cinematic filters, and share stunning videos in minutes.\n\nFeatures:\n• Multi-track timeline\n• 100+ transitions\n• Cinematic color grading\n• AI background removal\n• 4K export",
            "CreativeMedia Apps", "6.0.2", "287.4 MB",
            "https://placehold.co/200x200/E67E22/white?text=VV",
            "https://placehold.co/1200x400/E67E22/white?text=VideoVault",
            new BigDecimal("14.99"), 4.7, 1560, 75000L, false, photoVideo,
            "iOS 15+, macOS 12+", "4+",
            new String[]{"https://placehold.co/390x844/FDF2E9/333?text=Timeline",
                         "https://placehold.co/390x844/FDF2E9/333?text=Filters",
                         "https://placehold.co/390x844/FDF2E9/333?text=Export"});

        AppListing chessmaster = createApp("Chessmaster Elite", "The Ultimate Chess Game",
            "Chessmaster Elite is the definitive chess experience for players of all levels. Play against AI opponents at 20 difficulty levels, analyze your games, and improve with tutorials.\n\nFeatures:\n• 20 AI difficulty levels\n• Online multiplayer\n• Game analysis\n• Opening library\n• Puzzle training",
            "BoardGames Pro", "3.5.0", "78.9 MB",
            "https://placehold.co/200x200/2C3E50/white?text=CM",
            "https://placehold.co/1200x400/2C3E50/white?text=Chessmaster+Elite",
            new BigDecimal("4.99"), 4.8, 2140, 98000L, false, games,
            "iOS 13+, macOS 11+", "4+",
            new String[]{"https://placehold.co/390x844/ECF0F1/333?text=Board",
                         "https://placehold.co/390x844/ECF0F1/333?text=Analysis",
                         "https://placehold.co/390x844/ECF0F1/333?text=Puzzles"});

        AppListing meditateNow = createApp("MeditateNow", "Mindfulness & Meditation",
            "MeditateNow guides you through mindfulness practices to reduce stress, improve focus, and sleep better. With hundreds of guided sessions led by expert instructors, peace is always a breath away.\n\nFeatures:\n• 500+ guided sessions\n• Sleep sounds\n• Breathing exercises\n• Progress tracking\n• Offline listening",
            "MindfulApp Co", "5.6.1", "118.3 MB",
            "https://placehold.co/200x200/1ABC9C/white?text=MN",
            "https://placehold.co/1200x400/1ABC9C/white?text=MeditateNow",
            BigDecimal.ZERO, 4.9, 7820, 560000L, false, health,
            "iOS 13+, watchOS 7+", "4+",
            new String[]{"https://placehold.co/390x844/E8F8F5/333?text=Sessions",
                         "https://placehold.co/390x844/E8F8F5/333?text=Breathing",
                         "https://placehold.co/390x844/E8F8F5/333?text=Sleep"});

        AppListing podcastHub = createApp("PodcastHub", "Your Podcast Universe",
            "PodcastHub is the best way to discover and listen to podcasts. With smart recommendations, cross-device sync, and variable playback speed, your podcast experience is flawless.\n\nFeatures:\n• 2M+ podcasts\n• Smart recommendations\n• Variable speed playback\n• Sleep timer\n• Cross-device sync",
            "AudioWorld Apps", "11.2.0", "38.4 MB",
            "https://placehold.co/200x200/E91E63/white?text=PH",
            "https://placehold.co/1200x400/E91E63/white?text=PodcastHub",
            BigDecimal.ZERO, 4.5, 3950, 420000L, false, entertainment,
            "iOS 13+", "4+",
            new String[]{"https://placehold.co/390x844/FCE4EC/333?text=Browse",
                         "https://placehold.co/390x844/FCE4EC/333?text=Player",
                         "https://placehold.co/390x844/FCE4EC/333?text=Library"});

        AppListing cryptoTracker = createApp("CryptoWatch", "Crypto Portfolio Tracker",
            "CryptoWatch keeps you updated on all your cryptocurrency investments. Real-time price alerts, portfolio analytics, and market news in one sleek app.\n\nFeatures:\n• 10,000+ cryptocurrencies\n• Real-time price alerts\n• Portfolio P&L tracking\n• Market news aggregator\n• Price prediction charts",
            "CryptoApps Dev", "2.1.3", "29.5 MB",
            "https://placehold.co/200x200/F7931A/white?text=CW",
            "https://placehold.co/1200x400/F7931A/white?text=CryptoWatch",
            BigDecimal.ZERO, 4.1, 1680, 88000L, false, finance,
            "iOS 14+", "4+",
            new String[]{"https://placehold.co/390x844/FFF3E0/333?text=Portfolio",
                         "https://placehold.co/390x844/FFF3E0/333?text=Charts",
                         "https://placehold.co/390x844/FFF3E0/333?text=News"});

        AppListing qrScanner = createApp("QR Master", "QR & Barcode Scanner",
            "QR Master is the fastest and most reliable QR and barcode scanner available. Scan any code instantly and access links, contact info, Wi-Fi credentials, and more.\n\nFeatures:\n• Instant scanning\n• Create your own QR codes\n• Scan history\n• Wi-Fi QR support\n• Batch scanning",
            "ToolsApp Labs", "4.0.0", "12.8 MB",
            "https://placehold.co/200x200/34495E/white?text=QR",
            "https://placehold.co/1200x400/34495E/white?text=QR+Master",
            BigDecimal.ZERO, 4.3, 1420, 200000L, false, utilities,
            "iOS 12+", "4+",
            new String[]{"https://placehold.co/390x844/F2F3F4/333?text=Scanner",
                         "https://placehold.co/390x844/F2F3F4/333?text=Create+QR",
                         "https://placehold.co/390x844/F2F3F4/333?text=History"});

        // Reviews
        createReview("Absolutely love it!", "This app has completely changed how I manage my tasks. The AI prioritization is spot on.", 5, alice, focusFlow);
        createReview("Best productivity app", "Clean interface, works great. Syncs perfectly across all my devices.", 5, bob, focusFlow);
        createReview("Good but has bugs", "Great concept but crashes occasionally on older devices. Hope they fix it soon.", 3, carol, focusFlow);

        createReview("Outstanding photo editor", "PixelCraft rivals desktop software. The healing brush is incredible.", 5, demo, pixelCraft);
        createReview("Worth every penny", "I've tried dozens of photo editors and this is by far the best on mobile.", 5, alice, pixelCraft);
        createReview("Amazing but heavy", "Superb quality editing but takes up a lot of storage and can be slow.", 4, bob, pixelCraft);

        createReview("Professional quality", "I've been making music for 10 years and this app is genuinely impressive.", 5, carol, beatMaker);
        createReview("Fun and creative", "Even as a beginner, I was making beats in 10 minutes. So intuitive!", 4, demo, beatMaker);
        createReview("Solid but pricey", "Great app but the price point is high for occasional users.", 3, alice, beatMaker);

        createReview("Made coding fun!", "I tried other apps but this one actually made me stick with learning.", 5, bob, learnCode);
        createReview("Great for beginners", "Perfect step-by-step approach. Completed JavaScript track in 3 weeks.", 5, carol, learnCode);
        createReview("Could use more depth", "Good for basics but lacks advanced content for intermediate learners.", 3, demo, learnCode);

        createReview("Incredibly addictive", "Can't stop playing! The graphics are stunning and gameplay is perfect.", 5, alice, galaxyRun);
        createReview("Great time killer", "Simple but satisfying. My kids love it too.", 4, bob, galaxyRun);
        createReview("Gets repetitive", "Fun at first but becomes very repetitive after a few hours.", 3, carol, galaxyRun);

        createReview("Saved my budget", "I was overspending every month. MoneyWise helped me cut expenses by 30%.", 5, demo, moneyWise);
        createReview("Best finance app", "Automatic categorization is remarkably accurate. Highly recommended.", 5, alice, moneyWise);
        createReview("Bank sync issues", "The core features are great but bank sync fails often.", 3, bob, moneyWise);

        createReview("Transformed my fitness", "Down 15 pounds in 2 months following the workout plans. Amazing!", 5, carol, fitTrack);
        createReview("Comprehensive tracker", "Tracks everything I need: calories, sleep, heart rate. Love it.", 5, demo, fitTrack);
        createReview("Good app, minor issues", "Workout library is excellent but the nutrition tracker needs improvement.", 4, alice, fitTrack);

        createReview("Better than expected", "Streaming quality is top notch. Download for offline works flawlessly.", 5, bob, streamFlix);
        createReview("Great content library", "Good variety of movies and shows. Family profile feature is handy.", 4, carol, streamFlix);
        createReview("Needs more originals", "Decent library but needs exclusive content to compete with bigger names.", 3, demo, streamFlix);

        createReview("Peaceful experience", "MeditateNow has genuinely reduced my anxiety. The sleep sessions are magic.", 5, alice, meditateNow);
        createReview("Daily habit now", "I never thought I'd meditate daily but this app made it stick.", 5, bob, meditateNow);
        createReview("Life changing app", "The breathing exercises alone are worth the download. Completely free!", 5, carol, meditateNow);

        createReview("Learning Spanish fast", "I've tried Duolingo and others - LinguaLearn is the most effective by far.", 5, demo, linguaLearn);
        createReview("Great gamification", "Makes language learning fun! The streak system keeps me motivated.", 5, alice, linguaLearn);
        createReview("Very good overall", "Excellent app but the speaking recognition can be inconsistent.", 4, bob, linguaLearn);
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }

    private User createUser(String firstName, String lastName, String email, String password,
                             boolean enabled, Role... roles) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(enabled);
        Set<Role> roleSet = new HashSet<>(Arrays.asList(roles));
        user.setRoles(roleSet);
        return userRepository.save(user);
    }

    private Category createCategory(String name, String iconClass, String description) {
        Category cat = new Category();
        cat.setName(name);
        cat.setIconClass(iconClass);
        cat.setDescription(description);
        return categoryRepository.save(cat);
    }

    private AppListing createApp(String name, String subtitle, String description,
                                  String developer, String version, String size,
                                  String iconUrl, String headerImageUrl, BigDecimal price,
                                  double rating, int reviewCount, long downloadCount, boolean featured,
                                  Category category, String compatibility, String ageRating,
                                  String[] screenshotUrls) {
        AppListing app = new AppListing();
        app.setName(name);
        app.setSubtitle(subtitle);
        app.setDescription(description);
        app.setDeveloper(developer);
        app.setVersion(version);
        app.setSize(size);
        app.setIconUrl(iconUrl);
        app.setHeaderImageUrl(headerImageUrl);
        app.setPrice(price);
        app.setRating(rating);
        app.setReviewCount(reviewCount);
        app.setDownloadCount(downloadCount);
        app.setFeatured(featured);
        app.setCategory(category);
        app.setCompatibility(compatibility);
        app.setAgeRating(ageRating);
        AppListing saved = appListingRepository.save(app);

        if (screenshotUrls != null) {
            for (int i = 0; i < screenshotUrls.length; i++) {
                Screenshot ss = new Screenshot();
                ss.setImageUrl(screenshotUrls[i]);
                ss.setDisplayOrder(i);
                ss.setAppListing(saved);
                saved.getScreenshots().add(ss);
            }
            appListingRepository.save(saved);
        }
        return saved;
    }

    private void createReview(String title, String content, int rating, User user, AppListing app) {
        Review review = new Review();
        review.setTitle(title);
        review.setContent(content);
        review.setRating(rating);
        review.setUser(user);
        review.setAppListing(app);
        reviewRepository.save(review);
    }
}
