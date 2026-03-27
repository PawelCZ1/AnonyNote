package pl.pawelcz.AnonyNote.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesGcmEncryptionService implements EncryptionService {

	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int IV_LENGTH = 12;
	private static final int TAG_LENGTH_BITS = 128;

	private final SecretKeySpec keySpec;
	private final SecureRandom secureRandom = new SecureRandom();

	public AesGcmEncryptionService(
			@Value("${app.security.encryption-key-base64:MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=}")
			String keyBase64) {
		byte[] key = Base64.getDecoder().decode(keyBase64);
		if (key.length != 16 && key.length != 24 && key.length != 32) {
			throw new IllegalArgumentException("AES key must be 16, 24 or 32 bytes");
		}
		this.keySpec = new SecretKeySpec(key, "AES");
	}

	@Override
	public String encrypt(String plainText) {
		try {
			byte[] iv = new byte[IV_LENGTH];
			secureRandom.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));

			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			byte[] output = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, output, 0, iv.length);
			System.arraycopy(encrypted, 0, output, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(output);
		} catch (Exception ex) {
			throw new IllegalStateException("Encryption failed", ex);
		}
	}

	@Override
	public String decrypt(String encryptedText) {
		try {
			byte[] input = Base64.getDecoder().decode(encryptedText);
			if (input.length <= IV_LENGTH) {
				throw new IllegalArgumentException("Encrypted payload is too short");
			}

			byte[] iv = new byte[IV_LENGTH];
			byte[] encrypted = new byte[input.length - IV_LENGTH];
			System.arraycopy(input, 0, iv, 0, IV_LENGTH);
			System.arraycopy(input, IV_LENGTH, encrypted, 0, encrypted.length);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(TAG_LENGTH_BITS, iv));

			byte[] decrypted = cipher.doFinal(encrypted);
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new IllegalStateException("Decryption failed", ex);
		}
	}
}
