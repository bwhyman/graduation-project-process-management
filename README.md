# Graduation Project Process Management

### 基础功能

所有角色用户登录操作  
学生默认账号密码均为学号  
为保证安全，登录时检测到账号密码相同会要求修改密码，可忽略  
加密用户角色，基于解密的角色完成用户独有操作    
密码加密保存

### 选导师

#### 学生

未到开始时间无法选择  
开始后未选择，选择数量未满的指导教师  
开始后已选择，选择后不可更改；禁止已选学生改选

#### 教师

查看已选自己学生；查看所有未选/已选学生情况；导出信息excel表格

#### 管理员

独立角色，无教师操作权限  
设置开始时间；导入学生表格，导入教师表格(姓名/账号/数量/分组等)；重置密码等

#### 实现

未开始加载教师信息(带学生数量)但不可选择提交；  
后端保证并发操作的数据完整性，处理，学生不存在/教师不存在/教师数量已满(独占)/重复选择/开始前提交等逻辑判断；

简化以上冗余逻辑。学生不存在，token中解密出因此必然存在无需再次判断；
教师不存在，在更新时已基于ID查询不存在无法更新，但无法提供精确反馈信息，鉴于此场景属极端情况可忽略。  
并发下教师数量已满，返回新的教师信息列表，减少前端网络请求；  
提交成功后返回指导教师信息，不再加载选择列表减少数据请求；

### 分组/排序

选导师后将学生分组。原则，学生与指导教师不在同组。

需提前确定教师分组(导入教师时包含组，无指导学生教师也添加，带学生数为0)。前端完成，直接乱序生成组内顺序。

#### 实现

直接前端实现分组。遍历指导教师下学生，插入到其他组随机位置实现排序，动态显示结果，提交后端保存；

### 评分

毕业设计包含若干过程(开题答辩/期中检查/毕业答辩/演示)，过程100分按比例包含若干打分项；不同过程由学生所在组评审或指导教师打分。  
过程/项/比例等均为动态添加。
#### 实现

过程不支持打分，过程至少包含一个打分项；  
前端判断项分数之和为100；  
打分粒度为过程整体而非项，授权给组内评审或指导教师，加载指定过程时按类型加载对应学生，组学生或指导学生；    
添加过程启止时间节点？  
逆向打分分解？

过程包含的项以json数组保存，通过每项中的number区分不基于ID。  
教师对学生过程的评分项以json数组保存。

### 过程文档管理？

学生上传电子版开题/毕设报告，评审浏览评分？  
01-李明-开题报告

### Technologies
OpenJDK ^17  
springboot ^3.0.1  
spring-data-r2dbc   
spring-webflux   
jasync-r2dbc-mysql 2.1.12。r2dbc实现切换到jasync-r2dbc-mysql，原实现2021年至今不更新不支持1.0规范。  
MySQL 8.0.32

尝试了redis 7。spring-redis不支持原生JSON，redis官方OM框架支持有限且很不灵活。  
还是使用已原生支持JSON的MySQL，采用SQL/NoSQL混合开发模式。

### Others
#### MySQL JSON

查询items数组中包含number=1对象记录

```sql
select * from process p where json_contains(p.items, json_object('number', 1));
```

`->`，等效JSON_EXTRACT()函数，返回utf8mb4_0900_ai_ci字符集，不区分大小写；  
`->>`，等效JSON_UNQUOTE(JSON_EXTRACT())函数，将值取消引号，返回utf8mb4_bin编码，区分大小写；

致使创建索引/基于索引查询时，如果字符集不匹配则无法命中索引  
utf8mb4_bin字符按二进制，区分大小写
```sql
index ((cast(student ->> '$.teacherId' as char(21)) collate utf8mb4_bin))
```
```sql
select * from user u where u.student->>'$.teacherId'='1069607508454658048';
```

#### Batch Update

批量处理，当前仅支持占位符不支持名称变量。

```java
@Transactional
public Mono<Void> updateProjectTitles(List<StudentDTO> studentDTOs) {
    String sql = "update student s set s.project_title=? where s.number=?";
    return DatabaseClient.create(connectionFactory).sql(sql).filter(statement -> {
        for (StudentDTO s : studentDTOs) {
            statement.bind(0, s.getProjectTitle()).bind(1, s.getNumber()).add();
        }
        return statement;
    }).then();
}
```

#### PathPattern
spring提供PathPattern类实现路径模糊匹配功能，可用于webflux拦截器判断。

#### Publisher Errors
Reactive Publisher支持直接添加返回异常对象，而无需像传统方法的抛出捕获异常！~
即Mono/flux可以封装正常元素，以及异常对象，在订阅时单独以流的方式处理异常对象而非捕获。

#### Publisher Operators Not be Invoked
First of all, remember that operators such .map, .flatMap, .filter and many others will not be invoked at all if 
there no onNext has been invoked. That means that in your case next code.  
Using operators .switchIfEmpty/.defaultIfEmpty/Mono.repeatWhenEmpty.

mapNotNull()，允许映射null于新发布者。which is allowed to produce a null value.  
switchIfEmpty()，当订阅的元素为空时，回调。  

结合使用map()/flatMap()/filter()/mapNotNull()/switchIfEmpty()/onErrorResume()/Mono.error()等方法。

```java
public Mono<User> addSelection() {
    if() {
        return Mono.error(new XException());    
    }
    return Mono.just(new User());
}
```
```java
addSelection()
        .map(user -> Mono.just(user))
        .onErrorResume(XException.class, throwable -> Mono.just("error"));
```

#### Persistable

DO类实现`Persistable<ID>`接口重写isNew()方法，决定当对象包含ID值时执行save()方法时映射SQL insert还是update语句，
可用于手动添加共用主键(JPA使用注解即可)。

```json
{
  "name": "期中",
  "auth": 1,
  "items": [
    {
      "number": 0,
      "point": 50,
      "name": "5.1",
      "description": "期中-1"
    },
    {
      "number": 1,
      "point": 50,
      "name": "5.5",
      "description": "期中-2"
    }
  ]
}
```