package pl.pawelcz.AnonyNote.core.security;

public interface EncryptionService {
    String encrypt(String plainText);
    String decrypt(String encryptedText);
}