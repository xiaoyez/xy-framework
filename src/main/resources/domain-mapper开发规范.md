# 1.Service层

## 1.1 核心接口介绍

1. IBaseService,所有Service的父接口，定义了最基本的单表增删改查，一切Service必须直接或间接的继承或实现此接口，其定义如下:

   ```java
   /**
    * 基础Service
    * @param <T> 实体类
    * @param <M> 实体类对应的TkMapper类
    */
   public interface IBaseService <T, M extends Mapper<T>>{
       // 根据主键获取对象
       T getByPrimaryKey(Object primaryKey);
   
       // 条件查询获取对象
       T getOne(T domain);
   
       // 条件查询获取对象集合
       List<T> gets(T domain);
   
       // 获取所有对象集合
       List<T> getAll();
   
       // 插入对象
       int insert(T domain);
   
       // 修改对象
       int update(T domain);
   
       // 条件查询删除对象
       int delete(T domain);
   
       // 根据主键删除对象
       int deleteByPrimaryKey(Object primaryKey);
       
       // 根据主键批量删除对象
       int deleteByPrimaryKeys(List<?> primaryKeys);
   }
   ```

   其实现类为BaseServiceImpl，已将里面的方法都实现了，开发service实现类的时候只需要继承BaseServiceImpl即可

2. IBatchService,定义了最基本的批量操作，其定义如下

   ```java
   public interface IBatchService <T, M extends Mapper<T> & IBatchMapper<T>>
       extends IBaseService<T,M> {
   
       int batchInsert(List<T> list);
   
       int batchUpdate(List<T> list);
   }
   ```

   其实现类为BatchServiceImpl,已将里面的方法都实现了，若开发时需要进行批量新增或更新，可让底层service直接继承BatchServiceImpl即可

3. IMainTableService,定义了对主表及其子表的关联操作，所有主表对应Service需要继承此Service，其定义如下:

   ```java
   /**
    * 主表Service
    * @param <T> 主表对应实体类
    * @param <M> 主表对应Mapper
    */
   public interface IMainTableService<T extends IMainTableObject,
           M extends Mapper<T> & IMainTableMapper<T>
           >
           extends IBaseService<T,M>{
   
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
   }
   ```

   其实现类为MainTableServiceImpl，已将里面的方法都实现了，开发主表对应service实现类的时候只需要继承MainTableServiceImpl即可

4. IChildTableMultiService,定义了一对多子表的批量操作，所有一对多子表对应的service需要继承或实现这个接口，其定义如下:

   ```java
   /**
    * 一对多子表Service
    * @param <T> 一对多子表对应实体类
    * @param <M> 一对多子表对应Mapper
    */
   public interface IChildTableMultiService<T extends IChildTableMultiObject, M extends Mapper<T> & IChildTableMultiMapper<T>>
           extends IBatchService<T,M>{
   
       int batchInsert(List<T> list);
   
       int batchUpdate(List<T> list);
   
       int batchUpdate(List<T> oldList, List<T> newList);
   }
   
   ```

   其实现类为ChildTableMultiServiceImpl,已将里面的方法都实现了，开发一对多子表对应service实现类的时候只需要继承ChildTableMultiServiceImpl即可

## 2.2 service分层概述

本系统service分为通用service(底层Service)和特殊service(高层service):

- 底层service指的是定义在cgmanage-domain-mapper模块的service,其特点为直接操作mapper与数据库打交道
- 高层service指的是定义在cgmanage-admin模块的service，这种service禁止调用mapper，只能调用底层service，其特点为做数据库操作之前或之后的数据处理
- 底层service禁止实现本框架已经实现过的函数，必须去继承已有的实现类
- 高层service接口命名以FrontService结尾，实现类以FrontServiceImpl结尾


# 2.实体类层

## 2.1 核心接口介绍

1. IMainTableObject: 主表对象接口，主表对应的实体类需要实现这个接口
2. IChildTableSingleObject: 与主表一对一的子表对象接口，若一个子表与主表是一对一的关系，则这个子表对应的实体类需要实现这个接口
3. IChildTableMultiObject: 与主表一对多的子表对象接口，若一个子表与主表是一对多的关系，则这个子表对应的实体类需要实现这个接口



## 2.2 实体类通用开发规范
1. 所有实体类都必须放在cgmanage-domain-mapper模块的com.cgmanag.domain包或其子包下

2. 主键对应的属性必须加`@Id`注解，若这个主键是自增的，还需要加

   ```java
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   ```

3. 若一个属性不存在数据库里对应的字段，则该属性必须加`@Transient`注解




## 2.3 主表对应实体类的开发

1. 主表对应实体类必须实现IMainTableObject接口
2. 主表与子表对应的外键需要加`@MainFK`注解，如假设一个主表A有一个子表B，A的id字段是B的a_id的外键，则A类的id属性必须加`@MainFK`注解
3. 主表里与子表一对一的属性字段必须加`@SingleChildTableField`注解，如假设主表A和子表B是一对一，则类A的属性`private B b;`必须加一个`@SingleChildTableField`注解
4. 主表里与子表一对多的属性字段必须加`@MultiChildTableField`注解，如假设主表A和子表B是一对多，则类A的属性`private List<B> bs;`必须加一个`@MultiChildTableField`注解



## 2.4 子表对应实体类的开发

1. 一对一子表对应实体类必须实现IChildTableSingleObject
2. 一对多子表对应实体类必须实现IChildTableMultiObject
3. 子表与主表对应的外键需要加@ChildFK注解，如假设一个主表A有一个子表B，A的id字段是B的a_id的外键，则B类的a_id属性必须加`@ChildFK`注解



# 3.DAO层

## 3.1 通用规范

1. 所有实体类对应Mapper必须以TkMapper结尾

2. 所有实体类对应Mapper必须放在cgmanage-domain-mapper模块的com.cgmanag.mapper包或其子包下

3. 所有实体类对应的Mapper必须继承tk.mybatis.mapper.common.Mapper接口

4. 若这个Mapper需要批量操作在，则需要继承IBatchMapper接口

   



## 3.2 主表对应Mapper的开发

1. 主表Mapper必须继承IMainTableMapper接口
2. 主表Mapper必须重写IMainTableMapper接口中的4个方法

```java
    // T 为实体类类型
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

示例:

```java
public interface ManageCtExpenseApplyTkMapper
        extends Mapper<ManageCtExpenseApply> ,
        IMainTableMapper<ManageCtExpenseApply> {

    @ResultMap(value = "ManageCtExpenseApplyResult")
    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectCompleteByPrimaryKey"
    )
    ManageCtExpenseApply selectCompleteByPrimaryKey(@Param("pk") Object primaryKey,
                                 @Param("className") String className);

    @ResultMap(value = "ManageCtExpenseApplyResult")
    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectCompleteOne"
    )
    ManageCtExpenseApply selectCompleteOne(ManageCtExpenseApply domain);

    @ResultMap(value = "ManageCtExpenseApplyResult")
    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectComplete"
    )
    List<ManageCtExpenseApply> selectComplete(ManageCtExpenseApply domain);

    @ResultMap(value = "ManageCtExpenseApplyResult")
    @SelectProvider(
            type = MainTableProvider.class,
            method = "selectCompleteAll"
    )
    List<ManageCtExpenseApply> selectCompleteAll(@Param("className") String className);
}
```

## 3.4 主表的resultMap

- 主表的resultMap映射规则需写在对应的xml文件中
- 主表的resultMap可以借助工具类MybatisUtils的`buildResultMap()`方法来生成



## 3.3 一对多子表对应Mapper的开发

1. 一对多子表Mapper必须继承IChildTableMultiMapper接口


# 4.开发顺序
实体类 -> DAO -> Service



# 5.插件式开发理念

- DAO层和Service层采用了插件式开发理念，将各个功能拆分到各个接口里，需要什么功能可以通过直接继承这些接口来简化开发
- 目前已实现的插件mapper和插件service:
  - 权限相关: IAuthMapper和IAuthService
  - 批量删除: IBatchDeleteMapper
  - 批处理: IBatchMapper 和IBatchService
  - 批量查询(根据多个主键查询): IBatchSelectMapper
  - 搜索相关: ISearchMapper/ISearchAuthMapper和ISearchService/ISearchAuthService
- 注意: 不要随意继承插件mapper或插件service，只有当你确定你需要这个功能时才去继承，否则可能会导致代码出现无法预料的bug



# 6. 部分插件使用说明

## 6.1 搜索相关: ISearchMapper/ISearchAuthMapper和ISearchService/ISearchAuthService

- 此类插件功能是提供搜索功能，原理为根据注解限定的条件去搜索
- 注解需标注在实体类的属性上
- 注解:
  - @Condition: 声明这个属性是搜索条件之一，且搜索条件默认为 = 
  - @Like: 声明这个属性对应的搜索条件是模糊查询，即 like
    - prefix： 前缀，默认为%
    - suffix: 后缀，默认为%



## 6.2 权限相关: IAuthMapper/ISearchAuthMapper和IAuthService/ISearchAuthService

- 此类插件功能是提供权限功能
- 对应的实体类需继承BaseAuthEntity类