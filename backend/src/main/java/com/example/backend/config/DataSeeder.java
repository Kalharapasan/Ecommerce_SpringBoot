package com.example.backend.config;

import com.example.backend.model.Category;
import com.example.backend.model.Product;
import com.example.backend.model.User;
import com.example.backend.repository.CategoryRepo;
import com.example.backend.repository.ProductRepo;
import com.example.backend.repository.UserRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final SecurityConfig securityConfig;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.backend.repository.OrderItemRepo orderItemRepo;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.backend.repository.OrderRepo orderRepo;
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.example.backend.repository.CartRepo cartRepo;

    public DataSeeder(UserRepo userRepo, CategoryRepo categoryRepo, ProductRepo productRepo, SecurityConfig securityConfig) {
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
        this.securityConfig = securityConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            seedUsers();
        }
        if (categoryRepo.count() < 15) {
            System.out.println("Wiping existing catalog to seed premium computer store data...");
            wipeDatabase();
            seedCategoriesAndProducts();
        }
        fixPlaceholderUrls();
    }

    private void seedUsers() throws Exception {
        // Seed 1 Admin
        User admin = new User();
        admin.setFullName("System Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@byteforge.com");
        admin.setPassword(securityConfig.hashPassword("admin123"));
        admin.setRole("ADMIN");
        userRepo.save(admin);

        // Seed 10 Customers
        for (int i = 1; i <= 10; i++) {
            User customer = new User();
            customer.setFullName("Customer " + i);
            customer.setUsername("user" + i);
            customer.setEmail("user" + i + "@gmail.com");
            customer.setPassword(securityConfig.hashPassword("user123"));
            customer.setRole("USER");
            userRepo.save(customer);
        }
    }

    private void seedCategoriesAndProducts() {
        // Define 15 computer shop categories
        String[][] categoriesData = {
            {"Processors (CPUs)", "High-performance Intel and AMD processors for gaming and productivity.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400&auto=format&fit=crop"},
            {"Graphics Cards (GPUs)", "Power your gaming and rendering with Nvidia GeForce RTX and AMD Radeon graphics cards.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400&auto=format&fit=crop"},
            {"Motherboards", "Durable and feature-rich motherboards supporting latest AMD and Intel sockets.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400&auto=format&fit=crop"},
            {"RAM (Memory)", "High-speed DDR4 and DDR5 memory modules for multitasking and overclocking.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400&auto=format&fit=crop"},
            {"Storage (SSDs / HDDs)", "Fast NVMe M.2 SSDs and high-capacity HDDs for games and media files.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400&auto=format&fit=crop"},
            {"Power Supplies (PSUs)", "Quiet, efficient, and fully-modular power supply units with 80+ certifications.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400&auto=format&fit=crop"},
            {"PC Cases", "Sleek PC cases with tempered glass sides, optimized airflow, and RGB lighting layouts.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400&auto=format&fit=crop"},
            {"CPU Coolers", "Liquid AIO and air CPU coolers to keep system temperatures under control.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400&auto=format&fit=crop"},
            {"Gaming Laptops", "Portable gaming powerhouses featuring high-refresh rates and dedicated GPUs.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400&auto=format&fit=crop"},
            {"Desktop Systems", "Fully-assembled, tested, and ready-to-game custom built gaming desktop systems.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400&auto=format&fit=crop"},
            {"Monitors", "High-refresh rate, low latency gaming monitors in flat and curved panels.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400&auto=format&fit=crop"},
            {"Keyboards", "Mechanical gaming keyboards with tactile switches and custom RGB backlighting.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400&auto=format&fit=crop"},
            {"Mice", "Lightweight, high-precision optical gaming mice with customizable DPI profiles.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400&auto=format&fit=crop"},
            {"Gaming Headsets", "Immersive surround sound headsets with noise-cancelling microphones.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400&auto=format&fit=crop"},
            {"Accessories & Cables", "Custom sleeved extensions, thermal pastes, GPU brackets, and desk pads.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400&auto=format&fit=crop"}
        };

        // Create and save categories
        List<Category> savedCategories = new ArrayList<>();
        int catCount = 1;
        for (String[] cat : categoriesData) {
            System.out.println("Seeding Category " + catCount + "/15: " + cat[0]);
            Category category = new Category();
            category.setCategoryName(cat[0]);
            category.setDescription(cat[1]);
            category.setImageUrl(downloadAndSaveImage(cat[2]));
            savedCategories.add(categoryRepo.save(category));
            catCount++;
        }

        // Define 100 products (about 6-8 per category)
        String[][][] productsData = {
            // 0: CPUs (8 products)
            {
                {"Intel Core i9-14900K Processor", "Intel's flagship desktop CPU featuring 24 cores, 32 threads, and up to 6.0 GHz boost speeds.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "185000", "15"},
                {"AMD Ryzen 7 7800X3D Processor", "The absolute best gaming processor with 3D V-Cache technology, 8 cores, and 16 threads.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "135000", "20"},
                {"Intel Core i7-14700K Processor", "Powerful 20-core processor ideal for gaming, editing, and professional creative workloads.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "145000", "12"},
                {"AMD Ryzen 9 7950X Processor", "Extreme multi-threaded powerhouse with 16 cores and 32 threads, perfect for content creators.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "195000", "8"},
                {"Intel Core i5-14600K Processor", "Excellent value gaming CPU with 14 cores, 20 threads, and great overclocking capabilities.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "98000", "25"},
                {"AMD Ryzen 5 7600X Processor", "Affordable next-gen 6-core processor offering incredible gaming performance for mid-range builds.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "75000", "30"},
                {"AMD Ryzen 5 5600X Processor", "Classic 6-core AM4 processor that remains a budget-friendly gaming legend.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "48000", "40"},
                {"Intel Core i9-14900KS Special Edition", "A pre-binned special edition desktop processor reaching 6.2GHz out of the box.", "https://images.unsplash.com/photo-1591488320449-011701bb6704?q=80&w=400", "225000", "3"}
            },
            // 1: GPUs (8 products)
            {
                {"Nvidia GeForce RTX 4090 24GB", "The ultimate GPU featuring 24GB G6X memory, DLSS 3, and unmatched 4K gaming performance.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "650000", "5"},
                {"Nvidia GeForce RTX 4080 Super 16GB", "Premium desktop graphics card delivering high-refresh 4K gaming and powerful ray tracing.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "395000", "10"},
                {"AMD Radeon RX 7900 XTX 24GB", "AMD's flagship RDNA 3 graphics card with 24GB VRAM and massive rasterization throughput.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "365000", "8"},
                {"Nvidia GeForce RTX 4070 Ti Super 16GB", "High-performance GPU with 16GB VRAM, perfect for high refresh rate 1440p gaming.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "285000", "15"},
                {"Nvidia GeForce RTX 4060 Ti 8GB", "Mid-range graphics card offering excellent 1080p performance with DLSS 3 support.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "155000", "25"},
                {"AMD Radeon RX 7600 XT 16GB", "Great budget-friendly GPU featuring a massive 16GB buffer for entry-level 1440p gaming.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "125000", "20"},
                {"Nvidia GeForce RTX 3060 12GB", "The popular Ampere mid-range GPU with 12GB VRAM, perfect for budget gaming builds.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "95000", "35"},
                {"Nvidia GeForce RTX 4070 Super 12GB", "Supercharged gaming processor offering a massive upgrade for high-fps 1440p gaming.", "https://images.unsplash.com/photo-1582994274092-23f4931bc151?q=80&w=400", "235000", "15"}
            },
            // 2: Motherboards (7 products)
            {
                {"ASUS ROG Maximus Z790 Dark Hero", "High-end LGA1700 motherboard with robust power stages, DDR5 support, PCIe 5.0, and WiFi 7.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "215000", "6"},
                {"MSI MAG B760 Tomahawk WiFi", "Premium mid-range motherboard with great VRM heatsinks, DDR5 compatibility, and high reliability.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "68000", "15"},
                {"ASUS ROG Strix X670E-F Gaming WiFi", "Premium AMD AM5 motherboard designed to push PCIe 5.0 graphics cards and DDR5 memory.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "145000", "10"},
                {"Gigabyte B650 AORUS Elite AX", "Outstanding AM5 motherboard offering robust power delivery, triple M.2 slots, and built-in WiFi.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "78000", "18"},
                {"ASUS Prime H610M-K DDR4", "Budget Micro-ATX LGA1700 motherboard ideal for entry-level office and gaming builds.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "32000", "30"},
                {"ASRock B450M Steel Legend", "Classic AM4 motherboard with stylish RGB lighting, heavy-duty armor, and great stability.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "36000", "22"},
                {"MSI B650 Gaming Plus WiFi", "Feature-rich AM5 motherboard providing high performance and cooling at a great price.", "https://images.unsplash.com/photo-1518770660439-4636190af475?q=80&w=400", "64000", "20"}
            },
            // 3: RAM (7 products)
            {
                {"Corsair Dominator Titanium 32GB DDR5 6000MHz", "Ultra-premium DDR5 dual-channel desktop memory kit with customizable RGB lighting and premium ICs.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "65000", "12"},
                {"G.Skill Trident Z5 RGB 32GB DDR5 6400MHz", "Sleek aluminum heatspreader, top-tier performance, optimized for Intel and AMD builds.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "52000", "20"},
                {"TeamGroup T-Force Delta RGB 32GB DDR5 6000MHz", "Highly popular DDR5 memory kit with wide-angle RGB lighting and reliable performance.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "44000", "25"},
                {"Kingston FURY Beast 16GB DDR4 3200MHz", "Reliable dual-channel DDR4 memory kit, perfect for upgrading older AM4 and Intel systems.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "18500", "50"},
                {"Corsair Vengeance LPX 16GB DDR4 3600MHz", "Low-profile DDR4 memory modules designed for high-performance CPU cooler clearance.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "19500", "45"},
                {"Crucial Desktop Memory 8GB DDR4 3200MHz", "Standard OEM DDR4 memory module for budget office computers and standard upgrades.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "7800", "80"},
                {"G.Skill Ripjaws S5 32GB DDR5 5600MHz", "Low-profile desktop DDR5 memory, optimized for compact builds and solid overclocking.", "https://images.unsplash.com/photo-1562976540-1502c2145186?q=80&w=400", "38000", "25"}
            },
            // 4: Storage (8 products)
            {
                {"Samsung 990 PRO 2TB NVMe M.2 SSD", "The fastest PCIe 4.0 SSD delivering sequential reads up to 7450 MB/s for PC and PS5.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "64000", "15"},
                {"Crucial T700 1TB PCIe 5.0 NVMe SSD", "Next-gen PCIe Gen5 storage boasting blazing speeds up to 12,400 MB/s with heavy heatsink.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "68000", "8"},
                {"WD Black SN850X 2TB NVMe SSD", "High-performance gaming SSD with optional heatsink, outstanding loading times for game libraries.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "55000", "18"},
                {"Kingston NV2 1TB PCIe 4.0 NVMe SSD", "Cost-effective NVMe storage option offering solid speeds up to 3500 MB/s for daily builds.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "22500", "40"},
                {"Crucial MX500 1TB 2.5\" SATA SSD", "Industry standard 2.5-inch SATA SSD, perfect for storing secondary game files or office backups.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "26000", "30"},
                {"Seagate BarraCuda 2TB 3.5\" SATA HDD", "High-capacity mechanical hard drive for storage-heavy media backups and archives.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "19500", "50"},
                {"Samsung 870 EVO 500GB SATA SSD", "Reliable 2.5-inch SATA SSD with sequential read speeds up to 560 MB/s.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "16500", "25"},
                {"WD Blue 1TB SATA 2.5\" SSD", "High-capacity standard SATA SSD offering high reliability for general PC build storage.", "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?q=80&w=400", "28000", "30"}
            },
            // 5: PSUs (6 products)
            {
                {"Corsair RM1000x 1000W PSU", "80 Plus Gold certified fully modular power supply with quiet operations and premium components.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400", "62000", "15"},
                {"ASUS ROG Thor 1200W Platinum II", "High-end 1200W modular power supply featuring an OLED display, Aura Sync RGB, and quiet cooling.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400", "135000", "6"},
                {"MSI MAG A850GL 850W PCIe 5.0", "ATX 3.0 ready power supply featuring native 12VHPWR connector for Nvidia RTX 40-series cards.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400", "44000", "20"},
                {"Corsair CV650 650W 80+ Bronze", "Reliable Bronze efficiency power supply, perfect for entry-level and mid-range systems.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400", "24500", "30"},
                {"FSP Hydro K Pro 500W Bronze", "Budget-friendly 500W power supply offering stable power and solid safety protections.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400", "16500", "35"},
                {"DeepCool PF750 750W 80+ Power Supply", "Standard efficiency power supply offering 750W capacity at an accessible price point.", "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?q=80&w=400", "21000", "28"}
            },
            // 6: Cases (7 products)
            {
                {"Lian Li O11 Dynamic EVO RGB", "Stunning dual-chamber chassis with wrap-around tempered glass, extensive custom loop cooling space.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "68000", "8"},
                {"NZXT H9 Flow Dual-Chamber Mid-Tower", "High-airflow dual-chamber ATX case with perforated panels, GPU vertical mount support.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "59000", "12"},
                {"Corsair 4000D Airflow Tempered Glass", "Classic mid-tower case with high-airflow front panel, robust cable routing system.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "35000", "25"},
                {"Montech Sky Two ARGB Mid-Tower", "Includes 4 pre-installed ARGB PWM fans, dual tempered glass panels for absolute view.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "32000", "18"},
                {"MSI MAG Forge 100R", "Budget-friendly gaming case with mesh front panel and pre-installed Addressable RGB fans.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "19500", "30"},
                {"DeepCool CH370 Micro-ATX Case", "Compact and minimalistic Micro-ATX case featuring a built-in GPU support bracket.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "18500", "20"},
                {"Thermaltake Versa H15 Micro Case", "Budget classic design with simple layout, perfect for simple office workstations.", "https://images.unsplash.com/photo-1587202372775-e229f172b9d7?q=80&w=400", "14500", "40"}
            },
            // 7: Coolers (6 products)
            {
                {"Lian Li Galahad II Trinity 360 AIO", "Premium 360mm liquid cooler with magnetic daisy-chain fans, high cooling output, and custom RGB block.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400", "59000", "10"},
                {"Corsair iCUE Link H150i RGB 360mm", "Smart liquid CPU cooler featuring Corsair's next-gen Link simplified cable-less interface.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400", "78000", "6"},
                {"Noctua NH-D15 Chromax.Black Cooler", "The ultimate flagship dual-tower air cooler, ultra-quiet operation with premium 140mm fans.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400", "46000", "12"},
                {"DeepCool AK620 Digital Air Cooler", "Dual-tower air cooler featuring a real-time system temperature and usage digital display panel.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400", "28000", "15"},
                {"Thermalright Peerless Assassin 120 SE", "Incredible performance budget dual-tower cooler that easily outperforms expensive rivals.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400", "16500", "40"},
                {"DeepCool AG400 ARGB Single Tower", "Standard single tower CPU cooler with customizable ARGB fan for budget setups.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=400", "9500", "50"}
            },
            // 8: Laptops (6 products)
            {
                {"ASUS ROG Strix SCAR 18 (2024)", "Ultimate gaming laptop with 18\" QHD 240Hz screen, Core i9 CPU, and Nvidia RTX 4090 16GB GPU.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400", "1350000", "3"},
                {"Lenovo Legion 5 Pro 16\"", "High-performance laptop featuring AMD Ryzen 7, RTX 4070, 32GB RAM, and 165Hz IPS screen.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400", "680000", "8"},
                {"ASUS TUF Gaming A15", "Durable gaming laptop powered by AMD Ryzen 5, RTX 4050, 16GB RAM, and solid gaming features.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400", "345000", "15"},
                {"MSI Cyborg 15 Intel i7", "Thin and light budget gaming laptop featuring Nvidia RTX 4060 and 144Hz display.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400", "390000", "10"},
                {"Acer Nitro V 15 Ryzen 5", "Entry-level gaming laptop with 15.6\" 144Hz screen, RTX 4050, and 512GB SSD storage.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400", "295000", "12"},
                {"HP Victus 15 Core i5", "Simple budget-friendly gaming laptop offering a solid Nvidia GTX 1650 for entry esports.", "https://images.unsplash.com/photo-1603302576837-37561b2e2302?q=80&w=400", "225000", "18"}
            },
            // 9: Desktops (6 products)
            {
                {"ByteForge Apex Custom Gaming PC", "Fully watercooled flagship desktop featuring Core i9-14900K, GeForce RTX 4090, and 64GB DDR5.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400", "1450000", "2"},
                {"ByteForge Elite Gaming Build", "High-performance PC in Lian Li case, Ryzen 7 7800X3D, RTX 4080 Super, and 32GB RAM.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400", "795000", "5"},
                {"ByteForge Core Streamer PC", "Optimized content creation and streaming build featuring Intel i7, RTX 4070, and 2TB NVMe SSD.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400", "525000", "6"},
                {"ByteForge Esports Starter System", "Outstanding esports PC powered by Ryzen 5 7600, RTX 4060, and 16GB high-speed memory.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400", "285000", "10"},
                {"ByteForge Budget Champion Rig", "Ultra value-oriented gaming setup featuring Ryzen 5 5600, RX 7600, and tempered glass case.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400", "185000", "12"},
                {"ByteForge Home Workstation Desktop", "Reliable office and programming workstation with Core i5, integrated UHD graphics, and 16GB RAM.", "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?q=80&w=400", "115000", "15"}
            },
            // 10: Monitors (6 products)
            {
                {"ASUS ROG Swift 32\" OLED PG32UCDM", "Stunning 4K QD-OLED gaming monitor with 240Hz refresh rate, 0.03ms response time, and G-Sync.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400", "445000", "4"},
                {"GIGABYTE M27Q 27\" Gaming Monitor", "Popular 1440p KVM IPS monitor featuring 170Hz refresh rate, excellent colors, and flat panel.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400", "105000", "15"},
                {"MSI Optix 24\" G241 Gaming Monitor", "Highly accessible 1080p IPS display with 144Hz refresh rate, perfect for competitive shooter setups.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400", "59000", "25"},
                {"Samsung Odyssey G9 49\" Curved", "Ultrawide 32:9 dual QHD gaming display with massive 1000R curvature and 240Hz refresh rate.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400", "365000", "5"},
                {"ASUS VY249HE Eye Care Monitor", "23.8-inch IPS monitor with 75Hz refresh rate and blue light filter, designed for long office hours.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400", "38000", "30"},
                {"Samsung Odyssey G3 27\" Flat", "Large 27-inch 1080p gaming screen offering 144Hz refresh rate and adjustable stand.", "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?q=80&w=400", "72000", "18"}
            },
            // 11: Keyboards (6 products)
            {
                {"ASUS ROG Azoth Wireless Mechanical Keyboard", "75% custom DIY-style mechanical keyboard featuring hot-swappable switches, lubed stabilizers, and OLED display.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400", "95000", "8"},
                {"SteelSeries Apex Pro TKL (2023)", "OmniPoint 2.0 adjustable hyper-magnetic switches, rapid trigger support, and integrated smart display.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400", "78000", "12"},
                {"Razer BlackWidow V4 Pro", "Full-sized mechanical keyboard featuring Razer green clicky switches and dedicated macro keys.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400", "64000", "10"},
                {"Logitech G PRO X TKL Wireless", "Light-speed wireless tenkeyless layout designed for professional esports players.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400", "59000", "15"},
                {"Keychron V1 QMK Custom Keyboard", "Hot-swappable tactile Keychron switches, premium sound-absorbing foam, and full macro support.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400", "36000", "20"},
                {"Redragon K552 Kumara RGB", "The ultimate budget tenkeyless mechanical keyboard featuring highly tactile blue switches.", "https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?q=80&w=400", "14500", "50"}
            },
            // 12: Mice (6 products)
            {
                {"Logitech G PRO X Superlight 2", "Flagship ultra-lightweight wireless gaming mouse weighing only 60g, 32K sub-micron sensor.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400", "59000", "15"},
                {"Razer DeathAdder V3 Pro Wireless", "Ergonomic gaming mouse featuring optical mouse switches, 30K optical sensor, and high battery life.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400", "54000", "12"},
                {"Logitech G502 X Plus RGB Wireless", "Classic ergonomic gaming layout upgraded with hybrid optical-mechanical switches and RGB lighting.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400", "49500", "20"},
                {"SteelSeries Prime Wireless Mouse", "Prestige optical magnetic switches, lightweight 80g design for fast target sweeps.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400", "32000", "22"},
                {"Razer Viper Mini Wired", "Highly compact wired gaming mouse, perfect for smaller hands and claw grips at a budget.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400", "12500", "45"},
                {"Fantech Aria XD7 Wireless Mouse", "Incredible performance egg-shaped wireless mouse featuring Pixart 3395 flagship sensor.", "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?q=80&w=400", "24000", "25"}
            },
            // 13: Headsets (6 products)
            {
                {"SteelSeries Arctis Nova Pro Wireless", "Audiophile-quality active noise-cancelling wireless headset with dual-battery hot swap station.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400", "125000", "6"},
                {"HyperX Cloud III Wireless Headset", "Legendary comfort, ultra-long battery life up to 120 hours, and crystal-clear 53mm angled drivers.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400", "56000", "18"},
                {"Razer BlackShark V2 Pro (2023)", "Esports gaming headset with super wideband microphone, custom-tuned EQ profiles for shooters.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400", "59000", "15"},
                {"Logitech G435 SE Wireless Bluetooth", "Lightweight, eco-friendly wireless headset designed for casual gamers and portable setups.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400", "24000", "30"},
                {"Fantech HG11 Captain Headset", "Sturdy budget surround-sound wired headset with large padded cushions and RGB ring.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400", "9500", "40"},
                {"Corsair HS55 Stereo Wired", "Clean lightweight gaming headset with flip-to-mute microphone and Discord certification.", "https://images.unsplash.com/photo-1546435770-a3e426bf472b?q=80&w=400", "18500", "25"}
            },
            // 14: Accessories (7 products)
            {
                {"Grizzly Kryonaut Thermal Paste (1g)", "Premium high-conductivity thermal grease designed to push desktop CPU and GPU overclocks.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "3800", "100"},
                {"Lian Li Strimer Plus V2 24-Pin Extension", "Immersive addressable RGB sleeved motherboard power extension cable for build aesthetics.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "19500", "15"},
                {"DeepCool GH-01 Graphics Card Holder", "Simple metal GPU sag support bracket with built-in hidden cable management slots.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "4800", "35"},
                {"ByteForge Premium desk Mat (900x400)", "Waterproof micro-woven fabric desk pad with custom gaming patterns and stitched edges.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "8500", "50"},
                {"Orico M.2 NVMe SSD Enclosure USB-C", "Aluminum tool-free enclosure to convert internal NVMe SSDs into high-speed portable storage.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "9800", "40"},
                {"Antec Sleeved Cable Extension Kit (Red)", "Fully-sleeved custom power extensions for 24-pin and 8-pin desktop power supplies.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "12500", "30"},
                {"Logitech C922 Pro Stream Webcam", "Full HD 1080p webcam featuring custom background replacement and tripod mount for streaming.", "https://images.unsplash.com/photo-1547082299-de196ea013d6?q=80&w=400", "34000", "15"}
            }
        };

        // Create and save products linked to categories
        int prodCount = 1;
        for (int catIdx = 0; catIdx < savedCategories.size(); catIdx++) {
            Category category = savedCategories.get(catIdx);
            String[][] prodList = productsData[catIdx];
            for (String[] prod : prodList) {
                System.out.println("Seeding Product " + prodCount + "/100: " + prod[0]);
                Product product = new Product();
                product.setProductName(prod[0]);
                product.setDescription(prod[1]);
                product.setImageUrl(downloadAndSaveImage(prod[2]));
                product.setPrice(new BigDecimal(prod[3]));
                product.setCategory(category);
                product.setStock(Integer.parseInt(prod[4]));
                productRepo.save(product);
                prodCount++;
            }
        }
    }

    private String downloadAndSaveImage(String sourceUrl) {
        if (sourceUrl == null || !sourceUrl.startsWith("http")) {
            return sourceUrl;
        }
        try {
            // Ensure uploads directory exists
            java.io.File uploadDir = new java.io.File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Generate unique filename
            String fileExtension = ".jpg";
            String fileName = java.util.UUID.randomUUID().toString() + fileExtension;
            java.io.File destinationFile = new java.io.File(uploadDir, fileName);

            // Download file
            java.net.URL url = new java.net.URL(sourceUrl);
            try (java.io.InputStream in = url.openStream()) {
                java.nio.file.Files.copy(in, destinationFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Return local static resource URL
            return "http://localhost:8000/uploads/" + fileName;
        } catch (Exception e) {
            System.err.println("Failed to download image from " + sourceUrl + ": " + e.getMessage());
            return sourceUrl; // Fallback to original URL on download failure
        }
    }

    private void wipeDatabase() {
        try {
            if (orderItemRepo != null) orderItemRepo.deleteAll();
            if (orderRepo != null) orderRepo.deleteAll();
            if (cartRepo != null) cartRepo.deleteAll();
            productRepo.deleteAll();
            categoryRepo.deleteAll();
        } catch (Exception e) {
            System.err.println("Failed to wipe database: " + e.getMessage());
        }
    }

    private void fixPlaceholderUrls() {
        try {
            // Fix category image URLs
            for (Category category : categoryRepo.findAll()) {
                if (category.getImageUrl() != null && category.getImageUrl().contains("via.placeholder.com")) {
                    String newUrl = category.getImageUrl().replace("via.placeholder.com", "placehold.co");
                    category.setImageUrl(newUrl);
                    categoryRepo.save(category);
                    System.out.println("Updated category image placeholder to placehold.co: " + category.getCategoryName());
                }
            }
            
            // Fix product image URLs
            for (Product product : productRepo.findAll()) {
                if (product.getImageUrl() != null && product.getImageUrl().contains("via.placeholder.com")) {
                    String newUrl = product.getImageUrl().replace("via.placeholder.com", "placehold.co");
                    product.setImageUrl(newUrl);
                    productRepo.save(product);
                    System.out.println("Updated product image placeholder to placehold.co: " + product.getProductName());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fix placeholder URLs: " + e.getMessage());
        }
    }
}
