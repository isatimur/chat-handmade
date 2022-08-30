package com.timurisachenko.chat.chatservice.config.dbmigrations;

import com.timurisachenko.chat.chatservice.model.Authority;
import com.timurisachenko.chat.chatservice.model.Profile;
import com.timurisachenko.chat.chatservice.model.Role;
import com.timurisachenko.chat.chatservice.model.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.Set;

/**
 * Creates the initial database setup.
 */
@ChangeUnit(id = "users-initialization", order = "001")
public class InitialSetupMigration {

    private final MongoTemplate template;

    public InitialSetupMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        Authority userAuthority = createUserAuthority();
        userAuthority = template.save(userAuthority);
        Authority adminAuthority = createAdminAuthority();
        adminAuthority = template.save(adminAuthority);
        addUsers(userAuthority, adminAuthority);
    }

    @RollbackExecution
    public void rollback() {}

    private Authority createAuthority(Role authority) {
        Authority adminAuthority = new Authority();
        adminAuthority.setName(authority.getName());
        return adminAuthority;
    }

    private Authority createAdminAuthority() {
        Authority adminAuthority = createAuthority(Role.ADMIN);
        return adminAuthority;
    }

    private Authority createUserAuthority() {
        Authority userAuthority = createAuthority(Role.USER);
        return userAuthority;
    }

    private void addUsers(Authority userAuthority, Authority adminAuthority) {
        User user = createUser(userAuthority);
        template.save(user);
        User admin = createAdmin(adminAuthority, userAuthority);
        template.save(admin);
    }

    private User createUser(Authority userAuthority) {
        User userUser = new User();
        userUser.setId("user-2");
        userUser.setUsername("user");
        userUser.setPassword("$2a$10$VEjxo0jq2YG9Rbk2HmX9S.k1uZBGYUHdUcid3g/vfiEl7lwWgOH/K");
        Profile profile = new Profile();
        profile.setDisplayName("User User");
        userUser.setEmail("user@localhost");
        userUser.setActive(true);
        userUser.setCreatedAt(Instant.now());
        userUser.setRoles(Set.of(Role.USER));
        userUser.setUserProfile(profile);
        return userUser;
    }

    private User createAdmin(Authority adminAuthority, Authority userAuthority) {
        User adminUser = new User();
        adminUser.setId("user-1");
        adminUser.setUsername("admin");
        adminUser.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
        Profile profile = new Profile();
        profile.setDisplayName("admin Administrator");
        adminUser.setEmail("admin@localhost");
        adminUser.setActive(true);
        adminUser.setCreatedAt(Instant.now());
        adminUser.setRoles(Set.of(Role.USER, Role.ADMIN));
        return adminUser;
    }
}
