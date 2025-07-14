package gift.member.domain;

import gift.member.domain.enums.UserRole;

public class Member {

    private Long id;
    private String email;
    private String password;
    private UserRole userRole;

    public Member(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public Member(Long id, String email, String password, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public Member setId(Long id) {
        return new Member(id, email, password, userRole);
    }
}
