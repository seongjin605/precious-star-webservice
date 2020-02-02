package com.precious.star.web.domain.posts;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanUp() {
        postsRepository.deleteAll();
    }

    @Test
    public void savePostAndFindOne() {
        String title = "Test Post";
        String content = "Test Content";

        postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author(("test123@naver.com"))
                .build());

        List<Posts> postsList = postsRepository.findAll();

        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    @Test
    public void saveBaseTimeEntity() {
        LocalDateTime now = LocalDateTime.of(2020, 2, 2, 0, 0, 0);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("contents")
                .author("author")
                .build());

        List<Posts> posts = postsRepository.findAll();

        Posts post = posts.get(0);

        System.out.println("createdDate=" + post.getCreatedDate());
        System.out.println("modifiedDate=" + post.getCreatedDate());

        assertThat("createdDate=" + post.getCreatedDate().isAfter(now));
        assertThat("modifiedDate=" + post.getModifiedDate().isAfter(now));
    }
}
