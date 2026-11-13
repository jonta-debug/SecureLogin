package auth;

public class PasswordServiceTest {
    public static void main(String[] args) {
        PasswordService ps = new PasswordService();
        char[] testPwd = {'t', 'e', 's', 't'};

        try{
            String hashTestPwd = ps.hashPassword(testPwd);
            char[] verifyPwd = {'t', 'f', 's', 't'};

            if(ps.verifyPassword(verifyPwd, hashTestPwd)){
                System.out.println("Success");
            }
            else{
                System.out.println("Failed");
            }

            char[] pwd1 = "test".toCharArray();
            char[] pwd2 = "test".toCharArray();

            String hash1 = ps.hashPassword(pwd1);
            String hash2 = ps.hashPassword(pwd2);

            boolean result1 = ps.verifyPassword(
                    "test".toCharArray(), hash1
            );

            boolean result2 = ps.verifyPassword(
                    "test".toCharArray(), hash2
            );

            System.out.println(result1);
            System.out.println(result2);
        } catch(Exception e){
            e.printStackTrace();
        }




    }
}
