# HRouter / HRouter

[![Maven Central](https://img.shields.io/maven-central/v/io.github.hrouter/core.svg?label=Maven%20Central)](https://central.sonatype.com/namespace/io.github.hrouter)
[![License](https://img.shields.io/badge/License-Apache-green.svg)](LICENSE)

---

## 📖 English Documentation / 中文文档

---

### 🇺🇸 English Version (For Review)

<details>
<summary><b>Click to expand English documentation</b></summary>

# HRouter

HRouter is an Android routing framework based on **KSP code generation at compile time**.  
It does not pursue "automatically handling all exceptions", but emphasizes **predictable behavior and explainable failures**.

> ⚠️ **Core Philosophy**: Routing failure ≠ Must degrade | Interception failure ≠ System exception

---

## ✨ Features

- 🚀 **Compile-time Generation**: Generates routing tables at compile time using KSP, no reflection performance overhead
- 🔧 **Strongly-typed Parameters**: Parameter type errors directly throw exceptions (Fail-fast principle)
- 🛡️ **Predictable Behavior**: Clearly distinguishes between routing failures, interception failures, and parameter errors
- 🔄 **Safe Navigation**: Built-in detection for redirect loops and degrade loops
- 🧩 **Modular Decoupling**: Perfectly supports cross-module page navigation
- 📦 **Automatic Parameter Injection**: Supports automatic parameter injection with `@Autowired` annotation
- 🚧 **Flexible Interception**: Supports interceptor registration based on path patterns
- 🆘 **Controlled Degradation**: Degradation logic only triggers when routes/groups don't exist

---

## 📦 Installation

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.hrouter:core:$hrouter_version")
    ksp("io.github.hrouter:compiler:$hrouter_version")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'io.github.hrouter:core:$hrouter_version'
    ksp 'io.github.hrouter:compiler:$hrouter_version'
}
```

### Add Annotation Processor
Configure KSP in `build.gradle.kts`:

```kotlin
plugins {
    id("com.google.devtools.ksp") 
    id("io.github.hrouter") // Add HRouter gradle plugin
}
```

> **Note**: Ensure the KSP plugin is properly configured in your project.

---

## 🚀 Quick Start

### 1. Register during application startup
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        HRouter.init(this)
    }
}
```

### 2. Define Routed Pages

```kotlin
@Route(path = "/app/main")
class MainActivity : AppCompatActivity()

@Route(path = "/user/profile")
class ProfileActivity : AppCompatActivity()
```

### 3. Initiate Navigation

```kotlin
// Simple navigation
HRouter.build("/app/main").navigate()

// Navigation with parameters
HRouter.build("/user/profile")
    .withParams {
        "userId" to "12345"
        "fromPage" to "home"
    }
    .navigate()
```

---

## 📝 Detailed Usage Guide

### 🎯 Route Definition

```kotlin
@Route(
    path = "/module/page" // Required, globally unique path
)
class TargetActivity : AppCompatActivity()
```

**Path Rules**:
- Must start with `/`
- Recommended format: `/{module}/{page}`
- Globally unique, duplicate paths will cause compile-time errors

### 📦 Parameter Passing

#### Sending Parameters
```kotlin
HRouter.build("/detail/article")
    .withParams {
        // Basic types
        "id" to 1001
        "title" to "Hello HRouter"
        "isPremium" to true
        "price" to 9.99f
        
        // Complex objects (must implement Serializable)
        "user" to User("Android", 25)
        
        // Parcelable objects
        "data" to ParcelableData()
    }
    .navigate()
```

#### Receiving Parameters (Automatic Injection)

```kotlin
@Route(path = "/detail/article")
class ArticleActivity : AppCompatActivity() {
    
    @Autowired  // Must use @Autowired annotation
    lateinit var title: String
    
    @Autowired
    var user: User? = null  // Nullable type
    
    @Autowired
    var timestamp: Long = System.currentTimeMillis()  // With default value
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Parameters are automatically injected before onCreate
        println("Article Title: $title, ID: $articleId")
    }
}
```

**Injection Timing**: Completed automatically before `super.onCreate()`.

### 🚧 Route Interceptors

#### Define Interceptor
```kotlin
@RouterInterceptor(path = "/user/profile", priority = 5)  // Lower value = higher priority
class AuthInterceptor : IRouteInterceptor {
    
    override fun intercept(chain: RouteChain): InterceptorResult {
        val context = chain.context
        val request = chain.request
        
        return if (UserSession.isLoggedIn()) {
            // Allow to proceed
            InterceptorResult.Continue
        } else {
            // Redirect to login page
            InterceptorResult.Redirect(chain.redirect("/login/login"))
        }
    }
}
```

**Interceptor Chain Features**:
- Supports multiple interceptors executed in registration order
- Each interceptor can decide to proceed or redirect
- Interception failures do not trigger degradation

### 🆘 Route Degradation

#### Degradation Trigger Conditions
Only triggers when:
1. **Route doesn't exist**: Requested path is not registered
2. **Group doesn't exist**: Requested group is not registered

#### Define Degradation Handler
```kotlin
@RouteDegrade(path = "/login/*", priority = 3)  // Higher priority = earlier trigger
class GlobalDegradeHandler : IRouteDegrade {
    
    override fun onLost(request: DegradeRequest): DegradeResult {
        return when {
            request.path.startsWith("/product/") -> {
                // Redirect to product list
                DegradeResult.Redirect("/product/list")
            }
            request.path.startsWith("/user/") -> {
                // Redirect to user center
                DegradeResult.Redirect("/user/center")
            }
            else -> {
                // do nothing
                DegradeResult.Ignore
            }
        }
    }
}
```

### 🔄 Loop Protection Mechanism

HRouter includes two types of loop protection:

#### 1. Redirect Loop Protection
```kotlin
InterceptorResult.Redirect("/login")
```
- Automatically counts redirects within the same navigation
- Default threshold: 5 times
- Exceeding threshold: Throws `RedirectLoopException`

#### 2. Degradation Loop Protection
```kotlin
DegradeResult.Redirect("/home")
```
- Maintains independent degradation context
- Records degradation navigation paths
- Terminates and throws `DegradeLoopException` when loops are detected

---

## ⚠️ Exception Handling Guide

### 1. Parameter Type Mismatch
```kotlin
// Send String type
HRouter.build("/test").withParams { "count" to "100" }.navigate()

// Receive declared as Int
@Autowired var count: Int = 0  // ❌ Throws TypeMismatchException at runtime
```

**Recommendation**: Use nullable types or provide default values
```kotlin
@Autowired var count: Int? = null
// or
@Autowired var count: Int = 0
```

### 2. Required Parameter Missing
```kotlin
@Autowired lateinit var requiredParam: String  // ❌ Throws UninitializedPropertyAccessException when parameter not provided
```

**Recommendation**: Provide default values for important parameters or use nullable types

### 3. Route Doesn't Exist
Triggers degradation process, won't crash.

### 4. Interception Failure
Returns redirect result, won't trigger degradation.

---

## 📋 Best Practices

### Path Planning (Detailed rules can refer to unit test cases)
```
/app/home                    # Application homepage
/app/settings                # Settings page
/order/list                 # Order list
/order/detail/{orderNo}     # Order details
```

### Parameter Design
1. Use **basic types** as primary parameters
2. Ensure **complex objects** are serializable
3. For **data sharing between pages**, consider using ViewModel or Repository
4. Do not pass **sensitive data** through routes

### Interceptor Usage
1. Login verification: `/user/*`, `/order/*`
2. Permission verification: `/admin/*`
3. Page tracking: Global interceptor for page visit recording

---

## ❓ Frequently Asked Questions

### Q1: Why do parameter type errors cause crashes directly?
A: Following the **Fail-fast** principle. Type errors are coding issues that should be discovered during testing, not silently fail at runtime.

### Q2: Difference between interceptors and degradation?
A: Interceptors handle "whether can access", degradation handles "whether exists". Interception failure is a business logic issue, route non-existence is a configuration issue.

### Q3: Does it support Fragment navigation?
A: Current version mainly supports Activity routing. For Fragment navigation, consider using it with Navigation component.

### Q4: How to debug routing issues?
A: Enable debug logging:
```kotlin
HRouter.debug(true)
```
Compile-time generated code can be viewed in `build/generated/ksp/` directory.

---

</details>

---

### 🇨🇳 中文版本

<details>
<summary><b>点击展开中文文档</b></summary>

# HRouter

HRouter 是一个基于 **KSP 编译期生成代码** 的 Android 路由框架。  
它不追求"自动兜底一切异常"，而是强调 **行为可预期、失败可解释**。

> ⚠️ **核心理念**：路由失败 ≠ 一定要降级 | 拦截失败 ≠ 系统异常

---

## ✨ 特性

- 🚀 **编译期生成**：基于 KSP 在编译期生成路由表，无反射性能损耗
- 🔧 **强类型参数**：参数类型错误直接抛出异常（Fail-fast 原则）
- 🛡️ **可预测行为**：明确区分路由失败、拦截失败和参数错误
- 🔄 **安全跳转**：内置重定向循环和降级循环检测
- 🧩 **模块化解耦**：完美支持跨模块页面跳转
- 📦 **参数自动注入**：支持 `@Autowired` 注解自动注入参数
- 🚧 **灵活拦截**：支持基于路径模式的拦截器注册
- 🆘 **可控降级**：仅在路由/分组不存在时触发降级逻辑

---

## 📦 安装

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("io.github.hrouter:core:$hrouter_version")
    ksp("io.github.hrouter:compiler:$hrouter_version")
}
```

### Gradle (Groovy DSL)

```groovy
dependencies {
    implementation 'io.github.hrouter:core:$hrouter_version'
    ksp 'io.github.hrouter:compiler:$hrouter_version'
}
```

### 添加注解处理器
在 `build.gradle.kts` 中配置 KSP：

```kotlin
plugins {
    id("com.google.devtools.ksp") 
    id("io.github.hrouter") //添加HRouter的gradle插件
}
```

> **注意**：请确保项目中已正确配置 KSP 插件。

---

## 🚀 快速开始

### 1.应用启动时注册
```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        HRouter.init(this)
    }
}
```

### 2.定义路由页面

```kotlin
@Route(path = "/app/main")
class MainActivity : AppCompatActivity()

@Route(path = "/user/profile")
class ProfileActivity : AppCompatActivity()
```

### 3. 发起跳转

```kotlin
// 简单跳转
HRouter.build("/app/main").navigate()

// 带参数跳转
HRouter.build("/user/profile")
    .withParams {
        "userId" to "12345"
        "fromPage" to "home"
    }
    .navigate()
```

---

## 📝 详细使用指南

### 🎯 路由定义

```kotlin
@Route(
    path = "/module/page" // 必须，全局唯一路径
)
class TargetActivity : AppCompatActivity()
```

**路径规则**：
- 必须以 `/` 开头
- 建议格式：`/{模块}/{页面}`
- 全局唯一，重复路径会在编译期报错

### 📦 参数传递

#### 发送参数
```kotlin
HRouter.build("/detail/article")
    .withParams {
        // 基本类型
        "id" to 1001
        "title" to "Hello HRouter"
        "isPremium" to true
        "price" to 9.99f
        
        // 复杂对象（需实现 Serializable）
        "user" to User("Android", 25)
        
        // Parcelable 对象
        "data" to ParcelableData()
    }
    .navigate()
```

#### 接收参数（自动注入）

```kotlin
@Route(path = "/detail/article")
class ArticleActivity : AppCompatActivity() {
    
    @Autowired  // 必须使用 @Autowired 注解
    lateinit var title: String
    
    @Autowired
    var user: User? = null  // 可为空类型
    
    @Autowired
    var timestamp: Long = System.currentTimeMillis()  // 带默认值
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 参数已在 onCreate 前自动注入完成
        println("文章标题：$title, ID: $articleId")
    }
}
```

**注入时机**：`super.onCreate()` 之前自动完成。

### 🚧 路由拦截器

#### 定义拦截器
```kotlin
@RouterInterceptor(path = "/user/profile",priority = 5)  //数值越小，优先级越高
class AuthInterceptor : IRouteInterceptor {
    
    override fun intercept(chain: RouteChain): InterceptorResult {
        val context = chain.context
        val request = chain.request
        
        return if (UserSession.isLoggedIn()) {
            // 放行
            InterceptorResult.Continue
        } else {
            // 重定向到登录页
            InterceptorResult.Redirect(chain.redirect("/login/login"))
        }
    }
}
```

**拦截器链特性**：
- 支持多个拦截器按注册顺序执行
- 每个拦截器可决定放行或重定向
- 拦截失败不会触发降级处理

### 🆘 路由降级

#### 降级触发条件
仅当以下情况发生时：
1. **路由不存在**：请求的路径未注册
2. **分组不存在**：请求的分组未注册

#### 定义降级处理器
```kotlin
@RouteDegrade(path = "/login/*", priority = 3)  // 优先级越高，越早触发
class GlobalDegradeHandler : IRouteDegrade {
    
    override fun onLost(request: DegradeRequest): DegradeResult {
        return when {
            request.path.startsWith("/product/") -> {
                // 跳转到产品列表页
                DegradeResult.Redirect("/product/list")
            }
            request.path.startsWith("/user/") -> {
                // 跳转到用户中心
                DegradeResult.Redirect("/user/center")
            }
            else -> {
                // 不做处理
                DegradeResult.Ignore
            }
        }
    }
}
```

### 🔄 循环保护机制

HRouter 内置两种循环保护：

#### 1. 重定向循环保护
```kotlin
InterceptorResult.Redirect("/login")
```
- 自动统计同一次跳转中的重定向次数
- 默认阈值：5次
- 超过阈值：抛出 `RedirectLoopException`

#### 2. 降级循环保护
```kotlin
DegradeResult.Redirect("/home")
```
- 维护独立的降级上下文
- 记录降级跳转路径
- 检测到循环时直接终止并抛出 `DegradeLoopException`

---

## ⚠️ 异常处理指南

### 1. 参数类型不匹配
```kotlin
// 发送 String 类型
HRouter.build("/test").withParams { "count" to "100" }.navigate()

// 接收处声明为 Int
@Autowired var count: Int = 0  // ❌ 运行时抛出 TypeMismatchException
```

**建议**：使用可空类型或提供默认值
```kotlin
@Autowired var count: Int? = null
// 或
@Autowired var count: Int = 0
```

### 2. 必填参数缺失
```kotlin
@Autowired lateinit var requiredParam: String  // ❌ 未传参时抛出 UninitializedPropertyAccessException
```

**建议**：为重要参数提供默认值或使用可空类型

### 3. 路由不存在
触发降级流程，不会崩溃。

### 4. 拦截失败
返回重定向结果，不会触发降级。

---

## 📋 最佳实践

### 路径规划(详细规则可参考单测案例)
```
/app/home                    # 应用主页
/app/settings                # 设置页
/order/list                 # 订单列表
/order/detail/{orderNo}     # 订单详情
```

### 参数设计
1. **使用基本类型**作为主要参数
2. **复杂对象**确保可序列化
3. **页面间共享数据**考虑使用 ViewModel 或 Repository
4. **敏感数据**不要通过路由传递

### 拦截器使用
1. 登录校验：`/user/*`, `/order/*`
2. 权限校验：`/admin/*`
3. 页面埋点：全局拦截器记录页面访问

---

## ❓ 常见问题

### Q1: 为什么参数类型错误要直接崩溃？
A: 遵循 **Fail-fast** 原则。类型错误是编码期问题，应该在测试阶段发现，而不是运行时 silently fail。

### Q2: 拦截器和降级的区别？
A: 拦截器处理的是"能否访问"，降级处理的是"是否存在"。拦截失败是业务逻辑问题，路由不存在是配置问题。

### Q3: 支持 Fragment 跳转吗？
A: 当前版本主要支持 Activity 路由。Fragment 导航建议结合 Navigation 组件使用。

### Q4: 如何调试路由问题？
A: 启用调试日志：
```kotlin
HRouter.debug(true)
```
编译期生成的代码可在 `build/generated/ksp/` 目录查看。

---

</details>

---

## 🤝 贡献指南 / Contribution Guide

欢迎提交 Issue 和 Pull Request。  
在提交新功能前，请先开 Issue 讨论设计。

Welcome to submit Issues and Pull Requests.  
Please discuss design by opening an Issue before submitting new features.

### 开发环境 / Development Environment
- Android Studio Flamingo 2022.2.1+
- Kotlin 1.9.0
- KSP 1.0.0-beta08

---

## 📄 许可证 / License

```
Apache 2.0 License

Copyright (c) 2023 HRouter Contributors

Permission is hereby granted...
```
完整内容见 [LICENSE](LICENSE) 文件。  
Full content see [LICENSE](LICENSE) file.

---

## 🙏 致谢 / Acknowledgments

感谢所有贡献者和用户的支持！  
Thanks to all contributors and users for your support!
```

