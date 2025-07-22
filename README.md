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

### Step2

- 페이지네이션을 사용하여 상품 목록을 조회할 수 있도록 하기

  이를 구현하기 위해서 우선 QueryParameter를 받을 PageFindRequest라는 이름의 record 클래스를 선언하였습니다.

    ```java
    public record PageFindRequest(int page, int size, Sort.Direction direction, String criteria) {
    
    }
  
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findPage(PageFindRequest pageFindRequest) {
      ...
    ```

- 기존 Product 삭제 시 wishlist repository의 delete 메서드를 직접 호출하던 방식에서 Spring Event를 사용하는 방식으로 리팩토링 하였습니다.

    - EventPublisher 클래스를 만들어서, 특정 이벤트가 발생할 시 발행을 담당하도록 하였습니다.
    - Product를 삭제하여 발생하는 이벤트는 ProductDeleteEvent라는 record 클래스로 표현을 하였습니다.
    - 기존 wishlistRepository.deleteByProductId를 호출하여 삭제하는 코드를 eventPublisher로 이벤트를 발행하는 코드로 변경하였습니다.

  ```java    
  @Transactional
  public void delete(Long id) {
      eventPublisher.publish(ProductDeleteEvent.of(id));
      productRepository.deleteById(id);
  }
  ```

    - wishlistService에는 `@EventListener` 어노테이션을 추가하여 해당 이벤트 발행시 동기적으로 처리할 수 있도록 하였습니다.
  ```java
  @EventListener
  @Transactional
  public void handleDeleteEvent(ProductDeleteEvent event) {
      wishlistRepository.deleteByProductId(event.id());
  }
  ```

    - WishlistcontrollerTest에 해당 부분이 정상적으로 작동하는 지에 대한 테스트 코드를 추가하였습니다.

### Step3

#### 수정사항

- 코멘트를 주신대로 EventPublisher가 불필요한 래핑을 하고 있기 때문에 클래스를 삭제하고, ProductService에 ApplicationEventPublisher를
  직접 주입받는 방식으로 수정하였습니다.
- Wishlist 엔티티에서 참조하는 필드에 대한 fetch 전략을 EAGER 전략으로 수정하였습니다.
- 데이터가 이미 존재할 때 발생하는 오류가 엔티티가 늘어나면서 그에 따라 새로 생기는 것을 막기 위해 이를 분리하는 대신 하나로 통합하여 사용하도록 하였습니다.

#### 옵션 CRUD 기능 구현

##### 옵션 테이블

  ```java

@Entity
@Table(name = "option")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
  ```

옵션의 id, 이름, 수량을 필드로 놓고 상품과의 연관 관계를 생각하여 `@ManyToOne` 어노테이션으로 다대일 매핑을 하도록 하였습니다.

##### 옵션 추가 기능

  ```java
  Optional<Option> option =
    optionRepository.findByNameAndProductId(optionAddRequest.name(), productId);

  if(option.

isPresent()){
    throw

newAlreadyExistsException("이미 존재하는 옵션명입니다.");
  }
  ```

요청으로 들어온 옵션의 이름과 상품의 id를 함께 조회했을때, 해당 상품에 동일한 옵션명이 이미 존재하는 경우 예외를 던지도록 하였습니다.

  ```java
  private Product findProduct(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new FailedToFindException("존재하지 않는 상품입니다."));
}
  ```

CRUD 메서드에서 공통적으로 productId가 유효한지 먼저 검증을 한 후에 로직을 수행하므로 공통 메서드로 productId의 유효성을 확인하고 유효하지 않다면 예외를
반환하는 코드를 작성하였습니다.

##### 옵션 조회 기능

옵션 추가 기능과 마찬가지로 productId가 유효한지 확인하고, List로 Option들을 가져와 반환하도록 작성하였습니다.

##### 옵션 수정 기능

  ```java
  findProduct(productId);

Option option =
    optionRepository.findById(optionId)
        .orElseThrow(() -> new FailedToFindException("존재하지 않는 옵션입니다."));

  option.

update(optionUpdateRequest.name());

    return

convertToDTO(optionRepository.save(option));
  ```

마찬가지로 productId가 유효한지 확인하고, optionId 또한 유효한지 확인하였습니다. 반환받은 Option을 엔티티에 작성해둔 update 메서드를 통해 변경하고
repository에 저장하는 방식으로 코드를 작성했습니다.

  ```java
  public Option subQuantity(Long quantity) {
    this.quantity = this.quantity - quantity;

    return this;
}

public void update(String name) {
    this.name = name;
}
  ```

과제 요구사항 중, 입력받은 수 만큼 수량을 감소시키는 로직을 작성하되, 이에 대한 Http API는 구현하지 않고 나중에 활용한다고 하였습니다.
그렇기 때문에 update 메서드는 단순 name만을 변경하는 식으로 작성했습니다.

update request의 DTO가 따로 추가되면서, 중복되는 옵션명 검증 로직은 따로 어노테이션(`@OptionNameValidation`)을 만들어 적용하였습니다.

##### 옵션 삭제 기능
  ```java
  int size = optionRepository.findByProductId(productId).size();

  if (size <= 1) {
      throw new FailedToDeleteException("상품은 최소 하나의 옵션을 가지고 있어야 합니다.");
  }

  Product product = findProduct(productId);

  if (!productId.equals(product.getId())) {
      throw new FailedToDeleteException("해당하는 상품의 옵션이 아닙니다.");
  }

  optionRepository.deleteById(optionId);
  ```
요구사항 중 상품은 최소 하나의 옵션을 가져야한다고 하였으므로, 1개 이하의 옵션을 가진 경우 옵션의 삭제를 막는 코드를 작성하였습니다.

#### 옵션 기능에 대한 테스트 코드

HTTP API에 대한 테스트를 하기 위해 테스트 코드를 작성하였습니다.