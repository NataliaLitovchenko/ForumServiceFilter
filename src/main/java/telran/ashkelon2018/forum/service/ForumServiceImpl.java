package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.ForumRepository;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.Comment;
import telran.ashkelon2018.forum.domain.Post;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.DatePeriodDto;
import telran.ashkelon2018.forum.dto.NewCommentDto;
import telran.ashkelon2018.forum.dto.NewPostDto;
import telran.ashkelon2018.forum.dto.PostUpdateDto;
import telran.ashkelon2018.forum.exceptions.UserSecurityException;

@Service
@ManagedResource
public class ForumServiceImpl implements ForumService {
	@Autowired
	ForumRepository forumRepository;

	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public Post addNewPost(NewPostDto newPost, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		Post post = new Post(newPost.getTitle(), newPost.getContent(), credentials.getLogin(), newPost.getTags());
		return forumRepository.save(post);
	}

	@Override
	public Post getPost(String id) {
		return forumRepository.findById(id).orElse(null);
	}

	@Override
	public Post removePost(String id, String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount user = userRepository.findById(credentials.getLogin()).get();
		Post post = forumRepository.findById(id).orElse(null);
		if (post != null) {
			boolean filter1 = !(user.getRoles().contains("Moderator") || user.getRoles().contains("Admin"));
			boolean filter2 = !post.getAuthor().equals(credentials.getLogin());

			if (filter1) {
				if (filter2) {
					throw new UserSecurityException();
				}

			}
		}

		forumRepository.deleteById(id);
		return post;
	}

	@Override
	public Post updatePost(PostUpdateDto postUpdateDto, String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		
		Post post = forumRepository.findById(postUpdateDto.getId()).orElse(null);
		if (!post.getAuthor().equals(credentials.getLogin())) {
			throw new UserSecurityException();
		}
		if (post != null) {
			if (postUpdateDto.getTitle() != null && !postUpdateDto.getTitle().equals("")) {
				post.setTitle(postUpdateDto.getTitle());
			}
			if (postUpdateDto.getContent() != null && !postUpdateDto.getContent().equals("")) {
				post.setContent(postUpdateDto.getContent());
			}
			if (postUpdateDto.getTags() != null) {
				post.setTags(postUpdateDto.getTags());
			}
		}
		forumRepository.save(post);
		return post;
	}

	@Override
	public boolean addLike(String id, String token) {
		Post post = forumRepository.findById(id).orElse(null);
		if (post == null) {
			return false;
		}
		post.addLike();
		forumRepository.save(post);
		return true;
	}

	@Override
	public Post addComment(String id, NewCommentDto newComment, String token) {
		Post post = forumRepository.findById(id).orElse(null);
		if (post != null) {
			AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
			post.addComment(new Comment(credentials.getLogin(), newComment.getMessage()));
			forumRepository.save(post);
		}
		return post;
	}

	@Override
	public Iterable<Post> findPostByTags(List<String> tags) {
		return forumRepository.findPostByTagsIn(tags);
	}

	@Override
	public Iterable<Post> findPostByAuthor(String author) {
		return forumRepository.findPostByAuthor(author);
	}

	@Override
	public Iterable<Post> findPostByDateCreated(DatePeriodDto datesDto) {
		return forumRepository.findPostByDateCreatedBetween(LocalDateTime.of(datesDto.getFromDate(), LocalTime.MIN),
				LocalDateTime.of(datesDto.getToDate(), LocalTime.MIN));
	}

}
