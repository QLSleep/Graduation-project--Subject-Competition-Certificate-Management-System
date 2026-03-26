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

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PostgreSQL 数据库连接和操作测试类
 * 专用于测试 PostgreSQL 连接和 DAO 层操作
 */
@SpringBootTest
@Slf4j
@DisplayName("PostgreSQL 数据库测试")
public class PostgreSqlConnectionTests {

    @Autowired
    private DaoTestMapper daoTestMapper;

    @BeforeEach
    void setUp() {
        log.info("=== PostgreSQL 测试开始 ===");
    }

    @Test
    @DisplayName("测试 PostgreSQL 连接")
    void testPostgresqlConnection() {
        log.info("开始测试 PostgreSQL 连接...");
        try {
            // 执行一个简单的查询
            List<DaoTest> list = daoTestMapper.selectList(null);
            log.info("✓ PostgreSQL 连接成功，查询到 {} 条记录", list.size());
            assertNotNull(list, "查询结果不应为null");
        } catch (Exception e) {
            log.error("✗ PostgreSQL 连接失败", e);
            fail("PostgreSQL 连接失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试插入数据")
    void testInsertData() {
        log.info("开始测试插入数据...");
        try {
            DaoTest daoTest = DaoTest.builder()
                    .testCode("PG_INSERT_TEST_" + System.currentTimeMillis())
                    .testName("PostgreSQL 插入测试")
                    .testValue(100)
                    .isActive(true)
                    .testType("TYPE_A")
                    .remark("这是一条 PostgreSQL 插入测试数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(daoTest);
            
            log.info("✓ 数据插入成功，ID: {}", daoTest.getId());
            assertNotNull(daoTest.getId(), "插入后的 ID 不应为 null");
            assertTrue(daoTest.getId() > 0, "插入后的 ID 应大于 0");
        } catch (Exception e) {
            log.error("✗ 数据插入失败", e);
            fail("数据插入失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试查询数据")
    void testQueryData() {
        log.info("开始测试查询数据...");
        try {
            // 先插入一条测试数据
            String testCode = "PG_QUERY_TEST_" + System.currentTimeMillis();
            DaoTest insertData = DaoTest.builder()
                    .testCode(testCode)
                    .testName("PostgreSQL 查询测试")
                    .testValue(200)
                    .isActive(true)
                    .testType("TYPE_B")
                    .remark("用于查询测试的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(insertData);
            log.info("已插入测试数据，ID: {}", insertData.getId());
            
            // 按 testCode 查询这条数据
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getTestCode, testCode);
            DaoTest queryResult = daoTestMapper.selectOne(queryWrapper);
            
            log.info("✓ 数据查询成功: {}", queryResult);
            assertNotNull(queryResult, "查询结果不应为 null");
            assertEquals(testCode, queryResult.getTestCode(), "testCode 应匹配");
            assertEquals("PostgreSQL 查询测试", queryResult.getTestName(), "testName 应匹配");
        } catch (Exception e) {
            log.error("✗ 数据查询失败", e);
            fail("数据查询失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试更新数据")
    void testUpdateData() {
        log.info("开始测试更新数据...");
        try {
            // 先插入一条测试数据
            DaoTest insertData = DaoTest.builder()
                    .testCode("PG_UPDATE_TEST_" + System.currentTimeMillis())
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
            assertEquals("更新后的名称", queryResult.getTestName(), "testName 应被更新");
            assertEquals(350, queryResult.getTestValue(), "testValue 应被更新");
        } catch (Exception e) {
            log.error("✗ 数据更新失败", e);
            fail("数据更新失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试删除数据")
    void testDeleteData() {
        log.info("开始测试删除数据...");
        try {
            // 先插入一条测试数据
            DaoTest insertData = DaoTest.builder()
                    .testCode("PG_DELETE_TEST_" + System.currentTimeMillis())
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
            assertNull(queryResult, "删除后的查询结果应为 null");
        } catch (Exception e) {
            log.error("✗ 数据删除失败", e);
            fail("数据删除失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试按类型统计")
    void testCountByType() {
        log.info("开始测试按类型统计...");
        try {
            // 插入多条 TYPE_A 类型的测试数据
            for (int i = 0; i < 3; i++) {
                DaoTest daoTest = DaoTest.builder()
                        .testCode("PG_COUNT_TEST_" + i + "_" + System.currentTimeMillis())
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
            
            // 统计 TYPE_A 类型的记录
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getTestType, "TYPE_A");
            long count = daoTestMapper.selectCount(queryWrapper);
            
            log.info("✓ 统计测试成功，TYPE_A 类型的记录数: {}", count);
            assertTrue(count >= 3, "TYPE_A 类型的记录数应至少为 3");
        } catch (Exception e) {
            log.error("✗ 统计测试失败", e);
            fail("统计测试失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试激活状态统计")
    void testCountActive() {
        log.info("开始测试激活状态统计...");
        try {
            // 插入激活状态的测试数据
            DaoTest activeData = DaoTest.builder()
                    .testCode("PG_ACTIVE_TEST_" + System.currentTimeMillis())
                    .testName("激活状态测试")
                    .testValue(500)
                    .isActive(true)
                    .testType("TYPE_B")
                    .remark("用于激活状态统计的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(activeData);
            
            // 统计激活的记录
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getIsActive, true);
            long count = daoTestMapper.selectCount(queryWrapper);
            
            log.info("✓ 激活状态统计成功，激活的记录数: {}", count);
            assertTrue(count >= 1, "激活的记录数应至少为 1");
        } catch (Exception e) {
            log.error("✗ 激活状态统计失败", e);
            fail("激活状态统计失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试 PostgreSQL 事务一致性")
    void testTransactionConsistency() {
        log.info("开始测试 PostgreSQL 事务一致性...");
        try {
            // 插入一条数据
            DaoTest insertData = DaoTest.builder()
                    .testCode("PG_TRANSACTION_TEST_" + System.currentTimeMillis())
                    .testName("事务一致性测试")
                    .testValue(600)
                    .isActive(true)
                    .testType("TYPE_C")
                    .remark("用于事务一致性测试的数据")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            
            daoTestMapper.insert(insertData);
            
            // 立即查询，验证事务提交
            DaoTest queryResult = daoTestMapper.selectById(insertData.getId());
            
            log.info("✓ 事务一致性测试成功，数据已正确持久化");
            assertNotNull(queryResult, "查询结果不应为 null");
            assertEquals(insertData.getId(), queryResult.getId(), "ID 应匹配");
        } catch (Exception e) {
            log.error("✗ 事务一致性测试失败", e);
            fail("事务一致性测试失败: " + e.getMessage());
        }
    }
}
