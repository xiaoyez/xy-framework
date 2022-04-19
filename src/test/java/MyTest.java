import com.xy.common.domain.IChildTableMultiObject;
import com.xy.common.domain.IMainTableObject;
import com.xy.common.domain.annotation.ChildFK;
import com.xy.common.domain.annotation.MainRefKey;
import com.xy.common.domain.annotation.MultiChildTableField;
import com.xy.common.domain.annotation.search.Condition;
import com.xy.common.factory.ConditionBeanFactory;
import com.xy.common.mapper.IChildTableMultiMapper;
import com.xy.common.mapper.provider.MainTableProvider;
import com.xy.common.service.IChildTableMultiService;
import com.xy.common.utils.mybatis.MybatisUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import org.junit.Test;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author xiaoye
 * @create 2021-12-30 17:02
 */
public class MyTest {

    @Test
    public void test(){
        User user = ConditionBeanFactory.create(User.class);
        System.out.println(user);
    }

    @Test
    public void testSelectCompleteByPrimaryKey()
    {
        final val mainTableProvider = new MainTableProvider();
        final val s = mainTableProvider.selectCompleteByPrimaryKey(1, A.class.getName());
        System.out.println(s);
    }

    @Test
    public void testBuildResultMap()
    {
        System.out.println(MybatisUtils.buildResultMap(A.class));
    }

    @AllArgsConstructor
    @Data
    public static class User{

        @Condition(defaultValue = "aaa")
        private String username;
        private String password;

        public User(){}
    }

    @Data
    static class A implements IMainTableObject {
        @Id
        @MainRefKey
        private Integer id;

        @Column
        private String name;

        @Transient
        @MultiChildTableField(serviceClass = BService.class)
        private List<B> bs;
    }

    @Data
    static class B implements IChildTableMultiObject {
        @Id
        private Integer b_id;

        @ChildFK(mainTableClass = A.class)
        private Integer aId;

//        @Column
//        private String name;
    }

    static interface BMapper extends IChildTableMultiMapper<B>{

    }

    static interface BService extends IChildTableMultiService<B,BMapper> {

    }
}
