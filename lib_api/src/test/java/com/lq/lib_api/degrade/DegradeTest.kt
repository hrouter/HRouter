package com.lq.lib_api.degrade

import com.lq.lib_annotation.data.DegradeMeta
import com.lq.lib_api.HRouter
import com.lq.lib_api.exception.FakeDegradeRegistry
import com.lq.lib_api.interceptor.RouteDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DegradeTest {

    private val testDispatcher = StandardTestDispatcher()


    @BeforeEach
    fun setUp(){
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `加载降级并测试优先级与降级匹配`(){
        val degradeA = DegradeMeta("FakeDegrade::class.java", priority = 1,path="/mock/mock")
        val degradeB = DegradeMeta("FakeDegrade::class.java", priority = 2,path="*")
        val degradeC = DegradeMeta("FakeDegrade", priority = 3,path="/fake/fake")
        val routeDegradeA = FakeDegrade()
        val routeDegradeB = FakeDegrade()
        val routeDegradeC = FakeDegrade()

        val registry = FakeDegradeRegistry()
        registry.setDegrades(mapOf(
            degradeA to routeDegradeA,
            degradeB to routeDegradeB,
            degradeC to routeDegradeC
        ))
        DegradeManager.setRegistry(registry)
        val degrades = registry.getDegrades()
        assertTrue(degradeA in degrades)
        assertTrue(degradeB in degrades)
        assertEquals(listOf(routeDegradeA,routeDegradeB), DegradeManager.getDegradesFromRequest(degrades,"/mock/mock"))

        //验证当前的匹配规则
        val fakeMock = DegradeManager.getDegradesFromRequest(degrades,"/mock/fake")
        assertFalse(routeDegradeA in fakeMock)
        assertTrue(routeDegradeB in fakeMock)
        assertFalse { routeDegradeC in fakeMock  }

    }

    @Test
    fun `降级是否正常运行`(){

        val degradeA = DegradeMeta("FakeDegrade::class.java", priority = 1,path="/mock/mock")
        val degradeB = DegradeMeta("FakeDegrade::class.java", priority = 2,path="*")
        val degradeC = DegradeMeta("FakeDegrade", priority = 3,path="/fake/fake")
        val routeDegradeA = FakeDegrade()
        val routeDegradeB = FakeDegrade()
        val routeDegradeC = FakeDegrade()

        val registry = FakeDegradeRegistry()
        registry.setDegrades(mapOf(
            degradeA to routeDegradeA,
            degradeB to routeDegradeB,
            degradeC to routeDegradeC
        ))
        DegradeManager.setRegistry(registry)

        RouteDispatcher.mainDispatcher = testDispatcher

        HRouter.build("/mock/fake").navigateWithDegradeContext(DegradeContext()) //模拟内部跳转

    }
}