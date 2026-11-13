package auth;

import database.PostgresConnector;
import database.VerificationCodeRepository;
import model.VerificationCode;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.Instant;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class TwoFactorService {
    SecureRandom random = new SecureRandom();

    private final CodeHashService codeHashService;
    private final VerificationCodeRepository verificationCodeRepository;


    public TwoFactorService(CodeHashService codeHashService,
                            VerificationCodeRepository verificationCodeRepository) {
        this.codeHashService = codeHashService;
        this.verificationCodeRepository = verificationCodeRepository;
    }


    public String generateCode(){
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public VerificationResult createVerification(long userId) throws SQLException {
        String code = generateCode();
        String codeHash = codeHashService.createCodeHash(userId, code);

        VerificationCode vc =  new VerificationCode(0, userId,codeHash, false);
        verificationCodeRepository.replaceActiveCode(vc);

        return new VerificationResult(vc.getChallengeId(), code);
    }



    public boolean verifyCode(long userId, UUID challengeId , String enteredCode) throws SQLException{
//        Optional<VerificationCode> activeCode =
//                verificationCodeRepository.findActiveByUserId(userId);
//        if (activeCode.isEmpty()) {
//            return false;
//        }
//        String enteredHash = codeHashService.createCodeHash(userId, enteredCode);
//        String storedHash  = activeCode.get().getCodeHash();
//
//        byte[] storedHashBytes = Base64.getDecoder().decode(storedHash);
//        byte[] enteredHashBytes = Base64.getDecoder().decode(enteredHash);
//
//        return MessageDigest.isEqual(storedHashBytes, enteredHashBytes);

        String enteredHash = codeHashService.createCodeHash(userId, enteredCode);

        return verificationCodeRepository.verifyAndConsumeCode(
                userId,
                challengeId,
                enteredHash
        );

    }

}
