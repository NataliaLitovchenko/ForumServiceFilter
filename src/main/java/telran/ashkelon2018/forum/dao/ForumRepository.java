package telran.ashkelon2018.forum.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import telran.ashkelon2018.forum.domain.Post;

public interface ForumRepository extends MongoRepository<Post, String> {
//	@Query("{'tags':{'$in':?0}}")  
//	Iterable<Post> findPostByTags(List<String> tags);
	Iterable<Post> findPostByTagsIn(List<String> tags);

	Iterable<Post> findPostByAuthor(String author);

	Iterable<Post> findPostByDateCreatedBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
