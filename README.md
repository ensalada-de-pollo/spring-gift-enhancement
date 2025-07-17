# spring-gift-enhancement

### Step1

- 커스텀 예외 FailedToFindException를 추가하여 존재하지 않는 데이터에 대한 조회에 예외를 응답으로 반환하도록 하였습니다.

  ```java
  // WishlistService#addWish
  Product product =
      productRepository.findById(productId)
          .orElseThrow(() -> new FailedToFindException("존재하지 않는 상품입니다."));

  Member member =
      memberRepository.findById(memberId)
           .orElseThrow(() -> new FailedToFindException("존재하지 않는 회원입니다."));
  ```

- 기존 JDBC Client로 구현되어 있던 내용을 전부 JPA로 리팩토링하였습니다.

    - 과제 가이드에 나와있는 테이블 구성대로 nullable 여부, unique여부, PK 생성 전략, 최대 길이 등을 설정해주었습니다.
  ```java
  @Entity
  @Table(name = "member")
  public class Member {
    
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @Column(unique = true, nullable = false)
      private String email;

      @Column(nullable = false)
      private String password;

      @Enumerated(EnumType.STRING)
      @Column(length = 10, nullable = false)
      private UserRole userRole;
  ```

    - wishlist 테이블의 경우, productId와 memberId를 참조하고 있기 때문에 일대다 형태로, 지연 전략을 사용하여 매핑을 해주었습니다.
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;
  ```

- 각 Repository에 대한 Test 코드를 작성하여, Repository 메서드들의 작동 방식과 DB 연동을 테스트 하도록 했습니다.

#### step1 수정

- GlobalExceptionHandler를 비롯한 MemberController, ProductController, WishlistController의 메서드 선언 부분의 개행
  방식을 Google Style Guide에 맞게 수정하였습니다.
- 잘못 설정했던 WishlistRepository의 PK 타입을 Long으로 알맞게 수정하였습니다.
- 각 엔티티 클래스 디폴트 생성자는 JPA 이외에 사용처가 없기 때문에 접근제어자를 protected로 수정하였습니다.
- RepositoryTest의 테스트 메서드 이름을 테스트 용도를 파악할 수 있도록 수정하여 가독성을 개선하였습니다.