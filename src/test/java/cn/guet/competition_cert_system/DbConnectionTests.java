package cn.guet.competition_cert_system;

import cn.guet.competition_cert_system.entity.DaoTest;
import cn.guet.competition_cert_system.mapper.DaoTestMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接测试类
 * 用于测试PostgreSQL和Redis的连接和基本操作
 */
@SpringBootTest
@Slf4j
@DisplayName("数据库连接测试")
public class DbConnectionTests {

    @Autowired
    private DaoTestMapper daoTestMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_TEST_KEY_PREFIX = "db_test:";

    @BeforeEach
    void setUp() {
        log.info("=== 测试开始 ===");
    }

    @Test
    @DisplayName("PostgreSQL连接测试")
    void testPostgresqlConnection() {
        log.info("开始测试PostgreSQL连接...");
        try {
            // 执行一个简单的查询
            List<DaoTest> list = daoTestMapper.selectList(null);
            log.info("✓ PostgreSQL连接成功，查询到 {} 条记录", list.size());
            assertNotNull(list, "查询结果不应为null");
        } catch (Exception e) {
            log.error("✗ PostgreSQL连接失败", e);
            fail("PostgreSQL连接失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Redis连接测试")
    void testRedisConnection() {
        log.info("开始测试Redis连接...");
        try {
            String testKey = REDIS_TEST_KEY_PREFIX + "connection_test";
            String testValue = "Redis连接测试 - " + Instant.now();
            
            // 写入
            redisTemplate.opsForValue().set(testKey, testValue, 30, TimeUnit.SECONDS);
            
            // 读取
            Object value = redisTemplate.opsForValue().get(testKey);
            
            log.info("✓ Redis连接成功，写入值: {}，读取值: {}", testValue, value);
            assertNotNull(value, "Redis读取结果不应为null");
            assertTrue(value.toString().contains("Redis连接测试"), "Redis值应包含测试标记");
        } catch (Exception e) {
            log.error("✗ Redis连接失败", e);
            fail("Redis连接失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("插入测试数据到PostgreSQL")
    void testInsertDaoTest() {
        log.info("开始插入测试数据...");
        try {
            DaoTest daoTest = DaoTest.builder()
                    .testCode("JUNIT_TEST_001")
                    .testName("JUnit测试数据")
                    .testValue(100)
                    .isActive(true)
                    .testType("TYPE_A")
                    .remark("这是一条通过JUnit插入的测试数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(daoTest);
            
            log.info("✓ 数据插入成功，ID: {}", daoTest.getId());
            assertNotNull(daoTest.getId(), "插入后的ID不应为null");
            assertTrue(daoTest.getId() > 0, "插入后的ID应大于0");
        } catch (Exception e) {
            log.error("✗ 数据插入失败", e);
            fail("数据插入失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("查询测试数据")
    void testQueryDaoTest() {
        log.info("开始查询测试数据...");
        try {
            // 先插入一条数据
            DaoTest insertData = DaoTest.builder()
                    .testCode("JUNIT_QUERY_TEST")
                    .testName("查询测试")
                    .testValue(200)
                    .isActive(true)
                    .testType("TYPE_B")
                    .remark("用于查询测试的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(insertData);
            log.info("已插入测试数据，ID: {}", insertData.getId());
            
            // 查询这条数据
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getTestCode, "JUNIT_QUERY_TEST");
            DaoTest queryResult = daoTestMapper.selectOne(queryWrapper);
            
            log.info("✓ 数据查询成功: {}", queryResult);
            assertNotNull(queryResult, "查询结果不应为null");
            assertEquals("JUNIT_QUERY_TEST", queryResult.getTestCode(), "testCode应匹配");
        } catch (Exception e) {
            log.error("✗ 数据查询失败", e);
            fail("数据查询失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("更新测试数据")
    void testUpdateDaoTest() {
        log.info("开始测试数据更新...");
        try {
            // 先插入一条数据
            DaoTest insertData = DaoTest.builder()
                    .testCode("JUNIT_UPDATE_TEST")
                    .testName("更新前的名称")
                    .testValue(300)
                    .isActive(true)
                    .testType("TYPE_C")
                    .remark("用于更新测试的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(insertData);
            log.info("已插入测试数据，ID: {}", insertData.getId());
            
            // 更新数据
            insertData.setTestName("更新后的名称");
            insertData.setTestValue(350);
            insertData.setUpdatedAt(Instant.now());
            daoTestMapper.updateById(insertData);
            
            // 查询验证
            DaoTest queryResult = daoTestMapper.selectById(insertData.getId());
            
            log.info("✓ 数据更新成功，更新后的数据: {}", queryResult);
            assertEquals("更新后的名称", queryResult.getTestName(), "testName应被更新");
            assertEquals(350, queryResult.getTestValue(), "testValue应被更新");
        } catch (Exception e) {
            log.error("✗ 数据更新失败", e);
            fail("数据更新失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("删除测试数据")
    void testDeleteDaoTest() {
        log.info("开始测试数据删除...");
        try {
            // 先插入一条数据
            DaoTest insertData = DaoTest.builder()
                    .testCode("JUNIT_DELETE_TEST")
                    .testName("删除测试")
                    .testValue(400)
                    .isActive(true)
                    .testType("TYPE_A")
                    .remark("用于删除测试的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(insertData);
            log.info("已插入测试数据，ID: {}", insertData.getId());
            
            // 删除数据
            daoTestMapper.deleteById(insertData.getId());
            
            // 查询验证
            DaoTest queryResult = daoTestMapper.selectById(insertData.getId());
            
            log.info("✓ 数据删除成功");
            assertNull(queryResult, "删除后的查询结果应为null");
        } catch (Exception e) {
            log.error("✗ 数据删除失败", e);
            fail("数据删除失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Redis缓存集成测试")
    void testRedisCacheIntegration() {
        log.info("开始测试Redis缓存集成...");
        try {
            // 插入数据到PostgreSQL
            DaoTest daoTest = DaoTest.builder()
                    .testCode("JUNIT_REDIS_TEST")
                    .testName("Redis缓存集成测试")
                    .testValue(500)
                    .isActive(true)
                    .testType("TYPE_B")
                    .remark("用于Redis缓存集成测试的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(daoTest);
            log.info("已插入数据到PostgreSQL，ID: {}", daoTest.getId());
            
            // 存储到Redis缓存
            String cacheKey = REDIS_TEST_KEY_PREFIX + daoTest.getTestCode();
            redisTemplate.opsForValue().set(cacheKey, daoTest, 60, TimeUnit.SECONDS);
            
            // 从Redis读取
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
            
            log.info("✓ Redis缓存集成测试成功，缓存值: {}", cachedValue);
            assertNotNull(cachedValue, "缓存值不应为null");
        } catch (Exception e) {
            log.error("✗ Redis缓存集成测试失败", e);
            fail("Redis缓存集成测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("查询统计测试")
    void testCountByType() {
        log.info("开始测试按类型统计...");
        try {
            // 插入多条测试数据
            for (int i = 0; i < 3; i++) {
                DaoTest daoTest = DaoTest.builder()
                        .testCode("COUNT_TEST_" + i)
                        .testName("统计测试数据" + i)
                        .testValue(i * 100)
                        .isActive(true)
                        .testType("TYPE_A")
                        .remark("用于统计测试的数据")
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                daoTestMapper.insert(daoTest);
            }
            
            // 统计TYPE_A类型的记录
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getTestType, "TYPE_A");
            long count = daoTestMapper.selectCount(queryWrapper);
            
            log.info("✓ 统计测试成功，TYPE_A类型的记录数: {}", count);
            assertTrue(count >= 3, "TYPE_A类型的记录数应至少为3");
        } catch (Exception e) {
            log.error("✗ 统计测试失败", e);
            fail("统计测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("综合连接测试")
    void testComprehensiveConnection() {
        log.info("=== 开始综合连接测试 ===");
        
        try {
            // 测试PostgreSQL连接
            log.info("1. 测试PostgreSQL连接...");
            List<DaoTest> pgList = daoTestMapper.selectList(null);
            assertNotNull(pgList, "PostgreSQL查询失败");
            log.info("   ✓ PostgreSQL连接成功，表中有 {} 条记录", pgList.size());
            
            // 测试Redis连接
            log.info("2. 测试Redis连接...");
            String redisKey = REDIS_TEST_KEY_PREFIX + "comprehensive_test";
            redisTemplate.opsForValue().set(redisKey, "综合测试成功", 10, TimeUnit.SECONDS);
            Object redisValue = redisTemplate.opsForValue().get(redisKey);
            assertNotNull(redisValue, "Redis连接失败");
            log.info("   ✓ Redis连接成功");
            
            // 测试数据插入
            log.info("3. 测试数据插入...");
            DaoTest daoTest = DaoTest.builder()
                    .testCode("COMPREHENSIVE_TEST")
                    .testName("综合测试")
                    .testValue(9999)
                    .isActive(true)
                    .testType("TYPE_A")
                    .remark("综合连接测试")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            daoTestMapper.insert(daoTest);
            log.info("   ✓ 数据插入成功，ID: {}", daoTest.getId());
            
            log.info("=== 综合连接测试全部通过 ===");
            log.info("✓ PostgreSQL: 正常");
            log.info("✓ Redis: 正常");
            log.info("✓ 数据交互: 正常");
            
        } catch (Exception e) {
            log.error("✗ 综合连接测试失败", e);
            fail("综合连接测试失败: " + e.getMessage());
        }
    }
}
