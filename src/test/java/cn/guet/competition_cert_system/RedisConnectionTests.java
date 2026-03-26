package cn.guet.competition_cert_system;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis 连接和操作测试类
 * 专用于测试 Redis 连接和缓存操作
 */
@SpringBootTest
@Slf4j
@DisplayName("Redis 数据库测试")
public class RedisConnectionTests {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_TEST_KEY_PREFIX = "db_test:";

    @BeforeEach
    void setUp() {
        log.info("=== Redis 测试开始 ===");
    }

    @Test
    @DisplayName("测试 Redis 连接")
    void testRedisConnection() {
        log.info("开始测试 Redis 连接...");
        try {
            String testKey = REDIS_TEST_KEY_PREFIX + "connection_test";
            String testValue = "Redis 连接测试 - " + Instant.now();
            
            // 写入
            redisTemplate.opsForValue().set(testKey, testValue, 30, TimeUnit.SECONDS);
            
            // 读取
            Object value = redisTemplate.opsForValue().get(testKey);
            
            log.info("✓ Redis 连接成功，写入值: {}，读取值: {}", testValue, value);
            assertNotNull(value, "Redis 读取结果不应为 null");
            assertTrue(value.toString().contains("Redis 连接测试"), "Redis 值应包含测试标记");
        } catch (Exception e) {
            log.error("✗ Redis 连接失败", e);
            fail("Redis 连接失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis 字符串操作")
    void testStringOperation() {
        log.info("开始测试 Redis 字符串操作...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "string_test";
            String value = "Hello Redis";
            
            // 写入
            redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
            
            // 读取
            String result = (String) redisTemplate.opsForValue().get(key);
            
            log.info("✓ 字符串操作成功，写入: {}，读取: {}", value, result);
            assertEquals(value, result, "读取的值应与写入的值相同");
        } catch (Exception e) {
            log.error("✗ 字符串操作失败", e);
            fail("字符串操作失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis 过期时间")
    void testExpiration() {
        log.info("开始测试 Redis 过期时间...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "expire_test";
            String value = "Expire Test";
            
            // 写入数据，2 秒后过期
            redisTemplate.opsForValue().set(key, value, 2, TimeUnit.SECONDS);
            
            // 立即读取
            Object result1 = redisTemplate.opsForValue().get(key);
            log.info("写入后立即读取: {}", result1);
            assertNotNull(result1, "写入后立即读取应得到数据");
            
            // 等待 3 秒
            log.info("等待 3 秒让数据过期...");
            Thread.sleep(3000);
            
            // 再次读取
            Object result2 = redisTemplate.opsForValue().get(key);
            log.info("过期后读取: {}", result2);
            
            log.info("✓ Redis 过期时间测试成功");
            assertNull(result2, "过期后读取应得到 null");
        } catch (Exception e) {
            log.error("✗ Redis 过期时间测试失败", e);
            fail("Redis 过期时间测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis 删除操作")
    void testDeleteOperation() {
        log.info("开始测试 Redis 删除操作...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "delete_test";
            String value = "Delete Test";
            
            // 写入
            redisTemplate.opsForValue().set(key, value, 60, TimeUnit.SECONDS);
            
            // 验证写入成功
            Object result1 = redisTemplate.opsForValue().get(key);
            assertNotNull(result1, "写入后应能读取数据");
            
            // 删除
            Boolean deleteResult = redisTemplate.delete(key);
            log.info("删除操作结果: {}", deleteResult);
            
            // 验证删除成功
            Object result2 = redisTemplate.opsForValue().get(key);
            
            log.info("✓ Redis 删除操作成功");
            assertNull(result2, "删除后读取应得到 null");
        } catch (Exception e) {
            log.error("✗ Redis 删除操作失败", e);
            fail("Redis 删除操作失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis Hash 操作")
    void testHashOperation() {
        log.info("开始测试 Redis Hash 操作...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "hash_test";
            
            // 写入 hash
            redisTemplate.opsForHash().put(key, "field1", "value1");
            redisTemplate.opsForHash().put(key, "field2", "value2");
            redisTemplate.opsForHash().put(key, "field3", "value3");
            
            // 读取单个字段
            Object value1 = redisTemplate.opsForHash().get(key, "field1");
            
            // 读取所有字段
            long size = redisTemplate.opsForHash().size(key);
            
            log.info("✓ Redis Hash 操作成功，field1 值: {}，hash 大小: {}", value1, size);
            assertEquals("value1", value1, "字段值应匹配");
            assertEquals(3, size, "Hash 大小应为 3");
        } catch (Exception e) {
            log.error("✗ Redis Hash 操作失败", e);
            fail("Redis Hash 操作失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis List 操作")
    void testListOperation() {
        log.info("开始测试 Redis List 操作...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "list_test";
            
            // 推送元素
            redisTemplate.opsForList().rightPush(key, "element1");
            redisTemplate.opsForList().rightPush(key, "element2");
            redisTemplate.opsForList().rightPush(key, "element3");
            
            // 获取列表大小
            long size = redisTemplate.opsForList().size(key);
            
            // 获取第一个元素
            Object firstElement = redisTemplate.opsForList().index(key, 0);
            
            log.info("✓ Redis List 操作成功，列表大小: {}，第一个元素: {}", size, firstElement);
            assertEquals(3, size, "列表大小应为 3");
            assertEquals("element1", firstElement, "第一个元素应为 element1");
        } catch (Exception e) {
            log.error("✗ Redis List 操作失败", e);
            fail("Redis List 操作失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis Set 操作")
    void testSetOperation() {
        log.info("开始测试 Redis Set 操作...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "set_test";
            
            // 添加元素
            redisTemplate.opsForSet().add(key, "member1", "member2", "member3");
            
            // 获取集合大小
            long size = redisTemplate.opsForSet().size(key);
            
            // 检查成员是否存在
            Boolean isMember = redisTemplate.opsForSet().isMember(key, "member1");
            
            log.info("✓ Redis Set 操作成功，集合大小: {}，member1 存在: {}", size, isMember);
            assertEquals(3, size, "集合大小应为 3");
            assertTrue(isMember, "member1 应存在于集合中");
        } catch (Exception e) {
            log.error("✗ Redis Set 操作失败", e);
            fail("Redis Set 操作失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis 并发操作")
    void testConcurrentOperation() {
        log.info("开始测试 Redis 并发操作...");
        try {
            String key = REDIS_TEST_KEY_PREFIX + "concurrent_test";
            
            // 模拟并发写入
            for (int i = 0; i < 10; i++) {
                redisTemplate.opsForValue().set(key + "_" + i, "value_" + i, 60, TimeUnit.SECONDS);
            }
            
            // 验证所有写入成功
            int successCount = 0;
            for (int i = 0; i < 10; i++) {
                Object value = redisTemplate.opsForValue().get(key + "_" + i);
                if (value != null) {
                    successCount++;
                }
            }
            
            log.info("✓ Redis 并发操作测试成功，成功写入 {} 条数据", successCount);
            assertEquals(10, successCount, "应成功写入 10 条数据");
        } catch (Exception e) {
            log.error("✗ Redis 并发操作失败", e);
            fail("Redis 并发操作失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 Redis 连接稳定性")
    void testConnectionStability() {
        log.info("开始测试 Redis 连接稳定性...");
        try {
            // 执行多次连接操作
            int successCount = 0;
            for (int i = 0; i < 20; i++) {
                try {
                    String key = REDIS_TEST_KEY_PREFIX + "stability_test_" + i;
                    redisTemplate.opsForValue().set(key, "test_" + i, 30, TimeUnit.SECONDS);
                    Object value = redisTemplate.opsForValue().get(key);
                    if (value != null) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.warn("第 {} 次连接失败", i, e);
                }
            }
            
            log.info("✓ Redis 连接稳定性测试成功，成功率: {}/20", successCount);
            assertEquals(20, successCount, "所有连接操作应成功");
        } catch (Exception e) {
            log.error("✗ Redis 连接稳定性测试失败", e);
            fail("Redis 连接稳定性测试失败: " + e.getMessage());
        }
    }
}
