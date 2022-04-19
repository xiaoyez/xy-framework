# 1. 介绍

xy-framework是一个后台辅助开发工具包，其目的是为了将java后端程序员从繁重的增删改查中解脱出来

**系统需求**

- JDK >= 1.8
- Maven >= 3.0



# 2. 快速了解

## 项目简介

xy-framework是一款基于SpringBoot + MyBatis + TkMapper的后台辅助开发工具包



## 主要特性

- 使用接口与注解配合来配置表与表之间的关系
- 提供泛型接口Mapper和IBaseService，其提供了最基本的增删改查操作，开发者只需要继承或实现接口便可拥有对数据库的增删改查操作
- 提供条件查询的注解，让开发者可以进一步的少写sql
- 拥有良好的扩展性，开发者可以很方便的扩展此框架
- 以上所有特性均为非侵入式



## 技术选型

### 1. 系统环境

- jdk 8
- Servlet 3
- Apache Maven 3



## 2. 主框架

- mapper-spring-boot-starter  2.1.2



### 3. 工具包

- hutool-all 5.5.6
- lombok 1.18.20



# 3. 文件结构

```
com.xy.common
|-- domain			// 实体类层
|	|-- annotation 		//注解
|	|	·|-- search  		//条件查询用注解
|-- exception 		// 自定义异常
|-- factory 		// 工厂
|-- mapper  			// 持久层
|	|-- provider 			// Mybatis Provider
|-- service 		// service层，存放service接口
|	|-- impl 			// service实现类
|-- utils 			// 工具包
|	|-- mybatis 		// Mybatis工具包
|	|-- spring 			// Spring工具包
|	|-- xml 			// xml工具包
|	|	|-- definition		// xml结构定义
```



# 4. 具体功能

## 主子表关系配置

- 主表实体类实现 `IMainTableObject` 接口
- 一对一子表实体类实现 `ISingleChildTableObject` 接口
- 一对多子表实体类实现 `IMultiChildTableObject` 接口
- 主表实体类里的一对一子表属性标注 `@SingleChildTableField(serviceClass = 子表对应service接口.class)`
- 主表实体类里的一对多子表属性标注 `@MultiChildTableField(serviceClass = 子表对应service接口.class)`
- 主表被子表引用的键对应属性标注 `@MainRefKey`
- 子表外键标注 `@ChildFk(mainTableClass = 主表实体类.class)`



demo:

- 现在有主表名为main_table1,其一对一子表为single_child1,其一对多子表为multi_child1
- single_child1的字段main_id引用main_table1的字段id，类型为int
- multi_child1的字段main_id引用main_table1的字段id，类型为int
- single_child1对应service接口为ISingleChild1Service
- multi_child1对应service接口为IMultiChild1Service



则对应代码如下:

- MainTable1.java

  ```java
  // package 和 import
  
  //lombok注解...
  public class MainTable1 implements IMainTableObject{
      
      //其他注解...
      @MainRefKey
      private Integer id;
      
      //其他字段...
      
      //其他注解...
      @Transient
      @SingleChildTableField(serviceClass = ISingleChildService.class)
      private SingelChild1 singleChild1;
      
      //其他注解...
      @Transient
      @MultiChildTableField(serviceClass = IMultiChildService.class)
      private List<MultiChild1> multiChild1List;
      
      
  }
  ```

- SingleChild1.java

  ```java
  // package 和 import
  
  //lombok注解...
  public class SingleChild1 implements ISingleChildTableObject{
      
      //其他字段... 
      
      //其他注解...
      @ChildFk(mainTableClass = MainTable1.class)
      private Integer mainId;
      
  }
  ```

- MultiChild1.java

  ```java
  // package 和 import
  
  //lombok注解...
  public class MultiChild1 implements IMultiChildTableObject{
      
      //其他字段... 
      
      //其他注解...
      @ChildFk(mainTableClass = MainTable1.class)
      private Integer mainId;
      
  }
  ```



## Mapper介绍

**本章节介绍的所有Mapper接口均属于com.xy.common.mapper包**

### Mapper\<T> 

基础Mapper，提供了最基本的增删改查，其继承了TkMapper框架的Mapper\<T>接口、IBatchMapper\<T>接口、ISearchMapper\<T>,常用方法如下:

- T selectByPrimaryKey(Object primaryKey) : 根据主键获取对象

- T selectOne(T record): 根据record对象里值不为null且不被@Transient标注且不被transient关键字修饰的属性去查询，返回查询到的对象，若没查到则返回null，若查询到多个对象会抛出异常

- List\<T> select(T record):   根据record对象里值不为null且不被@Transient标注且不被transient关键字修饰的属性去查询，返回查询到的对象集合，若没查到则返回null

- List\<T>  selectAll(): 查询表里所有数据，返回对象集合

- int insertSelective(T record): 插入record对象，insert语句会忽略record里值为null或被@Transient标注或被transient关键字修饰的属性

- int updateSelective(T record): 根据record的主键属性(被@Id 标注的属性)更新record对象，update语句会忽略record里值为null或被@Transient标注或被transient关键字修饰的属性

- int deleteByPrimaryKey(Object primaryKey): 根据传入的主键值删除对象

- int deleteByPrimaryKeys(List<?> primaryKeys): 根据传入的主键集合删除对象

- int batchInsert(List\<T> list): 批量插入对象，insert语句会忽略list里的对象里被@Transient标注或被transient关键字修饰的属性

- int batchUpdateSelective(List\<T> list): 批量更新对象，update语句会忽略list里的对象里值为null或被@Transient标注或被transient关键字修饰的属性

- int batchUpdate(List\<T> list): 批量更新对象，update语句会忽略list里的对象里被@Transient标注或被transient关键字修饰的属性

- List\<T> selectByPrimaryKeys(List<?> primaryKeys): 根据传入的主键集合获取对象集合

- List\<T> search(T domain): 根据T 类里各字段标注的条件查询注解来搜索，具体查看[条件查询Mapper(搜索Mapper)](#ISearchMapper)

  

### 主表Mapper

IMainTableMapper\<T extends IMainTableObject>: 主表Mapper，主表对应的mapper应当继承此接口，并重写所有方法,以及标注@ResultMap:

方法名带Complete的表示获取主表以及子表

```java
@SelectProvider(
        type = MainTableProvider.class,
        method = "selectCompleteByPrimaryKey"
)
T selectCompleteByPrimaryKey(@Param("pk") Object primaryKey,
                             @Param("className") String className);

@SelectProvider(
        type = MainTableProvider.class,
        method = "selectCompleteOne"
)
T selectCompleteOne(T domain);

@SelectProvider(
        type = MainTableProvider.class,
        method = "selectComplete"
)
List<T> selectComplete(T domain);

@SelectProvider(
        type = MainTableProvider.class,
        method = "selectCompleteAll"
)
List<T> selectCompleteAll(@Param("className") String className);
```

demo: 现在有主表名为main_table1，其对应ResultMap名为MaionTable1ResultVo,则代码如下

```java
// package 和 import

public interface MainTable1Mapper extends IMainTableMapper<MainTable1>{
    
    @ResultMap("MaionTable1ResultVo")
    @SelectProvider(
        type = MainTableProvider.class,
        method = "selectCompleteByPrimaryKey"
	)
	MainTable1 selectCompleteByPrimaryKey(@Param("pk") Object primaryKey,
                             @Param("className") String className);

    @ResultMap("MaionTable1ResultVo")
	@SelectProvider(
        type = MainTableProvider.class,
        method = "selectCompleteOne"
	)
	MainTable1 selectCompleteOne(T domain);

    @ResultMap("MaionTable1ResultVo")
	@SelectProvider(
        type = MainTableProvider.class,
        method = "selectComplete"
	)
	List<MainTable1> selectComplete(MainTable1 domain);

    @ResultMap("MaionTable1ResultVo")
	@SelectProvider(
        type = MainTableProvider.class,
        method = "selectCompleteAll"
	)
	List<TMainTable1> selectCompleteAll(@Param("className") String className);
    
    //其他方法...
}
```

### 一对多子表Mapper

IChildTableMultiMapper\<T extends IChildTableMultiObject>，一对多子表对应Mapper，一对多子表对应的Mapper应当继承此接口

demo: 现在有主表名为main_table1,其一对多子表为multi_child1,则代码如下

```java
// package 和 import

public interface MultiChild1Mapper extends IChildTableMultiMapper<MultiChild1>{
    // 其他方法...
}
```



### 一对一子表Mapper

一对一子表对应Mapper直接继承Mapper即可

demo: 现在有主表名为main_table1,其一对一子表为single_child1

```java
// package 和 import

public interface SingleChild1Mapper extends Mapper<SingleChild1>{
    // 其他方法...
}
```



### <a name="ISearchMapper">条件查询Mapper(搜索Mapper)</a>

ISearchMapper\<T>: 用于搜索对象

#### 条件查询注解

- @Condition: 表示这个字段会作为搜索条件,若该字段上再无其他条件查询注解，则条件查询规则为 = 
- @Equal: 将条件查询规则设为 = 
- @NotEqual: 将条件查询规则设为 !=
- @GreaterThan: 将条件查询规则设为 >
- @LessThan: 将条件查询规则设为 <
- @GreaterEqual: 将条件查询规则设为 >=
- @LessEqual: 将条件查询规则设为 <=
- @Like: 将条件查询规则设为 like
  - prefix: 前缀，默认为%
  - suffix: 后缀,  默认为%



## Service介绍

### 基础service

IBaseService \<T, M extends Mapper\<T>>,一切service的基类，其实现类为BaseServiceImpl,其提供的方法如下:

- M getMapper(): 获取mapper对象
- T getByPrimaryKey(Object primaryKey) : 根据主键获取对象
- T getOne(T domain): 根据domain对象里 值不为null或空串 且不被@Transient标注且不被transient关键字修饰的属性去查询，返回查询到的对象，若没查到则返回null，若查询到多个对象会抛出异常
- T getOne(T domain,boolean ignoreEmptyString)：根据domain对象里值不为null且不被@Transient标注且不被transient关键字修饰的属性去查询，返回查询到的对象，若没查到则返回null，若查询到多个对象会抛出异常，ignoreEmptyString表示是否忽略空串
- List\<T> gets(T domain) : 根据domain对象里 值不为null或空串 且不被@Transient标注且不被transient关键字修饰的属性去查询，返回查询到的对象集合，若没查到则返回null
- List\<T> gets(T domain,boolean ignoreEmptyString)：根据domain对象里值不为null且不被@Transient标注且不被transient关键字修饰的属性去查询，返回查询到的对象集合，若没查到则返回null，ignoreEmptyString表示是否忽略空串
- List\<T> getAll(): 查询表里所有数据，返回对象集合
- int insert(T domain) : 插入record对象，insert语句会忽略record里值为null或被@Transient标注或被transient关键字修饰的属性
- int update(T domain) : 根据record的主键属性(被@Id 标注的属性)更新record对象，update语句会忽略record里值为null或被@Transient标注或被transient关键字修饰的属性
- int update(T domain, boolean isSelective): 根据record的主键属性(被@Id 标注的属性)更新record对象，update语句会忽略record里被@Transient标注或被transient关键字修饰的属性,isSelective表示是否忽略值为null的字段
- int delete(T domain): 根据domain对象里 值不为null或空串 且不被@Transient标注且不被transient关键字修饰的属性去删除对象
- int deleteByPrimaryKey(Object primaryKey):  根据给定主键删除对象
- int deleteByPrimaryKeys(List\<?> primaryKeys):  根据给定主键集合删除对象
- List\<T> getByPrimaryKeys(List\<?> primaryKeys): 根据给定主键集合查询对象，返回对象集合



### 批量操作service

IBatchService<T, M extends Mapper<T>>,批量操作service，其方法如下:

- int batchInsert(List\<T> list): 批量插入list集合
- int batchUpdate(List\<T> list): 批量更新list集合
- int batchUpdate(List\<T> list, boolean isSelective): 批量更新list集合，isSelective表示是否忽略值为null的字段



### 条件查询service

ISearchService\<T, M extends Mapper\<T>>,条件查询service，其方法如下:

- List\<T> search(T domain): 根据T 类里各字段标注的条件查询注解来搜索，具体查看[条件查询Mapper(搜索Mapper)](#ISearchMapper)



### 主表service

IMainTableService<T extends IMainTableObject,
        M extends Mapper\<T> & IMainTableMapper\<T>>，其实现类为MainTableServiceImpl

主表service，其方法如下:

```
/**
 * 根据主键获取主表及子表
 * @param primaryKey
 * @return
 */
T getCompleteByPrimaryKey(Object primaryKey);

/**
 * 根据条件查主表及子表
 * @param domain
 * @return
 */
T getCompleteOne(T domain);

/**
 * 根据条件查主表及子表
 * @param domain
 * @return
 */
List<T> getsComplete(T domain);

/**
 * 获取所有的主表及子表
 * @return
 */
List<T> getCompleteAll();

/**
 * 新增主表以及子表
 * @param domain
 * @return
 */
int insertComplete(T domain);

/**
 * 更新主表以及子表
 * @param domain
 * @return
 */
int updateComplete(T domain);

/**
 * 更新主表以及子表
 * @param domain
 * @param isSelective 是否忽略值为null的属性
 * @return
 */
int updateComplete(T domain,boolean isSelective);
```

demo: 现有主表名为my_main_table，则代码如下

IMyMainTableService.java

```java
// package和import

public interface IMyMainTableService extends IMainTableService<MyMainTable,MyMainTableMappaer>{
    
    // 其他方法...
}
```



MyMainTableServiceImpl.java

```java
// package和import

// 其他注解...
@Service
public class MyMainTableService extends MainTableServiceImpl<MyMainTable,MyMainTableMappaer>
    implements IMyMainTable1Service{
    
    // 其他方法...
}
```





### 一对多子表service

IChildTableMultiService\<T extends IChildTableMultiObject, M extends Mapper\<T> & IChildTableMultiMapper\<T>>，其实现类为ChildTableMultiServiceImpl

一对多子表service

其方法如下:

- int batchUpdate(List\<T> oldList, List\<T> newList) : 批量更新，将oldList更新为newList
- int batchUpdate(List\<T> oldList, List\<T> newList, boolean isSelective) : 批量更新，将oldList更新为newList,isSelective表示是否忽略值为null的属性



demo: 现有主表名为my_main_table，其一对多子表为my_multi_child,则代码如下

IMyMultiChildService.java:

```java
// package和import

public interface IMyMultiChildService extends IMultiChildTableService<MyMultiChild,MyMultiChildMappaer>{
    
    // 其他方法...
}
```



MyMultiChildServiceImpl.java:

```java
// package和import

public class MyMultiChildServiceImpl extends MultiChildTableServiceImpl<MyMultiChild,MyMultiChildMappaer>
    implements IMyMultiChildService{
    
    // 其他方法...
}
```

