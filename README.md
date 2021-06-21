# quick-lib-restlet

基于Mybatis-Plus的基础RESTful增删改查接口实现。

基本宗旨是以最简单的方式实现数据的基础增删查改接口，同时保证一定的可扩展性。

## 引用

```xml

<dependency>
    <groupId>plus.extvos</groupId>
    <artifactId>quick-lib-restlet</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 基础用法

### `Entity` 定义

```java
package plus.extvos.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("students")
@Data
public class Student {
    @TableId(type = IdType.AUTO)
    private long id;

    private String name;

    private String gender;
}

```

### `Mapper`定义

```java
package plus.extvos.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import plus.extvos.example.entity.Student;

public interface StudentMapper extends BaseMapper<Student> {
}

```

### `Service`定义

```java
package plus.extvos.example.service;

import plus.extvos.example.entity.Student;
import plus.extvos.restlet.service.BaseService;

public interface StudentService extends BaseService<Student> {
}

```

### `ServiceImpl`定义

```java
package plus.extvos.example.service.impl;

import plus.extvos.example.entity.Student;
import plus.extvos.example.mapper.StudentMapper;
import plus.extvos.example.service.StudentService;
import plus.extvos.restlet.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements StudentService {
    @Autowired
    private StudentMapper myMapper;

    @Override
    public StudentMapper getMapper() {
        return myMapper;
    }
}
```

### `Controller`定义

```java
package plus.extvos.example.controller;

import plus.extvos.example.entity.Student;
import plus.extvos.example.service.StudentService;
import plus.extvos.restlet.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
public class StudentController extends BaseController<Student, StudentService> {
    @Autowired
    private StudentService studentService;

    @Override
    public StudentService getService() {
        return studentService;
    }
}
```

### 至此支持的API接口

- `GET` `/students?xx=xx&xx=xx`
- `GET` `/students/{id}`
- `POST` `/students`
- `PUT` `/students?xx=xx&xx=xx`
- `PUT` `/students/{id}`
- `DELETE` `/students?xx=xx&xx=xx`
- `DELETE` `/students/{id}`

### 常用返回数据样式（JSON）

接口通常返回的数据格式以以下格式为样例：

```JSON
{
  "code": 20000,
  "msg": "",
  "data": null
  | {} | [],
"total": NUM,
"page": NUM,
"pageSize": NUM
}
```

- `code`: 接口执行结果。`20XXX` 为成功，`code`值除以`100`得数为HTTP的状态码，具体错误码以实际项目定义为基准；
- `msg`: 错误消息。失败时返回的错误提示消息。成功时可以不包含该数据；
- `data`：返回的数据内容。可以为单个对象或对象列表；无数据可以返回`null`；
- `total`：当前查询条件下面的总记录数。（仅列表查询时可用）
- `page`：当前返回的页码。（起始为0，仅列表查询时可用）
- `pageSize`：每页的数量。（仅列表查询时可用）

### 接口Query参数使用规范

#### 特殊控制参数

- `__page`：分页查询页码控制，默认为`0`；
- `__pageSize`：分页查询每页大小控制，默认为`20`或`50`；
- `__orderBy`：字段排序控制，字段前面加`-`表示降序，否则为升序；多个控制之间以`,`分隔；
- `__includes`：仅包含指定字段，多个字段以`,`分隔，表格主键与此参数无关，必须包含在输出里面；
- `__excludes`: 排除指定字段，多个字段以`,`分隔，表格主键与此参数无关，必须包含在输出里面；

**注：**`__includes`与`__excludes`是互斥的，`__includes`优先级高于`__excludes`

特殊控制参数可以在配置中指定，请参考 [配置说明](#配置说明)

#### 一般查询条件参数

- `{fieldName}=VALUE`

  字段值等于匹配，适用于多种类型字段，等同于： `WHERE fieldName = VALUE`

- `{fieldName}__not=VALUE`

  字段不等于匹配，适用于多种字段类型，等同于： `WHERE fieldName <> VALUE`

- `{stringFieldName}__contains=VALUE`

  字符型字段包含匹配，等同于： `WHERE stringFieldName LIKE %VALUE%`

- `{stringFieldName}__startwith=VALUE`

  字符型字段包含匹配，等同于： `WHERE stringFieldName LIKE VALUE%`

- `{stringFieldName}__endwith=VALUE`

  字符型字段包含匹配，等同于： `WHERE stringFieldName LIKE %VALUE`

- `{numberFieldName}__gt=VALUE`

  数值型字段大于匹配，等同于： `WHERE numberFieldName > VALUE`

- `{numberFieldName}__gte=VALUE`

  数值型字段大于等于匹配，等同于： `WHERE numberFieldName >= VALUE`

- `{numberFieldName}__lt=VALUE`

  数值型字段小于匹配，等同于： `WHERE numberFieldName < VALUE`

- `{numberFieldName}__lte=VALUE`

  数值型字段小于等于匹配，等同于： `WHERE numberFieldName <= VALUE`

- `{numberFieldName}__range=VALUE1,VALUE2`
- `{numberFieldName}__between=VALUE1,VALUE2`


- `{numberFieldName}__in=VALUE1,VALUE2,VALUE3...`


- `{fieldName}__notnull`

- `{fieldName}__isnull`

以上数值型的特殊匹配方式，同样适用于日期、时间型数据字段。

#### `或`查询

在上述查询条件的方式基础上，可以添加`or`作为`或`条件查询，同样是以`__`方式，在字段名和操作符之间分隔，比如：

- `{fieldName}__or`
- `{fieldName}__or__lt`
- `{fieldName}__or__gt`
- `{fieldName}__or__in`
- `{fieldName}__or__contains`

等等。 比如：
`GET /students?age__lt=12&age__or__gt=16` 则等同于 `SELECT * FROM students WHERE (age < 12 OR age > 16)`。

## 配置说明

`restlet`支持如下配置：

```yaml
quick:
  restlet:
    log-trace: true
    page-key: __page
    page-size-key: __pageSize
    order-by-key: __orderBy
    includes-key: __includes
    excludes-key: __excludes
    default-page-size: 10
    pretty-json: false
    delete-response-body: false
```

- `log-trace`：指定是否在日志中输出异常信息，默认为`false`
- `page-key`：指定分页参数名，默认为`__page`
- `page-size-key`：指定分页大小参数名，默认为`__pageSize`
- `order-by-key`：指定排序参数名，默认为`__orderBy`
- `includes-key`：指定包含字段参数名，默认为`__includes`
- `excludes-key`：指定排除字段参数名，默认为`__excludes`
- `default-page-size`：默认分页大小，默认为`50`
- `pretty-json`：是否输出为整齐的`JSON`格式，默认为`false`则输出紧凑格式
- `delete-response-body`: 是否在删除成功后输出一个标准Result，默认情况下删除成功后返回`204`状态值不带任何Content。

## 进阶

### 自定义接口、自定义查询

#### 单独新增自定义查询条件

在`ServiceImpl`中重写下面这个方法：

```java
public boolean parseQuery(String k,Object v,QueryWrapper<?> wrapper){
        return false;
        }
```

注：重写方法里面仅处理自定义的查询条件，处理了的返回`true`，未处理则返回`false`。

#### 更复杂的

请跟着Mybatis + Mybatis-Plus走


### 默认排除或包含字段

```java
public String[] defaultIncludes();
public String[] defaultExcludes();
```

在`Controller`中`Override`上面连个方法，可以添加默认在查询时要包含或排除的字段，具体规则仍然如`__includes`和`__excludes`。


### 接口拦截处理

在`Controller`中`Override`下列方法：

```java
public T preInsert(T entity)throws RestletException;
public List<T> preInsert(List<T> entities)throws RestletException;
public void postInsert(T entity)throws RestletException;
public void postInsert(List<T> entities)throws RestletException;


public T preUpdate(Serializable id,T entity)throws RestletException;
public T preUpdate(QuerySet<T> qs,T entity)throws RestletException;
public void postUpdate(Serializable id,T entity)throws RestletException;
public void postUpdate(QuerySet<T> qs,T entity)throws RestletException;

public void preDelete(Serializable id)throws RestletException;
public QuerySet<T> preDelete(QuerySet<T> qs)throws RestletException;
public void postDelete(Serializable id)throws RestletException;
public void postDelete(QuerySet<T> qs)throws RestletException;

public void preSelect(Serializable id)throws RestletException;
public QuerySet<T> preSelect(QuerySet<T> qs)throws RestletException;
public T postSelect(T entity)throws RestletException;
public List<T> postSelect(List<T> entities)throws RestletException;
```

以上任意方法的重写，皆可以对操作进行拦截处理或输出重写。

### `plus.extvos.restlet.Assert`类

提供了一些基本的数据有效性断言，断言失败会抛出`RestletException`异常，亦可自定义异常。

例如：

```java

@RestController
@RequestMapping("/students")
public class StudentController extends BaseController<Student, StudentService> {

    @Override
    public Student preInsert(Student entity) throws RestletException {
        Assert.notEmpty(entity.getName(), RestletException.badRequest("name can not be null or empty"));
        return entity;
    }
}
```

### `plus.extvos.restlet.Code` 接口

定义了一个返回结果的Code接口，如`ResultCode`提供了一些基本的返回结果Code定义。如需扩展，请按照`ResultCode`的方式扩展，同时，保证`value()`返回的值除以`100`所得的数值为接口返回的`HTTP`
状态值，且满足`HTTP`的标准定义。

### `plus.extvos.restlet.Result` 类

是`restlet`基础的接口返回数据类型。可以在其它自实现的Controller接口中使用，以保证返回数据的格式一致性。

