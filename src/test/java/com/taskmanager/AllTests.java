package com.taskmanager;

import com.taskmanager.domain.CategoryTest;
import com.taskmanager.domain.PriorityTest;
import com.taskmanager.domain.StatusTest;
import com.taskmanager.domain.TagTest;
import com.taskmanager.domain.TaskTest;
import com.taskmanager.domain.UserTest;
import com.taskmanager.persistence.FileCategoryRepositoryTest;
import com.taskmanager.persistence.FileTaskRepositoryTest;
import com.taskmanager.persistence.FileUserRepositoryTest;
import com.taskmanager.testframework.TestCase;
import com.taskmanager.testframework.TestFrameworkTest;
import com.taskmanager.util.ConsoleIOTest;
import com.taskmanager.util.FileStorageTest;
import com.taskmanager.util.IdGeneratorTest;
import com.taskmanager.util.PasswordHasherTest;

import java.util.List;

public final class AllTests {

    private AllTests() {
    }

    public static List<Class<? extends TestCase>> suite() {
        return List.of(
                TestFrameworkTest.class,
                PriorityTest.class,
                StatusTest.class,
                TagTest.class,
                TaskTest.class,
                UserTest.class,
                CategoryTest.class,
                FileStorageTest.class,
                PasswordHasherTest.class,
                IdGeneratorTest.class,
                ConsoleIOTest.class,
                FileCategoryRepositoryTest.class,
                FileTaskRepositoryTest.class,
                FileUserRepositoryTest.class

        );
    }
}