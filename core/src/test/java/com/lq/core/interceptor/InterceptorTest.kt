package com.lq.core.interceptor

import com.lq.annotation.data.InterceptorMeta
import com.lq.core.exception.GroupNotFoundException
import com.lq.core.exception.PathIllegalException
import com.lq.core.route.RouteHelper
import com.lq.core.util.pathMatches
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InterceptorTest {
    @Test
    fun `匹配规则是否正确`() {
        val ruleA = "/login/login".pathMatches("/login/login")
        val ruleB = "/login/123".pathMatches("/login/*")
        val ruleC = "/login/login".pathMatches("*")
        val ruleD = "/login/login".pathMatches("")
        val ruleE = "/login/login".pathMatches("*/login")
        val ruleF = "/login/login".pathMatches("/*/login")
        val ruleG = "/login/login".pathMatches("/*/*")

        val ruleH = "/login/login".pathMatches("/login/*/*")
        val ruleI = "/login/login".pathMatches("/user/*")
        val ruleJ = "/login/login".pathMatches("*/user")
        val ruleK = "/login/login".pathMatches("/user/user/login")

        assertTrue(ruleA, "规则A应该匹配")
        assertTrue(ruleB, "规则B应该匹配")
        assertTrue(ruleC, "规则C应该匹配")
        assertTrue(ruleD, "规则D应该匹配")
        assertTrue(ruleE, "规则E应该匹配")
        assertTrue(ruleF, "规则F应该匹配")
        assertTrue(ruleG, "规则G应该匹配")

        assertFalse(ruleH, "规则H不应该匹配")
        assertFalse(ruleI, "规则I不应该匹配")
        assertFalse(ruleJ, "规则J不应该匹配")
        assertFalse(ruleK, "规则K不应该匹配")
    }

    @Test
    fun `拦截器匹配`() {
        val a = TestInterceptor("A")
        val b = TestInterceptor("B")
        val c = TestInterceptor("C")
        val metaA = InterceptorMeta("A", path = "/user/*", priority = 0)
        val metaB = InterceptorMeta("B", path = "/order", priority = 1)
        val metaAll = InterceptorMeta("C", path = "*", priority = 2)
        val map =
            mapOf(
                metaA to a,
                metaB to b,
                metaAll to c,
            )
        val data = InterceptorManager.selectInterceptors(map, "/user/123")
        assertFalse(b in data, "B不应该被匹配")
        assertTrue(a in data, "A应该被匹配")
        assertTrue(c in data, "C应该被匹配")

        assertEquals(listOf(a, c), data) // 验证优先级顺序，最小数值优先级最高，并列优先级不处理
    }

    @Test
    fun `边界情况-路由地址不合法`() {
        val ext = assertThrows<PathIllegalException> { RouteHelper.findGroup("//user/mock") }
        assertTrue { ext.message?.contains("//user/mock") == true } // 验证诊断信息是否合理
        assertThrows<PathIllegalException> { RouteHelper.findGroup("/login/") }
        assertThrows<PathIllegalException> { RouteHelper.findGroup("/user//mock") }
        assertThrows<GroupNotFoundException> { RouteHelper.findGroup("/login/user") }
        assertThrows<PathIllegalException> { RouteHelper.findGroup("/login") }
    }

    @Test
    fun `路由缓存命中与非命中`() {
        var loadCount = 0

        RouteHelper.initWithRouteRoots(
            mapOf("fake" to "FakeRouteGroup::class.java"),
        )
        RouteHelper.groupLoader = {
            loadCount++
            FakeRouteGroup()
        }
        RouteHelper.findGroup("/fake/fake")
        assertTrue { loadCount == 1 }
        RouteHelper.findGroup("/fake/fake")
        assertTrue { loadCount == 1 }
        RouteHelper.findGroup("/fake/other")
        assertTrue { loadCount == 2 } // 验证缓存非命中的情况
    }
}
