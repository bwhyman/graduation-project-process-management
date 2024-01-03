### MySQL JSON

查询items数组中包含number=1对象记录

```sql
select * from process p where json_contains(p.items, json_object('number', 1));
```

`->`，等效JSON_EXTRACT()函数，返回utf8mb4_0900_ai_ci字符集，不区分大小写；  
`->>`，等效JSON_UNQUOTE(JSON_EXTRACT())函数，将值取消引号，返回utf8mb4_bin编码，区分大小写；

致使创建索引/基于索引查询时，如果字符集不匹配则无法命中索引  
utf8mb4_bin字符按二进制，区分大小写

```sql
index ((cast(student ->> '$.teacherId' as char(19)) collate utf8mb4_bin))
```

```sql
select * from user u where u.student->>'$.teacherId'='1069607508454658048';
```

json_arrayagg()函数，将字段转为json数组。

```sql
select ps.id, 
       json_arrayagg(json_object('teahcerId', ps.teacher_id, 'processId', ps.process_id, 'score', ps.score)) as json
from process_score ps group by ps.id;
```

全表扫描，查询所有导师已选数量

```sql
select u.name, count(u.id) as count, u.teacher -> '$.total' as total
from user u
         join user u2
where u2.student ->> '$.teacherId' = u.id
group by u.id;
```

指定导师已选数量

```sql
select u.id, u.name, count(u.id) as count, u.teacher -> '$.total' as total
from user u
         join user u2
where u.id = :tid
  and u2.student ->> '$.teacherId' = u.id
group by u.id
having count < total;
```

更新时使用表锁确保查询数据的准确性，但执行效率？

```sql
update user u, (select u.id, count(u.id) as count, u.teacher -> '$.total' as total
                from user u
                         join user u2
                where u.id = :tid
                  and u2.student ->> '$.teacherId' = u.id
                group by u.id
                having count < total) u2
set u.student=json_object('teacherId', :tid)
where u.id = :sid;
```

JSON_TABLE()函数，可将json字段作为table处理，从数组中提取属性值。不知道执行效率怎样。  
concat()函数，拼接字符串。

添加/更新数组中元素。不存在时，在数组中添加；存在，则更新。不建议处理过大的json数据。  
使用isnull()函数替代on连接，可在null时将索引置于最后。

```sql
update process_score pt
    join JSON_TABLE(
            pt.detail,
            '$[*]'
            columns (
                `id` for ordinality,
                `tid` char(19) path '$.teacherId'
                )
        ) jt
set pt.detail=json_set(
    pt.detail,
   if(isnull(json_search(pt.detail, 'one', '1127183851567038464')),
      concat('$[', json_length(pt.detail), ']'),
      json_unquote(replace(json_search(pt.detail, 'one', '1127183851567038464', null, '$**.teacherId'), '.teacherId',
                           ''))),
                   json_object('teacherId', '1127183851567038464', 'score', 2))
where pt.id = '1133711988347621376';
```

可在json table中查询，但属于on，查询结果为空时无法更新

```sql
update process_test ps join JSON_TABLE(
        ps.detail,
        '$[*]'
        columns (
            `id` for ordinality,
            `tid` char(19) path '$.teacherId'
            )
    ) jt on jt.tid = '1127183851567038464'
set ps.detail = json_set(ps.detail, concat('$[', jt.id - 1, ']'),
                         json_object('teacherId', '1127183851567038464', 'score', 3))
where pt.id = '1133711988347621376';
```

### Batch Update

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

### PathPattern

spring提供PathPattern类实现路径模糊匹配功能，可用于webflux拦截器判断。

### Publisher Errors

Reactive Publisher支持直接添加返回异常对象，而无需像传统方法的抛出捕获异常！~
即Mono/flux可以封装正常元素，以及异常对象，在订阅时单独以流的方式处理异常对象而非捕获。

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

### Publisher Operators Not be Invoked

First of all, remember that operators such .map, .flatMap, .filter and many others will not be invoked at all if
there no onNext has been invoked. That means that in your case next code.  
Using operators .switchIfEmpty/.defaultIfEmpty/Mono.repeatWhenEmpty.

mapNotNull()，允许映射null于新发布者。which is allowed to produce a null value.  
switchIfEmpty()，当订阅的元素为空时，回调。

结合使用map()/flatMap()/filter()/mapNotNull()/switchIfEmpty()/onErrorResume()/Mono.error()等方法。

```java
public Mono<ResultVO> login() {
    return userService.getUser()
                      .filter()
                      .defaultIfEmpty();
}
```

预在返回空后执行异步操作时

```java
.switchIfEmpty(Mono.defer(() -> {}));
```

### Persistable

没用上

DO类实现`Persistable<ID>`接口重写isNew()方法，决定当对象包含ID值时执行save()方法时映射SQL insert还是update语句，
可用于手动添加共用主键(JPA使用注解即可)。

### MySQL Index

MySQL索引类型与传入参数类型不匹配可能使索引失效。  
当索引类型为数字，传入数字或字符串MySQL均可完成隐形转换命中索引；  
但当索引类型为字符串，如果传入数字类型参数将无法命中索引。  
因此查询使类型一定要匹配。

### MySQL JSON Multi-Valued Indexes

[MySQL Multi-Valued Indexes](https://dev.mysql.com/doc/refman/8.0/en/create-index.html#create-index-multi-valued)

json数组字段声明多值索引，适合多标签声明及检索。  
支持复合索引。  
限制较多，当前支持命中索引的函数：member of，等3个。

```sql
INDEX (( cast(group_number AS unsigned array) )),
```

### Docker

终于实现基于healthcheck检测容器中程序运行状况，从而按顺序启动容器。
