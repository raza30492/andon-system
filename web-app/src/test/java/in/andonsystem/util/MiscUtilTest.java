package in.andonsystem.util;

import in.andonsystem.Level;
import in.andonsystem.v1.entity.Designation;
import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v2.entity.User;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mdzahidraza on 17/06/17.
 */
public class MiscUtilTest {

    @Test
    public void getUsersMobilev1(){
        Problem problem = new Problem(1L,"Test Problem","Test Department");
        User user1 = new User(1L,"Test User1","8987525008","USER","FACTORY","LEVEL1");
        User user2 = new User(2L,"Test User2","8904360418","USER","FACTORY","LEVEL1");
        User user3 = new User(3L,"Test User3","9852704092","USER","FACTORY","LEVEL1");
        Designation designation = new Designation(1L,"Test Designation","1,2,3,4,5,6,7,8",1);
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);
        designation.setUsers(users);
        Set<Designation> designations = new HashSet<>();
        designations.add(designation);
        problem.setDesignations(designations);

        String mobileNumbers = MiscUtil.getUserMobileNumbers(problem, Level.LEVEL1);
        Assert.assertEquals(mobileNumbers, "8987525008,8904360418,9852704092");
    }
}