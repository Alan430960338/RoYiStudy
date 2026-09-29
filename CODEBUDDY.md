# CODEBUDDY.md This file provides guidance to CodeBuddy when working with code in this repository.

本仓库是 **RuoYi-Vue 后端**（v3.9.2，Spring Boot 3.5.16 / JDK 17，Maven 多模块），前端（Vue2/Vue3/TS）在独立仓库中，此处不含前端代码。JDK 17+ 与 Maven 3.6+ 是硬前提。

## 常用命令

### 构建整个项目
```bash
mvn clean package -Dmaven.test.skip=true
```
在仓库根目录执行，按 admin→framework→system→quartz→generator→common 顺序构建，产物为 `ruoyi-admin/target/ruoyi-admin.jar`（可执行 jar，已 repackage）。Windows 下可直接双击 `bin/package.bat`，等价于该命令。

### 运行后端
```bash
java -jar ruoyi-admin/target/ruoyi-admin.jar
```
服务端口 `8888`，context-path 为 `/`。Windows 下 `bin/run.bat` 已内置 JVM 参数并指向该 jar。启动前须先在 MySQL 建库并执行 `sql/ry_20260417.sql` 与 `sql/quartz.sql`，再改 `ruoyi-admin/src/main/resources/application-druid.yml` 的连接信息。

### 以开发模式热启动
```bash
mvn clean install -Dmaven.test.skip=true && mvn -pl ruoyi-admin spring-boot:run
```
先 `install` 把各模块装进本地仓库，再只对 `ruoyi-admin` 执行 `spring-boot:run`（`spring-boot-maven-plugin` 从根 pom 继承，直接加 `-am` 会让非启动模块也尝试执行该 goal 而失败）。已引入 `spring-boot-devtools`，classpath 变更会触发快速重启。

### 生产部署启停脚本
```bash
./ry.sh start|stop|restart|status
```
Linux/Mac 用 `ry.sh`，Windows 用 `ry.bat`（交互式菜单 1 启动 / 2 关闭 / 3 重启 / 4 状态）。脚本按进程名 `ruoyi-admin.jar` 匹配，需先 `mvn clean package` 并把 jar 放到脚本同级目录。

### 代码检查与测试
```bash
mvn clean compile
```
本仓库**没有任何测试基础设施**：6 个模块均无 `src/test` 目录，所有 pom 中都没有 junit / spring-boot-starter-test / mockito 依赖，也没有 linter 配置。因此没有可用的 `mvn test` 或 lint 命令。改动后请用 `mvn clean compile` 验证编译，再启动服务用 Swagger UI（`http://localhost:8888/swagger-ui.html`）或 `http://localhost:8888/captchaImage` 做冒烟验证。`ruoyi-admin/.../web/core/config/SwaggerConfig.java` 未限制包路径，所有模块下被扫描到的 Controller 都会出现在接口文档里。默认账号 `admin/admin123`。

## 架构总览

### 模块划分与依赖方向

依赖严格单向、无循环，新增业务模块时请照此放置代码：

```
ruoyi-admin ──> ruoyi-framework ──> ruoyi-system ──> ruoyi-common
     ├────────> ruoyi-quartz     ──> ruoyi-common
     └────────> ruoyi-generator  ──> ruoyi-common
```

根 `pom.xml` 用 `<dependencyManagement>` 统一锁定所有版本，子模块声明依赖时不写版本号。

- **ruoyi-admin**：唯一可启动入口，含 `RuoYiApplication`（`@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`，数据源由 `DruidConfig` 手工装配以支持主从）。**所有 Controller 都在这里**，位于 `com.ruoyi.web.controller.**`；全部 resources 配置也在此。
- **ruoyi-framework**：框架能力层——Security 配置、AOP 切面、拦截器、数据源、异步任务、全局异常。是唯一横跨安全与业务的模块。
- **ruoyi-system**：系统业务（用户/角色/菜单/部门/字典等），只有 domain/service/mapper，**没有 Controller**。新增业务模块应复制它的结构，pom 只依赖 `ruoyi-common`。
- **ruoyi-common**：纯工具底座，不依赖任何兄弟模块。注意 Security、Redis、PageHelper、POI、JJWT 依赖都放在这里，而 MyBatis/Druid 放在 framework。
- **ruoyi-quartz / ruoyi-generator**：定时任务与代码生成，各自只依赖 common。

### 分层约定

一个完整功能横跨多个模块和目录，例如用户模块：Controller 在 `ruoyi-admin/.../web/controller/system/SysUserController.java`，Service 接口与实现在 `ruoyi-system/.../system/service/`，Mapper 接口在 `ruoyi-system/.../system/mapper/`，XML 在 `ruoyi-system/src/main/resources/mapper/system/`，而**跨模块复用的实体类在 `ruoyi-common/.../core/domain/entity/SysUser.java`**，只有模块私有的 DO 才放 `ruoyi-system/.../system/domain/`。

Controller 必须 `extends BaseController`，它提供 `startPage()`/`getDataTable()`、`toAjax(int)`、`success/error/warn`，以及 `getUserId()/getDeptId()/getLoginUser()`。Mapper 扫描由 `framework/config/ApplicationConfig.java` 的 `@MapperScan("com.ruoyi.**.mapper")` 统一完成。

### 认证与权限的两条耦合链路

这是本仓库最需要跨文件理解的部分。

**链路一（鉴权）**：Controller 上写 `@PreAuthorize("@ss.hasPermi('system:user:list')")`。`ss` 是 `framework/web/service/PermissionService.java`（`@Service("ss")`），它从 `SecurityUtils.getLoginUser().getPermissions()` 比对权限串；权限集合来源为 `SysPermissionService#getMenuPermission`（超管返回 `*:*:*`，否则查菜单表）。注意 `hasPermi` 除了返回布尔值，**还会把权限串写入 `framework/security/context/PermissionContextHolder`**。

**链路二（数据权限）**：`@DataScope` 注解由 `framework/aspectj/DataScopeAspect.java` 处理。`doBefore` 先清空 `params.dataScope`（防注入），超管直接跳过，然后按角色 `dataScope` 值拼接 `OR` 片段（1 全部 / 2 自定义 / 3 本部门 / 4 部门及以下 / 5 仅本人），最终写入 `baseEntity.getParams().put("dataScope", ...)`。Mapper XML 用 `${params.dataScope}`（不是 `#{}`）接收该 SQL 片段。

**关键耦合**：`DataScopeAspect` 会从 `PermissionContextHolder` 读取权限串，而该值由链路一的 `hasPermi` 写入。也就是说 `@PreAuthorize("@ss.hasPermi(...)")` 与 `@DataScope` 是隐式配对的，改动任一侧都会影响另一侧。

`params` 字段定义在 `ruoyi-common/.../core/domain/BaseEntity.java` 的 `Map<String,Object> params`，这是注入 SQL 的载体——新增需要数据权限的查询必须三处配合：Service 加 `@DataScope`、Mapper XML 加 `${params.dataScope}`、Controller 加对应 `@PreAuthorize`。

### 其他切面与注解

全部注解定义在 `ruoyi-common/.../annotation/`，实现在 `ruoyi-framework/.../aspectj/` 或 `interceptor/`：

- `@Log` → `LogAspect`，记录操作日志，脱敏 `password` 等字段，异步落库。
- `@RateLimiter` → `RateLimiterAspect`，Redis Lua 计数，超限抛 `ServiceException`。
- `@RepeatSubmit` → `RepeatSubmitInterceptor`（抽象基类）+ `SameUrlDataInterceptor`，是 MVC `HandlerInterceptor`，由 `config/ResourcesConfig.java` 注册到 `/**`，**不是 AOP 切面**。
- `@DataSource` → `DataSourceAspect` + `DynamicDataSource` + `DynamicDataSourceContextHolder`，主从切换。
- `@Anonymous` → 启动时由 `config/properties/PermitAllUrlProperties.java` 扫描并放行。

安全配置集中在 `framework/config/SecurityConfig.java`：无状态（`SessionCreationPolicy.STATELESS`）、禁用 CSRF、过滤器链为 `corsFilter` → `LogoutFilter` → `JwtAuthenticationTokenFilter`。Token 由 `framework/web/service/TokenService.java` 签发并存 Redis，`BCryptPasswordEncoder` 加密密码。

### 异常、日志与异步

全局异常处理器 `framework/web/exception/GlobalExceptionHandler.java`（`@RestControllerAdvice`）把所有异常统一转成 `AjaxResult`。**业务异常类名是 `ServiceException`（`ruoyi-common/.../exception/`），不是 `BusinessException`**，它带 `code/message/detailMessage`。同目录还有 `DemoModeException`、`UtilException`、`GlobalException`，状态码常量在 `constant/HttpStatus.java`。

异步体系：`framework/manager/AsyncManager.java` 是单例（`AsyncManager.me().execute(task)`，延迟 10ms），线程池来自 `config/ThreadPoolConfig.java`；`manager/factory/AsyncFactory.java` 提供 `recordLogininfor` 与 `recordOper` 两个工厂方法，登录日志和操作日志都通过它异步落库。优雅停机在 `manager/ShutdownManager.java`。

### 代码生成器

`ruoyi-generator` 用 Velocity 把表结构渲染成前后端代码。模板在 `ruoyi-generator/src/main/resources/vm/`（java/xml/sql/js/ts/vue，vue 下又分 v3、v3ts）。三个核心方法都在 `generator/util/VelocityUtils.java`：`prepareContext` 组装 VelocityContext 变量、`getTemplateList` 按 `tplCategory`（crud/tree/sub）与 `tplWebType` 挑选模板、`getFileName` 决定输出路径。

`generator/service/GenTableServiceImpl.java` 提供两种方式：`generatorCode(tableName)` 只渲染 java/xml 并**直接写盘到 `user.dir/src/...`**（自定义路径），`downloadCode(tableNames)` 渲染全部模板打成 ZIP。生成器的 `author/packageName/tablePrefix/allowOverwrite` 配置在 `ruoyi-generator/src/main/resources/generator.yml`。

**维护要点**：新增 vm 模板后必须同步更新 `VelocityUtils.getTemplateList` 与 `getFileName`，否则模板不会被渲染。生成出的 Controller 权限串前缀 `permissionPrefix` = `moduleName:businessName`，与上文鉴权链路一致。

### 配置体系

配置都在 `ruoyi-admin/src/main/resources/`。`application.yml` 通过 `spring.profiles.active: druid` 激活 `application-druid.yml`（数据源）。要点：端口 8888、`ruoyi.profile` 上传路径（默认 `D:/ruoyi/uploadPath`）、`captchaType`（math/char）、Token 配置（`token.header/secret/expireTime`）、MyBatis（`typeAliasesPackage: com.ruoyi.**.domain`，`mapperLocations: classpath*:mapper/**/*Mapper.xml`）、PageHelper、XSS 开关。

数据源在 `application-druid.yml`：主库 `jdbc:mysql://localhost:3306/ry_vue`，**从库默认 `slave.enabled: false`**。MyBatis 实际装配在 `framework/config/MyBatisConfig.java`（手工 `SqlSessionFactoryBean`），数据源装配在 `config/DruidConfig.java` + `config/properties/DruidProperties.java`。日志配置在 `logback.xml`。

### 部署方式

jar 方式：`mvn clean package` 后 `java -jar ruoyi-admin.jar`。war 方式：按 `doc/若依环境使用手册.docx` 所述，把 `ruoyi-admin/pom.xml` 的 packaging 改为 war、排除 `spring-boot-starter-tomcat`、部署到 tomcat 的 webapps。前端（独立仓库）用 `npm install --registry=https://registry.npmmirror.com` + `npm run dev` 开发，`npm run build:prod` 产出 `dist/` 交给 nginx 托管。
