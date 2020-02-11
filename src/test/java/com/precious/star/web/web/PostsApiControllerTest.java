package com.precious.star.web.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.precious.star.web.domain.posts.Posts;
import com.precious.star.web.domain.posts.PostsRepository;
import com.precious.star.web.dto.PostsSaveRequestDto;
import com.precious.star.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
    .apply(springSecurity())
    .build();
    }

    @After
    public void tearDown() throws Exception {
        postsRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void savePost() throws Exception {
        String title = "title";
        String content = "content";

        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("psj")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(postsSaveRequestDto)))
                .andExpect(status().isOk());


        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);

    }

    @Test
    @WithMockUser(roles="USER")
    public void updatePost() throws Exception {
        Posts savedPosts = postsRepository.save(Posts.builder()
                                                            .title("title")
                                                            .content("content")
                                                            .author("author")
                                                            .build());

        Long willUpdateId = savedPosts.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                                                                        .title(expectedTitle)
                                                                        .content(expectedContent)
                                                                        .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + willUpdateId;

        mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(all.get(0).getContent()).isEqualTo(requestDto.getContent());

    }
}
