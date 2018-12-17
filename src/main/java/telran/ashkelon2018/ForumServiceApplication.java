package telran.ashkelon2018;

import java.time.LocalDateTime;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.service.ForumService;

@SpringBootApplication
public class ForumServiceApplication implements CommandLineRunner {
	@Autowired
	ForumService forumService;
	
	@Autowired
	UserAccountRepository repository;

	public static void main(String[] args) {
		SpringApplication.run(ForumServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (!repository.existsById("Admin")) {
			String hashPassword = BCrypt.hashpw("admin", BCrypt.gensalt());
			UserAccount userAccount = UserAccount.builder()
					.login("Admin")
					.password(hashPassword)
					.firstName("Super")
					.lastName("Admin")
					.expdate(LocalDateTime.now().plusYears(25))
					.role("Admin")
					.build();
			repository.save(userAccount);
		}
		
		
		
		
//		Set<String> tags1 = new HashSet<>();
//		Set<String> tags2 = new HashSet<>();
//		Set<String> tags3 = new HashSet<>();
//		tags1.addAll(Arrays.asList("#java", "#spring", "#jackson"));
//		tags2.addAll(Arrays.asList("#python", "#bigdata", "#nosql"));
//		tags3.addAll(Arrays.asList("#webstorm", "#css", "#frontend"));
//
//		NewPostDto[] newposts = {
//				new NewPostDto("Backend Programming", "Java is most powerfull backend language", "Oracle fans", tags1),
//				new NewPostDto("This language for bigdata", "Talks about python, bigdata", "Peter", tags2),
//				new NewPostDto("Frontend", "All around frontend", "John", tags3) };
//
//		for (int i = 0; i < newposts.length; i++) {
//			forumService.addNewPost(newposts[i]);
//		}

	}
}
