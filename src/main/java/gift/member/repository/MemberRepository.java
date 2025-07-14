package gift.member.repository;

import gift.member.domain.Member;
import gift.member.domain.enums.UserRole;
import java.util.Optional;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    private final JdbcClient jdbcClient;

    public MemberRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Member save(Member member) {
        String sql = """
            INSERT INTO member (email, password, user_role)
            VALUES (?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcClient.sql(sql)
            .param(member.getEmail())
            .param(member.getPassword())
            .param(member.getUserRole().name())
            .update(keyHolder);

        Long id = keyHolder.getKey().longValue();

        return member.setId(id);
    }

    public Optional<Member> findByEmail(String email) {
        String sql = """
            SELECT *
            FROM member
            WHERE email = ?
            """;

        return jdbcClient.sql(sql)
            .param(email)
            .query(memberRowMapper())
            .stream()
            .findFirst();
    }

    public Member findById(Long id) {
        String sql = """
            SELECT *
            FROM member
            WHERE id = ?
            """;

        return jdbcClient.sql(sql)
            .param(id)
            .query(memberRowMapper())
            .single();
    }

    public void deleteAll() {
        String sql = "DELETE FROM member";
        jdbcClient.sql(sql).update();
    }

    private RowMapper<Member> memberRowMapper() {
        return ((rs, rowNum) ->
            new Member(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                UserRole.valueOf(rs.getString("user_role"))
            )
        );
    }
}
