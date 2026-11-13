package auth;

public class CodeHashServiceTest {
    public static void main(String[] args) {
        CodeHashService service = new CodeHashService();

        String hash1 = service.createCodeHash(1, "123456");
        String hash2 = service.createCodeHash(1, "123456");
        String hash3 = service.createCodeHash(2, "123456");

        System.out.println(hash1.equals(hash2));
        System.out.println(hash1.equals(hash3));
    }

}
