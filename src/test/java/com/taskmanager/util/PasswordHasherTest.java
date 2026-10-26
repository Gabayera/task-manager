package com.taskmanager.util;

import com.taskmanager.testframework.Assert;
import com.taskmanager.testframework.TestCase;

public final class PasswordHasherTest extends TestCase {

    public void testSaltGenerationIsRandom() {
        String salt1 = PasswordHasher.generateSalt();
        String salt2 = PasswordHasher.generateSalt();
        Assert.assertNotEquals(salt1, salt2);
        Assert.assertEquals(32, salt1.length()); // 16 bytes = 32 hex chars
    }

    public void testHashAndVerify() {
        String salt = PasswordHasher.generateSalt();
        String hash = PasswordHasher.hash("secret123", salt);

        Assert.assertTrue(PasswordHasher.verify("secret123", salt, hash));
        Assert.assertFalse(PasswordHasher.verify("wrong", salt, hash));
    }

    public void testDifferentSaltsProduceDifferentHashes() {
        String salt1 = "salt1";
        String salt2 = "salt2";
        String hash1 = PasswordHasher.hash("password", salt1);
        String hash2 = PasswordHasher.hash("password", salt2);

        Assert.assertNotEquals(hash1, hash2);
    }
}