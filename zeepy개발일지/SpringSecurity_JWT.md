# 기본적인 Security 동작 플로우

![image](https://user-images.githubusercontent.com/57162257/122236600-9d874a80-cef9-11eb-856b-706406e869b5.png)

1. 사용자가 로그인 정보와 함께 인증 요청(Http Request)
2. `AuthenticationFilter`에서 (1)의 요청을 가로채고 가로챈 정보를 통해 `UsernamePasswordAuthentication Token`으로 **인증 객체**(Authentication)를 생성
3. `AuthenticationFilter`에서 인증 객체를 `AuthenticationManager`에게 보낸다
   - `AuthenticationManager`에게 보내지지만 실제로는 `AuthenticationManager`의 구현체인 `ProviderManager`에게 보내져 `ProviderManager`가 인증 객체를 인증한다.
4. `ProviderManager`에서는 직접 인증을 해주는게 아니다. 다양한 인증을 해줄 멤버변수인 Provider들이 있는데, 그 중에 하나인 `AuthenticationProvider`객체가 인증 객체를 받아서 인증을한다.
5. `AuthenticationProvider`는 실제 데이터베이스를 통해 사용자 정보를 가져와 주는 `UserDetailService`에 사용자 정보를 넘겨받는다.
6. 넘겨 받은 사용자 정보를 통해 DB에서 찾은 사용자 정보인 `UserDetails`객체를 만든다.(저는 **UserDetails를 도메인영 객체에 상속**받아서 구현했습니다.)
7. `AuthenticationProvider`에게 `UserDetails`를 넘겨주고 사용자 정보를 비교한다.
8. 인증이 완료되면 인증이 된 사용자 정보를 인증 객체에 담아서 인증 객체를 반환한다.
9. `ProviderManager`에서 인증이 완료됬다고 알려주면 `AuthenticationManager`가 `isAuthneticated`값을 true로 변경해주면서 인증된 인증 객체를 반환해준다.
10. 인증객체를 `SecurityContext`에 저장한다.
    - SecurityContextHolder는 세션 영역에 있는 SecurityContext에 Authentication객체를 저장하는데, 세션영역에 Authentication객체를 저장한다는 것은 스프링 시큐리티가 세션-쿠키 기반의 인증 방식을 사용한다는 것을 의미한다(저는 JWT를 사용할꺼기 떄문에 저장하지 않았습니다.)

# Dependency

`implementation 'org.springframework.boot:spring-boot-starter-security'`

`implementation 'io.jsonwebtoken:jjwt:0.9.1'`

# Config

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private CustomAuthenticationProvider authProvider;
	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		http	//1
			.httpBasic().disable()	//1-1
			.csrf().disable()	//1-2	//2
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)	//1-3
			.and()
			.headers().frameOptions().disable()	//2
			.and()
			.authorizeRequests()	//1-4
			.antMatchers("/h2-console/*").permitAll()	//2
			.antMatchers("/api/user").permitAll()	//1-5
			.anyRequest().permitAll()	//1-6
			.and()
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class);	//1-7
	}

	@Override
	protected  void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authProvider);	//3
	}

	@Bean
	public PasswordEncoder passwordEncoder(){	//4
		return new BCryptPasswordEncoder();
	}
```

1. http : SpringSecurity를 사용하면서 url과 관련된 설정

   1. `httpBasic().disable()` : rest api만을 고려해서 기본 설정 해제

   2. `crsf().disalbe()` : csrf보안 토큰 해제

   3. `sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)` : 토큰 기반 인증이므로 세션 사용 해제

   4. `authorizeRequests()` : 요청에 대한 사용권한 체크

   5. `antMatchers("/api/user").permitAll()` : 로그인 및 회원가입을 요청하는 공통 url이 `/api/user` 이므로 해당 기능을 수행하기 위해 접근 허용 

   6. `anyRequest().permitAll()` : 그 외 나머지 요청은 누구나 접근 가능(**필요에 따라 접근 여부 설정해주면됨**)

   7. **`addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),UsernamePasswordAuthenticationFilter.class)`** : AuthenticationFilter가 HttpRequest를 가로채고 AuthenticationFilter에 존재하는 여러 필터중 UsernamePasswordAuthenticationFilter가 실행되면
      ![image](https://user-images.githubusercontent.com/57162257/122248059-c4964a00-cf02-11eb-80a9-a46598cdafbe.png)

      이런 창이 나오게 된다.
      하지만 RestAPI 서버를 사용할때는 필요없는 기능이기 때문에 이 창을 띄워주는 UsernamePasswordAuthenticationFilter보다 먼저 사용ㄷ할 필요에 맞게 사용할 커스텀Filter를 만들어 줄꺼기 때문에 먼저 사용할꺼라 설정.

2. H2 사용하기 위한 설정

   - `csrf().disable()`
   - `headers().frameOptions().disable()` : X-Frame_Options in Spring Secuirty중지
   - `antMatchers("/h2-console/*").permitAll()` : /h2-console로 오는 요청은 접근 허용

3. `AuthenticationManger`를 생성하기 위해 `AuthenticationManagerBuilder`로 AuthenticationManger생성

4. 암호 인코딩, 디코딩을 위한 security에서 제공하는 `BCryptPasswordEncoder` 빈 등록



# JwtAuthenticationFilter

```java
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,ServletException {
		String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);//1
		if(token != null && jwtTokenProvider.validateToken(token)){//2
			Authentication auth = jwtTokenProvider.getAuthentication(token); //3
		}
		chain.doFilter(request, response);//4
	}
}
```

AuthenticationFilter가 HttpRequest요청을 가로채 왔을때 실행해줄 토큰을 이용한 커스텀 Filter

1. 헤더에 들어있는 token을 가져온다
2. token의 유무와 token의 유효성검사, 만료시간 체크
3. 토큰 인증이 완료되면 토큰의 정보로 인증
4. FilterChain
   ![image](https://user-images.githubusercontent.com/57162257/122250515-b0534c80-cf04-11eb-9579-111198385197.png)
   Filter는 클라이언트가 요청하는 정보를 변경할수 있는 서블릿 컨테이너들인데, 예를 들어 두번째 있는 Filter가 변경하는 정보는 맨 처음에 요청된 클라이언트의 요청이 아닌 앞에 있는 첫번째 Filter에서 변경된 정보를 변경하게 되는 것인데, Filter들이 묶여있다고 해서 FilterChain이라고 한다.
   - `destroy()` : 필터가 웹 컨테이너에서 삭제될 떄 호출
   - **`doFilter(ServletRequest request, ServletResponse response, FilterChain chain)`** : 이 메소드를 통해 요청과 응답 쌍이 체인을 통과 할때마다 컨테이너에서 호출된다. 즉, 체인을 따라서 다음에 존재하는 필터로 이동하는것
   - `init()` : 필터를 웹 컨테이너 내에 생성한 후 초기화할떄 호출



# JwtTokenProvider

```java
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
	private String secretKey = "apple";
	private final long tokenValidTime = 1000L * 60 *60 * 10;//10시간

	@Autowired
	private UserDetailsService userDetailsService; //1

	@PostConstruct
	protected void init(){
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes()); //2
	}

	//JWT토큰 생성
	public String createToken(String userPk){//3
		Claims claims = Jwts.claims().setSubject(userPk);//3-1
		Date now = new Date();
		return Jwts.builder()
			.setClaims(claims)//3-2
			.setIssuedAt(now)//3-3
			.setExpiration(new Date(now.getTime() + tokenValidTime))//3-4
			.signWith(SignatureAlgorithm.HS256,secretKey)//3-5
			.compact();//3-6
	}

    //토큰 인증
	public Authentication getAuthentication(String token){//4
		UserDetails userDetails = customDetailsService.loadUserByUsername(this.getUserPk(token));//4-1
		return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());//4-2
	}

	//토큰에서 회원 정보 추출
	public String getUserPk(String token){//5
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();//5-1
	}

	//Request의 Header에서 token을 가져옴, "X-AUTH-TOKEN"
	public String resolveToken(HttpServletRequest request){//6
		return request.getHeader("X-AUTH-TOKEN");
	}

	//토큰의 유효성 + 만료일자 확인
	public boolean validateToken(String jwtToken){//7
		try{
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		}catch (Exception e){
			return false;
		}
	}
}
```

1. `AuthenticationProvider`인 `JwtTokenProvider`에서 사용할 커스텀한 `UserDetailsService`를 DI
2. JWT에서 사용할 비밀키 인코딩
3. 로그인 할때 성공시 JWT생성
   1. 토큰의 사용자 정보를 claims의 subject에 저장
   2. JWT에 사용자 정보를 담은 claims 저장
   3. 토큰 생성시간
   4. 토큰 유효시간 설정
   5. 토큰 비밀키 설정
   6. 토큰 생성
4. 로그인이 아닌 사용자 인증을 할때 토큰인증 메소드
   1. customDetailsService에서 DB를 통해 사용자 정보 비교
   2. 동일하다면 인증된 인증 객체(Authentcation)를 반환
5. 토큰에서 사용자 정보 추출
   1. 토큰에서 사용자 정보가 들어있는 claims의 subject에서 정보를 가져와 반환
6. AuthenticationFilter를 커스텀한 `JwtTokenProvider`에서 가로채 가져온 요청 정보의 Header에 있는 `X-AUTH-TOKEN`에서 토큰을 가져와 인증한다.
7. 토큰의 유효성검사를 할떄 현재시간이 유효시간을 지났다면 유효하지 못한 토큰이기 때문에 false반환, 그렇지 않다면 true반환



# CustomDetailsService

```java
@RequiredArgsConstructor
@Service
public class CustomDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).orElseThrow(()-> new NotFoundUser(email));//1
	}
}
```

1. `JwtTokenProvider`에서 받은 토큰의 정보가 DB에 저장되어있는 정보인지 비교해서 반환



# User

```java
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_gen")
	@SequenceGenerator(name = "user_sequence_gen", sequenceName = "user_sequence")
	private Long id;

	private String name;

	private String email;

	private String password;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles = new ArrayList<>();

	@Builder
	public User(Long id, String name, String email, String password){
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	@Override
	public String getUsername() {//1
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {//2
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {//3
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {//4
		return true;
	}

	@Override
	public boolean isEnabled() {//5
		return true;
	}
}
```

SpringSecurity는 `UserDetails`객체를 통해 권한 정보를 관리하기 때문에 `User`클래스에 `UserDetails`를 구현하고 추가 정보를 재정의 해야한다.

1. getUsername을 통해 SpringSecurity에서 사용하는 username을 가져가는데, 나는 username을 email로 사용했다.
2. isAccountNotExpired : 계정이 만료되지 않았는지 리턴(true : 만료안됨)
3. isAccountNoLocked : 계정이 잠겨있지 않았는지 리턴(true : 잠겨있지 않음)
4. isCredentialNonExpired : 비밀번호가 만료되지 않았는지 리턴(true : 만료안됨)
5. isEnabled : 계정이 활성화(사용가능)한지 리턴(true : 활성화)



# UserService

```java
@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final PasswordEncoder passwordEncoder;

	public void registration(RegistrationReqDto registrationReqDto) {
		userRepository.save(registrationReqDto.toEntity());
	}

	public String login(LoginReqDto loginReqDto) {
		User user = userRepository.findByEmail(loginReqDto.getEmail())
			.orElseThrow(()-> new NotFoundUser("가입되지 않은 이메일입니다"));
		if(!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())){
			throw new IllegalStateException("잘못된 비밀번호");
		}
		return jwtTokenProvider.createToken(user.getEmail());//1
	}
}
```

1. 로그인을 했을때 토큰이 존재하지 않아 `AuthenticationFilter`를 지나쳤고 사용자 정보 유무를 확인한후 인증 되었다면 `jwtTokenProvider`에서 토큰을 생성해줌.
   그렇다면 다른 요청을 했을때 `X-AUTH-TOKEN`에 로그인 성공후 보내준 토큰을 넣어서 인증을 하면 된다.

