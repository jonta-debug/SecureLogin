package auth;

import database.VerificationCodeRepository;
import model.VerificationCode;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

public class TwoFactorServiceTest {
    public static void main(String[] args) throws SQLException {
        String secret = System.getenv("TWO_FACTOR_SECRET");

        System.out.println("Variable vorhanden: " + (secret != null));
        System.out.println("Variable leer: " + (secret != null && secret.isBlank()));
        System.out.println(System.getenv("TWO_FACTOR_SECRET") != null);

        CodeHashService codeHashService =
                new CodeHashService();

        VerificationCodeRepository verificationCodeRepository =
                new VerificationCodeRepository();

        TwoFactorService service = new TwoFactorService(
                codeHashService,
                verificationCodeRepository
        );

        long userId = 1;
        VerificationResult result = service.createVerification(userId);
//        System.out.println("Generated code: " + code);
//
//
//        Optional<VerificationCode> verificationCode =
//                verificationCodeRepository.findActiveByUserId(userId);
//
//        if(verificationCode.isPresent()) {
//            System.out.println("Found active verification code: " + verificationCode.get().getCodeHash());
//            System.out.println("Expires at: "
//                    + verificationCode.get().getExpiresAt()
//                    .atZone(ZoneId.of("Europe/Berlin")));
//            System.out.println("Attempts: " + verificationCode.get().getAttempts());
//            verificationCodeRepository.incrementAttempts(verificationCode.get().getId());
//
//           verificationCode = verificationCodeRepository.findActiveByUserId(userId);
//
//            System.out.println("New attempts: " + verificationCode.get().getAttempts());
//
//            verificationCodeRepository.markAsUsed(verificationCode.get().getId());
//
//            Optional<VerificationCode> afterUsed =
//                    verificationCodeRepository.findActiveByUserId(userId);
//
//            System.out.println("Code still active: " + afterUsed.isPresent());
//
//            String newCode = service.createVerification(userId);

//            String code = service.createVerification(userId);
//
//            System.out.println(service.verifyCode(userId, code));
//            System.out.println(service.verifyCode(userId, code));
//
//            String code1 = service.createVerification(userId);
//            String code2 = service.createVerification(userId);
//
//            System.out.println("Alter Code: " +
//                    service.verifyCode(userId, code1));
//
//            System.out.println("Neuer Code: " +
//                    service.verifyCode(userId, code2));

            UUID wrongChallengeId = UUID.randomUUID();

            VerificationCode activeCode = verificationCodeRepository
                    .findActiveByUserId(userId)
                    .orElseThrow();

            System.out.println("Challenge-ID: " + activeCode.getChallengeId());
            System.out.println("Wrong Challenge ID: " + wrongChallengeId);

            System.out.println(service.verifyCode(
                    userId, wrongChallengeId, result.code()));

            System.out.println(service.verifyCode(
                    userId, activeCode.getChallengeId(), result.code()));



//        } else {
//            System.out.println("No active verification code found");
//        }


    }

}
