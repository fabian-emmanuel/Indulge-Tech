package com.indulgetech;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

//@Testcontainers
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@AutoConfigureMockMvc
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AbstractTest {
//    private static MySQLContainer<?> container = (MySQLContainer) new MySQLContainer("mysql:latest")
//            .withReuse(true);

    static final MySQLContainer<?> container;

    static {
        container =
                new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
                        .withDatabaseName("indulge_test")
                        .withUsername("duke")
                        .withPassword("s3cret")
                        .withReuse(true);

        container.start();
    }

    @DynamicPropertySource
    private static void overrideProps(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

//    @BeforeAll
//    public static void setUp() {
//        container.start();
//    }

//    protected static final String JDBC_URL = "jdbc.url=";
//    protected static final String JDBC_USERNAME = "jdbc.username=";
//    protected static final String JDBC_PASSWORD = "jdbc.password=";
//    protected static final String JDBC_DRIVER_CLASS_NAME_ORG_POSTGRESQL_DRIVER = "jdbc.driverClassName=org.postgresql.Driver";
//    protected static final String ACTIVE_PROFILE_NAME_TEST = "TestContainerTests";
//
//    //--
//    public static PostgreSQLContainer<?> postgreDBContainer;
//    protected ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//
//    static {
//        // Init DB Script here
//        postgreDBContainer = new PostgreSQLContainer<>(IntegrationTestConstants.POSTGRESQL_IMAGE);
//        postgreDBContainer
//                .withInitScript(IntegrationTestConstants.INIT_DB_SCRIPT)
//                .withDatabaseName(IntegrationTestConstants.DB_NAME)
//                .withUsername(IntegrationTestConstants.DB_USERNAME)
//                .withPassword(IntegrationTestConstants.DB_PASSWORD);
//
//        postgreDBContainer.start();
//        var containerDelegate = new JdbcDatabaseDelegate(postgreDBContainer, "");
//
//        // Adding Database scripts here
//        ScriptUtils.runInitScript(containerDelegate, IntegrationTestConstants.MISSING_TABLES_SQL);
//        ScriptUtils.runInitScript(containerDelegate, IntegrationTestConstants.SAMPLE_DATA_SQL);
//    }
//
//    // This class adds the DB properties to Testcontainers.
//    public static class DockerPostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//
//        @Override
//        public void initialize(ConfigurableApplicationContext applicationContext) {
//
//            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
//                    applicationContext,
//                    JDBC_DRIVER_CLASS_NAME_ORG_POSTGRESQL_DRIVER,
//                    JDBC_URL + postgreDBContainer.getJdbcUrl(),
//                    JDBC_USERNAME + postgreDBContainer.getUsername(),
//                    JDBC_PASSWORD + postgreDBContainer.getPassword()
//            );
//        }
//    }

}
