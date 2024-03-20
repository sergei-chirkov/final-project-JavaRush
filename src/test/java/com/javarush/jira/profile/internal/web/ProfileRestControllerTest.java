package com.javarush.jira.profile.internal.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.profile.internal.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.login.internal.web.UserTestData.*;
import static com.javarush.jira.profile.internal.web.ProfileTestData.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class ProfileRestControllerTest extends AbstractControllerTest {
    private static final String PROFILE_URL = ProfileRestController.REST_URL;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void getSuccess() throws Exception {
        perform(MockMvcRequestBuilders.get(PROFILE_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(admin.id()));


    }

    @Test
    void getUnauthorize() throws Exception {
        perform(MockMvcRequestBuilders.get(PROFILE_URL))
                .andExpect(status().isUnauthorized())
                .andDo(print());

    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(PROFILE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getUpdatedTo())))
                .andDo(print());
        PROFILE_MATCHER.assertMatch(profileRepository.getOrCreate(user.id()),getUpdated(user.id()));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateNew() throws Exception {
        perform(MockMvcRequestBuilders.put(PROFILE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getNewTo())))
                .andDo(print());
        PROFILE_MATCHER.assertMatch(profileRepository.getOrCreate(user.id()),getNew(user.id()));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateUnprocessable() throws Exception {
        perform(MockMvcRequestBuilders.put(PROFILE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getInvalidTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateUnknownNotification() throws Exception {
        perform(MockMvcRequestBuilders.put(PROFILE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getWithUnknownNotificationTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateUnknownContact() throws Exception {
        perform(MockMvcRequestBuilders.put(PROFILE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getWithUnknownContactTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateContactHtmlUnsafe() throws Exception {
        perform(MockMvcRequestBuilders.put(PROFILE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(getWithContactHtmlUnsafeTo())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}