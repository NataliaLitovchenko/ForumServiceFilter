package telran.ashkelon2018.forum.dto;

import java.util.Set;

import lombok.Getter;

@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
public class NewPostDto {
	String title;
	String content;
	String author;
	Set<String> tags;
}
