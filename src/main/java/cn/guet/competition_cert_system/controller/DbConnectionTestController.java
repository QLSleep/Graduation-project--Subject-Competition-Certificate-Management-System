package cn.guet.competition_cert_system.controller;

import cn.guet.competition_cert_system.entity.DaoTest;
import cn.guet.competition_cert_system.mapper.DaoTestMapper;
import cn.guet.competition_cert_system.common.ResultCode;
import cn.guet.competition_cert_system.utils.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 数据库连接测试Controller
 * 用于测试PostgreSQL和Redis数据库连接
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DbConnectionTestController {

    private final DaoTestMapper daoTestMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_TEST_KEY_PREFIX = "db_test:";

    /**
     * 测试PostgreSQL和Redis连接
     * GET /api/test/connection
     */
    @GetMapping("/connection")
    public Result<String> testConnection() {
        try {
            // 测试PostgreSQL连接
            List<DaoTest> pgTestResult = daoTestMapper.selectList(null);
            
            // 测试Redis连接
            String redisTestKey = REDIS_TEST_KEY_PREFIX + "connection_test";
            redisTemplate.opsForValue().set(redisTestKey, "PostgreSQL and Redis are connected!", 30, TimeUnit.SECONDS);
            Object redisTestResult = redisTemplate.opsForValue().get(redisTestKey);
            
            return Result.success("✓ PostgreSQL 和 Redis 连接测试成功");
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "✗ 数据库连接失败: " + e.getMessage());
        }
    }

    /**
     * 查询所有测试数据
     * GET /api/test/query-all
     */
    @GetMapping("/query-all")
    public Result<List<DaoTest>> queryAll() {
        try {
            List<DaoTest> list = daoTestMapper.selectList(null);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "查询失败: " + e.getMessage());
        }
    }

    /**
     * 按testCode查询数据
     * GET /api/test/query?testCode=xxx
     */
    @GetMapping("/query")
    public Result<DaoTest> queryByTestCode(@RequestParam String testCode) {
        try {
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getTestCode, testCode);
            DaoTest result = daoTestMapper.selectOne(queryWrapper);
            if (result == null) {
                return Result.error(ResultCode.NOT_FOUND);
            }
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "查询失败: " + e.getMessage());
        }
    }

    /**
     * 新增测试数据
     * POST /api/test/add
     */
    @PostMapping("/add")
    public Result<Long> addTestData(@RequestBody DaoTest daoTest) {
        try {
            // 自动生成testCode和初始化时间字段
            if (daoTest.getTestCode() == null) {
                daoTest.setTestCode("TEST_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            if (daoTest.getIsActive() == null) {
                daoTest.setIsActive(true);
            }
            if (daoTest.getCreatedAt() == null) {
                daoTest.setCreatedAt(Instant.now());
            }
            if (daoTest.getUpdatedAt() == null) {
                daoTest.setUpdatedAt(Instant.now());
            }

            daoTestMapper.insert(daoTest);
            
            // 存储到Redis缓存，30秒过期
            String cacheKey = REDIS_TEST_KEY_PREFIX + daoTest.getTestCode();
            redisTemplate.opsForValue().set(cacheKey, daoTest, 30, TimeUnit.SECONDS);
            
            return Result.success(daoTest.getId());
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "新增失败: " + e.getMessage());
        }
    }

    /**
     * 更新测试数据
     * PUT /api/test/update
     */
    @PutMapping("/update")
    public Result<Void> updateTestData(@RequestBody DaoTest daoTest) {
        try {
            if (daoTest.getId() == null) {
                return Result.error(ResultCode.PARAM_ERROR);
            }
            
            daoTest.setUpdatedAt(Instant.now());
            daoTestMapper.updateById(daoTest);
            
            // 更新Redis缓存
            String cacheKey = REDIS_TEST_KEY_PREFIX + daoTest.getTestCode();
            redisTemplate.delete(cacheKey);
            
            return Result.success();
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除测试数据
     * DELETE /api/test/delete?id=xxx
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteTestData(@RequestParam Long id) {
        try {
            if (id == null) {
                return Result.error(ResultCode.PARAM_ERROR);
            }
            
            // 先获取数据以删除缓存
            DaoTest daoTest = daoTestMapper.selectById(id);
            if (daoTest != null) {
                String cacheKey = REDIS_TEST_KEY_PREFIX + daoTest.getTestCode();
                redisTemplate.delete(cacheKey);
            }
            
            daoTestMapper.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "删除失败: " + e.getMessage());
        }
    }

    /**
     * 查询按类型统计
     * GET /api/test/count-by-type
     */
    @GetMapping("/count-by-type")
    public Result<Long> countByType(@RequestParam String testType) {
        try {
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getTestType, testType);
            long count = daoTestMapper.selectCount(queryWrapper);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "统计失败: " + e.getMessage());
        }
    }

    /**
     * 查询激活的记录数
     * GET /api/test/count-active
     */
    @GetMapping("/count-active")
    public Result<Long> countActive() {
        try {
            LambdaQueryWrapper<DaoTest> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DaoTest::getIsActive, true);
            long count = daoTestMapper.selectCount(queryWrapper);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "统计失败: " + e.getMessage());
        }
    }

    /**
     * Redis测试端点
     * GET /api/test/redis-test
     */
    @GetMapping("/redis-test")
    public Result<String> testRedis() {
        try {
            String testKey = REDIS_TEST_KEY_PREFIX + "redis_test_" + System.currentTimeMillis();
            String testValue = "Redis连接成功 - " + Instant.now();
            
            // 写入
            redisTemplate.opsForValue().set(testKey, testValue, 10, TimeUnit.SECONDS);
            
            // 读取
            Object value = redisTemplate.opsForValue().get(testKey);
            
            return Result.success("✓ Redis 连接成功: " + value);
        } catch (Exception e) {
            return Result.error(ResultCode.SYSTEM_ERROR.getCode(), "✗ Redis 连接失败: " + e.getMessage());
        }
    }
}
