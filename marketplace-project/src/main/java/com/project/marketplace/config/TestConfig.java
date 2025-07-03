package com.project.marketplace.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.project.marketplace.entities.Address;
import com.project.marketplace.entities.Category;
import com.project.marketplace.entities.Coupon;
import com.project.marketplace.entities.Customer;
import com.project.marketplace.entities.Order;
import com.project.marketplace.entities.OrderItem;
import com.project.marketplace.entities.Product;
import com.project.marketplace.entities.enums.AddressType;
import com.project.marketplace.entities.enums.CustomerType;
import com.project.marketplace.entities.enums.OrderStatus;
import com.project.marketplace.repositories.AddressRepository;
import com.project.marketplace.repositories.CategoryRepository;
import com.project.marketplace.repositories.CouponRepository;
import com.project.marketplace.repositories.CustomerRepository;
import com.project.marketplace.repositories.OrderItemRepository;
import com.project.marketplace.repositories.OrderRepository;
import com.project.marketplace.repositories.ProductRepository;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private CouponRepository couponRepository;

	@Override
	public void run(String... args) throws Exception {

		Category cat1 = new Category(null, "Action");
		Category cat2 = new Category(null, "Roguelike");
		Category cat3 = new Category(null, "Multiplayer");
		Category cat4 = new Category(null, "Adventure");
		Category cat5 = new Category(null, "RPG");
		Category cat6 = new Category(null, "Platformer");
		Category cat7 = new Category(null, "Strategy");
		Category cat8 = new Category(null, "Simulation");
		Category cat9 = new Category(null, "Horror");
		Category cat10 = new Category(null, "Indie");
		Category cat11 = new Category(null, "Open World");
		Category cat12 = new Category(null, "Puzzle");
		Category cat13 = new Category(null, "Fighting");

		categoryRepository.saveAll(
				Arrays.asList(cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8, cat9, cat10, cat11, cat12, cat13));

		List<String> otherImages = Arrays.asList(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/ss_92c7e8f34c00bdb455070ecdd5b746f0d2f6d808.600x338.jpg?t=1695270428",
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/ss_5478c9192023a3b3e438c030caa0809b62523587.600x338.jpg?t=1695270428",
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/ss_d5b6edd94e77ba6db31c44d8a3c09d807ab27751.600x338.jpg?t=1695270428");

		List<String> images1 = Arrays.asList(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/261570/header.jpg?t=1667504148",
				"https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/387290/ss_56f57022e1e0e8666a9fd141fe0933c1e8b36137.600x338.jpg?t=1701967651",
				"https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/387290/ss_d355fa04e0469134629eadcdef627cae490b8f8c.600x338.jpg?t=1701967651",
				"https://shared.cloudflare.steamstatic.com/store_item_assets/steam/apps/387290/ss_84985a89232bf29eb6e70f2e096ff346c07abfc0.600x338.jpg?t=1701967651");
		Product p1 = new Product(null, "Ori and the Blind Forest",
				"Explore a deeply emotional story about love, sacrifice, and the hope that exists in all of us.", 99.00,
				10, true, true, images1, "https://pastebin.com/raw/tEdFr645");
		p1.getCategories().add(cat2); // Roguelike
		p1.getCategories().add(cat6); // Platformer
		productRepository.saveAll(Arrays.asList(p1));

		List<String> images2 = new ArrayList<>();
		images2.add(0,
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/header.jpg?t=1695270428");
		images2.addAll(otherImages);
		Product p2 = new Product(null, "Hollow Knight",
				"Explore a vast interconnected world of forgotten paths, lush forests, and ruined cities.", 46.99, 10,
				false, true, images2, "https://pastebin.com/raw/tEdFr645");
		p2.getCategories().add(cat4); // Adventure
		p2.getCategories().add(cat6); // Platformer
		productRepository.saveAll(Arrays.asList(p2));

		List<String> images3 = Arrays.asList(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/268910/header.jpg?t=1709068852",
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/268910/ss_615455299355eaf552c638c7ea5b24a8b46e02dd.600x338.jpg?t=1709068852",
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/268910/ss_874f2d27a9120ee60cbce0c7bd4085525fd09b26.600x338.jpg?t=1709068852",
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/268910/ss_ae3db08c403209d868e52ae513540e1ba0489302.600x338.jpg?t=1709068852");
		Product p3 = new Product(null, "Cuphead",
				"A classic run and gun action game heavily focused on boss battles. Inspired by cartoons of the 1930s.",
				100.00, 10, true, false, images3, "https://pastebin.com/raw/tEdFr645");
		p3.getCategories().add(cat1); // Action
		p3.getCategories().add(cat6); // Platformer
		productRepository.saveAll(Arrays.asList(p3));

		List<String> images4 = new ArrayList<>();
		images4.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1145360/header.jpg?t=1715722799");
		images4.addAll(otherImages);
		Product p4 = new Product(null, "Hades",
				"Play as the immortal Prince of the Underworld using powers and mythical weapons from Olympus to escape the grasp of the god of the dead.",
				73.99, 10, true, true, images4, "https://pastebin.com/raw/tEdFr645");
		p4.getCategories().add(cat1); // Action
		p4.getCategories().add(cat2); // Roguelike
		productRepository.saveAll(Arrays.asList(p4));

		List<String> images5 = new ArrayList<>();
		images5.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2050650/header.jpg?t=1736385712");
		images5.addAll(otherImages);
		Product p5 = new Product(null, "Resident Evil 4",
				"Special agent Leon S. Kennedy is sent on a mission to rescue the U.S. President’s daughter who has been kidnapped.",
				39.99, 10, true, true, images5, "https://pastebin.com/raw/tEdFr645");
		p5.getCategories().add(cat1); // Action
		p5.getCategories().add(cat9); // Horror
		productRepository.saveAll(Arrays.asList(p5));

		List<String> images6 = new ArrayList<>();
		images6.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/292030/header.jpg?t=1736424367");
		images6.addAll(otherImages);
		Product p6 = new Product(null, "The Witcher 3: Wild Hunt",
				"Play as Geralt of Rivia, a monster hunter, as you explore a vast open world and search for your adopted daughter.",
				199.99, 15, true, true, images6, "https://pastebin.com/raw/tEdFr645");
		p6.getCategories().add(cat11); // Adventure
		p6.getCategories().add(cat5); // Action
		productRepository.saveAll(Arrays.asList(p6));

		List<String> images7 = new ArrayList<>();
		images7.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1174180/header.jpg?t=1720558643");
		images7.addAll(otherImages);
		Product p7 = new Product(null, "Red Dead Redemption 2",
				"Experience life as Arthur Morgan, an outlaw in the Old West, while dealing with issues of loyalty and survival.",
				249.99, 5, true, true, images7, "https://pastebin.com/raw/tEdFr645");
		p7.getCategories().add(cat11); // Adventure
		p7.getCategories().add(cat5); // Action
		productRepository.saveAll(Arrays.asList(p7));

		List<String> images8 = new ArrayList<>();
		images8.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1426210/header.jpg?t=1730911936");
		images8.addAll(otherImages);
		Product p8 = new Product(null, "It Takes Two",
				"Embark on the craziest journey of your life in It Takes Two, a genre-bending platform adventure created purely for co-op.",
				239.99, 12, true, true, images8, "https://pastebin.com/raw/tEdFr645");
		p8.getCategories().add(cat4); // Adventure
		p8.getCategories().add(cat12); // Puzzle
		p8.getCategories().add(cat3);
		productRepository.saveAll(Arrays.asList(p8));

		List<String> images9 = new ArrayList<>();
		images9.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1672970/header.jpg?t=1717003107");
		images9.addAll(otherImages);
		Product p9 = new Product(null, "Minecraft Dungeons",
				"Build and explore infinite worlds with blocks in the most popular sandbox game of all time.", 99.99,
				20, true, false, images9, "https://pastebin.com/raw/tEdFr645");
		p9.getCategories().add(cat2); // Adventure
		p9.getCategories().add(cat6); // Platformer
		productRepository.saveAll(Arrays.asList(p9));

		List<String> images10 = new ArrayList<>();
		images10.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1091500/header.jpg?t=1734434803");
		images10.addAll(otherImages);
		Product p10 = new Product(null, "Cyberpunk 2077",
				"Play as V, a mercenary in Night City, searching for the unique implant that guarantees immortality.",
				179.99, 8, true, true, images10, "https://pastebin.com/raw/tEdFr645");
		p10.getCategories().add(cat4); // Adventure
		p10.getCategories().add(cat11); // Open World
		productRepository.saveAll(Arrays.asList(p10));

		List<String> images11 = new ArrayList<>();
		images11.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1593500/header.jpg?t=1729030762");
		images11.addAll(otherImages);
		Product p11 = new Product(null, "God of War",
				"Epic adventure of Kratos and his son Atreus, facing gods and creatures from Norse mythology on a journey for redemption.",
				199.00, 14, true, true, images11, "https://pastebin.com/raw/tEdFr645");
		p11.getCategories().add(cat4); // Adventure
		p11.getCategories().add(cat1); // Action
		productRepository.saveAll(Arrays.asList(p11));

		List<String> images12 = new ArrayList<>();
		images12.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/632360/header.jpg?t=1741113466");
		images12.addAll(otherImages);
		Product p12 = new Product(null, "Risk of Rain 2",
				"Escape a chaotic alien planet by fighting through hordes of frenzied monsters – with your friends, or on your own.",
				179.99, 18, true, true, images12, "https://pastebin.com/raw/tEdFr645");
		p12.getCategories().add(cat2); // Adventure
		p12.getCategories().add(cat6); // Action
		productRepository.saveAll(Arrays.asList(p12));

		List<String> images13 = new ArrayList<>();
		images13.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2379780/header.jpg?t=1741618762");
		images13.addAll(otherImages);
		Product p13 = new Product(null, "Balatro",
				"A hypnotically satisfying deckbuilder where you play illegal poker hands, discover game-changing jokers, and trigger adrenaline-pumping, outrageous combos",
				249.99, 7, true, true, images13, "https://pastebin.com/raw/tEdFr645");
		p13.getCategories().add(cat12);
		p13.getCategories().add(cat10);
		p13.getCategories().add(cat2); // Roguelike
		productRepository.saveAll(Arrays.asList(p13));

		List<String> images14 = new ArrayList<>();
		images14.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1946700/header.jpg?t=1732889537");
		images14.addAll(otherImages);
		Product p14 = new Product(null, "Layers of Fear",
				"This narrative-focused psychological horror experience is ready for its final brushstrokes, its curtain call, its final chapter.",
				299.99, 25, true, true, images14, "https://pastebin.com/raw/tEdFr645");
		p14.getCategories().add(cat9); // Horror
		p14.getCategories().add(cat12); // Puzzle
		productRepository.saveAll(Arrays.asList(p14));

		List<String> images15 = new ArrayList<>();
		images15.add(
				"https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/637650/header.jpg?t=1737963877");
		images15.addAll(otherImages);
		Product p15 = new Product(null, "Final Fantasy XV",
				"Explore the world of Eos as Noctis, prince of a devastated empire, traveling with friends to defeat evil forces.",
				179.99, 10, true, true, images15, "https://pastebin.com/raw/tEdFr645");
		p15.getCategories().add(cat5); // Adventure
		p15.getCategories().add(cat13); // Action
		productRepository.saveAll(Arrays.asList(p15));

		Customer c1 = new Customer(null, "Gabriel", "987654321", "12345678911", "3000", CustomerType.LEGAL_PERSON, null,
				null);
		Customer c2 = new Customer(null, "Luana", "12345678", "12345678912", "2000", CustomerType.NATURAL_PERSON, null,
				null);
		Customer c4 = new Customer(null, "Gabriel23", "9876543211", "12345678901", "6000", CustomerType.LEGAL_PERSON,
				null, new ArrayList<>(Arrays.asList(p1, p2)));

		List<Product> library = new ArrayList<>();
		library.add(p5);
		c1.setLibrary(library);

		Address ad1 = new Address(null, "Rua Doná Joaquina", 201, "Avenida", 37504048, "Brazil", "Itajuba",
				AddressType.HOME_ADDRESS, c1);
		Address ad2 = new Address(null, "Rua Doná Joaquina", 201, "Avenida", 37504048, "Brazil", "Itajuba",
				AddressType.HOME_ADDRESS, c2);

		Coupon coupon = new Coupon(null, "DEZPORCENTO", 0.0, 10.0, true);
		Coupon coupon2 = new Coupon(null, "DEZREAIS", 10.0, 0.0, true);
		Coupon coupon3 = new Coupon(null, "EXPIRED", 10.0, 0.0, false);

		Order o1 = new Order(null, OrderStatus.PAID, c1, ad1, coupon);
		Order o2 = new Order(null, OrderStatus.WAITING_PAYMENT, c2, ad2, coupon2);
		Order o3 = new Order(null, OrderStatus.SHIPPED, c1, ad1, null);

		couponRepository.saveAll(Arrays.asList(coupon, coupon2, coupon3));
		customerRepository.saveAll(Arrays.asList(c1, c2, c4));
		addressRepository.saveAll(Arrays.asList(ad1, ad2));
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));

		OrderItem oi1 = new OrderItem(o1, p1, 2);
		OrderItem oi2 = new OrderItem(o1, p3, 1);
		OrderItem oi3 = new OrderItem(o2, p3, 2);
		OrderItem oi4 = new OrderItem(o3, p3, 2);

		o3.setDeliveryAddress(ad1);
		customerRepository.saveAll(Arrays.asList(c1));

		orderRepository.saveAll(Arrays.asList(o3));

		orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4));

		o1.setTotal(2.0);
		o2.setTotal(o2.getTotal());
		o3.setTotal(o3.getTotal());
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));

	}

}
